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
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.ObjComm;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.smartvan.smart_van.android.commons.SVDefinitions;
import com.robypomper.smartvan.smart_van.android.commons.SVSpec;
import com.robypomper.smartvan.smart_van.android.commons.SVSpecs;
import com.robypomper.smartvan.smart_van.android.databinding.ActivitySvenergyBinding;


public class SVEnergyActivity extends BaseRemoteObjectActivity {

    // Constants

    public final static String PARAM_OBJ_ID = SVDefinitions.PARAM_ACTIVITY_SVMAIN_OBJID;
    /**
     * COMP_STORAGE_PATH
     * - SVMobileApp:   SVSpecs.SVBox.Energy.Storage.Voltage
     * - SV Specs:      Energy>Storage>Voltage
     * - FW Victron:    com.victron.SmartSolarMPPT.battery_voltage
     */
    public final static SVSpec COMP_STORAGE_PATH = SVSpecs.SVBox.Energy.Storage.Voltage;
    /**
     * COMP_GENERATION_PATH
     * - SVMobileApp:   SVSpecs.SVBox.Energy.Generation.Power
     * - SV Specs:      Energy>Generation>Power
     * - FW Victron:    com.victron.SmartSolarMPPT.panel_power
     */
    public final static SVSpec COMP_GENERATION_PATH = SVSpecs.SVBox.Energy.Generation.Power;
    /**
     * COMP_CONSUMPTION_PATH
     * - SVMobileApp:   SVSpecs.SVBox.Energy.Consumption.Power
     * - SV Specs:      Energy>Consumption>Power
     * - FW Victron:    com.victron.SmartSolarMPPT.load_power
     */
    public final static SVSpec COMP_CONSUMPTION_PATH = SVSpecs.SVBox.Energy.Consumption.Power;
    public final static String COMP_STORAGE_LABEL = "Battery";
    public final static String COMP_STORAGE_UNIT = "V";
    public final static int COMP_STORAGE_COLOR = Color.rgb(0, 200, 0);
    public final static String COMP_GENERATION_LABEL = "Generation";
    public final static String COMP_GENERATION_UNIT = "W";
    public final static int COMP_GENERATION_COLOR = Color.rgb(200, 200, 0);
    public final static String COMP_CONSUMPTION_LABEL = "Cons.";
    public final static String COMP_CONSUMPTION_UNIT = "W";
    public final static int COMP_CONSUMPTION_COLOR = Color.rgb(0, 200, 200);


    // Internal variables

    private JSLRangeState storageComp;
    private JSLRangeState generationComp;
    private JSLRangeState consumptionComp;
    private JSLChartViewAdapter chartAdapter;


    // UI widgets

    private ActivitySvenergyBinding binding;


    // Android

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate ui
        binding = ActivitySvenergyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // set up donuts views
        binding.donutStorageValue.setLabel(COMP_STORAGE_LABEL);
        binding.donutStorageValue.setUnit(COMP_STORAGE_UNIT);
        binding.donutStorageValue.setColor(COMP_STORAGE_COLOR);
        binding.donutGenerationValue.setLabel(COMP_GENERATION_LABEL);
        binding.donutGenerationValue.setUnit(COMP_GENERATION_UNIT);
        binding.donutGenerationValue.setColor(COMP_GENERATION_COLOR);
        binding.donutConsumptionValue.setLabel(COMP_CONSUMPTION_LABEL);
        binding.donutConsumptionValue.setUnit(COMP_CONSUMPTION_UNIT);
        binding.donutConsumptionValue.setColor(COMP_CONSUMPTION_COLOR);

        // set up chart view
        chartAdapter = new JSLChartViewAdapter(getJSLApplication(), binding.viewChart,
                ChartDateTimeFormatter.X_FORMATTER_MINUTES(), ChartUnitFormatter.Y_FORMATTER_UNIT_0001(), ChartUnitFormatter.Y_FORMATTER_UNIT_0001());
        binding.viewChart.setAdapter(chartAdapter);
        binding.viewChart.setActivity(this);

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

        // Deregister, update UI and remove component from chart
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
        storageComp = findRangeStateComponent(COMP_STORAGE_PATH.getPath());
        if (storageComp == null) throw new RuntimeException("Storage component not found");
        generationComp = findRangeStateComponent(COMP_GENERATION_PATH.getPath());
        if (generationComp == null) throw new RuntimeException("Generation component not found");
        consumptionComp = findRangeStateComponent(COMP_CONSUMPTION_PATH.getPath());
        if (consumptionComp == null) throw new RuntimeException("Consumption component not found");

