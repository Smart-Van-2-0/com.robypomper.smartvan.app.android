package com.robypomper.smartvan.smart_van.android.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;

import com.robypomper.java.JavaThreads;
import com.robypomper.josp.jcp.defs.base.internal.status.executable.Params20;
import com.robypomper.josp.jsl.android.app.JSLApplication;
import com.robypomper.josp.jsl.android.service.JSLService;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.history.HistoryCompStatus;
import com.robypomper.josp.jsl.objs.remote.ObjStruct;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPHistory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

public abstract class SVChartViewJSLAbs
        extends SVChartViewAbs
        implements SVChartView, SVChartViewTSFiltered {


    // Internal vars

    private JSLApplication<? extends JSLService> jslApplication;


    public SVChartViewJSLAbs(int layout, Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(layout, context, attrs, defStyleAttr);
    }


    // JSL Application setter

    public void setJSLApplication(JSLApplication<? extends JSLService> jslApplication) {
        this.jslApplication = jslApplication;
    }


    // Data fetching and processing

    @Override
    protected void doFetching() {
        doFetching_tmpData.clear();
        doFetching_TimerTasks.clear();

        List<ChartComponentInfo> components = getComponents();
        if (components.isEmpty())
            return;

        // Set history limits
        HistoryLimits historyLimits = doFetching_getHistoryLimits();

        /*// Fetch data (SEQUENTIAL)
        for (ChartComponentInfo chartComponentInfo : components) {
            JSLComponent jslComp = chartComponentInfo.jslRangeComponent;
            jslApplication.runOnNetworkThread(new Runnable() {
                @Override
                public void run() {
                    doFetching_startFetch(chartComponentInfo, jslComp, jslComp.getRemoteObject().getStruct(), historyLimits);
                }
            });
        }//*/

        // Fetch data (PARALLEL)
        jslApplication.runOnNetworkThread(new Runnable() {
            @Override
            public void run() {
                // Fetch data
                for (ChartComponentInfo chartComponentInfo : components) {
                    JSLComponent jslComp = chartComponentInfo.jslRangeComponent;
                    doFetching_startFetch(chartComponentInfo, jslComp, jslComp.getRemoteObject().getStruct(), historyLimits);
                    JavaThreads.softSleep(100);
                }
            }
        });//*/
    }

    private HistoryLimits doFetching_getHistoryLimits() {
        Date fromDate = SVChartViewTSFiltered.calculateFromDate(filterTSPeriod, filterTSQty, filterTSOffset);
        Date toDate = SVChartViewTSFiltered.calculateToDate(filterTSPeriod, filterTSQty, filterTSOffset);

        setFilterTSFromDate(fromDate);
        Log.d("SVChartViewJSLAbs", "Filtering from " + fromDate + " to " + toDate + " (offset " + filterTSOffset + " of " + filterTSQty + " partitions)");

        return new HistoryLimits(null, null, null, null,
                fromDate, toDate,
                null, null);
    }

    private void doFetching_startFetch(ChartComponentInfo chartComponentInfo, JSLComponent jslComp, ObjStruct struct, HistoryLimits historyLimits) {
        try {
            struct.getComponentHistory(jslComp, historyLimits, new ChartHistoryListener(chartComponentInfo));
        } catch (JSLRemoteObject.ObjectNotConnected | JSLRemoteObject.MissingPermission e) {
            onDataFetchedError(chartComponentInfo, e);
        }
        doFetching_startTimer(chartComponentInfo);
    }

    private final static long FETCHING_TIMEOUT_MS = 15 * 1000;
    private final Timer timer = new Timer("TIMER_FOR_CHART_FETCHING");
    private final Map<ChartComponentInfo, TimerTask> doFetching_TimerTasks = new HashMap<>();

    private void doFetching_startTimer(ChartComponentInfo chartComponentInfo) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                onDataFetchedError(chartComponentInfo, new TimeoutException("Timeout fetching dataset " + chartComponentInfo.label));
            }
        };

        timer.schedule(task, FETCHING_TIMEOUT_MS);
        doFetching_TimerTasks.put(chartComponentInfo, task);
    }

    private void doFetching_cancelTimer(ChartComponentInfo chartComponentInfo) {
        for (Map.Entry<ChartComponentInfo, TimerTask> entry : doFetching_TimerTasks.entrySet()) {
            if (entry.getKey() == chartComponentInfo) {
                entry.getValue().cancel();
                doFetching_TimerTasks.remove(entry.getKey());
                return;
            }
        }
    }

    private final class ChartHistoryListener implements HistoryCompStatus.StatusHistoryListener {
        private final ChartComponentInfo chartComponentInfo;

        private ChartHistoryListener(ChartComponentInfo chartComponentInfo) {
            this.chartComponentInfo = chartComponentInfo;
        }

        @Override
        public void receivedStatusHistory(List<JOSPHistory> history) {
            doFetching_cancelTimer(chartComponentInfo);

            if (!getComponents().contains(chartComponentInfo))   // Data discharged because the component was removed from the chart
                return;

            // TODO convert history to dataSet
            ChartDataSet dataSet = doFetching_JOSPHistory2ChartDataSet(chartComponentInfo, history);

            doFetching_storeData(chartComponentInfo, dataSet);
        }
    }

    private ChartDataSet doFetching_JOSPHistory2ChartDataSet(ChartComponentInfo chartComponentInfo, List<JOSPHistory> history) {
        Map<Date, Double> data = new HashMap<>();
        for (JOSPHistory status : history) {
            Date date = status.getUpdatedAt();
            double value = doFetching_JOSPHistory2ChartDataSet_PayloadToDouble(status.getPayload());
            data.put(date, value);
        }
        return new ChartDataSet(chartComponentInfo, data);
    }

    private double doFetching_JOSPHistory2ChartDataSet_PayloadToDouble(String payload) {
        JSLRangeState.JOSPRange range = new JSLRangeState.JOSPRange(payload);
        return range.newState;
    }

    private final List<ChartDataSet> doFetching_tmpData = new ArrayList<>();

    private void doFetching_storeData(ChartComponentInfo chartComponentInfo, ChartDataSet dataSet) {
        doFetching_tmpData.add(dataSet);

        if (doFetching_tmpData.size() == getComponents().size()) {
            onDataFetched(doFetching_tmpData);
            doFetching_tmpData.clear();
        }
    }


    // TS/History Limits filter

    private int filterTSPeriod = Calendar.MINUTE;
    private int filterTSQty = 15;
    private int filterTSOffset = 0;
    private int filterTSPartitions = 5; // depends on filterTSPeriod and filterTSQty, max 20
    private Date filterTSFromDate = null;

    @Override
    public void setFilterTS(int period, int qty, int offset, int partitions) {
        if (isFetching()) throw new IllegalStateException("Can't set filter while fetching data");

        filterTSPeriod = period;
        filterTSQty = qty;
        filterTSOffset = offset;
        filterTSPartitions = partitions;

        notifyChangedListeners();

        fetchData();
    }

    @Override
    public int getFilterTSPeriod() {
        return filterTSPeriod;
    }

    @Override
    public int getFilterTSQty() {
        return filterTSQty;
    }

    @Override
    public int getFilterTSOffset() {
        return filterTSOffset;
    }

    @Override
    public int getFilterTSPartitions() {
        return filterTSPartitions;
    }

    @Override
    public Date getFilterTSFromDate() {
        return filterTSFromDate;
    }

    private void setFilterTSFromDate(Date fromDate) {
        filterTSFromDate = fromDate;
    }


    // TSFiltered Listeners

    private final List<TSFilteredListener> tsFilteredListeners = new ArrayList<>();

    @Override
    public void addTSFilteredListener(TSFilteredListener listener) {
        tsFilteredListeners.add(listener);
    }

    @Override
    public void removeTSFilteredListener(TSFilteredListener listener) {
        tsFilteredListeners.remove(listener);
    }

    private void notifyChangedListeners() {
        for (TSFilteredListener listener : tsFilteredListeners) {
            listener.onFilterChanged(filterTSPeriod, filterTSQty, filterTSOffset, filterTSPartitions);
        }
    }

}
