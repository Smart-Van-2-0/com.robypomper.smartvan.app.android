package com.robypomper.josp.jsl.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


/**
 * This class is used to start the JSLService when the Android system send the
 * {@link Intent#ACTION_BOOT_COMPLETED} broadcast.
 * <p>
 * To use this class, you must create a subclass and implements the
 * {@link #getServiceClass()} method.
 * <p>
 * Then, you must add the following lines to your AndroidManifest.xml file:
 * <pre>
 * <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
 * <application ...>
 *     ...
 *     <receiver android:name="com.robypomper.josp.jsl.android.service.JSLServiceAutoStart">
 *         <intent-filter>
 *             <action android:name="android.intent.action.BOOT_COMPLETED" />
 *         </intent-filter>
 *     </receiver>
 *     ...
 * </application>
 * </pre>
 * <p>
 * This class is used by the JSLService to start itself when the Android system
 * send the {@link Intent#ACTION_BOOT_COMPLETED} broadcast.
 */
public abstract class JSLServiceAutoStart extends BroadcastReceiver {

    private static final String LOG_TAG = "JSLA.JSLService";

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent
     * broadcast.
     * <p>
     * This method is called when the Android system send the
     * {@link Intent#ACTION_BOOT_COMPLETED} broadcast.
     * <p>
     * This method starts the JSLService.
     */
    public void onReceive(Context context, Intent arg1) {
        Log.i(LOG_TAG, "JSLService Autostart received signal");

        Intent intent_service = new Intent(context, getServiceClass());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(intent_service);
        context.startService(intent_service);

        Log.i(LOG_TAG, "JSLService Autostart completed");
    }

    /**
     * Return the JSLService class to start.
     */
    protected abstract Class<?> getServiceClass();

}

