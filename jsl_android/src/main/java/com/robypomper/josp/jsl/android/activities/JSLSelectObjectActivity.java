package com.robypomper.josp.jsl.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.robypomper.discovery.Discover;
import com.robypomper.josp.jsl.android.R;
import com.robypomper.josp.jsl.android.adapters.RemoteObjectAdapter;
import com.robypomper.josp.jsl.android.app.JSLApplication;
import com.robypomper.josp.jsl.android.databinding.ActivityJslWaitObjectBinding;
import com.robypomper.josp.jsl.android.service.JSLService;
import com.robypomper.josp.jsl.comm.JSLLocalClientsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.states.JSLState;
import com.robypomper.josp.states.StateException;

import java.util.Collections;
import java.util.List;

/**
 * Base class for any activity that must wait for an object.
 * <p>
 * TODO add filter by object id and others params
 * @noinspection unused
 */
public abstract class JSLSelectObjectActivity extends BaseObjectsActivity {

    private static final String LOG_TAG = "JSLA.Actvt.SelectObject";

    private final String modelName;
    private final Class<? extends Activity> nextActivityClass;
    private ActivityJslWaitObjectBinding binding;


    private class ViewHolderImpl extends RemoteObjectAdapter.ViewHolder {

        private final View view;

        public ViewHolderImpl(View view) {
            super(view);
            this.view = view;
        }

        @Override
        public void bind(JSLRemoteObject obj) {
            // fill layout fields
            TextView txtObjName = view.findViewById(R.id.txtObjName);
            txtObjName.setText(obj.getName());
            TextView txtObjId = view.findViewById(R.id.txtObjId);
            txtObjId.setText(obj.getId());

            view.setTag(obj);

            // register new listener for current item
            // (automatically delete the old one)
            view.setOnClickListener(onItemClickListener);
        }

    }


    // Constructor

    /**
     * Create an activity that wait for `modelName` objects and when the users
     * choose one, it starts the {@link #nextActivityClass} activity.
     *
     * @param modelName         the object's model to use as a filter.
     * @param nextActivityClass the class of the activity to show after the user selected an object.
     */
    public JSLSelectObjectActivity(String modelName, Class<? extends Activity> nextActivityClass) {
        super(Collections.singletonList(modelName), null, null);
        this.modelName = modelName;
        this.nextActivityClass = nextActivityClass;
    }


    // Android

    /**
     * Setup the UI and add the main listener from the JSLClient.
     * <p>
     * It also checks if there are already available objects.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, String.format("JSLSelectObjectActivity is being created to filter `%s` object models", modelName));

        super.onCreate(savedInstanceState);

        setupUI();
    }

    /**
     * Remove the main listener from the JSLClient.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "JSLSelectObjectActivity is being destroyed");
    }


    // Getters

    /**
     * @return the model name used to filter the remote objects.
     */
    public String getModelName() {
        return modelName;
    }


    // Next activity to show

    /**
     * @return the activity's class to show when an object has been selected.
     */
    public Class<? extends Activity> getNextActivityClass() {
        return nextActivityClass;
    }

    /**
     * Start the next activity.
     */
    protected void goToNextActivity() {
        goToNextActivity(null);
    }

    /**
     * Start the next activity with the given bundle.
     *
     * @param bundle the bundle to pass to the next activity.
     */
    protected void goToNextActivity(Bundle bundle) {
        Log.i(LOG_TAG, String.format("JSLSelectObjectActivity start next activity '%s'", getNextActivityClass().getName()));
        Intent intent_activity = new Intent(this, getNextActivityClass());
        if (bundle != null) intent_activity.putExtras(bundle);
        startActivity(intent_activity);
    }


    // UI management (for subclassing)

