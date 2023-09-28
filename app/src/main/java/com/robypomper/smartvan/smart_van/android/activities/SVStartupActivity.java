package com.robypomper.smartvan.smart_van.android.activities;

import android.app.Activity;

import com.robypomper.josp.jsl.android.activities.JSLStartupActivity;
import com.robypomper.smartvan.smart_van.android.commons.SVDefinitions;
import com.robypomper.smartvan.smart_van.android.service.SVService;

public class SVStartupActivity extends JSLStartupActivity<SVService> {

    private static final Class<? extends Activity> NEXT_ACTIVITY = SVDefinitions.NEXT_ACTIVITY_STARTUP;

    @Override
    protected Class<? extends Activity> getNextActivityClass() {
        return NEXT_ACTIVITY;
    }

}
