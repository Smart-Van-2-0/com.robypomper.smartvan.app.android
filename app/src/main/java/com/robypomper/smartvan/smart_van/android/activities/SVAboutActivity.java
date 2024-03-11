package com.robypomper.smartvan.smart_van.android.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.robypomper.smartvan.smart_van.android.R;
import com.robypomper.smartvan.smart_van.android.databinding.ActivitySvaboutBinding;


/**
 * Simple activity to show application and project's information.
 */
public class SVAboutActivity extends AppCompatActivity {

    // Constants

    private static final String LOG_TAG = "JSLA.Actvt.About";


    // Constructor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate ui
        ActivitySvaboutBinding binding = ActivitySvaboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // set up version
        String version = getString(R.string.activity_svabout_txt_version_placeholder, "N/A");
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            version = getString(R.string.activity_svabout_txt_version_placeholder, pInfo.versionName);
        } catch (PackageManager.NameNotFoundException ignore) {
        }
        binding.txtVersion.setText(version);

        // set up links
        binding.txtSVWebSite.setMovementMethod(LinkMovementMethod.getInstance());
        binding.txtAppWebSite.setMovementMethod(LinkMovementMethod.getInstance());
        binding.txtGitOrganization.setMovementMethod(LinkMovementMethod.getInstance());
        binding.txtAppLicence.setMovementMethod(LinkMovementMethod.getInstance());
        binding.txtOSDependencies.setMovementMethod(LinkMovementMethod.getInstance());
        binding.txtEmail.setMovementMethod(LinkMovementMethod.getInstance());

        // set up action bar
        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
        else if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        else
            Log.w(LOG_TAG, "No ActionBar available for this activity");
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