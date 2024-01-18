package com.robypomper.josp.jsl.android.components.charts.adapters;

import android.graphics.Color;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BaseEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.LineRadarDataSet;
import com.github.mikephil.charting.data.LineScatterCandleRadarDataSet;
import com.robypomper.josp.jsl.android.components.charts.MPAndroidChartUtils;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartBaseFormatter;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartDateTimeFormatter;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartUnitFormatter;
import com.robypomper.josp.jsl.android.components.charts.utils.ChartAdapterObserver;
import com.robypomper.josp.jsl.android.components.charts.utils.TimeRangeLimits;
import com.robypomper.josp.jsl.android.utils.CustomGradientDrawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/** @noinspection unused*/
public abstract class ChartViewAdapterAbs implements ChartViewAdapter {

    // Internal vars

    private final ChartAdapterObserver chartObserver;
    private final List<String> dataSetNames = new ArrayList<>();
    private final Map<String, String> dataSetLabels = new HashMap<>();
    private final Map<String, Integer> dataSetColors = new HashMap<>();
    private final Map<String, YAxis.AxisDependency> dataSetYAxisDeps = new HashMap<>();
    private final ChartBaseFormatter xFormatter;
    private final ChartBaseFormatter yLeftFormatter;
    private final ChartBaseFormatter yRightFormatter;


    // Constructors

    protected ChartViewAdapterAbs(ChartAdapterObserver chartObserver) {
        this(chartObserver, ChartDateTimeFormatter.X_FORMATTER_MINUTES(), ChartUnitFormatter.Y_FORMATTER_UNIT(), ChartUnitFormatter.Y_FORMATTER_UNIT());
    }

    protected ChartViewAdapterAbs(ChartAdapterObserver chartObserver, ChartBaseFormatter xFormatter, ChartBaseFormatter yLeftFormatter, ChartBaseFormatter yRightFormatter) {
        this.chartObserver = chartObserver;
        this.xFormatter = xFormatter;
        this.yLeftFormatter = yLeftFormatter;
        this.yRightFormatter = yRightFormatter;
    }


    // ChartViewAdapter implementation - Data sets getters

    public void addDataSet(String dataSetName, String dataSetLabel, int dataSetColor, YAxis.AxisDependency dataSetYAxisDep) {
        dataSetNames.add(dataSetName);
        dataSetLabels.put(dataSetName, dataSetLabel);
        dataSetColors.put(dataSetName, dataSetColor);
        dataSetYAxisDeps.put(dataSetName, dataSetYAxisDep);
        notifyDataSetAdded(dataSetName);
    }

    public void removeDataSet(String dataSetName) {
        dataSetNames.remove(dataSetName);
        dataSetLabels.remove(dataSetName);
        dataSetColors.remove(dataSetName);
        dataSetYAxisDeps.remove(dataSetName);
        notifyDataSetRemoved(dataSetName);
    }

    @Override
    public List<String> getDataSetNames() {
        return dataSetNames;
    }

    @Override
    public String getDataSetLabel(String dataSetName) {
        if (!dataSetNames.contains(dataSetName))
            throw new IllegalArgumentException("Invalid data set name");
        return dataSetLabels.get(dataSetName);
    }

    @Override
    public int getDataSetColor(String dataSetName) {
        if (!dataSetNames.contains(dataSetName))
            throw new IllegalArgumentException("Invalid data set name");
        //noinspection DataFlowIssue
        return dataSetColors.get(dataSetName);
    }

    @Override
    public YAxis.AxisDependency getDataSetYAxisDep(String dataSetName) {
        if (!dataSetNames.contains(dataSetName))
            throw new IllegalArgumentException("Invalid data set name");
        return dataSetYAxisDeps.get(dataSetName);
    }

    public void removeAllDataSets() {
        List<String> tmpList = new ArrayList<>(dataSetNames);
        dataSetNames.clear();
        dataSetLabels.clear();
        dataSetColors.clear();
        dataSetYAxisDeps.clear();
        for (String dataSetName : tmpList)
            notifyDataSetRemoved(dataSetName);
    }


    // ChartViewAdapter implementation - Formatters getters

    @Override
    public ChartBaseFormatter getXFormatter() {
        return xFormatter;
    }

    @Override
    public ChartBaseFormatter getYLeftFormatter() {
        return yLeftFormatter;
    }

    @Override
    public ChartBaseFormatter getYRightFormatter() {
        return yRightFormatter;
    }

    @Override
    public ChartBaseFormatter getDataSetYFormatter(String dataSet) {
        return getDataSetYAxisDep(dataSet) == YAxis.AxisDependency.RIGHT ? getYRightFormatter() : getYLeftFormatter();
    }


    // ChartViewAdapter implementation - Style methods

