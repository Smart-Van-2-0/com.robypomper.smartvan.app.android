package com.robypomper.smartvan.smart_van.android.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.core.app.NavUtils;

import com.robypomper.josp.jsl.android.activities.JSLObjectDetailsActivity;

public class SVObjectDetailsActivity extends JSLObjectDetailsActivity {

    // Constants

    private static final String LOG_TAG = "JSLA.Actvt.ObjDet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
        else
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getParentActivityIntent() == null) {
                Log.w(LOG_TAG, "You have forgotten to specify the parentActivityName in the AndroidManifest!");
                //onBackPressed();
                getOnBackPressedDispatcher().onBackPressed();
            } else
                NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
