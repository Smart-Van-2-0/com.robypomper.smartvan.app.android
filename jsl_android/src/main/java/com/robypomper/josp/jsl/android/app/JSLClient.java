package com.robypomper.josp.jsl.android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.jsl.JSLListeners;
import com.robypomper.josp.jsl.android.service.JSLService;
import com.robypomper.josp.states.JSLState;

import java.util.Vector;


/**
 * Class to allow Android applications bind the JSLService.
 * <p>
 * Added JSLService management (bound and get)<br/>
 * Added JSLClientStatus as an extension of the JSL status<br/>
 * Added JSLClientStateListener<br/>
 * Added service's JSL Instance management (safe-state and get)<br/>
 * Added JSLListeners linked to the JSL Instance from service<br/>
 * <p>
 * This class can be used to bind the {@link JSLService} service and get an easy
 * access to the {@link JSL} instance.
 * <p>
 * Before use the provided JSL instance, developers can check his status and
 * availability ith the following methods:
 * <p>
 * * {@link #getState()}: return the extended JSL Client state that include
 * also the JSL instance state
 * * {@link #getJSLState()}: return the JSL instance state, using the
 * {@link JSLState#STOP} value if the JSL instance is not available.
 * <p>
 * This is an abstract class, that means, in order to use it you must implement
 * a subclass. That subclass must provide the JSLService's subclass that must be
 * used by the JSLClient. If you implemented a JSLService subclass called `MyAppService`
 * then, the JSLClient subclass must return the `MyAppService.class` instance.
 *
 * @param <T> specify the {@link JSLService} implementation to use.
 */
public abstract class JSLClient<T extends JSLService> {

    private final String LOG_TAG = "JSLA.JSLClient";
    private final Context context;
    private T jslService;
    private JSLClientState extendedState = JSLClientState.NOT_BOUND;
    private final Vector<JSLClientStateListener> onJSLStateChange = new Vector<>();
    private final JSLListeners jslListeners = new JSLListeners();


    // Constructor

    /**
     * Create a new JSLClient and starts his internal thread.
     *
     * @param context the Android context to use for current JSLClient.
     */
    public JSLClient(Context context) {
        this.context = context;
    }

    /**
     * Unbound the service, if bounded, and stops his internal thread.
     */
    @Override
    protected void finalize() {
        if (isBound()) unboundService();
    }

    /**
     * @return `true` if the JSLService instance is set, otherwise it return `false`.
     */
    public boolean isBound() {
        return jslService != null;
    }

    /**
     * @return `true` if the JSL instance is set, otherwise it return `false`.
     */
    public boolean isReady() {
        return getJSL() != null;
    }


    // JSL Service

    /**
     * @return bound JSLService if any, otherwise it returns null.
     */
    public T getService() {
        return jslService;
    }

    /**
     * @return the class instance of the JSLService's subclass to connect to.
     */
    protected abstract Class<?> getJSLServiceClass();

    /**
     * Start request to bound to the {@link JSLApplication#getJSLClient()} service.
     * Bound events can be handled using the {@link JSLClientStateListener} listener.
     */
    public void boundService() {
        if (isBound()) return;
        Log.i(LOG_TAG, "JSLApplication connecting to the JSL Service");
        Intent intent_service = new Intent(this.context, getJSLServiceClass());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            try {
                context.startForegroundService(intent_service);
            } catch (Exception e) {
                Log.w(LOG_TAG, "JSLApplication failed to start foreground service", e);
            }
        context.startService(intent_service);
        context.bindService(intent_service, connection, Context.BIND_AUTO_CREATE);
        JSLClientState oldState = getState();
        extendedState = JSLClientState.BOUNDING;
        emitOnJSLStateChange(extendedState, oldState);
    }

    /**
     * Start request to unbound to the {@link JSLApplication#getJSLClient()} service.
     * Unbound events can be handled using the {@link JSLClientStateListener} listener.
     */
    public void unboundService() {
        if (!isBound()) return;
        Log.i(LOG_TAG, "JSLApplication disconnecting to the JSL Service");
        context.unbindService(connection);
        Intent intent_service = new Intent(this.context, getJSLServiceClass());
        context.stopService(intent_service);
    }

    /**
     * Defines callbacks for service binding, passed to bindService().
     */
    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            T.LocalBinder binder = (T.LocalBinder) service;
            //noinspection unchecked
            jslService = (T) binder.getService();
            jslService.addJSLInstanceListener(jslInstanceListener);

