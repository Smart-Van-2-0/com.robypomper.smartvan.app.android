package com.robypomper.smartvan.smart_van.android.commons;

import android.app.Activity;

import com.robypomper.smartvan.smart_van.android.activities.SVMainActivity;

public class SVDefinitions {

    /** Define which activity must be show after the SVStartupActivity detect the JSL initialization. */
    public static final Class<? extends Activity> NEXT_ACTIVITY_STARTUP = SVMainActivity.class;

}
