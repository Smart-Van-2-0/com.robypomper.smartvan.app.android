package com.robypomper.smartvan.smart_van.android.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.material.navigation.NavigationView;
import com.robypomper.josp.jsl.android.activities.BaseRemoteObjectActivity;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.ObjComm;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.smartvan.smart_van.android.R;
import com.robypomper.smartvan.smart_van.android.commons.SVDefinitions;
import com.robypomper.smartvan.smart_van.android.commons.SVSpec;
import com.robypomper.smartvan.smart_van.android.commons.SVSpecs;
import com.robypomper.smartvan.smart_van.android.components.SVBoxIconView;
import com.robypomper.smartvan.smart_van.android.databinding.ActivitySvmainBinding;
import com.robypomper.smartvan.smart_van.android.storage.SVStorageSingleton;

import java.util.ArrayList;
import java.util.List;


/**
 * Main activity for the Smart Van application.
 * <p>
 * This activity shows the main components of the Smart Van object.
 */
public class SVMainActivity extends BaseRemoteObjectActivity {

    // Constants

    public final static String PARAM_OBJ_ID = SVDefinitions.PARAM_ACTIVITY_SVMAIN_OBJID;
    /**
     * COMP_POWER_PATH
     * - SVMobileApp:   SVSpecs.SVBox.Energy.Storage.Percentage
     * - SV Specs:      Energy>Storage>Percentage
     * - FW Victron:    com.victron.SmartSolarMPPT.battery_voltage_percent
     */
    public final static SVSpec COMP_POWER_PATH = SVDefinitions.COMP_MAIN_POWER;
    /**
     * COMP_GENERATION_PATH
     * - SVMobileApp:   SVSpecs.SVBox.Energy.Generation.Power
     * - SV Specs:      Energy>Generation>Power
     * - FW Victron:    com.victron.SmartSolarMPPT.panel_power
     */
    public final static SVSpec COMP_PANELS_PATH = SVSpecs.SVBox.Energy.Generation.Power;
    /**
     * COMP_CONSUMPTION_PATH
     * - SVMobileApp:   SVSpecs.SVBox.Energy.Consumption.Power
     * - SV Specs:      Energy>Consumption>Power
     * - FW Victron:    com.victron.SmartSolarMPPT.load_power
     */
    public final static SVSpec COMP_SERVICES_PATH = SVSpecs.SVBox.Energy.Consumption.Power;
    /**
     * TODO document component origins
     */
    public final static SVSpec COMPS_BINARY_CONTROLLERS = SVSpecs.SVBox.Services.Controllers.Binary;
    /**
     * TODO document component origins
     */
    public final static SVSpec COMPS_PERCENT_CONTROLLERS = SVSpecs.SVBox.Services.Controllers.Percentage;
    /**
     * TODO document component origins
     */
    public final static SVSpec COMPS_SWITCH_ACTUATORS_LOW = SVSpecs.SVBox.Services.Actuators.SwitchLow;
    /**
     * TODO document component origins
     */
    public final static SVSpec COMPS_SWITCH_ACTUATORS_HIGH = SVSpecs.SVBox.Services.Actuators.SwitchHigh;
    /**
     * TODO document component origins
     */
    public final static SVSpec COMPS_DIMMER_ACTUATORS_HIGH = SVSpecs.SVBox.Services.Actuators.DimmerHigh;
    /**
     * TODO document component origins
     */
    public final static SVSpec COMPS_DIMMER_ACTUATORS_LOW = SVSpecs.SVBox.Services.Actuators.DimmerLow;


    // Internal variables

    private JSLRangeState powerComp;
    private JSLRangeState panelsComp;
    private JSLRangeState serviceComp;
    private final List<JSLComponent> serviceComps = new ArrayList<>();


    // UI widgets

    private ActivitySvmainBinding binding;


