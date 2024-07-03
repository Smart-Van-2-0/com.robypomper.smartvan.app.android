package com.robypomper.smartvan.smart_van.android.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.StyleRes;

import com.robypomper.smartvan.smart_van.android.R;

import java.util.List;

/**
 * TODO: document your custom view class.
 * TODO: move UI binding and setup into onCreateView() method
 * TODO: handle ParseException in constructors and remove constructor's throws
 * <p>
 * TimeSettingsView
 * <= Time Range Period     setRangePeriod(period)
 * <= Time Range Qty        setRangeQty(qty)
 * <p>
 *
 * @noinspection unused
 */
public class SVTimeSettingsView extends LinearLayout {

    // Constants

    private final static int LAYOUT = R.layout.view_sv_charts_time_settings;
    private static final int LAYOUT_ITEMS = R.layout.view_sv_charts_time_settings_item;


    // Internal vars

    private SVChartViewTSFiltered chart = null;
    private int rangePeriod = SVChartViewTSFiltered.DEFAULT_TIME_RANGE_PERIOD;
    private int rangeQty = SVChartViewTSFiltered.DEFAULT_TIME_RANGE_QTY;
    private int textAppearanceTxt = 0;   //  com.google.android.material.R.attr.textAppearanceButton
    private int textAppearanceFlds = 0;   //  com.google.android.material.R.attr.textAppearanceButton

    private final TextView txtLabel;
    private final Spinner spnPeriod;
    private final Spinner spnQty;
    private boolean isInternalUpdate = false;


    // Constructors

    public SVTimeSettingsView(Context context) {
        this(context, null, 0);
    }

    public SVTimeSettingsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SVTimeSettingsView(Context context, AttributeSet attrs, int defStyle) {
        super(new ContextThemeWrapper(context, defStyle), attrs, defStyle);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SVTimeSettingsView, defStyle, 0);
        rangePeriod = a.getInt(R.styleable.SVTimeSettingsView_settings_range_period, rangePeriod);
        rangeQty = a.getInt(R.styleable.SVTimeSettingsView_settings_range_qty, rangeQty);
        textAppearanceFlds = a.getResourceId(R.styleable.SVTimeSettingsView_settings_text_appearance_flds, textAppearanceFlds);
        textAppearanceTxt = a.getResourceId(R.styleable.SVTimeSettingsView_settings_text_appearance_txt, textAppearanceTxt);
        a.recycle();

        // Inflate ui
        LayoutInflater.from(context).inflate(LAYOUT, this, true);

        // Get ui components
        txtLabel = findViewById(R.id.txtLabel);
        updateTxtTextAppearance();
        spnPeriod = findViewById(R.id.spnPeriod);
        spnQty = findViewById(R.id.spnQty);
        updateFldsTextAppearance();

        // set period selection listener
        spnPeriod.setOnItemSelectedListener(onPeriodItemClick);
        spnQty.setOnItemSelectedListener(onQtyItemClick);

        // set period adapter
        List<String> periodsList = SVChartViewTSFiltered.getTimeRangePeriodsStr();
        ArrayAdapter<String> periodListAdapter = new ArrayAdapter<>(getContext(), LAYOUT_ITEMS, R.id.txtLabel, periodsList);
        spnPeriod.setAdapter(periodListAdapter);

        // set rangeQty adapter depending on rangePeriod
        setupQtyAdapter();

