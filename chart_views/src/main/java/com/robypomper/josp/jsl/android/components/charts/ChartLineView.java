package com.robypomper.josp.jsl.android.components.charts;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BaseEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartDateTimeFormatter;
import com.robypomper.josp.jsl.android.charts.R;
import com.robypomper.josp.jsl.android.components.charts.utils.TimeRangeLimits;

public class ChartLineView extends ChartBaseView {

    // Constants

    private final static int LAYOUT = R.layout.view_chart_line;


    // Internal vars

    private final LineChart chart;
    private final ViewGroup layOverlay;
    private final TextView txtOverlay;


    // Constructors

    public ChartLineView(Context context) {
        this(context, null);
    }

    public ChartLineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        /*// Parse attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.LineChartView, defStyleAttr, 0);

        if (a.hasValue(R.styleable.LineChartView_chart_range_offset))
            try {
                rangeOffset = a.getInt(R.styleable.LineChartView_chart_range_offset, rangeOffset);
            } catch (NumberFormatException ignore) { }
        if (a.hasValue(R.styleable.LineChartView_chart_range_unit))
            try {
                rangeUnit = a.getInt(R.styleable.LineChartView_chart_range_unit, rangeUnit);
            } catch (NumberFormatException ignore) { }
        if (a.hasValue(R.styleable.LineChartView_chart_range_qty))
            try {
                rangeQty = a.getInt(R.styleable.LineChartView_chart_range_qty, rangeQty);
            } catch (NumberFormatException ignore) { }
        if (a.hasValue(R.styleable.LineChartView_chart_range_offset))
            try {
                rangePartitions = a.getInt(R.styleable.LineChartView_chart_reduction_count, rangePartitions);
            } catch (NumberFormatException ignore) { }

        a.recycle();*/

        // Inflate ui - already inflated from the super constructor
        // LayoutInflater.from(context).inflate(getLayout(), this, true);

        // Setup chart
        chart = findViewById(R.id.chartComponents);
        chart.setData(new LineData());

        layOverlay = findViewById(R.id.layOverlay);
        txtOverlay = findViewById(R.id.txtOverlay);
        assert layOverlay != null || txtOverlay != null : "Overlay view not found";

        Log.v("ChartLineView", "Created");
    }


    // Getters

    @Override
    protected int getLayout() {
        return LAYOUT;
    }

    @Override
    protected ViewGroup getOverlayView() {
        return layOverlay;
    }

    @Override
    protected TextView getOverlayText() {
        return txtOverlay;
    }

    @Override
    public Class<? extends DataSet<?>> getChartDataSetClass() {
        return LineDataSet.class;
    }

    @Override
    public Class<? extends BaseEntry> getChartEntryClass() {
        return Entry.class;
    }


    @Override
    protected void doInit() {
        getAdapter().setupChartStyle(chart);
        getAdapter().setupDataStyle(chart.getData());
    }

    @Override
    protected void doUpdateTimeRangeOnChart(TimeRangeLimits limits) {
        chart.getXAxis().setLabelCount(getRangePartitions(), true);
        chart.setLogEnabled(true);
        Log.v("ChartLineView", String.format("Updated chart time range: %s -> %s",
                LOG_SDF.format(limits.getFromDate()),
                LOG_SDF.format(limits.getToDate())));
    }

    @Override
    protected DataSet<?> doPrepareDataSet(String dataSetName, DataSet<?> dataSet, TimeRangeLimits limits) {

        // Filter data set to time range limits bounds
        int preSize = dataSet.getEntryCount();
        dataSet = MPAndroidChartUtils.filterDataSetByTimeRangeLimits(dataSet, limits, getAdapter().getXFormatter());
        Log.d("ChartLineView", String.format("DataSet '%s': removed entry out of time bounds %d -> %d items", dataSetName, preSize, dataSet.getEntryCount()));

        // Filter data set to reduce items
        if (getRangePartitions() > 0) {
            preSize = dataSet.getEntryCount();
            assert getAdapter().getXFormatter() instanceof ChartDateTimeFormatter : "XFormatter must be SVDateTimeFormatter";
            //lineDataSet = MPAndroidChartUtils.reduceMiddleValueLineDataSet(lineDataSet, filterCount, true);
            //lineDataSet = MPAndroidChartUtils.reduceAvgLineDataSet(lineDataSet, reductionCount, (ChartFormatters.DateTimeFormatter) adapter.getXFormatter(), true);
            //dataSet = MPAndroidChartUtils.reducePartitionDataSet(dataSet, getRangePartitions(), (ChartFormatters.DateTimeFormatter) getAdapter().getXFormatter(), limits, true);
            dataSet = MPAndroidChartUtils.reduceEqualsPartitionDataSet(dataSet, getChartEntryClass(), getRangePartitions(), false, (ChartDateTimeFormatter) getAdapter().getXFormatter(), limits, true);
            Log.d("ChartLineView", String.format("DataSet '%s': reduced %d -> %d items", dataSetName, preSize, dataSet.getEntryCount()));
        }

        // Remove zero-value entries (reduce missing values chart's malformations)
        preSize = dataSet.getEntryCount();
        dataSet = MPAndroidChartUtils.removeZeroValueEntries(dataSet);
        Log.d("ChartLineView", String.format("DataSet '%s': removed zero-value entries %d -> %d items", dataSetName, preSize, dataSet.getEntryCount()));

        // Prepare data set
        getAdapter().setupDataSetStyle(dataSetName, dataSet);
        dataSet.setAxisDependency(getAdapter().getDataSetYAxisDep(dataSetName));

        return dataSet;
    }

    @Override
    protected void doAddDataSetFromChart(String dataSetName, DataSet<?> dataSet) {
        LineData chartData = chart.getData();
        assert dataSet instanceof LineDataSet : "DataSet must be LineDataSet";
        chartData.addDataSet((LineDataSet) dataSet);
        chart.setData(chartData);
    }

    @Override
    protected void doRemoveDataSetFromChart(String dataSetName) {
        LineData chartData = chart.getData();
        ILineDataSet oldDataSet = chartData.getDataSetByLabel(getAdapter().getDataSetLabel(dataSetName), false);
        if (oldDataSet == null) return;

        chartData.removeDataSet(oldDataSet);
        Log.v("ChartLineView", String.format("Removed '%s data set from chart", dataSetName));
    }

    @Override
    protected void doInvalidateChart(boolean animate) {
        Log.e("ChartLineView", "INVALIDATE LINE CHART " + animate);
        chart.notifyDataSetChanged();

        if (!animate) {
            chart.invalidate();
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chart.animateX(1000, Easing.Linear);
            }
        });
    }

}
