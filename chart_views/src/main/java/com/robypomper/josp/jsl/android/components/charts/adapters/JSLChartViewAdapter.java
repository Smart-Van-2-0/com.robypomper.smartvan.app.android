package com.robypomper.josp.jsl.android.components.charts.adapters;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BaseEntry;
import com.robypomper.josp.jsl.android.app.JSLApplication;
import com.robypomper.josp.jsl.android.components.charts.ChartLineView;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartBaseFormatter;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartDateTimeFormatter;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartUnitFormatter;
import com.robypomper.josp.jsl.android.components.charts.formatters.JSLUnitFormatter;
import com.robypomper.josp.jsl.android.components.charts.utils.ChartAdapterObserver;
import com.robypomper.josp.jsl.android.components.charts.utils.TimeRangeLimits;
import com.robypomper.josp.jsl.android.service.JSLService;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.history.HistoryCompStatus;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPStatusHistory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class JSLChartViewAdapter extends ChartViewAdapterAbs {

    // Internal vars

    private final JSLApplication<? extends JSLService> jslApplication;
    private final Map<String, JSLComponent> componentsAsDataSets = new HashMap<>();


    // Constructors

    public JSLChartViewAdapter(JSLApplication<? extends JSLService> jslApplication, ChartAdapterObserver chartObserver) {
        this(jslApplication, chartObserver, ChartDateTimeFormatter.X_FORMATTER_MINUTES(), ChartUnitFormatter.Y_FORMATTER_UNIT(), ChartUnitFormatter.Y_FORMATTER_UNIT());
    }

    public JSLChartViewAdapter(JSLApplication<? extends JSLService> jslApplication, ChartAdapterObserver chartObserver,
                               ChartBaseFormatter xFormatter, ChartBaseFormatter yLeftFormatter, ChartBaseFormatter yRightFormatter) {
        super(chartObserver, xFormatter, yLeftFormatter, yRightFormatter);
        this.jslApplication = jslApplication;
    }


    public void addComponent(JSLComponent comp, String lineLabel, int color, YAxis.AxisDependency axisYSide) {
        if (comp == null) throw new IllegalArgumentException("Component cannot be null");

        String dataSetName = comp.getPath().getString();
        if (componentsAsDataSets.containsKey(dataSetName))
            throw new IllegalArgumentException(String.format("Component '%s' already added", dataSetName));

        componentsAsDataSets.put(dataSetName, comp);
        addDataSet(dataSetName, lineLabel, color, axisYSide);
    }

    public void removeComponent(JSLComponent comp) {
        if (comp == null) throw new IllegalArgumentException("Component cannot be null");

        String dataSetName = comp.getPath().getString();
        if (!componentsAsDataSets.containsKey(dataSetName))
            return;

        removeDataSet(dataSetName);
        componentsAsDataSets.remove(dataSetName);
    }


    // ChartViewAdapter implementation - Fetching methods

    @Override
    public void doFetch(String dataSetName, TimeRangeLimits timeRangeLimits) {
        JSLComponent dataSetComp = componentsAsDataSets.get(dataSetName);
        if (dataSetComp == null) throw new IllegalArgumentException("Invalid data set name");

        final HistoryLimits historyLimits = toHistoryLimits(timeRangeLimits);
        jslApplication.runOnNetworkThread(new Runnable() {
            @Override
            public void run() {
                try {
                    dataSetComp.getRemoteObject().getStruct().getComponentHistory(dataSetComp, historyLimits,
                            new HistoryCompStatus.StatusHistoryListener() {
                                @Override
                                public void receivedStatusHistory(List<JOSPStatusHistory> history) {
                                    System.out.println("[DataSet: " + dataSetName + "] - doFetch from " + ChartLineView.LOG_SDF.format(historyLimits.getFromDate()) + " to " + ChartLineView.LOG_SDF.format(historyLimits.getToDate()) + " => " + history.size());

                                    List<BaseEntry> dataSetEntry = new ArrayList<>();
                                    for (JOSPStatusHistory status : history) {
                                        Date date = status.getUpdatedAt();
                                        float value = (float) JSLUnitFormatter.payloadToValue(status.getPayload());
                                        BaseEntry entryItem = newEntry(getXFormatter().from(date), getDataSetYFormatter(dataSetName).from(value));
                                        dataSetEntry.add(entryItem);
                                    }

                                    // Notify chart
                                    notifyDataSetFetched(dataSetName, newDataSet(dataSetEntry, dataSetName), timeRangeLimits);
                                    //notifyDataSetFetched(dataSetName, new LineDataSet(dataSetEntry, dataSetName), timeRangeLimits);
                                }
                            }
                    );
                } catch (JSLRemoteObject.ObjectNotConnected e) {
                    throw new RuntimeException(e);
                } catch (JSLRemoteObject.MissingPermission e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    // JSL conversion methods

    public static HistoryLimits toHistoryLimits(TimeRangeLimits timeRangeLimits) {
        return new HistoryLimits(null, null, null, null, timeRangeLimits.getFromDate(), timeRangeLimits.getToDate(), null, null);
    }

}
