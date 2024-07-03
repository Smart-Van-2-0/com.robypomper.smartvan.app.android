package com.robypomper.smartvan.smart_van.android.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.LineRadarDataSet;
import com.github.mikephil.charting.data.LineScatterCandleRadarDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.robypomper.smartvan.smart_van.android.R;
import com.robypomper.smartvan.smart_van.android.utils.SVCustomGradientDrawable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class SVChartLineView
        extends SVChartViewMPAbs<LineChart>
        implements SVChartView {

    // Constants

    private static final int LAYOUT = R.layout.view_sv_chart_line;
    private static final int MAX_DISPLAYED_ENTRIES = 100;
    private static final float SCALE_FACTOR = 0.001f;
    private static final float EMPTY_RANGE_SCALE = 10;   // used in Period * Qty / EMPTY_RANGE_SCALE
    private static final long MAX_EMPTY_RANGE_MS = (long) (1.5 * (60 * 1000));  // must be 1.5 times the sampling period
    private static final long EMPTY_RANGE_DELTA_MS = 1000;
    private static final boolean ENABLE_AXIS_RIGHT = false;


    // Internal vars

    private final LineChart lineChart;


    // Constructors

    public SVChartLineView(Context context) {
        this(context, null);
    }

    public SVChartLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SVChartLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(LAYOUT, context, attrs, defStyleAttr);

        lineChart = findViewById(R.id.chartComponents);
        lineChart.setData(new LineData());


    }


    // Data management

    @Override
    protected void doProcessDataSet(List<ChartDataSet> dataSetsRaw) {
        // TODO process data sets for bar chart
        // reduce, aggregate, etc...

        List<ChartDataSet> dataSetsProcessed = new ArrayList<>();

        for (ChartDataSet dataSetRaw : dataSetsRaw) {
            ChartDataSet dataSetProcessed = dataSetRaw;
            //dataSetProcessed = doProcessDataSet_addZeroAsMissingValues(this, dataSetProcessed, EMPTY_RANGE_SCALE, EMPTY_RANGE_DELTA_MS);
            dataSetProcessed = doProcessDataSet_addZeroAsMissingValues(this, dataSetProcessed, MAX_EMPTY_RANGE_MS, EMPTY_RANGE_DELTA_MS);
            dataSetProcessed = doProcessDataSet_alterDataSet_MaxDP(dataSetProcessed, MAX_DISPLAYED_ENTRIES);
            dataSetProcessed = doProcessDataSet_scaleDataSet(dataSetProcessed, SCALE_FACTOR);

            dataSetsProcessed.add(dataSetProcessed);
        }

        onDataProcessed(dataSetsProcessed);
    }

    private static ChartDataSet doProcessDataSet_alterDataSet_MaxDP(ChartDataSet dataSetsRaw, int maxDataPointCount) {
        if (dataSetsRaw.data.isEmpty() || dataSetsRaw.data.size() < maxDataPointCount)
            return dataSetsRaw;

        else
            // TODO improve: make sampled DPs heterogeneous distributed on X axis
            // (the same DP count for each section of the X axis)
            return doProcessDataSet___samplingDataSet(dataSetsRaw, maxDataPointCount);
    }

    private static ChartDataSet doProcessDataSet_alterDataSet_FixedDPCount(ChartDataSet dataSetRaw, int fixedDataPointCount) {
        if (dataSetRaw.data.isEmpty() || dataSetRaw.data.size() == fixedDataPointCount)
            return dataSetRaw;

        else if (dataSetRaw.data.size() < fixedDataPointCount)
            return doProcessDataSet___interpolateDataSet(dataSetRaw, fixedDataPointCount);

        else
            return doProcessDataSet___samplingDataSet(dataSetRaw, fixedDataPointCount);
    }

    private static ChartDataSet doProcessDataSet___samplingDataSet(ChartDataSet dataSetRaw, int maxDataPointCount) {
        // TODO improve: update sampling strategy calculating the avg value for each section of the X axis
        // Linear sampling
        Map<Comparable<?>, Number> sampledDataSet = new LinkedHashMap<>();

        // Sampling
        float step = (float)dataSetRaw.data.size() / maxDataPointCount;
        for (int i = 0; i < maxDataPointCount; i++) {
            int index = Math.round(i * step);
            Map.Entry<?, ? extends Number> entry = (Map.Entry<?, ? extends Number>) dataSetRaw.data.entrySet().toArray()[index];
            sampledDataSet.put((Comparable)entry.getKey(), (Number)entry.getValue());
        }

        return new ChartDataSet(dataSetRaw.componentInfo, sampledDataSet);
    }

    private static ChartDataSet doProcessDataSet___interpolateDataSet(ChartDataSet dataSetRaw, int minDataPointCount) {
        if (dataSetRaw.data.isEmpty())
            return dataSetRaw;

        Object firstKey = dataSetRaw.data.keySet().iterator().next();
        Object firstValue = dataSetRaw.data.values().iterator().next();

        if (firstKey instanceof Date)
            return doProcessDataSet___interpolateDataSet_Date(dataSetRaw, minDataPointCount);

        throw new IllegalArgumentException("Unsupported key types: " + firstKey.getClass().getSimpleName() + " / " + firstValue.getClass().getSimpleName());
    }

    private static ChartDataSet doProcessDataSet___interpolateDataSet_Date(ChartDataSet dataSetRaw, int minDataPointCount) {
        // Linear interpolation on Dates
        Map<Date, Number> interpolatedDataSet = new LinkedHashMap<>();

        List<Date> keys = new ArrayList<>(((Map<Date,?>)dataSetRaw.data).keySet());
        List<Number> values = new ArrayList<>(dataSetRaw.data.values());

        long totalInterval = keys.get(keys.size() - 1).getTime() - keys.get(0).getTime();

        for (int i = 0; i < minDataPointCount; i++) {
            long interpolatedTime = keys.get(0).getTime() + i * totalInterval / (minDataPointCount - 1);
            Date interpolatedDate = new Date(interpolatedTime);

            int index = 0;
            while (index < keys.size() - 1 && keys.get(index + 1).getTime() < interpolatedTime) {
                index++;
            }

            double ratio = (double) (interpolatedTime - keys.get(index).getTime()) / (keys.get(index + 1).getTime() - keys.get(index).getTime());

            double interpolatedValue = values.get(index).doubleValue() * (1 - ratio) +
                    values.get(index + 1).doubleValue() * ratio;

            interpolatedDataSet.put(interpolatedDate, interpolatedValue);
        }

        return new ChartDataSet(dataSetRaw.componentInfo, interpolatedDataSet);
    }

    private static ChartDataSet doProcessDataSet_scaleDataSet(ChartDataSet dataSetRaw, float scaleFactor) {
        Map<Comparable<?>, Number> scaledDataSet = new LinkedHashMap<>();
        for (Map.Entry<? extends Comparable<?>, ? extends Number> entry : dataSetRaw.data.entrySet()) {
            Number scaledValue = entry.getValue().doubleValue() * scaleFactor;
            scaledDataSet.put(entry.getKey(), scaledValue);
        }
        return new ChartDataSet(dataSetRaw.componentInfo, scaledDataSet);
    }

    /**
     * Add zero values to the data set where the time range between two
     * consecutive data points is too big.
     * <p>
     * This method calls the {@link #doProcessDataSet_addZeroAsMissingValues(SVChartViewTSFiltered, ChartDataSet, long, long)}
     * with the maximum empty range (`maxEmptyRangeMS`) calculated as the product
     * of the period duration and the quantity of periods divided by `emptyRangeScale`.
     *
     * @param chart the chart
     * @param dataSetRaw the data set
     * @param emptyRangeScale the scale to determinate the maximum time range
     *                        between two consecutive data points
     * @param deltaRangeMS the time range to add a zero value, if 0 or less,
     *                     `emptyRangeScale` is used
     * @return the data set with zero values added
     */
    private static ChartDataSet doProcessDataSet_addZeroAsMissingValues(SVChartViewTSFiltered chart, ChartDataSet dataSetRaw, float emptyRangeScale, long deltaRangeMS) {
        int period = chart.getFilterTSPeriod();
        int qty = chart.getFilterTSQty();
        long maxEmptyRangeMS = (long) (SVChartViewTSFiltered.periodDurationMS(period) * qty / emptyRangeScale);
        return doProcessDataSet_addZeroAsMissingValues(chart, dataSetRaw, maxEmptyRangeMS, deltaRangeMS);
    }

    /**
     * Add zero values to the data set where the time range between two
     * consecutive data points is too big.
     * <p>
     * The time range between two consecutive data points is considered too big
     * if it is greater than the maximum empty range.
     * <p>
     * For each data point, if the time range between the previous/next data
     * points is too big, a zero value is added at `deltaRangeMS` before/after
     * the current data point. If the previous/next data point is the first/last
     * data point, a zero value is added also at the beginning/end of the data
     * set.
     *
     * @param chart the chart
     * @param dataSetRaw the data set
     * @param maxEmptyRangeMS the maximum time range between two consecutive
     *                        data points, the best value is 1.5 times the
     *                        sampling period
     * @param deltaRangeMS the time range to add a zero value, if 0 or less,
     *                     `maxEmptyRangeMS` is used
     * @return the data set with zero values added
     */
    private static ChartDataSet doProcessDataSet_addZeroAsMissingValues(SVChartViewTSFiltered chart, ChartDataSet dataSetRaw, long maxEmptyRangeMS, long deltaRangeMS) {
        Map<Comparable<?>, Number> zeroFilledDataSet = new LinkedHashMap<>();
        List<Comparable<Object>> keys = (List<Comparable<Object>>)new ArrayList<>(dataSetRaw.data.keySet());
        Collections.sort(keys);

        Date fromDate = SVChartViewTSFiltered.calculateFromDate(chart.getFilterTSPeriod(), chart.getFilterTSQty(), chart.getFilterTSOffset());
        Date toDate = SVChartViewTSFiltered.calculateToDate(chart.getFilterTSPeriod(), chart.getFilterTSQty(), chart.getFilterTSOffset());

        if (deltaRangeMS <= 0)
            deltaRangeMS = maxEmptyRangeMS;

        if (keys.isEmpty()) {
            // Set the first and last date
            zeroFilledDataSet.put(fromDate, 0);
            zeroFilledDataSet.put(toDate, 0);
        } else {
            Date prevDate = fromDate;
            Date nextDate = toDate;
            for (int i = 0; i < keys.size(); i++) {
                // Get the previous and next date
                if (i > 0) {
                    Comparable<?> prevDateC = keys.get(i - 1);
                    prevDate = (Date) prevDateC;
                }
                if (i < keys.size() - 1) {
                    Comparable<?> nextDateC = keys.get(i + 1);
                    nextDate = (Date) nextDateC;
                } else nextDate = toDate;

                // Get the current date
                Comparable<?> currDateC = keys.get(i);
                Date currDate = (Date) currDateC;

                // If the current date is too far from the previous date, add a zero value
                if (currDate.getTime() - prevDate.getTime() > maxEmptyRangeMS) {
                    if (prevDate.compareTo(fromDate) == 0)  // assert i==0
                        zeroFilledDataSet.put(prevDate, 0);
                    Date currDatePrevZero = new Date(currDate.getTime() - deltaRangeMS);
                    zeroFilledDataSet.put(currDatePrevZero, 0);
                }

                zeroFilledDataSet.put(currDate, dataSetRaw.data.get(currDate));

                if (nextDate.getTime() - currDate.getTime() > maxEmptyRangeMS) {
                    Date currDateNextZero = new Date(currDate.getTime() + deltaRangeMS);
                    zeroFilledDataSet.put(currDateNextZero, 0);

                    if (nextDate.compareTo(toDate) == 0)    // assert i==keys.size()-1
                        zeroFilledDataSet.put(nextDate, 0);
                  }
            }
        }

        return new ChartDataSet(dataSetRaw.componentInfo, zeroFilledDataSet);
    }

    @Override
    protected LineChart getChart() {
        return lineChart;
    }

    @Override
    protected void doAddDataSetsToChart_addMPDataSet(LineChart data, ChartDataSet dataSet, List<Entry> mpEntries) {
        LineDataSet lineDataSet = new LineDataSet(mpEntries, dataSet.componentInfo.label);
        lineChart.getLineData().addDataSet(lineDataSet);
    }

    @Override
    protected void doAddDataSetsToChart_FormatChart(Object xMin, float xMinF, float xMaxF, float yMinF, float yMaxF) {
        lineChart.setFitsSystemWindows(true);
        Chart<?> chart = lineChart;
        BarLineChartBase<?> barLineChart = lineChart;
        chart.setMarker(new ChartMPAndroidMarker(getContext(), R.layout.view_sv_charts_marker));

        // Generic chart settings
        // https://weeklycoding.com/mpandroidchart-documentation/general-settings-styling/
        //chart.setBackground(drawable);
        chart.getDescription().setEnabled(false);
        //chart.getDescription().setText("");
        //chart.getDescription().set...(...);
        chart.setNoDataText("No data available");
        barLineChart.setDrawGridBackground(false);
        //barLineChart.setGridBackgroundColor(colorRes);
        barLineChart.setDrawBorders(false);
        //barLineChart.setBorder...(...);
        chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        chart.getLegend().setDrawInside(false);

        // Interaction chart settings
        // https://weeklycoding.com/mpandroidchart-documentation/interaction-with-the-chart/
        chart.setTouchEnabled(true);    // Allows to enable/disable all possible touch-interactions with the chart.
        barLineChart.setDragEnabled(true);     // Enables/disables dragging (panning) for the chart.
        barLineChart.setScaleEnabled(true);    // Enables/disables scaling for the chart on both axes.
        barLineChart.setScaleXEnabled(true);   // Enables/disables scaling on the x-axis.
        barLineChart.setScaleYEnabled(true);   // Enables/disables scaling on the y-axis.
        barLineChart.setPinchZoom(false);      // If set to true, pinch-zooming is enabled. If disabled, x- and y-axis can be zoomed separately.
        barLineChart.setDoubleTapToZoomEnabled(true);

        // Fling and deceleration chart settings
        // https://weeklycoding.com/mpandroidchart-documentation/interaction-with-the-chart/
        chart.setDragDecelerationEnabled(true);         // If set to true, chart continues to scroll after touch up. Default: true.
        chart.setDragDecelerationFrictionCoef(0.9f);    // Deceleration friction coefficient in [0 ; 1] interval, higher values indicate that speed will decrease slowly, for example if it set to 0, it will stop immediately. 1 is an invalid value, and will be converted to 0.9999 automatically.

        // Highlight chart settings
        // https://weeklycoding.com/mpandroidchart-documentation/highlighting/
        chart.setHighlightPerTapEnabled(true);         // Set this to false on your Chart to prevent values from being highlighted by tap gesture. Values can still be highlighted via drag or programmatically. Default: true
        barLineChart.setHighlightPerDragEnabled(true);  // Set this to true on your Chart to allow highlighting per dragging over the chart surface when it is fully zoomed out. Default: true

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setEnabled(true);             //Sets the axis enabled or disabled. If disabled, no part of the axis will be drawn regardless of any other settings.
        xAxis.setDrawLabels(true);          //Set this to true to enable drawing the labels of the axis.
        xAxis.setDrawAxisLine(true);        //Set this to true if the line alongside the axis (axis-line) should be drawn or not.
        xAxis.setDrawGridLines(false);      //Set this to true to enable drawing the grid lines for the axis.
        xAxis.setLabelRotationAngle(0f);    //Sets the angle for drawing the X axis labels (in degrees).
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new XAxisLineFormatter(this));    // REF_DATE
        xAxis.setCenterAxisLabels(true);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setEnabled(true);             //Sets the axis enabled or disabled. If disabled, no part of the axis will be drawn regardless of any other settings.
        leftAxis.setDrawLabels(true);          //Set this to true to enable drawing the labels of the axis.
        leftAxis.setDrawAxisLine(true);        //Set this to true if the line alongside the axis (axis-line) should be drawn or not.
        leftAxis.setDrawGridLines(false);      //Set this to true to enable drawing the grid lines for the axis.
        leftAxis.setDrawZeroLine(true);        // draw a zero line
        leftAxis.setValueFormatter(new YAxisLineFormatter());
        //leftAxis.setTextSize(10f);
        leftAxis.setCenterAxisLabels(true);

        YAxis rightAxis = lineChart.getAxisRight();
        if (ENABLE_AXIS_RIGHT) {
            rightAxis.setEnabled(true);             //Sets the axis enabled or disabled. If disabled, no part of the axis will be drawn regardless of any other settings.
            rightAxis.setDrawLabels(true);          //Set this to true to enable drawing the labels of the axis.
            rightAxis.setDrawAxisLine(true);        //Set this to true if the line alongside the axis (axis-line) should be drawn or not.
            rightAxis.setDrawGridLines(false);      //Set this to true to enable drawing the grid lines for the axis.
            rightAxis.setDrawZeroLine(true);        // draw a zero line
            rightAxis.setValueFormatter(new YAxisLineFormatter());
            //rightAxis.setTextSize(10f);
            rightAxis.setCenterAxisLabels(true);
        } else
            rightAxis.setEnabled(false);

        // Format DataSets
        for (ChartComponentInfo componentInfo : getComponents()) {
            LineDataSet lineDataSet = (LineDataSet) lineChart.getLineData().getDataSetByLabel(componentInfo.label, false);
            /*lineDataSet.setColor(componentInfo.color);
            lineDataSet.setDrawValues(false);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setDrawCircleHole(false);
            lineDataSet.setDrawFilled(true);
            lineDataSet.setLineWidth(2f);
            //lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSet.setCubicIntensity(0.2f);
            lineDataSet.setDrawHorizontalHighlightIndicator(false);
            lineDataSet.setDrawVerticalHighlightIndicator(false);
            lineDataSet.setHighlightEnabled(true);
            lineDataSet.setHighLightColor(Color.LTGRAY);
            System.out.println("Color: " + componentInfo.color);*/

            DataSet<?> dataSet = lineDataSet;
            LineScatterCandleRadarDataSet<?> dataSetLScatter = lineDataSet;
            LineRadarDataSet<?> dataSetLRadar = lineDataSet;

            dataSet.setHighlightEnabled(true);
            dataSetLScatter.setDrawHighlightIndicators(true);
            dataSetLScatter.setHighLightColor(Color.GRAY); // color for highlight indicator

            dataSet.setColor(componentInfo.color);
            dataSet.setFormLineWidth(0.8f);
            dataSet.setDrawValues(false);
            dataSetLRadar.setDrawFilled(true);
            dataSetLRadar.setFillDrawable(new SVCustomGradientDrawable(componentInfo.color));
            lineDataSet.setMode(LineDataSet.Mode.LINEAR);
            lineDataSet.setDrawCircles(true);
            lineDataSet.setCircleColor(componentInfo.color);
            lineDataSet.setCircleRadius(1.5f);
            lineDataSet.setDrawCircleHole(false);
        }
    }

    private static class XAxisLineFormatter extends ValueFormatter {

        private final SVChartViewTSFiltered chart;

        public XAxisLineFormatter(SVChartViewTSFiltered chart) {
            this.chart = chart;
        }

        @Override
        public String getFormattedValue(float value) {
            SimpleDateFormat sdf = SVChartViewTSFiltered.periodFormatterDetails(chart.getFilterTSPeriod(), chart.getFilterTSQty());
            Date date = MPEntry2MapEntry_MPFloat2Date(chart.getFilterTSFromDate(), chart.getFilterTSPeriod(), chart.getFilterTSQty(), value);
            return sdf.format(date);
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            SimpleDateFormat sdf = SVChartViewTSFiltered.periodFormatterAxis(chart.getFilterTSPeriod(), chart.getFilterTSQty());
            Date date = MPEntry2MapEntry_MPFloat2Date(chart.getFilterTSFromDate(), chart.getFilterTSPeriod(), chart.getFilterTSQty(), value);
            return sdf.format(date);
        }

    }

    private static class YAxisLineFormatter extends ValueFormatter {

        public YAxisLineFormatter() {}

        @SuppressLint("DefaultLocale")
        @Override
        public String getFormattedValue(float value) {
            return String.format("%.2f", value);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return String.format("%.0f", value);
        }

    }
}