    // Android

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate ui
        binding = ActivitySvmainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // setup navigation drawer
        String version;
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "N/A";
        }
        binding.navigationView.setNavigationItemSelectedListener(onNavDrawerClickListener);
        binding.txtSVVersion.setText(Html.fromHtml(getResources().getString(R.string.activity_svmain_navigation_drawer_version_label, version)));

        // setup views
        binding.imgSVIcon.setOnClickListener(onClickMainLayoutsListener);
        binding.txtWebsite.setText(Html.fromHtml(getResources().getString(R.string.activity_svmain_txt_website)));

        // register ui listeners and callbacks
        binding.layEnergy.setOnClickListener((onClickMainLayoutsListener));
        binding.layServices.setOnClickListener((onClickMainLayoutsListener));
        binding.laySpecs.setOnClickListener((onClickMainLayoutsListener));
        View navDrawerHeader = binding.navigationView.getHeaderView(0);
        navDrawerHeader.findViewById(R.id.imgSVIcon).setOnClickListener(onClickMainLayoutsListener);
    }

    @Override
    protected void onResume() {
        // During the super.onResume() execution, it check if the remote object
        // is ready, and if it is ready, it calls the onRemoteObjectReady()
        // method. So, the registerRemoteObject() method is called by the
        // super.onResume() method, only if required.
        super.onResume();

        // Always close the nav drawer on reopen the activity
        binding.baseLayout.close();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Deregister and update UI
        if (getRemoteObject() != null)
            deregisterRemoteObject();
    }


    // BaseRemoteObjectActivity

    @Override
    protected void onRemoteObjectReady() {
        // Register object and components listeners
        registerRemoteObject();
    }

    @Override
    protected void onRemoteObjectNotReady() {
        // Deregister object and components listeners
        if (getRemoteObject() != null)
            deregisterRemoteObject();
    }


    // Remote object management

    private void registerRemoteObject() {
        // Look for components
        powerComp = findRangeStateComponent(COMP_POWER_PATH.getPath());
        if (powerComp == null) throw new RuntimeException("Power component not found");
        panelsComp = findRangeStateComponent(COMP_PANELS_PATH.getPath());
        if (panelsComp == null) throw new RuntimeException("Panels component not found");
        serviceComp = findRangeStateComponent(COMP_SERVICES_PATH.getPath());
        if (serviceComp == null) throw new RuntimeException("Consumption component not found");
        if (findContainerComponent(COMPS_BINARY_CONTROLLERS.getPath()) != null)
            serviceComps.addAll(findContainerComponent(COMPS_BINARY_CONTROLLERS.getPath()).getComponents());
        if (findContainerComponent(COMPS_PERCENT_CONTROLLERS.getPath()) != null)
            serviceComps.addAll(findContainerComponent(COMPS_PERCENT_CONTROLLERS.getPath()).getComponents());
        if (findContainerComponent(COMPS_SWITCH_ACTUATORS_LOW.getPath()) != null)
            serviceComps.addAll(findContainerComponent(COMPS_SWITCH_ACTUATORS_LOW.getPath()).getComponents());
        if (findContainerComponent(COMPS_SWITCH_ACTUATORS_HIGH.getPath()) != null)
            serviceComps.addAll(findContainerComponent(COMPS_SWITCH_ACTUATORS_HIGH.getPath()).getComponents());
        if (findContainerComponent(COMPS_DIMMER_ACTUATORS_LOW.getPath()) != null)
            serviceComps.addAll(findContainerComponent(COMPS_DIMMER_ACTUATORS_LOW.getPath()).getComponents());
        if (findContainerComponent(COMPS_DIMMER_ACTUATORS_HIGH.getPath()) != null)
            serviceComps.addAll(findContainerComponent(COMPS_DIMMER_ACTUATORS_HIGH.getPath()).getComponents());

        registerRemoteObjectListeners();
        updateRemoteObjectUI();
    }

    private void deregisterRemoteObject() {
        deregisterRemoteObjectListeners();
        updateRemoteObjectUI();

        // Clear components
        powerComp = null;
        panelsComp = null;
        serviceComp = null;
        serviceComps.clear();
    }

    private void registerRemoteObjectListeners() {
        getRemoteObject().getComm().addListener(listenerComm);

        powerComp.addListener(listenerComps);
        panelsComp.addListener(listenerComps);
        serviceComp.addListener(listenerComps);

        for (JSLComponent c : serviceComps)
            if (c instanceof JSLBooleanState)
                ((JSLBooleanState) c).addListener(listenerServiceBooleanComps);
            else if (c instanceof JSLRangeState)
                ((JSLRangeState) c).addListener(listenerServiceRangeComps);

    }

    private void deregisterRemoteObjectListeners() {
        getRemoteObject().getComm().removeListener(listenerComm);

        powerComp.removeListener(listenerComps);
        panelsComp.removeListener(listenerComps);
        serviceComp.removeListener(listenerComps);

        for (JSLComponent c : serviceComps)
            if (c instanceof JSLBooleanState)
                ((JSLBooleanState) c).removeListener(listenerServiceBooleanComps);
            else if (c instanceof JSLRangeState)
                ((JSLRangeState) c).removeListener(listenerServiceRangeComps);
    }


    // Remote Object listeners

    private final ObjComm.RemoteObjectConnListener listenerComm = new ObjComm.RemoteObjectConnListener() {

        @Override
        public void onLocalConnected(JSLRemoteObject obj, JSLLocalClient localClient) {
            updateConnectionWidgets();
        }

        @Override
        public void onLocalDisconnected(JSLRemoteObject obj, JSLLocalClient localClient) {
            updateConnectionWidgets();
        }

        @Override
        public void onCloudConnected(JSLRemoteObject obj) {
            updateConnectionWidgets();
        }

        @Override
        public void onCloudDisconnected(JSLRemoteObject obj) {
            updateConnectionWidgets();
        }

    };

    private final JSLRangeState.RangeStateListener listenerComps = new JSLRangeState.RangeStateListener() {

        @Override
        public void onStateChanged(JSLRangeState component, double newState, double oldState) {
            if (component == powerComp) updatePowerComp();
            else if (component == panelsComp) updatePanelsComp();
            else if (component == serviceComp) updateServicesComp();
            else assert false : "Unknown component";
        }

        @Override
        public void onMinReached(JSLRangeState component, double state, double min) {
        }

        @Override
        public void onMaxReached(JSLRangeState component, double state, double max) {
        }

    };

    private final JSLBooleanState.BooleanStateListener listenerServiceBooleanComps = new JSLBooleanState.BooleanStateListener() {

        @Override
        public void onStateChanged(JSLBooleanState component, boolean newState, boolean oldState) {
            updateServicesCount();
        }

    };

    private final JSLRangeState.RangeStateListener listenerServiceRangeComps = new JSLRangeState.RangeStateListener() {

        @Override
        public void onStateChanged(JSLRangeState component, double newState, double oldState) {
            updateServicesCount();
        }

        @Override
        public void onMinReached(JSLRangeState component, double state, double min) {
        }

        @Override
        public void onMaxReached(JSLRangeState component, double state, double max) {
        }

    };


    // UI widgets

    private void updateRemoteObjectUI() {
        updateRemoteObject();
        updateConnectionWidgets();
        updatePowerComp();
        updatePanelsComp();
        updateServicesComp();
        updateServicesCount();
    }

    private void updateRemoteObject() {
        if (binding == null) return;

        if (getRemoteObject() == null)
            Log.i("SVMain", "updateRemoteObject() for unregistered remote object");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.imgSVIcon.setSVBox(getRemoteObject());

                String strObjId = getObjId();   // fallback to objId
                int objColor = SVStorageSingleton.getInstance().getCurrentPreferencesApp().getSVBoxColor();

                JSLRemoteObject obj = getRemoteObject();
                if (obj != null) strObjId = obj.getName();
                strObjId = getResources().getString(R.string.activity_svmain_txt_content, "<b>" + strObjId + "</b>");
                binding.txtContent.setText(Html.fromHtml(strObjId));
                binding.imgSVIcon.setIconBackgroundColor(objColor);

                // Update navigation drawer
                View navDrawerHeader = binding.navigationView.getHeaderView(0);
                ((SVBoxIconView) navDrawerHeader.findViewById(R.id.imgSVIcon)).setSVBox(getRemoteObject());
                ((SVBoxIconView) navDrawerHeader.findViewById(R.id.imgSVIcon)).setIconBackgroundColor(objColor);
                ((TextView) navDrawerHeader.findViewById(R.id.txtSVName)).setText(getRemoteObject() != null ? getRemoteObject().getName() : "N/A");
                ((TextView) navDrawerHeader.findViewById(R.id.txtSVId)).setText(getRemoteObject() != null ? getRemoteObject().getId() : getResources().getString(R.string.activity_svmain_navigation_drawer_id_placeholder));
                binding.txtSVBoxModel.setText(Html.fromHtml(getResources().getString(R.string.activity_svmain_navigation_drawer_model_label, getRemoteObject() != null ? getRemoteObject().getInfo().getModel() : "N/A")));
                int totalSpecs = SVSpecs.SVBox.asListOnlyGroups().size();
                int providedSpecs = SVSpecs.SVBox.asProvidedListOnlyGroups(getRemoteObject()).size();
                binding.txtSVBoxSpecs.setText(Html.fromHtml(getResources().getString(R.string.activity_svmain_navigation_drawer_svspecs_label, providedSpecs, totalSpecs)));
            }
        });
    }

    private void updateConnectionWidgets() {
        if (binding == null) return;

        // TODO check communication module availability

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSLRemoteObject obj = getRemoteObject();
                int imgState = R.drawable.ic_not_available;
                String text = "N/A";
                if (obj != null) {
                    ObjComm comm = obj.getComm();
                    if (comm.isLocalConnected() && comm.isCloudConnected()) {
                        text = "Object fully connected";
                        imgState = R.drawable.ic_online_full;
                    } else if (comm.isLocalConnected() && !comm.isCloudConnected()) {
                        text = "Object connected\nvia DIRECT communication";
                        imgState = R.drawable.ic_online_direct;
                    } else if (!comm.isLocalConnected() && comm.isCloudConnected()) {
                        text = "Object connected\nvia CLOUD communication";
                        imgState = R.drawable.ic_online_cloud;
                    } else {
                        text = "Object NOT connected";
                        imgState = R.drawable.ic_offline;
                    }
                }
                binding.txtConnectionState.setImageDrawable(AppCompatResources.getDrawable(SVMainActivity.this, imgState));
                binding.txtConnectionText.setText(text);
            }
        });
    }

    private void updatePowerComp() {
        if (binding == null) return;

        if (powerComp == null)
            Log.i("SVMain", "updateConnectionWidgets() for unregistered power component");
        else
            Log.d("SVMain", String.format("updateConnectionWidgets(%s) => %f", powerComp.getState(), powerComp.getState() / 1000));

        runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                String text = "N/A";
                if (powerComp != null)
                    text = String.format("%.2f", powerComp.getState()); // % to %
                binding.txtPowerValue.setText(text);
            }
        });
    }

    private void updatePanelsComp() {
        if (binding == null) return;

        if (panelsComp == null)
            Log.i("SVMain", "updatePanelsComp() for unregistered panels component");
        else
            Log.d("SVMain", String.format("updatePanelsComp(%s) => %f", panelsComp.getState(), panelsComp.getState() / 1000));

        runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                String text = "N/A";
                if (panelsComp != null)
                    text = String.format("%.2f", panelsComp.getState() / 1000); // mW to W
                binding.txtPanelsValue.setText(text);
            }
        });
    }

    private void updateServicesComp() {
        if (binding == null) return;

        if (serviceComp == null)
            Log.i("SVMain", "updateServicesComp() for unregistered services component");
        else
            Log.d("SVMain", String.format("updateServicesComp(%s) => %f", serviceComp.getState(), serviceComp.getState() / 1000));

        runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                String text = "N/A";
                if (serviceComp != null)
                    text = String.format("%.2f", serviceComp.getState() / 1000); // mW to W
                binding.txtConsumptionValue.setText(text);
            }
        });
    }

    private void updateServicesCount() {
        if (binding == null) return;

        int countComponents = serviceComps.size();
        int isUseComponents = 0;
        for (JSLComponent c : serviceComps) {
            if (c instanceof JSLBooleanState) {
                if (((JSLBooleanState) c).getState()) isUseComponents++;
            } else if (c instanceof JSLRangeState) {
                if (((JSLRangeState) c).getState() > 0) isUseComponents++;
            }
        }
        final int finalInUseComponents = isUseComponents;

        runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                binding.txtServicesInUse.setText(String.valueOf(finalInUseComponents));
                binding.txtServicesCount.setText(Html.fromHtml(getResources().getString(R.string.activity_svmain_txt_services_footer, countComponents)));
            }
        });
    }


    // UI listeners

    private final View.OnClickListener onClickMainLayoutsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JSLRemoteObject obj = getRemoteObject();
            if (obj == null) return;

            View navDrawerHeader = binding.navigationView.getHeaderView(0);

            Intent intent;
            if (v == binding.layEnergy) {
                intent = new Intent(SVMainActivity.this, SVEnergyActivity.class);
                intent.putExtra(SVEnergyActivity.PARAM_OBJ_ID, obj.getId());
                //} else if (v == binding.layPanels) {
                //    intent = new Intent(SVMainActivity.this, SVXYActivity.class);
                //    intent.putExtra(SVXYActivity.PARAM_OBJ_ID, obj.getId());
            } else if (v == binding.layServices) {
                intent = new Intent(SVMainActivity.this, SVServicesActivity.class);
                intent.putExtra(SVServicesActivity.PARAM_OBJ_ID, obj.getId());
            } else if (v == binding.laySpecs) {
                intent = new Intent(SVMainActivity.this, SVObjectSpecsActivity.class);
                intent.putExtra(SVObjectSpecsActivity.PARAM_OBJ_ID, obj.getId());
            } else if (v == binding.imgSVIcon) {
                binding.baseLayout.open();
                return;
            } else if (v == navDrawerHeader.findViewById(R.id.imgSVIcon)) {
                intent = new Intent(SVMainActivity.this, SVSelectObjectActivity.class);
                intent.putExtra(SVSelectObjectActivity.PARAM_AVOID_FAVOURITE, true);
            } else
                return;

            startActivity(intent);
        }

    };

    private final NavigationView.OnNavigationItemSelectedListener onNavDrawerClickListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            JSLRemoteObject obj = getRemoteObject();
            if (obj == null) return false;

            Intent intent;
            if (item.getItemId() == R.id.itemEnergyMonitor) {
                intent = new Intent(SVMainActivity.this, SVEnergyActivity.class);
                intent.putExtra(SVEnergyActivity.PARAM_OBJ_ID, obj.getId());
            } else if (item.getItemId() == R.id.itemServicesControl) {
                intent = new Intent(SVMainActivity.this, SVServicesActivity.class);
                intent.putExtra(SVEnergyActivity.PARAM_OBJ_ID, obj.getId());
            } else if (item.getItemId() == R.id.itemSVBox) {
                intent = new Intent(SVMainActivity.this, SVObjectDetailsActivity.class);
                intent.putExtra(SVEnergyActivity.PARAM_OBJ_ID, obj.getId());
            } else if (item.getItemId() == R.id.itemSVSpecs) {
                intent = new Intent(SVMainActivity.this, SVObjectSpecsActivity.class);
                intent.putExtra(SVEnergyActivity.PARAM_OBJ_ID, obj.getId());
            } else if (item.getItemId() == R.id.itemConfigs) {
                intent = new Intent(SVMainActivity.this, SVSettingsActivity.class);
            //} else if (item.getItemId() == R.id.itemFeedback) {
            //    intent = new Intent(SVMainActivity.this, SVFeedbackActivity.class);
            //    intent.putExtra(SVEnergyActivity.PARAM_OBJ_ID, obj.getId());
            //} else if (item.getItemId() == R.id.itemAbout) {
            //    intent = new Intent(SVMainActivity.this, SVAboutActivity.class);
            //    intent.putExtra(SVEnergyActivity.PARAM_OBJ_ID, obj.getId());
            } else
                return false;

            startActivity(intent);
            return true;
        }
    };

}