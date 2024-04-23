package com.robypomper.smartvan.smart_van.android.components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.github.mikephil.charting.utils.MPPointF;
import com.robypomper.smartvan.smart_van.android.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public abstract class SVChartViewMPAbs <T extends Chart<? extends ChartData<? extends IDataSet<? extends Entry>>>>
        extends SVChartViewJSLAbs
        implements SVChartView {


    // Constructors

    public SVChartViewMPAbs(int layout, Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(layout, context, attrs, defStyleAttr);
    }


    // Data management

    protected abstract T getChart();

    @Override
    protected void doCleanChart() {
        getChart().highlightValue(null);
        getChart().setDrawMarkers(false);
        getChart().getData().clearValues();
        getChart().getData().notifyDataChanged();
        getChart().notifyDataSetChanged();
        getChart().invalidate();
    }

    @Override
    protected void doAddDataSetsToChart(List<ChartDataSet> dataSetsProcessed) {
        Object xMin = null;
        float xMinF = Float.MAX_VALUE;
        float xMaxF = Float.MIN_VALUE;
        float yMinF = Float.MAX_VALUE;
        float yMaxF = Float.MIN_VALUE;

        // Update chart data
        ChartData<? extends IDataSet<? extends Entry>> data = getChart().getData();
        assert data != null: "Chart data must be set during chart initialization";
        for (ChartDataSet dataSet : dataSetsProcessed) {
            List<Entry> mpEntries = new ArrayList<>();
            for (Comparable<?> key : dataSet.data.keySet()) {
                Entry entryMPChart = MapEntry2MPEntry(key, dataSet.data.get(key), getFilterTSFromDate(), getFilterTSPeriod(), getFilterTSQty());
                if (entryMPChart == null)
                    continue;   // skip this data point
                System.out.println(new SimpleDateFormat("hh:mm:ss.SSS").format(key) + "     entryMPChart: " + entryMPChart.getX() + " " + entryMPChart.getY());
                mpEntries.add(entryMPChart);

                // Updated bounds, if needed
                xMinF = Math.min(xMinF, entryMPChart.getX());
                xMaxF = Math.max(xMaxF, entryMPChart.getX());
                yMinF = Math.min(yMinF, entryMPChart.getY());
                yMaxF = Math.max(yMaxF, entryMPChart.getY());
                xMin = (xMin == null)
                            ? key
                            //: xMin.compareTo(key) < 0
                            : ((Comparable<Object>)key).compareTo(xMin) > 0
                                ? xMin
                            : key;
            }
            Collections.sort(mpEntries, new EntryXComparator());
            doAddDataSetsToChart_addMPDataSet(getChart(), dataSet, mpEntries);
            getChart().notifyDataSetChanged();
        }

        // Update chart formatters
        doAddDataSetsToChart_FormatChart(xMin, xMinF, xMaxF, yMinF, yMaxF);

        // Refresh chart
        ((Activity)getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getChart().getData().notifyDataChanged();
                getChart().notifyDataSetChanged();
                getChart().invalidate();
                getChart().setDrawMarkers(true);
            }
        });
        //getChart().getData().notifyDataChanged();
        //getChart().notifyDataSetChanged();
        //getChart().invalidate();

        // Notify "data sets added to chart" and "fetch and processing completed"
        onDataAddedToChart(dataSetsProcessed);
        onDoFetchAndProcessingCompleted();
    }

    protected abstract void doAddDataSetsToChart_addMPDataSet(T data, ChartDataSet dataSet, List<Entry> mpEntries);

    protected abstract void doAddDataSetsToChart_FormatChart(Object xMin, float xMinF, float xMaxF, float yMinF, float yMaxF);

    private static Entry doAddDataSetsToChart_Generic2MPChart(Map.Entry<Comparable<?>, Number> entry, Date refFromDate, int filterTSPeriod, int filterTSQty) {
        return MapEntry2MPEntry(entry, refFromDate, filterTSPeriod, filterTSQty);
    }

    protected static Entry MapEntry2MPEntry(Map.Entry<Comparable<?>, Number> entry, Date refFromDate, int filterTSPeriod, int filterTSQty) {
        return MapEntry2MPEntry(entry.getKey(), entry.getValue(), refFromDate, filterTSPeriod, filterTSQty);
    }

    protected static Entry MapEntry2MPEntry(Comparable<?> key, Number value, Date refFromDate, int filterTSPeriod, int filterTSQty) {
        float k;
        if (key instanceof Date) {
            k = MapEntry2MPEntry_Date2MPFloat(refFromDate, filterTSPeriod, filterTSQty, (Date) key);
            MPEntry2MapEntry_MPFloat2Date(refFromDate, filterTSPeriod, filterTSQty, k);
        }
        else if (key instanceof Number)
            k = ((Number) key).floatValue();
        else
            return null;    // skip this data point

        float v;
        if (value == null)
            return null;    // skip this data point
        v = value.floatValue();

        return new Entry(k, v);
    }

    private static float MapEntry2MPEntry_Date2MPFloat(Date refFromDate, int period, int qty, Date date) {
        long periodMS = SVChartViewTSFiltered.periodDurationMS(period);
        long rangeMS = periodMS * qty;

        long relativeTimeMS = date.getTime() - refFromDate.getTime();
        float relativeTime = relativeTimeMS / (float)(periodMS / 1);      // Keep 2 digits after the comma
        float range = rangeMS / (float)(periodMS / 1);                    // Keep 2 digits after the comma

        float dateFloat = (relativeTime * 100) / range;
        //Log.e("Date2MPFloat", String.format("refFromDate=%s, date=%s, => dateFloat=%f",
        //        refFromDate, new SimpleDateFormat("hh:mm:ss.SSS").format(date), dateFloat));
        return dateFloat;
    }

    protected static Date MPEntry2MapEntry_MPFloat2Date(Date refFromDate, int period, int qty, float dateFloat) {
        long periodMS = SVChartViewTSFiltered.periodDurationMS(period);
        long rangeMS = periodMS * qty;

        long relativeTimeMS = (long)((dateFloat / 100) * rangeMS);

        Date date = new Date(refFromDate.getTime() + relativeTimeMS);
        //Log.e("MPFloat2Date", String.format("refFromDate=%s, date=%s, <= dateFloat=%s",
        //        refFromDate, new SimpleDateFormat("hh:mm:ss.SSS").format(date), dateFloat));
        return date;
    }


    // Marker

    protected class ChartMPAndroidMarker extends MarkerView {

        private final TextView txtValue;
        private final TextView txtTime;

        public ChartMPAndroidMarker(Context context, int layoutResource) {
            super(context, layoutResource);
            this.txtValue = findViewById(R.id.txtMarkerValue);
            this.txtTime = findViewById(R.id.txtMarkerTime);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            Chart<?> chart = getChart();

            // Retrieve ComponentInfo from highlight
            ChartComponentInfo componentInfo = null;
            IDataSet<?> ds = getChart().getData().getDataSetByIndex(highlight.getDataSetIndex());
            if (ds != null) {
                String label = ds.getLabel();
                componentInfo = findComponentInfo(label);
            }

            // Set value
            String value;
            if (chart instanceof BarLineChartBase<?>) {
                ValueFormatter yAxis;
                BarLineChartBase<?> barLineChart = (BarLineChartBase<?>) chart;
                yAxis = highlight.getAxis() == YAxis.AxisDependency.LEFT
                        ? barLineChart.getAxisLeft().getValueFormatter()
                        : barLineChart.getAxisRight().getValueFormatter();
                value = yAxis.getFormattedValue(e.getY());
            } else
                value = String.format("%.2f", e.getY());
            if (componentInfo != null)
                value += String.format(" %s", componentInfo.unit);
            txtValue.setText(value);

            // Set time
            String time = getChart().getXAxis().getValueFormatter().getFormattedValue(e.getX());
            txtTime.setText(String.format("@ %s", time));

            super.refreshContent(e, highlight);
        }

        private MPPointF mOffset;

        @Override
        public MPPointF getOffset() {
            if(mOffset == null) {
                // center the marker horizontally and vertically
                mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
            }
            return mOffset;
        }

    }


    // Exportable methods

    @Override
    public View getChartView() {
        return getChart();
    }

}
