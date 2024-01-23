package com.robypomper.smartvan.smart_van.android.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.core.app.NavUtils;

import com.google.android.material.chip.Chip;
import com.robypomper.josp.jsl.android.activities.BaseRemoteObjectActivity;
import com.robypomper.smartvan.smart_van.android.R;
import com.robypomper.smartvan.smart_van.android.commons.SVDefinitions;
import com.robypomper.smartvan.smart_van.android.commons.SVSpec;
import com.robypomper.smartvan.smart_van.android.commons.SVSpecGroup;
import com.robypomper.smartvan.smart_van.android.commons.SVSpecs;
import com.robypomper.smartvan.smart_van.android.components.SVSpecsListAdapter;
import com.robypomper.smartvan.smart_van.android.databinding.ActivitySvobjectSpecsBinding;

import java.util.ArrayList;
import java.util.List;


/**
 * Activity to show the SV Specs provided by a Smart Van Box.
 * <p>
 * The Smart Van Box is represented by a JOD Smart Van object, so this activity
 * must be used with a specific JOD Smart Van object.
 * <p>
 * This activity can be started with the {@link #PARAM_SPECS_GROUP_PATH} parameter to
 * show a specific specs group, otherwise the root specs group will be shown. Moreover
 * this activity must be started with the {@link #PARAM_OBJ_ID} parameter to specify
 * the JOD Smart Van object to use.
 * <p>
 * When user navigate to the Specs hierarchy, this Activity will updated his components
 * to show the new specs group. It also handle the navigation back button to allow the
 * user to navigate back to the parent specs group, or to the previous activity if the
 * root specs group is shown.
 * <p>
 * This activity is based on the {@link com.robypomper.smartvan.smart_van.android.R.layout#activity_svobject_specs}
 * layout.
 */
public class SVObjectSpecsActivity extends BaseRemoteObjectActivity {

    // Constants

    public final static String PARAM_SPECS_GROUP_PATH = SVDefinitions.PARAM_ACTIVITY_SVOBJECT_SPECS_SPECS_GROUP_PATH;


    // Attributes

    private ActivitySvobjectSpecsBinding binding;
    private boolean isRegistered = false;           // avoid duplicated registration/deregistration
    private SVSpecsListAdapter specsListAdapter;
    private SVSpecGroup specGroup;
    private final List<SVSpecGroup> specGroupsStack = new ArrayList<>();
    private String specGroupUrl;
    private final boolean useNavStack = false;      // allow to configure back button behaviour (true: follow the NavStack, false: go to parent)


    // Android

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // inflate ui
        binding = ActivitySvobjectSpecsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        super.onCreate(savedInstanceState);

        // register ui listeners and callbacks
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        binding.btnMoreDetails.setOnClickListener(onClickMoreDetailsListener);
        binding.listSpecs.setOnItemClickListener(onClickListItemListener);