        // Update UI with initial values
        updateUI();
    }

    private void setupQtyAdapter() {
        // set rangeQty adapter depending on rangePeriod
        List<Integer> qtys = SVChartViewTSFiltered.getTimeRangeQtys(rangePeriod);
        ArrayAdapter<Integer> qtyListAdapter = new ArrayAdapter<>(getContext(), LAYOUT_ITEMS, R.id.txtLabel, qtys);
        spnQty.setAdapter(qtyListAdapter);
    }


    // Getters/Setters

    public void setChart(SVChartViewTSFiltered chartView) {
        if (chart != null)
            chart.removeTSFilteredListener(chartFilterListener);

        chart = chartView;

        if (chart != null) {
            chartFilterListener.onFilterChanged(chart.getFilterTSPeriod(), chart.getFilterTSQty(), chart.getFilterTSOffset(), chart.getFilterTSPartitions());
            chart.addTSFilteredListener(chartFilterListener);
        } else {
            chartFilterListener.onFilterChanged(
                    SVChartViewTSFiltered.DEFAULT_TIME_RANGE_PERIOD,
                    SVChartViewTSFiltered.DEFAULT_TIME_RANGE_QTY,
                    SVChartViewTSFiltered.DEFAULT_TIME_RANGE_OFFSET,
                    SVChartViewTSFiltered.DEFAULT_TIME_RANGE_PARTITIONS);
        }
    }

    private SVChartViewTSFiltered getChart() {
        assert chart != null : "Chart not set";
        return chart;
    }

    public void setFldsTextAppearance(@StyleRes int textAppearanceFlds) {
        this.textAppearanceFlds = textAppearanceFlds;
        updateFldsTextAppearance();
    }

    public void setTxtTextAppearance(@StyleRes int textAppearanceTxt) {
        this.textAppearanceTxt = textAppearanceTxt;
        updateTxtTextAppearance();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        txtLabel.setEnabled(enabled);
        spnQty.setEnabled(enabled);
        spnPeriod.setEnabled(enabled);
    }


    // UI

    private void updateUI() {
        if (chart == null) return;

        // from period to idx
        int idxperiod = SVChartViewTSFiltered.getTimeRangePeriodIdx(rangePeriod);
        spnPeriod.setSelection(idxperiod);

        // from rangeQty/rangePeriod to idx
        int idxQty = SVChartViewTSFiltered.getTimeRangeQtys(rangePeriod).indexOf(rangeQty);
        isInternalUpdate = true;
        spnQty.setSelection(idxQty);
        //isInternalUpdate = false;
    }

    private void updateFldsTextAppearance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //spnPeriod.setTextAppearance(textAppearanceBtn);
            //spnQty.setTextAppearance(textAppearanceBtn);
        }
    }

    private void updateTxtTextAppearance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            txtLabel.setTextAppearance(textAppearanceTxt);
        }
    }

    /** @noinspection FieldCanBeLocal*/
    private final AdapterView.OnItemSelectedListener onPeriodItemClick = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int period = SVChartViewTSFiltered.getTimeRangePeriods().get(position);
            if (period == -1) {
                Log.w("TimeSettingsView", "Invalid period selected, got " + period);
                return;
            }

            if (period == rangePeriod) return;

            Log.d("TimeSettingsView", "onItemSelected-Period: " + period);
            try {
                chart.setFilterTS(rangePeriod, chart.getFilterTSQty(), chart.getFilterTSOffset(), chart.getFilterTSPartitions());
            } catch (IllegalStateException e) {
                Log.d("TimeSettingsView", e.getMessage());
            }
            // Do not update UI, because it will be updated by the listener
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    /** @noinspection FieldCanBeLocal*/
    private final AdapterView.OnItemSelectedListener onQtyItemClick = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (isInternalUpdate) {
                isInternalUpdate = false;
                return;
            }

            int qty = SVChartViewTSFiltered.getTimeRangeQtys(rangePeriod).get(position);
            if (qty == -1) {
                Log.w("TimeSettingsView", "Invalid quantity selected, got " + qty);
                return;
            }

            if (qty == rangeQty) return;

            Log.d("TimeSettingsView", "onItemSelected-Qty: " + qty);
            try {
                chart.setFilterTS(rangePeriod, qty, chart.getFilterTSOffset(), chart.getFilterTSPartitions());
            } catch (IllegalStateException e) {
                Log.d("TimeSettingsView", e.getMessage());
            }
            // Do not update UI, because it will be updated by the listener
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private final SVChartViewTSFiltered.TSFilteredListener chartFilterListener = new SVChartViewTSFiltered.TSFilteredListener() {
        @Override
        public void onFilterChanged(int period, int qty, int offset, int partitions) {
            int oldRangePeriod = rangePeriod;
            rangePeriod = period;
            rangeQty = qty;

            updateUI();

            if (oldRangePeriod != rangePeriod)
                setupQtyAdapter();
        }
    };

}