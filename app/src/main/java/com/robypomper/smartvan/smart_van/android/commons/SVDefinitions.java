package com.robypomper.smartvan.smart_van.android.commons;

import android.app.Activity;

import com.robypomper.josp.jsl.android.JSLAndroid;
import com.robypomper.smartvan.smart_van.android.activities.SVMainActivity;
import com.robypomper.smartvan.smart_van.android.activities.SVSelectObjectActivity;

public class SVDefinitions {

    /** JOD Smart Van object's model used to filter available JOD Objects. */
    public static final String MODEL_NAME = "JOD Smart Van";


    // Shortcuts to main components

    /** Main component for the power system. */
    public static final SVSpec COMP_MAIN_POWER = SVSpecs.SVBox.Energy.Storage.Percentage;


    // Navigation references

    /** Define which activity must be show after the SVStartupActivity detect the JSL initialization. */
    public static final Class<? extends Activity> NEXT_ACTIVITY_STARTUP = SVSelectObjectActivity.class;
    /** Define which activity must be show after the SVSelectObjectActivity selected an object. */
    public static final Class<? extends Activity> NEXT_ACTIVITY_SELECTOBJECT = SVMainActivity.class;


    // Parameters' definitions

    public static final String PARAM_ACTIVITY_SVMAIN_OBJID = JSLAndroid.Params.OBJID;
    public static final String PARAM_ACTIVITY_SVSELECTOBJECT_AVOID_FAVOURITE = "avoid_favourite";
    public static final String PARAM_ACTIVITY_SVOBJECT_SPECS_SPECS_GROUP_PATH = "specs_group_path";

}