        // Get spec-group from intent's extras, otherwise use the root node as default
        SVSpecGroup initSpecGroup = SVSpecs.SVBox;
        if (getIntent().getExtras() != null) {
            String specsGroupPath = getIntent().getExtras().getString(PARAM_SPECS_GROUP_PATH);
            if (specsGroupPath != null) {
                SVSpec spec = SVSpecs.fromPath(specsGroupPath);
                if (spec instanceof SVSpecGroup) initSpecGroup = (SVSpecGroup) spec;
                else
                    throw new RuntimeException(String.format("Can't show specs for '%s', it's not a SpecGroup", specsGroupPath));
            }
        }
        goToSpecGroup(initSpecGroup);

        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
        else
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getParentActivityIntent() == null) {
                    Log.w("SVPowerActivity", "You have forgotten to specify the parentActivityName in the AndroidManifest!");
                    //onBackPressed();
                    getOnBackPressedDispatcher().onBackPressed();
                } else
                    NavUtils.navigateUpFromSameTask(this);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // BaseSVObjectActivity re-implementations

    @Override
    protected void onRemoteObjectReady() {
        registerRemoteObjectToSpecs();
    }

    @Override
    protected void onRemoteObjectDeregistered() {
        deregisterRemoteObjectToSpecs();
    }

    private void registerRemoteObjectToSpecs() {
        if (specsListAdapter == null) return;
        if (isRegistered) return;
        specsListAdapter.setRemoteObj(getRemoteObject());
        isRegistered = true;
    }

    private void deregisterRemoteObjectToSpecs() {
        if (specsListAdapter == null) return;
        if (!isRegistered) return;
        specsListAdapter.setRemoteObj(null);
        isRegistered = false;
    }


    // Navigation management

    private void goToSpecGroup(SVSpecGroup specGroup) {
        this.specGroup = specGroup;
        specGroupsStack.add(specGroup);
        populateSpecsList();
        populateNavigationBreadcrumb();
        String path;
        if (specGroup == SVSpecs.SVBox) path = "specs";
        else if (specGroup.getParent() == SVSpecs.SVBox) {
            path = specGroup.getPath().toLowerCase();
            path = "category/" + path;
        } else {
            path = specGroup.getPath().replace(SVSpecs.SEPARATOR_FORMATTED, "/").toLowerCase();
            path = "specs/" + path.toLowerCase();
        }
        path = path.replace("iot box", "iot");
        specGroupUrl = "https://smartvan.johnosproject.org/docs/" + path;
    }

    private void goToBack() {
        if (useNavStack) goToBack_FollowNavStack();
        else goToBack_GoToParent();
    }

    private void goToBack_FollowNavStack() {
        if (specGroupsStack.size() == 1) {
            SVObjectSpecsActivity.this.finish();
            return;
        }
        specGroupsStack.remove(specGroupsStack.size() - 1);
        specGroup = specGroupsStack.get(specGroupsStack.size() - 1);
        populateSpecsList();
        populateNavigationBreadcrumb();
        goToBack();
    }

    private void goToBack_GoToParent() {
        // always remove last element from the NavStack
        specGroupsStack.remove(specGroupsStack.size() - 1);
        if (specGroup == SVSpecs.SVBox) {
            SVObjectSpecsActivity.this.finish();
            return;
        }
        specGroup = specGroup.getParent();
        assert specGroup != null;
        populateSpecsList();
        populateNavigationBreadcrumb();
    }


    // Specs list management

    private void populateSpecsList() {
        specsListAdapter = new SVSpecsListAdapter(this, specGroup.getSpecs(), isRemoteObjectReady() ? getRemoteObject() : null);
        binding.listSpecs.setAdapter(specsListAdapter);
    }


    // Breadcrumb management

    private void populateNavigationBreadcrumb() {
        LinearLayout layNavigation = binding.layNavigation;
        layNavigation.removeAllViews();

        // Create chip for current spec group
        addNewChip(specGroup.getName(), specGroup, layNavigation, null);

        // Create chips for parent spec groups
        List<SVSpecGroup> parents = new ArrayList<>();
        SVSpecGroup currentParent = specGroup != null ? specGroup.getParent() : null;
        while (currentParent != null) {
            addNewChip(currentParent.getName(), currentParent, layNavigation, onClickNavSpecListener);
            parents.add(currentParent);
            currentParent = currentParent.getParent();
        }
    }

    private void addNewChip(String name, SVSpecGroup specGroup, LinearLayout layNavigation, View.OnClickListener onClickListener) {
        String SEP = ">";
        LinearLayout.LayoutParams LLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LLP.setMargins(0, 0, 16, 0);

        if (onClickListener != null) {
            TextView sep = new TextView(this);
            sep.setLayoutParams(LLP);
            sep.setText(SEP);
            layNavigation.addView(sep, 0);
        }

        Chip chip = new Chip(this);
        chip.setLayoutParams(LLP);
        chip.setText(name);
        if (onClickListener != null) chip.setOnClickListener(onClickListener);
        else {
            chip.setChecked(true);
            chip.setSelected(true);
            chip.setClickable(false);
        }
        chip.setTag(specGroup);
        layNavigation.addView(chip, 0);
    }

    private final AdapterView.OnItemClickListener onClickListItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            SVSpec spec = specsListAdapter.getItem(i);
            if (spec instanceof SVSpecGroup) goToSpecGroup((SVSpecGroup) spec);
        }
    };

    private final View.OnClickListener onClickNavSpecListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!(view.getTag() instanceof SVSpecGroup)) return;
            goToSpecGroup((SVSpecGroup) view.getTag());
        }
    };

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true /* enabled by default */) {
        @Override
        public void handleOnBackPressed() {
            goToBack();
        }
    };


    // UI listeners

    private final View.OnClickListener onClickMoreDetailsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Open the specGroupUrl in a browser
            Intent intent_url = new Intent(Intent.ACTION_VIEW);
            intent_url.setData(Uri.parse(specGroupUrl));
            startActivity(intent_url);
        }
    };

}