    /**
     * Commodity method for subclasses. It can be overwritten to use a different
     * activity UI.
     */
    protected void setupUI() {
        binding = ActivityJslWaitObjectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.listFoundObjects.setHasFixedSize(false);
        binding.listFoundObjects.setAdapter(new RemoteObjectAdapter(this, getJSLClient()) {

            /**
             * Create new views (invoked by the layout manager).
             * <p>
             * Create a new view, which defines the UI of the list item
             *
             * @param viewGroup The ViewGroup into which the new View will be added after it is bound to
             *                  an adapter position.
             * @param viewType  The view type of the new View.
             * @return the created view.
             */
            @NonNull
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lay_remote_object_simple, viewGroup, false);
                return new ViewHolderImpl(view);
            }

        });
        binding.listFoundObjects.setLayoutManager(new LinearLayoutManager(this));

        binding.fab.setOnClickListener(onFabClickListener);
    }


    // Remote obj selection

    private final View.OnClickListener onItemClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view.getTag() == null || !(view.getTag() instanceof JSLRemoteObject)) {
                Log.e(LOG_TAG, "View tag must be a JSLRemoteObject");
                throw new RuntimeException("View tag must be a JSLRemoteObject");
            }
            JSLRemoteObject obj = (JSLRemoteObject) view.getTag();
            Log.i(LOG_TAG, String.format("User selected object '%s'", obj.getName()));
            proposeFoundedSmartVan(obj);
        }

    };

    // TODO rename proposeFoundedSmartVan

    /**
     * Simplified version of the {@link #proposeFoundedSmartVan(List)} method.
     */
    private void proposeFoundedSmartVan(JSLRemoteObject obj) {
        proposeFoundedSmartVan(Collections.singletonList(obj));
    }

    // TODO rename proposeFoundedSmartVan

    /**
     * Propose given objects to the user. Then, if user chose one, starts the next activity.
     *
     * @param objs the list of objects to propose.
     */
    protected abstract void proposeFoundedSmartVan(List<JSLRemoteObject> objs);


    // JSL local communication restart

    private final View.OnClickListener onFabClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            restartLocalDiscovery();
        }

    };

    /**
     * React on JSL Local communication events to handle the Restart procedure.
     * At the end of the procedure, it remove itself from the registered listeners.
     */
    final JSLLocalClientsMngr.CommLocalStateListener restartListener = new JSLLocalClientsMngr.CommLocalStateListener() {

        @Override
        public void onStarted() {
            JSLLocalClientsMngr localMngr = getJSLClient().getJSL().getCommunication().getLocalConnections();
            localMngr.removeListener(restartListener);
            Log.d(LOG_TAG, "JSL Local communication restarted, successfully");
            notifyRestartPhase("JSL Local communication restarted, successfully");
        }

        @Override
        public void onStopped() {
            Log.d(LOG_TAG, "JSL Local communication stopped, restarting");
            notifyRestartPhase("JSL Local communication stopped, restarting");
            JSLLocalClientsMngr localMngr = getJSLClient().getJSL().getCommunication().getLocalConnections();
            try {
                localMngr.start();
            } catch (StateException | Discover.DiscoveryException e) {
                Log.e(LOG_TAG, "Error while restarting JSL Local communication", e);
                notifyRestartPhase("Error while restarting JSL Local communication");
                throw new RuntimeException("Error while restarting JSL Local communication", e);
            }
        }

    };

    /**
     * It starts the JSL Local communication Restart procedure using the
     * {@link #restartListener} as support listener.
     */
    private void restartLocalDiscovery() {
        if (getJSLClient().getJSLState() != JSLState.RUN) {
            Log.w(LOG_TAG, "Can't restart JSL Local Communication stopped because JSL not running, please retry later");
            notifyRestartPhase("Can't restart JSL Local Communication stopped because JSL not running, please retry later");
            return;
        }

        JSLLocalClientsMngr localMngr = getJSLClient().getJSL().getCommunication().getLocalConnections();
        localMngr.addListener(restartListener);
        JSLApplication<? extends JSLService> app = getJSLApplication();
        app.runOnNetworkThread(new Runnable() {
            @Override
            public void run() {
                Log.w(LOG_TAG, "JSL Local Communication stopping");
                notifyRestartPhase("JSL Local Communication stopping");

                try {
                    localMngr.stop();
                } catch (StateException | Discover.DiscoveryException e) {
                    Log.e(LOG_TAG, "Error while stopping JSL Local communication", e);
                    notifyRestartPhase("Error while stopping JSL Local communication");
                    throw new RuntimeException("Error while stopping JSL Local communication", e);
                }
            }
        });
    }

    /**
     * Show the phase's message to the user.
     *
     * @param text the message that describe the phase.
     */
    private void notifyRestartPhase(String text) {
        Snackbar.make(binding.fab, text, Snackbar.LENGTH_LONG).setAnchorView(R.id.fab).setAction("Action", null).show();
    }

}