package com.robypomper.josp.jsl.android.components.charts;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BaseEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.robypomper.josp.jsl.android.charts.R;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartBaseFormatter;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartDateTimeFormatter;
import com.robypomper.josp.jsl.android.components.charts.utils.TimeRangeLimits;

public class ChartBarView extends ChartBaseView {

    // Constants

    private final static int LAYOUT = R.layout.view_chart_bar;


    private final BarChart chart;
    private final ViewGroup layOverlay;
    private final TextView txtOverlay;

    public ChartBarView(Context context) {
        this(context, null);
    }

    public ChartBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        /*// Parse attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BarChartView, defStyleAttr, 0);

        if (a.hasValue(R.styleable.BarChartView_chart_range_offset))
            try {
                rangeOffset = a.getInt(R.styleable.BarChartView_chart_range_offset, rangeOffset);
            } catch (NumberFormatException ignore) { }
        if (a.hasValue(R.styleable.BarChartView_chart_range_unit))
            try {
                rangeUnit = a.getInt(R.styleable.BarChartView_chart_range_unit, rangeUnit);
            } catch (NumberFormatException ignore) { }
        if (a.hasValue(R.styleable.BarChartView_chart_range_qty))
            try {
                rangeQty = a.getInt(R.styleable.BarChartView_chart_range_qty, rangeQty);
            } catch (NumberFormatException ignore) { }
        if (a.hasValue(R.styleable.BarChartView_chart_range_offset))
            try {
                rangePartitions = a.getInt(R.styleable.BarChartView_chart_reduction_count, rangePartitions);
            } catch (NumberFormatException ignore) { }

        a.recycle();*/

        // Inflate ui - already inflated from the super constructor
        // LayoutInflater.from(context).inflate(getLayout(), this, true);

        // Setup chart
        chart = findViewById(R.id.chartComponents);
        chart.setData(new BarData());

        layOverlay = findViewById(R.id.layOverlay);
        txtOverlay = findViewById(R.id.txtOverlay);
        assert layOverlay != null || txtOverlay != null : "Overlay view not found";