        registerRemoteObjectListeners();
        updateRemoteObjectUI();
        addComponentsToChart();
    }

    private void deregisterRemoteObject() {
        deregisterRemoteObjectListeners();
        updateRemoteObjectUI();
        removeComponentsFromChart();

        storageComp = null;
        generationComp = null;
        consumptionComp = null;
    }

    private void registerRemoteObjectListeners() {
        getRemoteObject().getComm().addListener(listenerComm);
        storageComp.addListener(listenerComps);
        generationComp.addListener(listenerComps);
        consumptionComp.addListener(listenerComps);
    }

    private void deregisterRemoteObjectListeners() {
        getRemoteObject().getComm().removeListener(listenerComm);
        storageComp.removeListener(listenerComps);
        generationComp.removeListener(listenerComps);
        consumptionComp.removeListener(listenerComps);
    }


    // Remote Object listeners

    private final ObjComm.RemoteObjectConnListener listenerComm = new ObjComm.RemoteObjectConnListener() {
        @Override
        public void onLocalConnected(JSLRemoteObject obj, JSLLocalClient localClient) {
            setRemoteObjectUIEnable(getRemoteObject().getComm().isConnected());
        }

        @Override
        public void onLocalDisconnected(JSLRemoteObject obj, JSLLocalClient localClient) {
            setRemoteObjectUIEnable(getRemoteObject().getComm().isConnected());
        }

        @Override
        public void onCloudConnected(JSLRemoteObject obj) {
            setRemoteObjectUIEnable(getRemoteObject().getComm().isConnected());
        }

        @Override
        public void onCloudDisconnected(JSLRemoteObject obj) {
            setRemoteObjectUIEnable(getRemoteObject().getComm().isConnected());
        }
    };

    private final JSLRangeState.RangeStateListener listenerComps = new JSLRangeState.RangeStateListener() {

        @Override
        public void onStateChanged(JSLRangeState component, double newState, double oldState) {
            if (component == storageComp) updateStorageComp();
            else if (component == generationComp) updateGenerationComp();
            else if (component == consumptionComp) updateConsumptionComp();
            else assert false : "Unknown component";
        }

        @Override
        public void onMinReached(JSLRangeState component, double state, double min) {
        }

        @Override
        public void onMaxReached(JSLRangeState component, double state, double max) {
        }

    };


    // UI widgets

    private void setRemoteObjectUIEnable(boolean enable) {
        boolean connected = getRemoteObject().getComm().isConnected();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.viewChart.setEnabled(enable);
            }
        });
    }

    private void updateRemoteObjectUI() {
        updateRemoteObject();
        updateStorageComp();
        updateGenerationComp();
        updateConsumptionComp();
    }

    private void updateRemoteObject() {
        if (binding == null) return;

        if (getRemoteObject() == null)
            Log.i("SVEnergy", "updateRemoteObject() for unregistered remote object");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String text = getObjId();   // fallback to objId
                JSLRemoteObject obj = getRemoteObject();
                if (obj != null) text = obj.getName();

                //binding.txtTitleName.setText(text);
            }
        });
    }

    private void updateStorageComp() {
        if (binding == null) return;

        if (storageComp == null) Log.i("SVEnergy", "updateStorageComp() for unregistered storage component");
        else Log.d("SVEnergy", String.format("updateStorageComp(%s) => %f", storageComp.getState(), storageComp.getState() / 1000));

        runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                binding.donutStorageValue.setValue(storageComp == null ? 0 : storageComp.getState() / 1000); // mV to V
            }
        });
    }

    private void updateGenerationComp() {
        if (binding == null) return;

        if (generationComp == null) Log.i("SVEnergy", "updateGenerationComp() for unregistered storage component");
        else Log.d("SVEnergy", String.format("updateGenerationComp(%s) => %f", generationComp.getState(), generationComp.getState() / 1000));

        runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                binding.donutGenerationValue.setValue(generationComp == null ? 0 : generationComp.getState() / 1000); // mW to W
            }
        });
    }

    private void updateConsumptionComp() {
        if (binding == null) return;

        if (consumptionComp == null) Log.i("SVEnergy", "updateGenerationComp() for unregistered storage component");
        else Log.d("SVEnergy", String.format("updateConsumptionComp(%s) => %f", consumptionComp.getState(), consumptionComp.getState() / 1000));

        runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                binding.donutConsumptionValue.setValue(consumptionComp == null ? 0 : consumptionComp.getState() / 1000); // mW to W
            }
        });
    }


    // UI Chart

    private void addComponentsToChart() {
        addComponentToChart(storageComp, COMP_STORAGE_LABEL, COMP_STORAGE_COLOR, YAxis.AxisDependency.LEFT);
        addComponentToChart(generationComp, COMP_GENERATION_LABEL, COMP_GENERATION_COLOR, YAxis.AxisDependency.RIGHT);
        addComponentToChart(consumptionComp, COMP_CONSUMPTION_LABEL, COMP_CONSUMPTION_COLOR, YAxis.AxisDependency.RIGHT);
    }

    private void removeComponentsFromChart() {
        removeComponentFromChart(storageComp);
        removeComponentFromChart(generationComp);
        removeComponentFromChart(consumptionComp);
    }

    private void addComponentToChart(JSLRangeState comp, String compLabel, int compColor, YAxis.AxisDependency axisDependency) {
        if (chartAdapter == null) return;
        try {
            chartAdapter.addComponent(comp, compLabel, compColor, axisDependency);
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && !e.getMessage().contains("already added"))
                throw e;
        }
    }

    private void removeComponentFromChart(JSLRangeState comp) {
        if (chartAdapter == null) return;
        chartAdapter.removeComponent(comp);
    }

}
