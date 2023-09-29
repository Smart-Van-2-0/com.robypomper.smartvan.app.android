package com.robypomper.smartvan.smart_van.android.activities;

import android.app.Activity;
import android.os.Bundle;

import com.robypomper.josp.jsl.android.activities.JSLSelectObjectActivity;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.smartvan.smart_van.android.commons.SVDefinitions;

import java.util.List;

public class SVSelectObjectActivity extends JSLSelectObjectActivity {

    public static final String MODEL_NAME = SVDefinitions.MODEL_NAME;
    public static final Class<? extends Activity> NEXT_ACTIVITY = SVDefinitions.NEXT_ACTIVITY_SELECTOBJECT;

    /**
     * Create an activity that wait for `modelName` objects and when the users
     * choose one, it starts the {@link #getNextActivityClass()} activity.
     */
    public SVSelectObjectActivity() {
        super(MODEL_NAME, NEXT_ACTIVITY);
    }

    @Override
    protected void proposeFoundedSmartVan(List<JSLRemoteObject> objs) {
        // ask to the user if founded device is the desired one
        // TODO update remote object's selection
        if (objs.size() == 0) return;

        Bundle b = new Bundle();
        // TODO update bundle creation for SVMainActivity
        //JSLRemoteObject remObj = objs.get(0);
        //b.putString(SVMainActivity.PARAM_OBJ_ID, remObj.getId());

        showNextActivity(b);
    }

}