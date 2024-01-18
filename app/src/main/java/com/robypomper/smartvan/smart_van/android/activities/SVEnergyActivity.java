package com.robypomper.smartvan.smart_van.android.activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.core.app.NavUtils;

import com.github.mikephil.charting.components.YAxis;
import com.robypomper.josp.jsl.android.activities.BaseRemoteObjectActivity;
import com.robypomper.josp.jsl.android.components.charts.adapters.JSLChartViewAdapter;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartDateTimeFormatter;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartUnitFormatter;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.smartvan.smart_van.android.commons.SVDefinitions;
import com.robypomper.smartvan.smart_van.android.commons.SVSpec;
import com.robypomper.smartvan.smart_van.android.commons.SVSpecs;
import com.robypomper.smartvan.smart_van.android.databinding.ActivitySvenergyBinding;


public class SVEnergyActivity extends BaseRemoteObjectActivity {


    public final static String PARAM_OBJ_ID = SVDefinitions.PARAM_ACTIVITY_SVMAIN_OBJID;
    public final static SVSpec COMP_STORAGE_PATH = SVSpecs.SVBox.Energy.Storage.Voltage;
    public final static SVSpec COMP_GENERATION_PATH = SVSpecs.SVBox.Energy.Generation.Power;
    public final static SVSpec COMP_CONSUMPTION_PATH = SVSpecs.SVBox.Energy.Consumption.Power;
    private final static String COMP_STORAGE_LABEL = "Battery";
    private final static String COMP_STORAGE_UNIT = "V";
    private final static int COMP_STORAGE_COLOR = Color.rgb(0, 200, 0);
    private final static String COMP_GENERATION_LABEL = "Generation";
    private final static String COMP_GENERATION_UNIT = "W";
    private final static int COMP_GENERATION_COLOR = Color.rgb(200, 200, 0);
    private final static String COMP_CONSUMPTION_LABEL = "Cons.";
    private final static String COMP_CONSUMPTION_UNIT = "W";
    private final static int COMP_CONSUMPTION_COLOR = Color.rgb(0, 200, 200);

    private ActivitySvenergyBinding binding;
    private JSLRangeState storageComp;
    private JSLRangeState generationComp;
    private JSLRangeState consumptionComp;
    private JSLChartViewAdapter chartAdapter;


    // Android

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate ui
        binding = ActivitySvenergyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // set value's donnuts
        binding.donutStorageValue.setLabel(COMP_STORAGE_LABEL);
        binding.donutStorageValue.setUnit(COMP_STORAGE_UNIT);
        binding.donutStorageValue.setColor(COMP_STORAGE_COLOR);
        binding.donutGenerationValue.setLabel(COMP_GENERATION_LABEL);
        binding.donutGenerationValue.setUnit(COMP_GENERATION_UNIT);
        binding.donutGenerationValue.setColor(COMP_GENERATION_COLOR);
        binding.donutConsumptionValue.setLabel(COMP_CONSUMPTION_LABEL);
        binding.donutConsumptionValue.setUnit(COMP_CONSUMPTION_UNIT);
        binding.donutConsumptionValue.setColor(COMP_CONSUMPTION_COLOR);

        // set chart view
        chartAdapter = new JSLChartViewAdapter(getJSLApplication(), binding.viewChart,
                ChartDateTimeFormatter.X_FORMATTER_MINUTES(), ChartUnitFormatter.Y_FORMATTER_UNIT_0001(), ChartUnitFormatter.Y_FORMATTER_UNIT_0001());
        binding.viewChart.setAdapter(chartAdapter);
        binding.viewChart.setActivity(this);

        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
        else
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if (storageComp == null && getRemoteObject() != null)
        registerRemoteObjectToUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        deregisterRemoteObjectToUI();
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


    // BaseRemoteObjectActivity re-implementations

    @Override
    protected void onRemoteObjectReady() {
        if (binding != null)
            registerRemoteObjectToUI();
    }

    @Override
    protected void onRemoteObjectDeregistered() {
        if (binding != null)
            deregisterRemoteObjectToUI();
    }


