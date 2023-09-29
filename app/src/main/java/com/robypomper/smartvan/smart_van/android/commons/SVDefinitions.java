package com.robypomper.smartvan.smart_van.android.commons;

import android.app.Activity;

import com.robypomper.smartvan.smart_van.android.activities.SVMainActivity;
import com.robypomper.smartvan.smart_van.android.activities.SVSelectObjectActivity;

public class SVDefinitions {

    /** JOD Smart Van object's model used to filter available JOD Objects. */
    public static final String MODEL_NAME = "JOD Smart Van";

    /** Define which activity must be show after the SVStartupActivity detect the JSL initialization. */
    public static final Class<? extends Activity> NEXT_ACTIVITY_STARTUP = SVSelectObjectActivity.class;
    /** Define which activity must be show after the SVSelectObjectActivity selected an object. */
    public static final Class<? extends Activity> NEXT_ACTIVITY_SELECTOBJECT = SVMainActivity.class;

}