            Log.i(LOG_TAG, "JSLApplication connected to JSL Service");

            if (getJSL() == null) {
                JSLClientState oldState = getState();
                extendedState = JSLClientState.NOT_INIT_JSL;
                emitOnJSLStateChange(extendedState, oldState);
            } else {
                updateExtendedStateFromJSL();   // first state from JSL (if already initialized on service bound)
                JSLClientState oldState = getState();
                extendedState = JSLClientState.valueOf(getJSL().getState().name());
                emitOnJSLStateChange(extendedState, oldState);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            jslService.removeJSLInstanceListener(jslInstanceListener);
            jslService = null;

            Log.i(LOG_TAG, "JSLApplication disconnected from JSL service");

            JSLClientState oldState = getState();
            extendedState = JSLClientState.NOT_BOUND;
            emitOnJSLStateChange(extendedState, oldState);
        }
    };


    // Extended JSL Client status (include JSL status)

    /**
     * Listen for JSLService's JSLInstance events and update current `extendedState`
     * It registers also the `jslStateListener` to the JSL State events.
     */
    private final JSLService.JSLInstanceListener jslInstanceListener = new JSLService.JSLInstanceListener() {
        @Override
        public void onJSLInstanceCreated(JSL jsl, JSLService service) {
            updateExtendedStateFromJSL();   // first sate from JSL (if NOT yet initialized on service bound)
            jsl.addListener(jslStateListener);
        }

        @Override
        public void onJSLInstanceReady(JSL jsl, JSLService service) {
        }

        @Override
        public void onJSLInstanceShutdown(JSLService service) {
        }
    };

    /**
     * Listen for JSLState events and update current `extendedState`.
     */
    private final JSL.JSLStateListener jslStateListener = new JSL.JSLStateListener() {
        @Override
        public void onJSLStateChanged(JSLState newState, JSLState oldState) {
            updateExtendedStateFromJSL();
        }
    };

    /**
     * @return the state of the JSL Client, this state include also the JSL
     * instance states (if an JSL instance is available).
     */
    public JSLClientState getState() {
        return extendedState;
    }

    /**
     * Updates the `extendedState` using the state from current JSL instance.
     */
    private void updateExtendedStateFromJSL() {
        JSLClientState oldState = getState();
        extendedState = JSLClientState.valueOf(getJSL().getState().name());
        emitOnJSLStateChange(extendedState, oldState);

        if (oldState == JSLClientState.SHOUTING && extendedState == JSLClientState.STOP) {
            oldState = JSLClientState.STOP;
            extendedState = JSLClientState.UNBOUNDING;
            emitOnJSLStateChange(extendedState, oldState);
        }
    }


    // JSL

    /**
     * Before return current JSL instance, this method checks the initialized
     * components like the jslService. If something is not yet initialized or
     * ready, then this method return `null`.
     * <p>
     *
     * @return the JSL instance, or `null` if not yet ready.
     */
    public JSL getJSL() {
        if (jslService == null) return null;
        return jslService.getJSL();
    }

    /**
     * @return the JSL instance state in a safe way. That means, if the JSL
     * instance is not yet ready, then it return the {@link JSLState#STOP} value.
     */
    public JSLState getJSLState() {
        if (getJSL() != null) return getJSL().getState();
        throw new IllegalStateException("JSL instance not yet ready");
    }


    // Listeners

    /**
     * Listener interface for {@link JSLClient}'s state events.
     */
    public interface JSLClientStateListener {
        void stateChanged(JSLClientState newState, JSLClientState oldState);
    }

    /**
     * Register listener {@link JSLClient}'s state events.
     */
    public void registerOnJSLStateChange(JSLClientStateListener listener) {
        if (!onJSLStateChange.contains(listener)) onJSLStateChange.add(listener);
    }

    /**
     * Deregister listener {@link JSLClient}'s state events.
     */
    public void deregisterOnJSLStateChange(JSLClientStateListener listener) {
        onJSLStateChange.remove(listener);
    }

    /**
     * Emits event `onStateChange` as {@link JSLClient}'s state events.
     */
    private void emitOnJSLStateChange(JSLClientState new_state, JSLClientState old_state) {
        jslListeners.setJSL(getJSL());
        for (JSLClientStateListener l : onJSLStateChange)
            l.stateChanged(new_state, old_state);
    }


    // JSL Listeners

    /**
     * @return the {@link JSLListeners} instance linked to the JSL Instance
     * from current JSLClient.
     */
    public JSLListeners getJSLListeners() {
        return jslListeners;
    }

}
