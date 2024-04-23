package com.robypomper.josp.jsl.android.service;

import android.util.Log;

import com.robypomper.josp.jsl.FactoryJSL;
import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.jsl.JSLListeners;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.android.impls.DiscoverAndroid;
import com.robypomper.josp.states.JSLState;
import com.robypomper.josp.states.StateException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * ...
 * <p>
 * Moreover, the JSLService, on his create method, sets the {@link DiscoverAndroid}
 * context. So, the JSL instance can use it as discover implementation.
 * @noinspection unused
 */
public class JSLService extends JSLServiceBase {

    private static final String LOG_TAG = "JSLA.JSLService";
    private final ExecutorService executor_init = Executors.newFixedThreadPool(1);
    private JSL jslInstance = null;
    private final List<JSLInstanceListener> jslInstanceListeners = new ArrayList<>();
    private final JSLListeners jslListeners = new JSLListeners();


    // Android

    /**
     * Initialize the JSL background thread.
     * <p>
     * This method, sets also the {@link DiscoverAndroid} context.
     * <p>
     * This method is executed for both startingService() and bindService()
     * Android's methods.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "JSLService is being created");

        DiscoverAndroid.setContext(this);
        jslInitThread();
    }

    /**
     * Shutdown the JSL instance (stops his internal thread).
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        jslShutdown();
        jslInstance = null;

        Log.d(LOG_TAG, "JSLService is no longer used and is being destroyed");
    }


    // Background thread

    /**
     * Start a background thread that initialize and run the JSL instance.
     * This thread is linked to the JSL instance execution, so it can be
     * terminated calling the {@link JSL#shutdown()} method.
     */
    private void jslInitThread() {
        executor_init.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(LOG_TAG, "JSLService started JSL thread");
                Log.i(LOG_TAG, "JSLService initialing JSL instance...");
                jslInit();
                Log.i(LOG_TAG, "JSLService starting JSL instance...");
                jslStartup();
                Log.i(LOG_TAG, "JSLService started JSL instance successfully");
            }
        });
    }


    // JSL instance

    /**
     * The JSL instance is created by the background thread, so it's will
     * available some moments after initialize this class.
     *
     * @return current JSL instance.
     */
    public JSL getJSL() {
        return jslInstance;
    }

    /**
     * Prepare the JSL settings and then init the JSL instance.
     * <p>
     * NB: this code must run on a background thread.
     */
    private void jslInit() {
        if (jslInstance != null) return;

        try {
            String settingsFilePath = new File(getFilesDir(), "jsl.yml").getAbsolutePath();
            JSL.Settings jslSettings = FactoryJSL.loadSettings(settingsFilePath);
            ((JSLSettings_002) jslSettings).setSrvBaseDir(getFilesDir().getAbsolutePath());
            jslInstance = FactoryJSL.createJSL(jslSettings);
        } catch (JSL.FactoryException e) {
            Log.e(LOG_TAG, "JSLService error while initializing down JSL instance", e);
            throw new RuntimeException("JSLService error while initializing down JSL instance", e);
        }

        jslListeners.setJSL(jslInstance);

        Log.d(LOG_TAG, "JSLService initialized JSL instance");
        emitOnJSLInstanceCreated();
    }

    /**
     * Startup the JSL settings and then init the JSL instance.
     * This method keep the control until the JSL instance is shutdown.
     * <p>
     * NB: this code must run on a background thread.
     */
    private void jslStartup() {
        if (jslInstance == null || jslInstance.getState() != JSLState.STOP) return;

        try {
            jslInstance.startup();
        } catch (StateException e) {
            Log.e(LOG_TAG, "JSLService error while startup down JSL instance", e);
            throw new RuntimeException("JSLService error while startup down JSL instance", e);
        }

        Log.d(LOG_TAG, "JSLService started JSL instance");
        emitOnJSLInstanceReady();
    }

    /**
     * Shutdown the JSL instance.
     */
    private void jslShutdown() {
        if (jslInstance == null) return;

        jslListeners.setJSL(null);

        if (jslInstance.getState() != JSLState.RUN) return;

        try {
            jslInstance.shutdown();
        } catch (StateException e) {
            Log.e(LOG_TAG, "JSLService error while shutting down JSL instance", e);
            throw new RuntimeException("JSLService error while shutting down JSL instance", e);
        }

        Log.d(LOG_TAG, "JSLService stopped JSL instance");
        emitOnJSInstanceShutdown();
    }


    // Listeners

    /**
     * Adds given listener from the {@link JSLInstanceListener} list.
     */
    public void addJSLInstanceListener(JSLInstanceListener listener) {
        if (jslInstanceListeners.contains(listener)) return;

        jslInstanceListeners.add(listener);
    }

    /**
     * Removes given listener from the {@link JSLInstanceListener} list.
     */
    public void removeJSLInstanceListener(JSLInstanceListener listener) {
        if (!jslInstanceListeners.contains(listener)) return;

        jslInstanceListeners.remove(listener);
    }

    /**
     * Trigger {@link JSLInstanceListener#onJSLInstanceCreated(JSL, JSLService)} event.
     */
    private void emitOnJSLInstanceCreated() {
        for (JSLInstanceListener l : jslInstanceListeners)
            l.onJSLInstanceCreated(jslInstance, this);
    }

    /**
     * Trigger {@link JSLInstanceListener#onJSLInstanceReady(JSL, JSLService)} event.
     */
    private void emitOnJSLInstanceReady() {
        for (JSLInstanceListener l : jslInstanceListeners)
            l.onJSLInstanceReady(jslInstance, this);
    }

    /**
     * Trigger {@link JSLInstanceListener#onJSLInstanceShutdown(JSLService)} event.
     */
    private void emitOnJSInstanceShutdown() {
        for (JSLInstanceListener l : jslInstanceListeners)
            l.onJSLInstanceShutdown(this);
    }

    /**
     * Interface for JSL Service's JSL Instance events.
     */
    public interface JSLInstanceListener {

        void onJSLInstanceCreated(JSL jsl, JSLService service);

        void onJSLInstanceReady(JSL jsl, JSLService service);

        void onJSLInstanceShutdown(JSLService service);

    }


    // JSL Listeners

    /**
     * @return the {@link JSLListeners} instance linked to the JSL Instance
     * from current JSLService.
     */
    protected JSLListeners getJSLListeners() {
        return jslListeners;
    }

}
