package com.robypomper.smartvan.smart_van.android.commons;

import android.app.Activity;

import com.robypomper.smartvan.smart_van.android.activities.SVMainActivity;
import com.robypomper.smartvan.smart_van.android.activities.SVSelectObjectActivity;

public class SVDefinitions {

    /** JOD Smart Van object's model used to filter available JOD Objects. */
    public static final String MODEL_NAME = "JOD Smart Van";


    // Shortcuts to main components

    /** Main component for the power system. */
    public static final String COMP_MAIN_POWER = Components.POWER_BATTERY_VOLTAGE;


    // Navigation references

    /** Define which activity must be show after the SVStartupActivity detect the JSL initialization. */
    public static final Class<? extends Activity> NEXT_ACTIVITY_STARTUP = SVSelectObjectActivity.class;
    /** Define which activity must be show after the SVSelectObjectActivity selected an object. */
    public static final Class<? extends Activity> NEXT_ACTIVITY_SELECTOBJECT = SVMainActivity.class;


    // Parameters' definitions

    public static final String PARAM_ACTIVITY_SVMAIN_OBJID = "obj_id";


    // Other grouped definitions

    /** Collection of all component's path from {@value MODEL_NAME} objects.  */
    public static class Components {

        // Power components

        public static final String POWER_BASE = "FWVictron";
        public static final String POWER_MPPT = POWER_BASE + " > MPPT";
        public static final String POWER_OFF_REASON = POWER_BASE + " > Off Reason";
        public static final String POWER_PANELS_VOLTAGE = POWER_BASE + " > Panels Voltage";
        public static final String POWER_PANELS_POWER = POWER_BASE + " > Panels Power";
        public static final String POWER_BATTERY_VOLTAGE = POWER_BASE + " > Battery Voltage";
        public static final String POWER_BATTERY_CURRENT = POWER_BASE + " > Battery Current";
        public static final String POWER_LOAD_CURRENT = POWER_BASE + " > Load Current";
        public static final String POWER_LOAD_OUTPUT_STATUS = POWER_BASE + " > Load Output Status";
        public static final String POWER_MAX_POWER_TODAY = POWER_BASE + " > Maximum Power Today";
        public static final String POWER_MAX_POWER_YESTERDAY = POWER_BASE + " > Maximum Power Yesterday";
        public static final String POWER_YIELD_TODAY = POWER_BASE + " > Yield Today";
        public static final String POWER_YIELD_TOTAL = POWER_BASE + " > Yield Total";
        public static final String POWER_YELD_YESTERDAY = POWER_BASE + " > Yield Yesterday";
        public static final String POWER_PROD_ID = POWER_BASE + " > Product ID";
        public static final String POWER_FW_VERSION = POWER_BASE + " > Firmware Version";
        public static final String POWER_STATE_OF_OPERATION = POWER_BASE + " > State of operation";
        public static final String POWER_ERR_CODE = POWER_BASE + " > Error Code";
        public static final String POWER_DAY_SEQ_NUMBER = POWER_BASE + " > Day Sequence Number";
    }

}
