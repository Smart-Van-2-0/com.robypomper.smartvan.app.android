package com.robypomper.smartvan.smart_van.android.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StyleRes;

import com.robypomper.smartvan.smart_van.android.R;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * TODO: document your custom view class.
 * TODO: move UI binding and setup into onCreateView() method
 * TODO: handle ParseException in constructors and remove constructor's throws
 * <p>
 * TimeRangeView
 * => Reference Date        setReferenceDate(date)
 * => Time Range Period     setRangePeriod(period)
 * => Time Range Qty        setRangeQty(qty)
 * <= Time Range Offset     TimeNavigatorView.OffsetObserver.onOffsetChanged(offset)
 * <p>
 * TODO disable `next` btn when offset is zero.
 * @noinspection unused
 */
public class SVTimeNavigatorView extends LinearLayout {

    // Constants

    private final static int LAYOUT = R.layout.view_sv_charts_time_navigator;


    // Internal vars

    private SVChartViewTSFiltered chart = null;
    private int rangeOffset = SVChartViewTSFiltered.DEFAULT_TIME_RANGE_OFFSET;
    private int textAppearanceTxt = 0;   //  com.google.android.material.R.attr.textAppearanceButton
    private int textAppearanceBtn = 0;   //  com.google.android.material.R.attr.textAppearanceButton

    private final Button btnTimeRangeNavPrev;
    private final Button btnTimeRangeNavNext;
    private final TextView txtTimeRangeStart;
    private final TextView txtTimeRangeEnd;
    private final TextView txtTimeRangeSeparator;


    // Constructors

    public SVTimeNavigatorView(Context context) {
        this(context, null, 0);
    }

    public SVTimeNavigatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SVTimeNavigatorView(Context context, AttributeSet attrs, int defStyle) {
        super(new ContextThemeWrapper(context, defStyle), attrs, defStyle);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SVTimeNavigatorView, defStyle, 0);
        String referenceDateStr = a.getString(R.styleable.SVTimeNavigatorView_navigator_reference_date);
        rangeOffset = a.getInt(R.styleable.SVTimeNavigatorView_navigator_range_offset, rangeOffset);
        textAppearanceBtn = a.getResourceId(R.styleable.SVTimeNavigatorView_navigator_text_appearance_btn, textAppearanceBtn);
        textAppearanceTxt = a.getResourceId(R.styleable.SVTimeNavigatorView_navigator_text_appearance_txt, textAppearanceTxt);
        a.recycle();

        // Inflate ui
        LayoutInflater.from(context).inflate(LAYOUT, this, true);

        // Get ui components
        btnTimeRangeNavPrev = findViewById(R.id.btnTimeRangeNav_Prev);
        btnTimeRangeNavNext = findViewById(R.id.btnTimeRangeNav_Next);
        updateBtnTextAppearance();
        btnTimeRangeNavPrev.setOnClickListener(onTimeRangeNavClick);
        btnTimeRangeNavNext.setOnClickListener(onTimeRangeNavClick);

        txtTimeRangeStart = findViewById(R.id.txtTimeRangeStart);
        txtTimeRangeEnd = findViewById(R.id.txtTimeRangeEnd);
        txtTimeRangeSeparator = findViewById(R.id.txtTimeRangeSeparator);
        updateTxtTextAppearance();

        // Update UI with initial values
        updateUI();
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

    public void setBtnTextAppearance(@StyleRes int textAppearanceBtn) {
        this.textAppearanceBtn = textAppearanceBtn;
        updateBtnTextAppearance();
    }

    public void setTxtTextAppearance(@StyleRes int textAppearanceTxt) {
        this.textAppearanceTxt = textAppearanceTxt;
        updateTxtTextAppearance();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        btnTimeRangeNavPrev.setEnabled(enabled);
        btnTimeRangeNavNext.setEnabled(enabled);
        txtTimeRangeStart.setEnabled(enabled);
        txtTimeRangeEnd.setEnabled(enabled);
        txtTimeRangeSeparator.setEnabled(enabled);
    }


    // UI

    private void updateUI() {
        if (chart == null) return;

        int rangePeriod = chart.getFilterTSPeriod();
        int rangeQty = chart.getFilterTSQty();

        Date fromDate = SVChartViewTSFiltered.calculateFromDate(rangePeriod, rangeQty, rangeOffset);
        Date toDate = SVChartViewTSFiltered.calculateToDate(rangePeriod, rangeQty, rangeOffset);

        SimpleDateFormat dateFormatter = SVChartViewTSFiltered.periodFormatterNavigator(rangePeriod, rangeQty);
        txtTimeRangeStart.setText(dateFormatter.format(fromDate).replace(" ", "\n"));
        txtTimeRangeEnd.setText(dateFormatter.format(toDate).replace(" ", "\n"));

        btnTimeRangeNavNext.setEnabled(rangeOffset < 0);
    }

    private void updateBtnTextAppearance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btnTimeRangeNavPrev.setTextAppearance(textAppearanceBtn);
            btnTimeRangeNavNext.setTextAppearance(textAppearanceBtn);
        }
    }

    private void updateTxtTextAppearance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            txtTimeRangeStart.setTextAppearance(textAppearanceTxt);
            txtTimeRangeEnd.setTextAppearance(textAppearanceTxt);
            txtTimeRangeSeparator.setTextAppearance(textAppearanceTxt);
        }
    }

    /** @noinspection FieldCanBeLocal*/
    private final OnClickListener onTimeRangeNavClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int newRangeOffset;
            if (v == btnTimeRangeNavPrev)
                newRangeOffset = rangeOffset - 1;
            else if (v == btnTimeRangeNavNext)
                newRangeOffset = rangeOffset + 1;
            else return;

            try {
                chart.setFilterTS(chart.getFilterTSPeriod(), chart.getFilterTSQty(), newRangeOffset, chart.getFilterTSPartitions());
            } catch (IllegalStateException e) {
                Log.d("TimeSettingsView", e.getMessage());
            }
            // Do not update UI, because it will be updated by the listener
        }
    };

    private final SVChartViewTSFiltered.TSFilteredListener chartFilterListener = new SVChartViewTSFiltered.TSFilteredListener() {
        @Override
        public void onFilterChanged(int period, int qty, int offset, int partitions) {
            rangeOffset = offset;
            updateUI();
        }
    };


    // Static utils

    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat newSimpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

}