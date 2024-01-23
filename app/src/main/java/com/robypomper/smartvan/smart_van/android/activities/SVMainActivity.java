package com.robypomper.smartvan.smart_van.android.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.content.res.AppCompatResources;

import com.robypomper.josp.jsl.android.activities.BaseRemoteObjectActivity;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.ObjComm;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.smartvan.smart_van.android.R;
import com.robypomper.smartvan.smart_van.android.commons.SVDefinitions;
import com.robypomper.smartvan.smart_van.android.commons.SVSpec;
import com.robypomper.smartvan.smart_van.android.commons.SVSpecs;
import com.robypomper.smartvan.smart_van.android.databinding.ActivitySvmainBinding;


/**
 * Main activity for the Smart Van application.
 * <p>
 * This activity shows the main components of the Smart Van object.
 */
public class SVMainActivity extends BaseRemoteObjectActivity {

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

    private ActivitySvmainBinding binding;
    private JSLRangeState energyComp;
    private JSLRangeState panelsComp;
    private JSLRangeState servicesComp;


    // Android

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // inflate ui
        binding = ActivitySvmainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        super.onCreate(savedInstanceState);

        // register ui listeners and callbacks
        binding.layPower.setOnClickListener((onClickMainLayoutsListener));
        binding.laySpecs.setOnClickListener((onClickMainLayoutsListener));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (energyComp == null && getRemoteObject() != null)
            registerRemoteObjectToUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        deregisterRemoteObjectToUI();
    }


    // BaseRemoteObjectActivity re-implementations

    @Override
    protected void onRemoteObjectReady() {
        registerRemoteObjectToUI();
    }

    @Override
    protected void onRemoteObjectDeregistered() {
        deregisterRemoteObjectToUI();
    }

    @Override
    protected void onRemoteObjectConnectedLocal() {
        updateConnectionWidgets();
    }

    @Override
    protected void onRemoteObjectDisconnectedLocal() {
        updateConnectionWidgets();
    }

    @Override
    protected void onRemoteObjectConnectedCloud() {
        updateConnectionWidgets();
    }

    @Override
    protected void onRemoteObjectDisconnectedCloud() {
        updateConnectionWidgets();
    }


    // Remote object management

    private void registerRemoteObjectToUI() {
        JSLRangeState component;

        component = findRangeStateComponent(COMP_POWER_PATH.getPath());
        if (component != null) registerPowerComp(component);
        component = findRangeStateComponent(COMP_PANELS_PATH.getPath());
        if (component != null) registerPanelsComp(component);
        component = findRangeStateComponent(COMP_SERVICES_PATH.getPath());
        if (component != null) registerServicesComp(component);

        updateRemoteObject();
        updateConnectionWidgets();
    }

    private void deregisterRemoteObjectToUI() {
        if (energyComp != null) deregisterPowerComp();
        if (panelsComp != null) deregisterPanelsComp();
        if (servicesComp != null) deregisterServicesComp();

        updateRemoteObject();
        updateConnectionWidgets();
    }

    private void registerPowerComp(JSLRangeState component) {
        energyComp = component;

        energyComp.addListener(listenerPowerComp);

        updatePowerComp(energyComp);
    }

    private void deregisterPowerComp() {
        energyComp.removeListener(listenerPowerComp);

        energyComp = null;

        updatePowerComp(null);
    }

    private void registerPanelsComp(JSLRangeState component) {
        panelsComp = component;

        panelsComp.addListener(listenerPanelsComp);

        updatePanelsComp(panelsComp);
    }

    private void deregisterPanelsComp() {
        panelsComp.removeListener(listenerPanelsComp);

        panelsComp = null;

        updatePanelsComp(null);
    }

    private void registerServicesComp(JSLRangeState component) {
        servicesComp = component;

        servicesComp.addListener(listenerServicesComp);

        updateServicesComp(servicesComp);
    }

    private void deregisterServicesComp() {
        servicesComp.removeListener(listenerServicesComp);

        servicesComp = null;

        updateServicesComp(null);
    }


    // Components listeners

    private final JSLRangeState.RangeStateListener listenerPowerComp = new JSLRangeState.RangeStateListener() {

        @Override
        public void onStateChanged(JSLRangeState component, double newState, double oldState) {
            updatePowerComp(component);
        }

        @Override
        public void onMinReached(JSLRangeState component, double state, double min) {
        }

        @Override
        public void onMaxReached(JSLRangeState component, double state, double max) {
        }

    };

    private final JSLRangeState.RangeStateListener listenerPanelsComp = new JSLRangeState.RangeStateListener() {

        @Override
        public void onStateChanged(JSLRangeState component, double newState, double oldState) {
            updatePanelsComp(component);
        }

        @Override
        public void onMinReached(JSLRangeState component, double state, double min) {
        }

        @Override
        public void onMaxReached(JSLRangeState component, double state, double max) {
        }

    };

    private final JSLRangeState.RangeStateListener listenerServicesComp = new JSLRangeState.RangeStateListener() {

        @Override
        public void onStateChanged(JSLRangeState component, double newState, double oldState) {
            updateServicesComp(component);
        }

        @Override
        public void onMinReached(JSLRangeState component, double state, double min) {
        }

        @Override
        public void onMaxReached(JSLRangeState component, double state, double max) {
        }

    };


    // UI widgets

    private void updateRemoteObject() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSLRemoteObject obj = getRemoteObject();
                String text = "WAITING";
                if (obj != null) text = obj.getName();

                binding.txtTitleName.setText(text);
            }
        });
    }

    private void updateConnectionWidgets() {
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

    private void updatePowerComp(JSLRangeState comp) {
        runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                if (comp == null)
                    return;
                String text = String.format("%.2f", comp.getState()); // % to %W
                binding.txtPowerValue.setText(text);
            }
        });
    }

    private void updatePanelsComp(JSLRangeState comp) {
        runOnUiThread(new Runnable() {
            // comp = SVSpecs.SVBox.Energy.Generation.Power

            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                if (comp == null)
                    return;
                String text = String.format("%.2f", comp.getState() / 1000); // mW to W
                binding.txtPanelsValue.setText(text);
            }
        });
    }

    private void updateServicesComp(JSLRangeState comp) {
        runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                if (comp == null)
                    return;
                String text = String.format("%.2f", comp.getState() / 1000); // mW to W
                binding.txtServicesValue.setText(text);
            }
        });
    }


    // UI listeners

    private final View.OnClickListener onClickMainLayoutsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JSLRemoteObject obj = getRemoteObject();
            if (obj == null) return;

            Intent intent;
            if (v == binding.layPower) {
                intent = new Intent(SVMainActivity.this, SVEnergyActivity.class);
                intent.putExtra(SVEnergyActivity.PARAM_OBJ_ID, obj.getId());
            //} else if (v == binding.layPanels) {
            //    intent = new Intent(SVMainActivity.this, SVXYActivity.class);
            //    intent.putExtra(SVXYActivity.PARAM_OBJ_ID, obj.getId());
            //} else if (v == binding.layServices) {
            //    intent = new Intent(SVMainActivity.this, SVXYActivity.class);
            //    intent.putExtra(SVXYActivity.PARAM_OBJ_ID, obj.getId());
            } else if (v == binding.laySpecs) {
                intent = new Intent(SVMainActivity.this, SVObjectSpecsActivity.class);
                intent.putExtra(SVObjectSpecsActivity.PARAM_OBJ_ID, obj.getId());
            } else
                return;

            startActivity(intent);
        }
    };

}