    // Remote object management

    private void registerRemoteObjectToUI() {
        JSLRangeState component;

        component = findRangeStateComponent(COMP_STORAGE_PATH.getPath());
        if (component != null) registerStorageComp(component);
        component = findRangeStateComponent(COMP_GENERATION_PATH.getPath());
        if (component != null) registerGenerationComp(component);
        component = findRangeStateComponent(COMP_CONSUMPTION_PATH.getPath());
        if (component != null) registerConsumptionComp(component);

        updateRemoteObject();
    }

    private void deregisterRemoteObjectToUI() {
        if (storageComp != null) deregisterStorageComp();
        if (generationComp != null) deregisterGenerationComp();
        if (consumptionComp != null) deregisterConsumptionComp();

        updateRemoteObject();
    }

    private void registerStorageComp(JSLRangeState component) {
        storageComp = component;

        storageComp.addListener(listenerComps);

        updateStorageComp(storageComp);
        try {
            chartAdapter.addComponent(storageComp, COMP_STORAGE_LABEL, COMP_STORAGE_COLOR, YAxis.AxisDependency.LEFT);
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && !e.getMessage().contains("already added"))
                throw e;
        }
    }

    private void deregisterStorageComp() {
        storageComp.removeListener(listenerComps);

        storageComp = null;

        updateStorageComp(null);
    }

    private void registerGenerationComp(JSLRangeState component) {
        generationComp = component;

        generationComp.addListener(listenerComps);

        updateGenerationComp(generationComp);
        try {
            chartAdapter.addComponent(generationComp, COMP_GENERATION_LABEL, COMP_GENERATION_COLOR, YAxis.AxisDependency.RIGHT);
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && !e.getMessage().contains("already added"))
                throw e;
        }
    }

    private void deregisterGenerationComp() {
        generationComp.removeListener(listenerComps);

        generationComp = null;

        updateGenerationComp(null);
    }

    private void registerConsumptionComp(JSLRangeState component) {
        consumptionComp = component;

        consumptionComp.addListener(listenerComps);

        updateConsumptionComp(consumptionComp);
        try {
            chartAdapter.addComponent(consumptionComp, COMP_CONSUMPTION_LABEL, COMP_CONSUMPTION_COLOR, YAxis.AxisDependency.RIGHT);
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && !e.getMessage().contains("already added"))
                throw e;
        }
    }

    private void deregisterConsumptionComp() {
        consumptionComp.removeListener(listenerComps);

        consumptionComp = null;

        updateConsumptionComp(null);
    }


    // Components listeners

    private final JSLRangeState.RangeStateListener listenerComps = new JSLRangeState.RangeStateListener() {

        @Override
        public void onStateChanged(JSLRangeState component, double newState, double oldState) {
            if (component == storageComp)
                updateStorageComp(storageComp);
            else if (component == generationComp)
                updateGenerationComp(generationComp);
            else if (component == consumptionComp)
                updateConsumptionComp(consumptionComp);
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

                //binding.txtTitleName.setText(text);
            }
        });
    }

    private void updateStorageComp(JSLRangeState comp) {
        if (comp != null)
            Log.d("SVPower", String.format("updateStorageComp(%s) => %f", comp.getState(), comp.getState() / 1000));

        runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                binding.donutStorageValue.setValue(comp == null ? 0 : comp.getState() / 1000); // mV to V
            }
        });
    }

    private void updateGenerationComp(JSLRangeState comp) {
        if (comp != null)
            Log.d("SVPower", String.format("updateGenerationComp(%s) => %f", comp.getState(), comp.getState() / 1000));

        runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                binding.donutGenerationValue.setValue(comp == null ? 0 : comp.getState() / 1000); // mV to V
            }
        });
    }

    private void updateConsumptionComp(JSLRangeState comp) {
        if (comp != null)
            Log.d("SVPower", String.format("updateConsumptionComp(%s) => %f", comp.getState(), comp.getState() / 1000));

        runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                binding.donutConsumptionValue.setValue(comp == null ? 0 : comp.getState() / 1000); // mV to V
            }
        });
    }

}
