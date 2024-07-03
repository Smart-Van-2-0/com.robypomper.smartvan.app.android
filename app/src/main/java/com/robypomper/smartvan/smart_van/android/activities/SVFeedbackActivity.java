package com.robypomper.smartvan.smart_van.android.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.robypomper.smartvan.smart_van.android.R;
import com.robypomper.smartvan.smart_van.android.databinding.ActivitySvfeedbackBinding;

public class SVFeedbackActivity extends AppCompatActivity {

    // Constants

    private static final String LOG_TAG = "JSLA.Actvt.Feedback";


    // Constructor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate ui
        ActivitySvfeedbackBinding binding = ActivitySvfeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // set up links
        binding.txtCollaborate.setMovementMethod(LinkMovementMethod.getInstance());
        binding.txtFollow.setMovementMethod(LinkMovementMethod.getInstance());
        binding.txtEmail.setMovementMethod(LinkMovementMethod.getInstance());

        // set up button
        binding.btnIssuePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getString(R.string.activity_svfeedback_link_issue_post);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));

                startActivity(intent);
            }
        });
        binding.btnIssuesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getString(R.string.activity_svfeedback_link_issues_list);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));

                startActivity(intent);
            }
        });

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