        Log.v("ChartBarView", "Created");
    }

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
        return BarDataSet.class;
    }

    @Override
    public Class<? extends BaseEntry> getChartEntryClass() {
        return BarEntry.class;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        chart.setEnabled(enabled);
    }


    @Override
    protected void doInit() {
        getAdapter().setupChartStyle(chart);
        getAdapter().setupDataStyle(chart.getData());
    }

    /** @noinspection ConstantValue*/
    @Override
    protected void doUpdateTimeRangeOnChart(TimeRangeLimits limits) {
        final boolean LOG = false;
        ChartBaseFormatter xFormatter = getAdapter().getXFormatter();

        chart.getXAxis().setLabelCount(getRangePartitions(), true);

        float GROUP_SPACE_FULL_PERCENTAGE = 0.15f;
        float BAR_SPACE_PERCENTAGE = 0.10f;
        //int dataSetsCount = chart.getBarData().getDataSetCount();
        int dataSetsCount = getAdapter().getDataSetNames().size();
        if (LOG) System.out.println("dataSetsCount: " + dataSetsCount);
        // https://weeklycoding.com/mpandroidchart-documentation/setting-data/
        float fullWidth = xFormatter.from(limits.getToDate()) - xFormatter.from(limits.getFromDate());
        float spaceWidth = fullWidth * GROUP_SPACE_FULL_PERCENTAGE;                     // all spaces between bars
        float barWidth = (fullWidth - spaceWidth) / getRangePartitions();               // single bar
        chart.getBarData().setBarWidth(barWidth);
        if (LOG) System.out.println("barWidth: " + barWidth);
        if (dataSetsCount > 1) {
            float fullGroupSpacesWidth = fullWidth * GROUP_SPACE_FULL_PERCENTAGE;       // all group spaces
            float groupSpaceWidth = fullGroupSpacesWidth / getRangePartitions();        // single group space

            float fullGroupsWidth = fullWidth - fullGroupSpacesWidth;                   // all groups including bars and bar spaces
            float groupWidth = fullGroupsWidth / getRangePartitions();                  // single group
            if (LOG) System.out.println("groupWidth: " + groupWidth);
            float fullBarSpaceWidth = groupWidth * BAR_SPACE_PERCENTAGE;                // all bar spaces
            if (LOG) System.out.println("fullBarSpaceWidth: " + fullBarSpaceWidth);
            float groupBarWidth = (groupWidth - fullBarSpaceWidth) / dataSetsCount;     // single bar

            float barSpaceWidth = fullBarSpaceWidth / dataSetsCount;                    // single group space

            chart.getBarData().setBarWidth(groupBarWidth);
            chart.getBarData().groupBars(xFormatter.from(limits.getFromDate()), groupSpaceWidth, barSpaceWidth);
            if (LOG) System.out.println("groupBarWidth: " + groupBarWidth);
            if (LOG) System.out.println("groupSpaceWidth: " + groupSpaceWidth);
            if (LOG) System.out.println("barSpaceWidth: " + barSpaceWidth);
        }

        Log.v("ChartBarView", String.format("doUpdated chart time range: %s -> %s",
                LOG_SDF.format(limits.getFromDate()),
                LOG_SDF.format(limits.getToDate())));
    }

    @Override
    protected DataSet<?> doPrepareDataSet(String dataSetName, DataSet<?> dataSet, TimeRangeLimits limits) {

        // Filter data set to time range limits bounds
        int preSize = dataSet.getEntryCount();
        dataSet = MPAndroidChartUtils.filterDataSetByTimeRangeLimits(dataSet, limits, getAdapter().getXFormatter());
        Log.d("ChartBarView", String.format("DataSet '%s': removed entry out of time bounds %d -> %d items", dataSetName, preSize, dataSet.getEntryCount()));

        // Filter data set to reduce items
        if (getRangePartitions() > 0) {
            preSize = dataSet.getEntryCount();
            assert getAdapter().getXFormatter() instanceof ChartDateTimeFormatter : "XFormatter must be SVDateTimeFormatter";
            //lineDataSet = MPAndroidChartUtils.reduceMiddleValueLineDataSet(lineDataSet, filterCount, true);
            //lineDataSet = MPAndroidChartUtils.reduceAvgLineDataSet(lineDataSet, reductionCount, (ChartFormatters.DateTimeFormatter) adapter.getXFormatter(), true);
            //dataSet = MPAndroidChartUtils.reducePartitionDataSet(dataSet, getRangePartitions(), (ChartFormatters.DateTimeFormatter) getAdapter().getXFormatter(), limits, true);
            dataSet = MPAndroidChartUtils.reduceEqualsPartitionDataSet(dataSet, getChartEntryClass(), getRangePartitions(), false, (ChartDateTimeFormatter) getAdapter().getXFormatter(), limits, true);
            Log.d("ChartBarView", String.format("DataSet '%s': reduced %d -> %d items", dataSetName, preSize, dataSet.getEntryCount()));
        }

        // Remove zero-value entries (reduce missing values chart's malformations)
        //preSize = dataSet.getEntryCount();
        //dataSet = MPAndroidChartUtils.removeZeroValueEntries(dataSet);
        //Log.d("ChartBarView", String.format("DataSet '%s': removed zero-value entries %d -> %d items", dataSetName, preSize, dataSet.getEntryCount()));

        return dataSet;
    }

    @Override
    protected void doAddDataSetFromChart(String dataSetName, DataSet<?> dataSet) {
        synchronized (chart) {
            BarData chartData = chart.getData();
            assert dataSet instanceof BarDataSet : "DataSet must be BarDataSet";
            chartData.addDataSet((BarDataSet) dataSet);
            chart.setData(chartData);
            chart.notifyDataSetChanged();
        }
    }

    @Override
    protected void doRemoveDataSetFromChart(String dataSetName) {
        synchronized (chart) {
            BarData chartData = chart.getData();
            IBarDataSet oldDataSet = null;
            try {
                oldDataSet = chartData.getDataSetByLabel(getAdapter().getDataSetLabel(dataSetName), false);
            } catch (IllegalArgumentException ignore) { }
            if (oldDataSet == null) return;

            chartData.removeDataSet(oldDataSet);
        }
    }

    @Override
    protected void doInvalidateChart(boolean animate) {
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


    // Export methods from ChartExportable

    public Bitmap exportImg() {
        return loadBitmapFromView(chart);
    }

}
