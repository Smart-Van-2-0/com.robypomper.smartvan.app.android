package com.robypomper.josp.jsl.android.activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.robypomper.josp.jsl.android.app.JSLApplication;
import com.robypomper.josp.jsl.android.app.JSLClient;
import com.robypomper.josp.jsl.android.app.JSLClientState;
import com.robypomper.josp.jsl.android.service.JSLService;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.ObjComm;
import com.robypomper.josp.jsl.objs.remote.ObjStruct;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLContainer;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.josp.states.JSLState;

/**
 * Base class for all activities that need to work with the JSL Service.
 *
 * ...
 *
 * TODO: when the JSL Service is not ready, show an overlay with a warning message
 *       the message view must be customizable
 */
public class BaseJSLActivity extends AppCompatActivity {

    private JSLApplication<? extends JSLService> jslApp;
    private JSLClient<? extends JSLService> jslClient;

    private boolean onJSLReadyEmitted = false;


    // Android

    /**
     * Called when the activity is starting. This method looks for the object ID of the remote
     * object that must be used by the activity from the activity's intent, or from the activity's
     * saved instance state. If the object ID is not found, the method throws a RuntimeException.
     * <p>
     * The method also checks the state of the JSLClient object. If the JSLClient object is in the
     * RUN state, the method looks for the remote object that has the specified object ID. If the
     * remote object is found, the method registers itself as a listener for the remote object.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState(Bundle). Note: Otherwise it is
     *                           null. This value may be null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onJSLReadyEmitted = false;

        // Check JSL state, registerRemoteObject
        try {
            jslApp = (JSLApplication<? extends JSLService>) this.getApplication();
        } catch (ClassCastException e) {
            throw new RuntimeException("The application must be a JSLApplication", e);
        }
        jslClient = jslApp.getJSLClient();
        if (jslClient.isReady())
            emitOnJSLReady();
        else
            resetOnJSLReady();
        jslClient.registerOnJSLStateChange(onJSLStateChangeListener);
    }

    /**
     * Called when the activity is no longer visible to the user. This method deregisters itself
     * as a listener for the remote object that is used by the activity.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    // Getters

    /**
     * @return the JSLApplication object that executed the activity.
     */
    public JSLApplication<? extends JSLService> getJSLApplication() {
        return jslApp;
    }

    /**
     * @return the JSLClient object that is used by the activity.
     */
    public JSLClient<? extends JSLService> getJSLClient() {
        return jslClient;
    }

    /**
     * The method returns true if the remote object is ready. It means that the remote object has
     * been registered, and its structure has been received.
     */
    public boolean isJSLReady() {
        return onJSLReadyEmitted;
    }

    // Events methods for sub-classes

    private void emitOnJSLReady() {
        if (!onJSLReadyEmitted) {
            onJSLReadyEmitted = true;
            onJSLReady();
        }
    }

    private void resetOnJSLReady() {
        if (onJSLReadyEmitted) {
            onJSLReadyEmitted = false;
            onJSLNotReady();
        }
    }

    /**
     * The method is called when the JSL service is ready. It means that the JSL Service has been
     * started, and it is in the RUN state.
     * <p>
     * The value returned by the isJSLReady method is updated before this method is called.
     */
    protected void onJSLReady() {
    }


    /**
     * The method is called when the JSL service is NOT ready. It means that the JSL Service has
     * been stopped, or it is not in the RUN state.
     * <p>
     * The value returned by the isJSLReady method is updated before this method is called.
     */
    protected void onJSLNotReady() {
    }


    // JSL (Client) listeners

    private final JSLClient.JSLClientStateListener onJSLStateChangeListener = new JSLClient.JSLClientStateListener() {

        @Override
        public void stateChanged(JSLClientState newState, JSLClientState oldState) {
            if (newState == JSLClientState.RUN) emitOnJSLReady();
            else resetOnJSLReady();
        }

    };

}
