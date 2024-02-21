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
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
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
    /** Allow to configure back button behaviour (true: follow the NavStack, false: go to parent). */
    private final static boolean USE_NAVIGATION_STACK = false;


    // Internal variables

    private JSLRemoteObject remoteObject;
    private SVSpecGroup specGroup;
    private final List<SVSpecGroup> specGroupsStack = new ArrayList<>();
    private String specGroupUrl;


    // UI widgets

    private ActivitySvobjectSpecsBinding binding;
    private SVSpecsListAdapter specsListAdapter;


    // Android

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate ui
        binding = ActivitySvobjectSpecsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        // set up action bar
        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
        else if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        else
            Log.w("SVEnergy", "No ActionBar available for this activity");
    }

    @Override
    protected void onResume() {
        // During the super.onResume() execution, it check if the remote object
        // is ready, and if it is ready, it calls the onRemoteObjectReady()
        // method. So, the registerRemoteObject() method is called by the
        // super.onResume() method, only if required.
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Deregister and update UI
        if (getRemoteObject() != null)
            deregisterRemoteObject();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getParentActivityIntent() == null) {
                Log.w("SVEnergy", "You have forgotten to specify the parentActivityName in the AndroidManifest!");
                //onBackPressed();
                getOnBackPressedDispatcher().onBackPressed();
            } else
                NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // BaseRemoteObjectActivity

    @Override
    protected void onRemoteObjectReady() {
        registerRemoteObject();
    }

    @Override
    protected void onRemoteObjectNotReady() {
        if (getRemoteObject() != null)
            deregisterRemoteObject();
    }


    // Remote object management

    private void registerRemoteObject() {
        if (specsListAdapter != null)
            specsListAdapter.setRemoteObj(getRemoteObject());

        registerRemoteObjectListeners();
        updateRemoteObjectUI();
    }

    private void deregisterRemoteObject() {
        deregisterRemoteObjectListeners();
        updateRemoteObjectUI();
    }

    private void registerRemoteObjectListeners() {
        // N/A
    }

    private void deregisterRemoteObjectListeners() {
        // N/A
    }


    // UI widgets

    private void updateRemoteObjectUI() {
        // N/A
    }


    // UI Navigation

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
        if (USE_NAVIGATION_STACK) goToBack_FollowNavStack();
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


    // UI Specs list

    private void populateSpecsList() {
        specsListAdapter = new SVSpecsListAdapter(this, specGroup.getSpecs(), isRemoteObjectReady() ? getRemoteObject() : null);
        binding.listSpecs.setAdapter(specsListAdapter);
    }


    // UI Breadcrumb

    private void populateNavigationBreadcrumb() {
        LinearLayout layNavigation = binding.layNavigation;
        layNavigation.removeAllViews();

        // Create chip for current spec group
        addNewChip(specGroup.getName(), specGroup, layNavigation, null);

        // Create chips for parent spec groups
        SVSpecGroup currentParent = specGroup != null ? specGroup.getParent() : null;
        while (currentParent != null) {
            addNewChip(currentParent.getName(), currentParent, layNavigation, onClickNavSpecListener);
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


    // UI listeners

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