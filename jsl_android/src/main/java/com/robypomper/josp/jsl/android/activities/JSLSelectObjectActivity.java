package com.robypomper.josp.jsl.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.robypomper.discovery.Discover;
import com.robypomper.josp.jsl.android.R;
import com.robypomper.josp.jsl.android.app.JSLApplication;
import com.robypomper.josp.jsl.android.app.JSLClient;
import com.robypomper.josp.jsl.android.service.JSLService;
import com.robypomper.josp.jsl.comm.JSLLocalClientsMngr;
import com.robypomper.josp.jsl.android.databinding.ActivityJslWaitObjectBinding;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.states.JSLState;
import com.robypomper.josp.states.StateException;

import java.util.Collections;
import java.util.List;

/**
 * Base class for any activity that must wait for an object.
 * <p>
 * TODO add filter by object id and others params
 */
public abstract class JSLSelectObjectActivity extends AppCompatActivity {

    private final String modelName;
    private final Class<? extends Activity> nextActivityClass;
    private ActivityJslWaitObjectBinding binding;
    private JSLClient<JSLService> jslClient;


    // Constructor

    /**
     * Create an activity that wait for `modelName` objects and when the users
     * choose one, it starts the {@link #nextActivityClass} activity.
     *
     * @param modelName the object's model to use as a filter.
     * @param nextActivityClass the class of the activity to show after the user selected an object.
     */
    public JSLSelectObjectActivity(String modelName, Class<? extends Activity> nextActivityClass) {
        this.modelName = modelName;
        this.nextActivityClass = nextActivityClass;
    }

    /** @return the model name used to filter the remote objects. */
    public String getModelName() {
        return modelName;
    }

    /** @return the activity's class to show when an object has been selected. */
    public Class<? extends Activity> getNextActivityClass() {
        return nextActivityClass;
    }

    /** Utils class to show the next activity. This method is for subclasses. */
    protected void showNextActivity() {
        showNextActivity(null);
    }

    /**
     * Utils class to show the next activity. This method is for subclasses.
     *
     * @param bundle the intent's bundle to send to the next activity, it can be null.
     */
    protected void showNextActivity(Bundle bundle) {
        Intent intent_activity = new Intent(JSLSelectObjectActivity.this, getNextActivityClass());
        if (bundle != null)
            intent_activity.putExtras(bundle);
        startActivity(intent_activity);
    }


    // Android

    /**
     * Setup the UI and add the main listener from the JSLClient.
     * <p>
     * It also checks if there are already available objects.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();

        // Check JSL state, registerRemoteObject
        //noinspection unchecked
        JSLApplication<JSLService> app = ((JSLApplication<JSLService>) this.getApplication());
        jslClient = app.getJSLClient();
        if (jslClient.getJSLState() == JSLState.RUN) {
            // Search for remote objects by obj's model
            JSLObjsMngr objsMngr = jslClient.getJSL().getObjsMngr();
            List<JSLRemoteObject> objs = objsMngr.getByModel(modelName);
            if (objs.size() > 0)
                // propose found objects
                proposeFoundedSmartVan(objs);
        }
        // register JSL ObjsMngrListenersByModel
        jslClient.getJSLListeners().addObjsMngrListenersByModel(modelName, remObjByModelListener);
    }

    /**
     * Remove the main listener from the JSLClient.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        jslClient.getJSLListeners().removeObjsMngrListenersByModel(modelName, remObjByModelListener);
    }


    // UI management (for subclassing)

    /**
     * Commodity method for subclasses. It can be overwritten to use a different
     * activity UI.
     */
    protected void setupUI() {
        binding = ActivityJslWaitObjectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLocalConnRestartClick(view);
            }
        });
    }


    // Remote obj and comp

    /** Simplified version of the {@link #proposeFoundedSmartVan(List)} method. */
    private void proposeFoundedSmartVan(JSLRemoteObject obj) {
        proposeFoundedSmartVan(Collections.singletonList(obj));
    }

    /**
     * Propone given objects to the user. Then, if user chose one, starts the next activity.
     *
     * @param objs the list of objects to propose.
     */
    protected abstract void proposeFoundedSmartVan(List<JSLRemoteObject> objs);


    // Remote listeners

    /**
     * Listen for added objects with specified `model`, then propose found objects to the user.
     */
    private final JSLObjsMngr.ObjsMngrListener remObjByModelListener = new JSLObjsMngr.ObjsMngrListener() {

        @Override
        public void onObjAdded(JSLRemoteObject obj) {
            proposeFoundedSmartVan(obj);
        }

        @Override
        public void onObjRemoved(JSLRemoteObject obj) {
        }

    };


    // Local communication restart

    /**
     * React on JSL Local communication events to handle the Restart procedure.
     * At the end of the procedure, it remove itself from the registered listeners.
     */
    final JSLLocalClientsMngr.CommLocalStateListener restartListener = new JSLLocalClientsMngr.CommLocalStateListener() {

        @Override
        public void onStarted() {
            JSLLocalClientsMngr localMngr = jslClient.getJSL().getCommunication().getLocalConnections();
            localMngr.removeListener(restartListener);
            notifyRestartPhase("JSL Local communication restarted, successfully.");
        }

        @Override
        public void onStopped() {
            notifyRestartPhase("JSL Local communication stopped, restarting.");
            JSLLocalClientsMngr localMngr = jslClient.getJSL().getCommunication().getLocalConnections();
            try {
                localMngr.start();
            } catch (StateException | Discover.DiscoveryException e) {
                throw new RuntimeException(e);
            }
        }

    };

    /**
     * Re-action on JSL Local communication Restart button click.
     */
    private void onLocalConnRestartClick(View ignoredView) {
        if (jslClient.getJSLState() != JSLState.RUN) {
            notifyRestartPhase("JSL not running, please wait.");
            return;
        }
        restartLocalDiscovery();
    }

    /**
     * Show the phase's message to the user.
     * @param text the message that describe the phase.
     */
    private void notifyRestartPhase(String text) {
        Snackbar.make(binding.fab, text, Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.fab)
                .setAction("Action", null)
                .show();
    }

    /**
     * It starts the JSL Local communication Restart procedure using the
     * {@link #restartListener} as support listener.
     */
    private void restartLocalDiscovery() {
        JSLLocalClientsMngr localMngr = jslClient.getJSL().getCommunication().getLocalConnections();
        localMngr.addListener(restartListener);
        try {
            localMngr.stop();
        } catch (StateException | Discover.DiscoveryException e) {
            throw new RuntimeException(e);
        }
    }

}