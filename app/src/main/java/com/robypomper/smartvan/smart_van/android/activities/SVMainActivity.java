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
    public final static SVSpec COMP_POWER_PATH = SVDefinitions.COMP_MAIN_POWER;
    public final static SVSpec COMP_PANELS_PATH = SVSpecs.SVBox.Energy.Generation.Power;
    public final static SVSpec COMP_SERVICES_PATH = SVSpecs.SVBox.Energy.Consumption.Power;

    private ActivitySvmainBinding binding;
    private JSLRangeState powerComp;
    private JSLRangeState panelsComp;
    private JSLRangeState servicesComp;


    // Android

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // inflate ui
        binding = ActivitySvmainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        super.onCreate(savedInstanceState);
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
        if (powerComp != null) deregisterPowerComp();
        if (panelsComp != null) deregisterPanelsComp();
        if (servicesComp != null) deregisterServicesComp();

        updateRemoteObject();
        updateConnectionWidgets();
    }

    private void registerPowerComp(JSLRangeState component) {
        powerComp = component;

        powerComp.addListener(listenerPowerComp);

        updatePowerComp(powerComp);
    }

    private void deregisterPowerComp() {
        powerComp.removeListener(listenerPowerComp);

        powerComp = null;

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
            updateServicesComp(component);
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
            updatePanelsComp(component);
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
                String text = "N/A";
                if (comp != null)
                    // text = String.format("%.2f", comp.getState() / 1000); // mV to V
                    text = String.format("%.2f", comp.getState()); // % to %
                binding.txtPowerValue.setText(text);

                //*
                double state = findRangeStateComponent(SVSpecs.SVBox.Energy.Storage.Voltage.getPath()).getState();
                Log.v("SVMain", "###########      Battery voltage    : " + state);
                state = findRangeStateComponent(SVSpecs.SVBox.Energy.Storage.Min_Voltage.getPath()).getState();
                Log.v("SVMain", "###########      Battery voltage min: " + state);
                state = findRangeStateComponent(SVSpecs.SVBox.Energy.Storage.Max_Voltage.getPath()).getState();
                Log.v("SVMain", "###########      Battery voltage max: " + state);
                state = findRangeStateComponent(SVSpecs.SVBox.Energy.Storage.Percentage.getPath()).getState();
                Log.v("SVMain", "###########      Battery voltage %  : " + state);
                // */
            }
        });
    }

    private void updatePanelsComp(JSLRangeState comp) {
        runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                String text = "N/A";
                if (comp != null)
                    // text = String.format("%.2f", comp.getState() / 1000); // mV to V
                    text = String.format("%.2f", comp.getState()); // % to %
                binding.txtPanelsValue.setText(text);

                //*
                double state = findRangeStateComponent(SVSpecs.SVBox.Energy.Generation.Current.getPath()).getState();
                Log.v("SVMain", "###########      Panels current mA    : " + state);
                state = findRangeStateComponent(SVSpecs.SVBox.Energy.Generation.Voltage.getPath()).getState();
                Log.v("SVMain", "###########      Panels Voltage mV    : " + state);
                state = findRangeStateComponent(SVSpecs.SVBox.Energy.Generation.Power.getPath()).getState();
                Log.v("SVMain", "###########      Panels Power mW      : " + state);
                //state = findRangeStateComponent(SVSpecs.SVBox.Energy.Generation.Percentage.getPath()).getState();
                //Log.v("SVMain", "###########      Panels Power Perc %  : " + state);
                state = findRangeStateComponent(SVSpecs.SVBox.Energy.Generation.Max_Power.getPath()).getState();
                Log.v("SVMain", "###########      Panels Power Max mW  : " + state);
                // */
            }
        });
    }

    private void updateServicesComp(JSLRangeState comp) {
        runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                String text = "N/A";
                if (comp != null)
                    // text = String.format("%.2f", comp.getState() / 1000); // mV to V
                    text = String.format("%.2f", comp.getState()); // % to %
                binding.txtServicesValue.setText(text);

                //*
                double state = findRangeStateComponent(SVSpecs.SVBox.Energy.Consumption.Current.getPath()).getState();
                Log.v("SVMain", "###########      AllSrvs current mA    : " + state);
                state = findRangeStateComponent(SVSpecs.SVBox.Energy.Consumption.Voltage.getPath()).getState();
                Log.v("SVMain", "###########      AllSrvs Voltage mV    : " + state);
                state = findRangeStateComponent(SVSpecs.SVBox.Energy.Consumption.Power.getPath()).getState();
                Log.v("SVMain", "###########      AllSrvs Power mW      : " + state);
                //state = findRangeStateComponent(SVSpecs.SVBox.Energy.Consumption.Percentage.getPath()).getState();
                //Log.v("SVMain", "###########      AllSrvs Power Perc %  : " + state);
                state = findRangeStateComponent(SVSpecs.SVBox.Energy.Consumption.Max_Power.getPath()).getState();
                Log.v("SVMain", "###########      AllSrvs Power Max mW  : " + state);
                // */
            }
        });
    }

}