package com.robypomper.smartvan.smart_van.android.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.github.mikephil.charting.components.YAxis;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;

import java.util.ArrayList;
import java.util.List;

public abstract class SVChartViewAbs
        extends LinearLayout
        implements SVChartView, SVChartViewExportable {

    // Internal vars

    private boolean isFetching = false;


    // Constructors

    public SVChartViewAbs(@LayoutRes int layout, Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Inflate subclass layout
        //inflate(context, layout, this);
        LayoutInflater.from(context).inflate(layout, this, true);
    }


    // JSL Components management

    private final List<ChartComponentInfo> chartComponents = new ArrayList<>();

    protected List<ChartComponentInfo> getComponents() {
        return chartComponents;
    }

    @Override
    public void addComponent(JSLRangeState comp, String compLabel, String compUnit, int compColor, YAxis.AxisDependency axisDependency) {
        if (findComponentInfo(comp) != null)
            removeComponent(comp);

        chartComponents.add(new ChartComponentInfo(comp, compLabel, compUnit, compColor, axisDependency));
    }

    @Override
    public void removeComponent(JSLRangeState comp) {
        ChartComponentInfo componentInfo = findComponentInfo(comp);
        if (componentInfo != null)
            chartComponents.remove(componentInfo);
    }

    protected ChartComponentInfo findComponentInfo(JSLRangeState comp) {
        for (ChartComponentInfo chartComponentInfo : chartComponents) {
            if (chartComponentInfo.jslRangeComponent == comp) {
                return chartComponentInfo;
            }
        }
        return null;
    }

    protected ChartComponentInfo findComponentInfo(String label) {
        for (ChartComponentInfo chartComponentInfo : chartComponents) {
            if (chartComponentInfo.label == label) {
                return chartComponentInfo;
            }
        }
        return null;
    }


    // Data management

    List<ChartDataSet> dataSetsRaw = new ArrayList<>();
    List<ChartDataSet> dataSetsProcessed = new ArrayList<>();
    List<ChartDataSet> dataSetsDisplayed = new ArrayList<>();

    @Override
    public boolean isFetching() {
        return isFetching;
    }

    @Override
    public void fetchData() {
        if (isFetching)
            return;
        isFetching = true;

        clearData(false);
        notifyFetchStartedListeners();
        doFetching();
    }

    @Override
    public void clearData() {
        clearData(true);
    }

    private void clearData(boolean clearUI) {
        dataSetsRaw.clear();
        dataSetsProcessed.clear();
        dataSetsDisplayed.clear();
        if (clearUI)
            doCleanChart();
    }

    protected abstract void doCleanChart();


    // Data fetching and processing

    protected abstract void doFetching();  // -> must call the onDataFetched method

    protected void onDataFetched(List<ChartDataSet> dataSetsRaw) {
        this.dataSetsRaw = dataSetsRaw;
        List<String> dataSetsList = new ArrayList<>();
        for (ChartDataSet dataSet : dataSetsRaw)
            dataSetsList.add(String.format("%s [%d]", dataSet.componentInfo.label, dataSet.data.size()));
        Log.d("SVChartViewAbs", String.format("onDataFetched (#%d): %s", dataSetsRaw.size(), String.join(", ", dataSetsList)));

        notifyProcessingStartedListeners();
        doProcessDataSet(dataSetsRaw);
    }

    protected void onDataFetchedError(ChartComponentInfo componentInfo, Throwable error) {
        // TODO: handle error
        Log.w("SVChartViewAbs", "onDataFetchedError: " + error.getMessage() + " for component " + componentInfo.label);
    }

    protected abstract void doProcessDataSet(List<ChartDataSet> dataSetsRaw); // -> must call the onDataProcessed method

    protected void onDataProcessed(List<ChartDataSet> dataSetsProcessed) {
        this.dataSetsProcessed = dataSetsProcessed;
        List<String> dataSetsList = new ArrayList<>();
        for (ChartDataSet dataSet : dataSetsProcessed)
            dataSetsList.add(String.format("%s [%d]", dataSet.componentInfo.label, dataSet.data.size()));
        Log.d("SVChartViewAbs", String.format("onDataProcessed (#%d): %s", dataSetsProcessed.size(), String.join(", ", dataSetsList)));

        doCleanChart();
        notifyDisplayingStartedListeners();
        doAddDataSetsToChart(dataSetsProcessed);
    }

    protected abstract void doAddDataSetsToChart(List<ChartDataSet> dataSetsProcessed); // -> must call the onDataAddedToChart method

    protected void onDataAddedToChart(List<ChartDataSet> dataSetsDisplayed) {
        this.dataSetsDisplayed = dataSetsDisplayed;
        List<String> dataSetsList = new ArrayList<>();
        for (ChartDataSet dataSet : dataSetsDisplayed)
            dataSetsList.add(String.format("%s [%d]", dataSet.componentInfo.label, dataSet.data.size()));
        Log.d("SVChartViewAbs", String.format("onDataAddedToChart (#%d): %s", dataSetsDisplayed.size(), String.join(", ", dataSetsList)));
    }

    @SuppressLint("DefaultLocale")
    protected void onDoFetchAndProcessingCompleted() {
        List<String> dataSetsList = new ArrayList<>();
        for (ChartDataSet dataSet : dataSetsDisplayed)
            dataSetsList.add(String.format("%s [%d]", dataSet.componentInfo.label, dataSet.data.size()));
        Log.i("SVChartViewAbs", String.format("onDoFetchAndProcessingCompleted (#%d): %s", dataSetsDisplayed.size(), String.join(", ", dataSetsList)));
        isFetching = false;

        notifyFetchedTerminatedListeners();
    }


    // Data Listeners

    private final List<DataListener> dataListeners = new ArrayList<>();

    @Override
    public void addDataListener(DataListener listener) {
        dataListeners.add(listener);
    }

    @Override
    public void removeDataListener(DataListener listener) {
        dataListeners.remove(listener);
    }

    private void notifyFetchStartedListeners() {
        for (DataListener listener : dataListeners) {
            listener.onFetchStarted();
        }
    }

    private void notifyProcessingStartedListeners() {
        for (DataListener listener : dataListeners) {
            listener.onProcessingStarted();
        }
    }

    private void notifyDisplayingStartedListeners() {
        for (DataListener listener : dataListeners) {
            listener.onDisplayingStarted();
        }
    }

    private void notifyFetchedTerminatedListeners() {
        for (DataListener listener : dataListeners) {
            listener.onFetchedTerminated();
        }
    }


    // Exportable methods

    @Override
    public List<ChartDataSet> getDataSetsRaw() {
        return dataSetsRaw;
    }

    @Override
    public List<ChartDataSet> getDataSetsProcessed() {
        return dataSetsProcessed;
    }

    @Override
    public List<ChartDataSet> getDataSetsDisplayed() {
        return dataSetsDisplayed;
    }

}
