package com.robypomper.josp.jsl.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.robypomper.josp.jsl.android.databinding.ActivityJslStartupBinding;
import com.robypomper.josp.jsl.android.app.JSLApplication;
import com.robypomper.josp.jsl.android.app.JSLClient;
import com.robypomper.josp.jsl.android.app.JSLClientState;
import com.robypomper.josp.jsl.android.service.JSLService;


public abstract class JSLStartupActivity<T extends JSLService> extends AppCompatActivity {

    private final JSLClient.JSLClientStateListener listener_state = new JSLClient.JSLClientStateListener(){
        @Override
        public void stateChanged(JSLClientState newState, JSLClientState oldState) {
            Log.i("J_Android", "StartupActivity Received state " + newState.name());
            if (newState == JSLClientState.RUN)
                startNextActivity();
        }
    };

    // Android

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityJslStartupBinding binding = ActivityJslStartupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //noinspection unchecked
        JSLApplication<T> app = ((JSLApplication<T>) this.getApplication());
        JSLClient<T> jslClient = app.getJSLClient();
        if (jslClient.getState() == JSLClientState.RUN)
            startNextActivity();
        jslClient.registerOnJSLStateChange(listener_state);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //noinspection unchecked
        JSLApplication<T> app = ((JSLApplication<T>) this.getApplication());
        JSLClient<T> jslClient = app.getJSLClient();
        jslClient.deregisterOnJSLStateChange(listener_state);
    }


    // Next activity to show

    protected abstract Class<? extends Activity> getNextActivityClass();

    private void startNextActivity() {
        Intent intent_activity = new Intent(JSLStartupActivity.this, getNextActivityClass());
        startActivity(intent_activity);
    }

}