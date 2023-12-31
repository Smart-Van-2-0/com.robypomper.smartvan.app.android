package com.robypomper.josp.jsl.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


/**
 * Base class for JSLService, it implements the Android {@link Service} interface.
 * <p>
 * This class implements all methods required by the {@link Service} interface,
 * but it just print logging messages. All works to initialize and manage a
 * JSLService is triggered using the {@link JSLService#onCreate()} and
 * {@link JSLService#onDestroy()} methods.
 * <p>
 * This class provides also the {@link LocalBinder} to bind the JSLService
 * instance from the main application.
 */
public class JSLServiceBase extends Service {

    private final boolean DISABLE_LOGGING = true;


    int startMode;       // indicates how to behave if the service is killed
    IBinder binder = new LocalBinder();
    boolean allowRebind = true;


    // Android

    @Override
    public void onCreate() {
        if (!DISABLE_LOGGING) Log.d("J_Android", "The service is being created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!DISABLE_LOGGING) Log.d("J_Android", "The service is starting, due to a call to startService()");
        return startMode;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (!DISABLE_LOGGING) Log.d("J_Android", "A client is binding to the service with bindService()");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (!DISABLE_LOGGING) Log.d("J_Android", "All clients have unbound with unbindService()");
        return allowRebind;
    }

    @Override
    public void onRebind(Intent intent) {
        if (!DISABLE_LOGGING) Log.d("J_Android", "A client is binding to the service with bindService(), after onUnbind() has already been called");
    }

    @Override
    public void onDestroy() {
        if (!DISABLE_LOGGING) Log.d("J_Android", "The service is no longer used and is being destroyed");
    }


    // Local binder

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public JSLServiceBase getService() {
            return JSLServiceBase.this;
        }
    }

}
