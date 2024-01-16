package com.robypomper.josp.jsl.android.components.charts.adapters;

import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BaseEntry;
import com.github.mikephil.charting.data.DataSet;
import com.robypomper.josp.jsl.android.components.charts.ChartBaseView;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartBaseFormatter;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartDateTimeFormatter;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartUnitFormatter;
import com.robypomper.josp.jsl.android.components.charts.utils.ChartAdapterObserver;
import com.robypomper.josp.jsl.android.components.charts.utils.TimeRangeLimits;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;


/** @noinspection unused*/
public class ChartViewAdapterMap extends ChartViewAdapterAbs {

    /**
     * Like the ChartViewAdapterMap but fetches data in a separate thread.
     * <p>
     * That allows debug the chart simulating the network latency.
     */
    public static class Threaded extends ChartViewAdapterMap {

        public Threaded(ChartAdapterObserver chartObserver, Map<String, List<Map.Entry<Date, Double>>> dataSets) {
            super(chartObserver, dataSets);
        }

        public Threaded(ChartAdapterObserver chartObserver, Map<String, List<Map.Entry<Date, Double>>> dataSets, String[] labels, int[] colors, YAxis.AxisDependency[] yAxisDeps) {
            super(chartObserver, dataSets, labels, colors, yAxisDeps);
        }

        public Threaded(ChartAdapterObserver chartObserver, Map<String, List<Map.Entry<Date, Double>>> dataSets, String[] labels, int[] colors, YAxis.AxisDependency[] yAxisDeps, ChartBaseFormatter xFormatter, ChartBaseFormatter yLeftFormatter, ChartBaseFormatter yRightFormatter) {
            super(chartObserver, dataSets, labels, colors, yAxisDeps, xFormatter, yLeftFormatter, yRightFormatter);
        }


        // ChartViewAdapter re-implementation

        @Override
        public void doFetch(String dataSetName, TimeRangeLimits timeRangeLimits) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(new Random().nextInt(1300) + 200);
                    } catch (InterruptedException ignore) {}
                    Threaded.super.doFetch(dataSetName, timeRangeLimits);
                }
            });
            thread.start();
        }

    }

    // Constants

    private final static String[] DEF_LABELS = new String[]{"DataSet A", "DataSet B", "DataSet C", "DataSet D", "DataSet E", "DataSet F", "DataSet G", "DataSet H", "DataSet I", "DataSet J"};
    private final static int[] DEF_COLORS = new int[]{Color.rgb(255, 0, 0), Color.rgb(0, 255, 0), Color.rgb(0, 0, 255), Color.rgb(255, 255, 0), Color.rgb(255, 0, 255), Color.rgb(0, 255, 255), Color.rgb(255, 255, 255), Color.rgb(0, 0, 0), Color.rgb(128, 128, 128), Color.rgb(128, 0, 0)};
    private final static YAxis.AxisDependency[] DEF_Y_AXIS_DEPS = new YAxis.AxisDependency[]{YAxis.AxisDependency.LEFT, YAxis.AxisDependency.LEFT, YAxis.AxisDependency.LEFT, YAxis.AxisDependency.LEFT, YAxis.AxisDependency.LEFT, YAxis.AxisDependency.LEFT, YAxis.AxisDependency.LEFT, YAxis.AxisDependency.LEFT, YAxis.AxisDependency.LEFT, YAxis.AxisDependency.LEFT};


    // Internal vars

    private final Map<String, List<Map.Entry<Date, Double>>> dataSets;


    // Constructors

    public ChartViewAdapterMap(ChartAdapterObserver chartObserver, Map<String, List<Map.Entry<Date, Double>>> dataSets) {
        this(chartObserver, dataSets, DEF_LABELS, DEF_COLORS, DEF_Y_AXIS_DEPS);
    }

    public ChartViewAdapterMap(ChartAdapterObserver chartObserver, Map<String, List<Map.Entry<Date, Double>>> dataSets, String[] labels, int[] colors, YAxis.AxisDependency[] yAxisDeps) {
        this(chartObserver, dataSets, labels, colors, yAxisDeps, ChartDateTimeFormatter.X_FORMATTER_MINUTES(), ChartUnitFormatter.Y_FORMATTER_UNIT(), ChartUnitFormatter.Y_FORMATTER_UNIT());
    }

    public ChartViewAdapterMap(ChartAdapterObserver chartObserver, Map<String, List<Map.Entry<Date, Double>>> dataSets, String[] labels, int[] colors, YAxis.AxisDependency[] yAxisDeps,
                               ChartBaseFormatter xFormatter, ChartBaseFormatter yLeftFormatter, ChartBaseFormatter yRightFormatter) {
        super(chartObserver, xFormatter, yLeftFormatter, yRightFormatter);
        this.dataSets = dataSets;
        for (int i = 0; i < dataSets.size(); i++) {
            String dataSetName = (String) dataSets.keySet().toArray()[i];
            addDataSet(dataSetName, labels[i], colors[i], yAxisDeps[i]);
        }
    }


    // ChartViewAdapter implementation - Fetching methods

    @Override
    public void doFetch(String dataSetName, TimeRangeLimits timeRangeLimits) {
        List<Map.Entry<Date, Double>> dataSet = dataSets.get(dataSetName);
        if (dataSet == null) throw new IllegalArgumentException("Invalid data set name");

        // Filter by history limits
        List<Map.Entry<Date, Double>> filteredDataSet = new ArrayList<>();
        for (Map.Entry<Date, Double> entry : dataSet) {
            Date date = entry.getKey();
            if (date.getTime() >= timeRangeLimits.getFromDate().getTime() && date.getTime() <= timeRangeLimits.getToDate().getTime())
                filteredDataSet.add(entry);
        }

        // Convert using formatters
        ChartBaseFormatter xFormatter = getXFormatter();
        ChartBaseFormatter yFormatter = getDataSetYAxisDep(dataSetName) == YAxis.AxisDependency.LEFT ? getYLeftFormatter() : getYRightFormatter();
        List<BaseEntry> dataSetEntries = new ArrayList<>();
        for (Map.Entry<Date, Double> entry : filteredDataSet) {
            Date date = entry.getKey();
            Double value = entry.getValue();
            dataSetEntries.add(newEntry(xFormatter.from(date), yFormatter.from(value)));
        }
        DataSet<?> chartDataSet = newDataSet(dataSetEntries, dataSetName);

        Log.d("ChartViewAdapterMap", "DataSet `" + dataSetName + "`: doFetch from " + ChartBaseView.LOG_SDF.format(timeRangeLimits.getFromDate()) + " to " + ChartBaseView.LOG_SDF.format(timeRangeLimits.getToDate()) + " => " + dataSetEntries.size());

        // Notify chart
        notifyDataSetFetched(dataSetName, chartDataSet, timeRangeLimits);
    }

}
