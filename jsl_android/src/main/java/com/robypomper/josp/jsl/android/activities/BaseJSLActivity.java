package com.robypomper.josp.jsl.android.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.jsl.JSLListeners;
import com.robypomper.josp.jsl.android.app.JSLApplication;
import com.robypomper.josp.jsl.android.app.JSLClient;
import com.robypomper.josp.jsl.android.app.JSLClientState;
import com.robypomper.josp.jsl.android.service.JSLService;

/**
 * Base class for all activities that need to work with the JSL Service.
 * <p>
 * This class extends the {@link AppCompatActivity} and get the {@link JSLApplication}
 * reference during the {@link #onCreate(Bundle)} method execution, casting the
 * activity's application. That means that <b>any activity inheriting from this
 * class must use a {@link JSLApplication} as application</b>.
 * <p>
 * Moreover, the class provides the {@link JSLClient}, the {@link JSL} and the
 * {@link JSLListeners} references that can be used by the activities subclasses
 * to access the JSL Service.
 * <p>
 * In order to help subclasses handle the JSL ready/not ready states, this class
 * handle also the {@link JSLClient} events. In particular, when the JSL Service
 * is ready, the class emits the {@link #onJSLReady()} event, and when the JSL
 * Service is not ready, the class emits the {@link #onNotReady()} event. Then,
 * the class emits the {@link #onStateChanged(JSLClientState, JSLClientState)
 * event when the JSLClient state changes.
 * <p>
 * Note: the {@link #onJSLReadyEventEmitted} variable is used to avoid to emit the
 * {@link #onJSLReady()} and the {@link #onNotReady()} events more than once. It is
 * set by the {@link #emitOnJSLReady()} and the {@link #emitOnJSLNotReady()} methods.
 * <p>
 * TODO: when the JSL Service is not ready, show an overlay with a warning message
 *       the message view must be customizable
 * @noinspection unused
 */
public class BaseJSLActivity extends AppCompatActivity {

    // Internal variables

    private JSLApplication<? extends JSLService> jslApp;
    private JSLClient<? extends JSLService> jslClient;
    private JSLListeners jslListeners;
    private boolean onJSLReadyEventEmitted = false;


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

        onJSLReadyEventEmitted = false;

        try {
            jslApp = (JSLApplication<? extends JSLService>) this.getApplication();
        } catch (ClassCastException e) {
            throw new RuntimeException("The application must be a JSLApplication", e);
        }
        jslClient = jslApp.getJSLClient();
        jslListeners = jslClient.getJSLListeners();
    }

    /**
     * Called when the activity is no longer visible to the user. This method deregisters itself
     * as a listener for the remote object that is used by the activity.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onResume() {
        super.onResume();

        // Check JSL state
        if (jslClient.isReady()) {
            onJSLReadyEventEmitted = false;  // force the onJSLReady() event
            emitOnJSLReady();
        } else {
            onJSLReadyEventEmitted = true;  // force the onNotReady() event
            emitOnJSLNotReady();
        }

        // Register JSL state listener
        jslClient.registerOnJSLStateChange(onJSLStateChangeListener);
    }

    protected void onPause() {
        super.onPause();

        // Deregister JSL state listener
        jslClient.deregisterOnJSLStateChange(onJSLStateChangeListener);
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
     * @return the JSL object that is used by the activity.
     */
    public JSL getJSL() {
        return jslClient.getJSL();
    }

    /**
     * @return the JSL object that is used by the activity.
     */
    public JSLListeners getJSLListeners() {
        return jslListeners;
    }

    /**
     * The method returns true if the remote object is ready. It means that the remote object has
     * been registered, and its structure has been received.
     */
    public boolean isJSLReady() {
        return onJSLReadyEventEmitted;
    }


    // Event emitters

    private void emitOnJSLReady() {
        // this method can be called more than once, like each time the activity
        // is resumed and the JSL state is RUN
        if (onJSLReadyEventEmitted) return;

        onJSLReadyEventEmitted = true;
        onJSLReady();
    }

    private void emitOnJSLNotReady() {
        // this method can be called more than once (from the onResume and
        // onJSLStateChangeListener:stateChanged() methods)
        if (!onJSLReadyEventEmitted) return;

        onJSLReadyEventEmitted = false;
        onNotReady();
    }

    private void emitOnStateChanged(JSLClientState newState, JSLClientState oldState) {
        onStateChanged(newState, oldState);
    }


    // Events methods (to be overridden by sub-classes)

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
    protected void onNotReady() {
    }

    /**
     * The method is called when the JSLClient state changes.
     * <p>
     * The value returned by the {@link #isJSLReady()} method is updated before
     * this method is called. Also the {@link #onJSLReady()} and the
     * {@link #onNotReady()} methods are called before this method.
     */
    protected void onStateChanged(JSLClientState newState, JSLClientState oldState) {
    }


    // JSL (Client) listeners

    /**
     * Listener for the JSLClient state changes.
     * <p>
     * It emits the {@link #onJSLReady()} event when the JSLClient state changes to RUN,
     * and it emits the {@link #onNotReady()} event when the JSLClient state changes
     * to any state different from RUN.
     * <p>
     * It emits the {@link #onStateChanged(JSLClientState, JSLClientState)} event
     * every time the JSLClient state changes.
     */
    private final JSLClient.JSLClientStateListener onJSLStateChangeListener = new JSLClient.JSLClientStateListener() {

        @Override
        public void stateChanged(JSLClientState newState, JSLClientState oldState) {
            if (newState == JSLClientState.RUN) emitOnJSLReady();
            else emitOnJSLNotReady();

            emitOnStateChanged(newState, oldState);
        }

    };

}
