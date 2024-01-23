package com.robypomper.josp.jsl.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.robypomper.josp.jsl.android.app.JSLApplication;
import com.robypomper.josp.jsl.android.app.JSLClient;
import com.robypomper.josp.jsl.android.app.JSLClientState;
import com.robypomper.josp.jsl.android.databinding.ActivityJslStartupBinding;
import com.robypomper.josp.jsl.android.service.JSLService;


/**
 * Startup activity for JSL Android applications.
 * <p>
 * This activity is used to start the JSLService and wait for it to be ready.
 * When the JSLService is ready, this activity starts the next activity.
 * <p>
 * This class is an abstract class, so you must implement a subclass and
 * implements the {@link #getNextActivityClass()} method.
 *
 * @param <T> specify the {@link JSLService} implementation to use.
 */
public abstract class JSLStartupActivity<T extends JSLService> extends BaseJSLActivity {

    private static final String LOG_TAG = "JSLA.Actvt.JSLStartup";

    // Android

    /**
     * Create the activity and start the JSLService.
     * <p>
     * This method starts the JSLService and register a listener to the JSLClient
     * to receive the JSLClientState changes. When the JSLClientState will be
     * {@link JSLClientState#RUN}, this method starts the next activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "JSLStartupActivity is being created");

        ActivityJslStartupBinding binding = ActivityJslStartupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    /**
     * Destroy the activity and unregister the JSLClient listener.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "JSLStartupActivity is being destroyed");
    }

    @Override
    protected void onJSLReady() {
        Log.d(LOG_TAG, "JSLStartupActivity received READY state");
        goToNextActivity();
    }


    // Next activity to show

    /**
     * Return the next activity class to start when the JSLService will be ready.
     * <p>
     * This method must be implemented by the subclass and must return the class
     * of the next activity to start when the JSLService will be ready.
     */
    protected abstract Class<? extends Activity> getNextActivityClass();

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
        Log.i(LOG_TAG, String.format("JSLStartupActivity start next activity '%s'", getNextActivityClass().getName()));
        Intent intent_activity = new Intent(this, getNextActivityClass());
        if (bundle != null) intent_activity.putExtras(bundle);
        startActivity(intent_activity);
    }

}