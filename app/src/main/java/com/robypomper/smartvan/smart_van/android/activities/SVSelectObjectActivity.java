package com.robypomper.smartvan.smart_van.android.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.robypomper.josp.jsl.android.activities.JSLSelectObjectActivity;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.smartvan.smart_van.android.commons.SVDefinitions;
import com.robypomper.smartvan.smart_van.android.storage.SVStorage;
import com.robypomper.smartvan.smart_van.android.storage.SVStorageSingleton;

import java.util.List;

public class SVSelectObjectActivity extends JSLSelectObjectActivity {

    // Constants

    public static final String MODEL_NAME = SVDefinitions.MODEL_NAME;
    public static final Class<? extends Activity> NEXT_ACTIVITY = SVDefinitions.NEXT_ACTIVITY_SELECTOBJECT;

    // Internal vars

    private SVStorage svStorage;
    private String favouriteObjId;


    // Constructors

    /**
     * Create an activity that wait for `modelName` objects and when the users
     * choose one, it starts the {@link #getNextActivityClass()} activity.
     */
    public SVSelectObjectActivity() {
        super(MODEL_NAME, NEXT_ACTIVITY);
    }

    /**
     * Setup the UI and add the main listener from the JSLClient.
     * <p>
     * It also checks if there are already available objects.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        svStorage = SVStorageSingleton.getInstance();

        // Check if there is already a favourite object
        favouriteObjId = svStorage.getFavouriteObjectId();
        if (favouriteObjId != null) {
            JSLRemoteObject remObj = getJSL().getObjsMngr().getById(favouriteObjId);
            if (remObj != null)
                onRemoteObjectAdded(remObj);    // inject into onRemoteObjectAdded chain
        }
    }

    @Override
    protected void proposeFoundedSmartVan(List<JSLRemoteObject> objs) {
        assert objs.size() == 1;
        String remObjId = objs.get(0).getId();

        if (svStorage.getFavouriteObjectId() == null
                && !svStorage.getAppPreferences().askForSetFavouriteObjectId()) {
            doGoToNextActivity(remObjId);
            return;
        }

        // Ask to the user if this is the favourite device
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        svStorage.setFavouriteObjectId(remObjId);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
                doGoToNextActivity(remObjId);
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Would you use selected SV Box as default one?")
                .setPositiveButton("Yes, set as favourite SV Box", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }


    @Override
    protected void onRemoteObjectAdded(JSLRemoteObject remObj) {
        if (!remObj.getId().equals(favouriteObjId)) return;

        if (!svStorage.getAppPreferences().askForUseFavouriteObjectId()) {
            doGoToNextActivity(remObj.getId());
            return;
        }

        // Ask to the user if proceed with the favourite device
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        doGoToNextActivity(remObj.getId());
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(SVSelectObjectActivity.this);
                builder.setMessage("Favourite SV Box found, would you proceed with it?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("Back to SV Boxes List", dialogClickListener)
                        .show();
            }
        });
    }

    private void doGoToNextActivity(String objId) {
        // Store selected object as current object id
        svStorage.setCurrentObjectId(objId);
        svStorage.addKnownObjectId(objId);

        // Go to next activity
        Bundle b = new Bundle();
        b.putString(SVMainActivity.PARAM_OBJ_ID, objId);
        goToNextActivity(b);
    }

}