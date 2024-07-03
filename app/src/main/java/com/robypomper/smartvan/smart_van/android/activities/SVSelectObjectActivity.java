package com.robypomper.smartvan.smart_van.android.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.robypomper.josp.jsl.android.activities.JSLSelectObjectActivity;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.smartvan.smart_van.android.R;
import com.robypomper.smartvan.smart_van.android.commons.SVDefinitions;
import com.robypomper.smartvan.smart_van.android.storage.SVStorage;
import com.robypomper.smartvan.smart_van.android.storage.SVStorageSingleton;

import java.util.List;


/**
 * This activity is used to select the SmartVan object to use.
 * <p>
 * It waits for the SmartVan object and when the user choose one, it starts the
 * {@link SVMainActivity} activity.
 * <p>
 * If the {@link #PARAM_AVOID_FAVOURITE} is set to true, the favourite object is ignored
 * and no dialog is shown to the user.
 */
public class SVSelectObjectActivity extends JSLSelectObjectActivity {

    // Constants

    public final static String PARAM_AVOID_FAVOURITE = SVDefinitions.PARAM_ACTIVITY_SVSELECTOBJECT_AVOID_FAVOURITE;
    public static final String MODEL_NAME = SVDefinitions.MODEL_NAME;
    public static final Class<? extends Activity> NEXT_ACTIVITY = SVDefinitions.NEXT_ACTIVITY_SELECTOBJECT;


    // Internal vars

    private SVStorage svStorage;
    private String favouriteObjId;
    private boolean ignoreFavourite = false;


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

        // Parse intent params
        if (getIntent().getExtras() != null)
            ignoreFavourite = getIntent().getExtras().getBoolean(PARAM_AVOID_FAVOURITE, false);

        // Check if there is already a favourite object
        favouriteObjId = svStorage.getFavouriteObjectId();
        if (!ignoreFavourite && favouriteObjId != null) {
            JSLRemoteObject remObj = getJSL().getObjsMngr().getById(favouriteObjId);
            if (remObj != null)
                onRemoteObjectAdded(remObj);    // inject into onRemoteObjectAdded chain
        }
    }

    @Override
    protected void proposeFoundedSmartVan(List<JSLRemoteObject> objs) {
        assert objs.size() == 1;
        String remObjId = objs.get(0).getId();
        String remObjName = objs.get(0).getName();

        if ((svStorage.getFavouriteObjectId() != null
                && svStorage.getFavouriteObjectId().compareTo(remObjId) == 0)
                || !svStorage.askForUseFavouriteObjectId()) {
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
        String title = getResources().getString(R.string.activity_svselect_object_sv_box_selected);
        builder.setMessage(String.format(title, remObjName))
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }


    @Override
    protected void onRemoteObjectAdded(JSLRemoteObject remObj) {
        if (ignoreFavourite)
            return;

        if (!remObj.getId().equals(favouriteObjId)) return;

        if (!svStorage.askForUseFavouriteObjectId()) {
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
                CheckBox checkBox = new CheckBox(SVSelectObjectActivity.this);
                checkBox.setText(R.string.activity_svselect_object_do_not_ask_me_again);

                LinearLayout layout = new LinearLayout(SVSelectObjectActivity.this);
                layout.setPadding(48, 16, 48, 16);   // TODO convert dp to px
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(checkBox);

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        svStorage.setAskForUseFavouriteObjectId(!isChecked);
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(SVSelectObjectActivity.this);
                String title = getResources().getString(R.string.activity_svselect_object_favourite_sv_box_found);
                builder.setMessage(String.format(title, remObj.getName()))
                        .setView(layout)
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
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