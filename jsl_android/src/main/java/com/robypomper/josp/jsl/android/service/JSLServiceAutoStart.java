package com.robypomper.josp.jsl.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public abstract class JSLServiceAutoStart extends BroadcastReceiver {

    public void onReceive(Context context, Intent arg1) {
        Log.i("J_Android", "Autostart received signal");

        Intent intent = new Intent(context, getServiceClass());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(intent);
        else
            context.startService(intent);

        Log.i("J_Android", "Autostart started");
    }

    protected abstract Class<?> getServiceClass();

}