    @Override
    public void setupChartStyle(Chart<?> chart) {
        setupChartStyle_Classic(chart);
        // or setupChartStyle_FullWindow(chart);
        //chart.setMarker(new MarkerView(chart.getContext(), R.layout.chart_marker_view)));
    }

    public void setupChartStyle_Classic(Chart<?> chart) {
        // Generic chart settings
        // https://weeklycoding.com/mpandroidchart-documentation/general-settings-styling/
        //chart.setBackground(drawable);
        chart.getDescription().setEnabled(false);
        //chart.getDescription().setText("");
        //chart.getDescription().set...(...);
        chart.setNoDataText("No data available");
        if (chart instanceof BarLineChartBase) {
            ((BarLineChartBase<?>) chart).setDrawGridBackground(false);
            //((BarLineChartBase<?>) chart).setGridBackgroundColor(colorRes);
            ((BarLineChartBase<?>) chart).setDrawBorders(false);
            //((BarLineChartBase<?>) chart).setBorder...(...);
        }

        // Interaction chart settings
        // https://weeklycoding.com/mpandroidchart-documentation/interaction-with-the-chart/
        chart.setTouchEnabled(true);    // Allows to enable/disable all possible touch-interactions with the chart.
        if (chart instanceof BarLineChartBase) {
            ((BarLineChartBase<?>) chart).setDragEnabled(true);     // Enables/disables dragging (panning) for the chart.
            ((BarLineChartBase<?>) chart).setScaleEnabled(true);    // Enables/disables scaling for the chart on both axes.
            ((BarLineChartBase<?>) chart).setScaleXEnabled(true);   // Enables/disables scaling on the x-axis.
            ((BarLineChartBase<?>) chart).setScaleYEnabled(true);   // Enables/disables scaling on the y-axis.
            ((BarLineChartBase<?>) chart).setPinchZoom(false);      // If set to true, pinch-zooming is enabled. If disabled, x- and y-axis can be zoomed separately.
            ((BarLineChartBase<?>) chart).setDoubleTapToZoomEnabled(true);
        }

        // Fling and deceleration chart settings
        // https://weeklycoding.com/mpandroidchart-documentation/interaction-with-the-chart/
        chart.setDragDecelerationEnabled(true);         // If set to true, chart continues to scroll after touch up. Default: true.
        chart.setDragDecelerationFrictionCoef(0.9f);    // Deceleration friction coefficient in [0 ; 1] interval, higher values indicate that speed will decrease slowly, for example if it set to 0, it will stop immediately. 1 is an invalid value, and will be converted to 0.9999 automatically.

        // Highlight chart settings
        // https://weeklycoding.com/mpandroidchart-documentation/highlighting/
        chart.setHighlightPerTapEnabled(true);         // Set this to false on your Chart to prevent values from being highlighted by tap gesture. Values can still be highlighted via drag or programmatically. Default: true
        if (chart instanceof BarLineChartBase)
            ((BarLineChartBase<?>) chart).setHighlightPerDragEnabled(true);  // Set this to true on your Chart to allow highlighting per dragging over the chart surface when it is fully zoomed out. Default: true

        // XAxis chart settings
        // https://weeklycoding.com/mpandroidchart-documentation/axis-general/
        // https://weeklycoding.com/mpandroidchart-documentation/xaxis/
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(true);             //Sets the axis enabled or disabled. If disabled, no part of the axis will be drawn regardless of any other settings.
        xAxis.setDrawLabels(true);          //Set this to true to enable drawing the labels of the axis.
        xAxis.setDrawAxisLine(true);        //Set this to true if the line alongside the axis (axis-line) should be drawn or not.
        xAxis.setDrawGridLines(false);      //Set this to true to enable drawing the grid lines for the axis.
        xAxis.setLabelRotationAngle(0f);    //Sets the angle for drawing the X axis labels (in degrees).
        xAxis.setValueFormatter(getXFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setTextSize(10f);
        //xAxis.setCenterAxisLabels(true);

        // YAxis chart settings
        // https://weeklycoding.com/mpandroidchart-documentation/axis-general/
        // https://weeklycoding.com/mpandroidchart-documentation/yaxis/
        if (chart instanceof BarLineChartBase) {
            YAxis leftAxis = ((BarLineChartBase<?>) chart).getAxisLeft();
            if (getYLeftFormatter() != null) {
                leftAxis.setEnabled(true);             //Sets the axis enabled or disabled. If disabled, no part of the axis will be drawn regardless of any other settings.
                leftAxis.setDrawLabels(true);          //Set this to true to enable drawing the labels of the axis.
                leftAxis.setDrawAxisLine(true);        //Set this to true if the line alongside the axis (axis-line) should be drawn or not.
                leftAxis.setDrawGridLines(false);      //Set this to true to enable drawing the grid lines for the axis.
                leftAxis.setDrawZeroLine(true);        // draw a zero line
                leftAxis.setValueFormatter(getYLeftFormatter());
                //leftAxis.setTextSize(10f);
                leftAxis.setCenterAxisLabels(true);
            } else
                leftAxis.setEnabled(false);

            YAxis rightAxis = ((BarLineChartBase<?>) chart).getAxisRight();
            if (getYRightFormatter() != null) {
                rightAxis.setEnabled(true);             //Sets the axis enabled or disabled. If disabled, no part of the axis will be drawn regardless of any other settings.
                rightAxis.setDrawLabels(true);          //Set this to true to enable drawing the labels of the axis.
                rightAxis.setDrawAxisLine(true);        //Set this to true if the line alongside the axis (axis-line) should be drawn or not.
                rightAxis.setDrawGridLines(false);      //Set this to true to enable drawing the grid lines for the axis.
                rightAxis.setDrawZeroLine(true);        // draw a zero line
                rightAxis.setValueFormatter(getYRightFormatter());
                //rightAxis.setTextSize(10f);
                rightAxis.setCenterAxisLabels(true);
            } else
                rightAxis.setEnabled(false);
        }
    }

    public void setupChartStyle_FullWindow(Chart<?> chart) {
        setupChartStyle_Classic(chart);

        // BarLineChartBase and subclasses setup
        if (chart instanceof BarLineChartBase) {
            ((BarLineChartBase<?>) chart).getAxisLeft().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
            ((BarLineChartBase<?>) chart).getAxisRight().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        }
    }

    @Override
    public void setupDataStyle(ChartData<?> chartData) {
        // TODO finire di aggiornare le impostazioni dei dati del grafico
        //chartData.setValueTextSize(9f);
        chartData.setHighlightEnabled(true);
    }

    @Override
    public void setupDataSetStyle(String dataSetName, DataSet<?> dataSet) {
        // TODO finire di aggiornare le impostazioni del data set del grafico
        // Highlight chart settings
        // https://weeklycoding.com/mpandroidchart-documentation/highlighting/
        dataSet.setHighlightEnabled(true);
        if (dataSet instanceof LineScatterCandleRadarDataSet<?>) {
            ((LineScatterCandleRadarDataSet<?>) dataSet).setDrawHighlightIndicators(true);
            ((LineScatterCandleRadarDataSet<?>) dataSet).setHighLightColor(Color.LTGRAY); // color for highlight indicator
        }

        dataSet.setColor(getDataSetColor(dataSetName));
        dataSet.setDrawValues(true);
        if (dataSet instanceof LineRadarDataSet<?>) {
            ((LineRadarDataSet<?>) dataSet).setDrawFilled(true);
            ((LineRadarDataSet<?>) dataSet).setFillDrawable(new CustomGradientDrawable(getDataSetColor(dataSetName)));
        }
        if (dataSet instanceof LineDataSet) {
            ((LineDataSet) dataSet).setMode(LineDataSet.Mode.LINEAR);
            ((LineDataSet) dataSet).setDrawCircles(true);
            ((LineDataSet) dataSet).setCircleColor(getDataSetColor(dataSetName));
            ((LineDataSet) dataSet).setDrawCircleHole(true);
        }
        if (dataSet instanceof BarDataSet) {
            /*((BarDataSet) dataSet).setMode(LineDataSet.Mode.LINEAR);
            ((BarDataSet) dataSet).setDrawCircles(true);
            ((BarDataSet) dataSet).setCircleColor(getDataSetColor(dataSetName));
            ((BarDataSet) dataSet).setDrawCircleHole(true);*/
        }
    }


    // BaseChartView handlers

    protected BaseEntry newEntry(float x, float y) {
        Class<? extends BaseEntry> entryClass = chartObserver.getChartEntryClass();
        return MPAndroidChartUtils.newEntry(entryClass, x, y);
    }

    protected DataSet<?> newDataSet(List<? extends BaseEntry> dataSetEntries, String dataSetName) {
        Class<? extends DataSet<?>> dataSetClass = chartObserver.getChartDataSetClass();
        return MPAndroidChartUtils.newDataSet(dataSetClass, dataSetName, dataSetEntries);
    }

    protected void notifyDataSetAdded(String dataSetName) {
        chartObserver.fetch(dataSetName);
    }

    protected void notifyDataSetFetched(String dataSetName, DataSet<?> dataSet, TimeRangeLimits timeRangeLimits) {
        chartObserver.processFetchedDataSet(dataSetName, dataSet, timeRangeLimits);
    }

    protected void notifyDataSetRemoved(String dataSetName) {
        chartObserver.removeDataSetFromChart(dataSetName, true);
    }

}
