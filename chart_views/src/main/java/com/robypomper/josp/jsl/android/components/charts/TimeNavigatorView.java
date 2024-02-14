package com.robypomper.josp.jsl.android.components.charts;

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

import com.robypomper.java.JavaDate;
import com.robypomper.josp.jsl.android.charts.R;
import com.robypomper.josp.jsl.android.components.charts.utils.TimeRangeLimits;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * TODO: document your custom view class.
 * TODO: move UI binding and setup into onCreateView() method
 * TODO: handle ParseExeption in constructors and remove constructor's throws
 * TODO: rename Unit to Period
 * <p>
 * TimeRangeView
 * => Reference Date        setReferenceDate(date)
 * => Time Range Unit       setRangeUnit(unit)
 * => Time Range Qty        setRangeQty(qty)
 * <= Time Range Offset     TimeNavigatorView.OffsetObserver.onOffsetChanged(offset)
 * <p>
 * TODO disable `next` btn when offset is zero.
 * @noinspection unused
 */
public class TimeNavigatorView extends LinearLayout {

    // Constants

    private final static int LAYOUT = R.layout.view_time_navigator;


    // Internal vars

    private Date referenceDate = null;
    private int rangeUnit = Calendar.HOUR_OF_DAY;
    private int rangeQty = 1;
    private int rangeOffset = 0;
    private String dateTimeFormat = getResources().getString(R.string.view_time_navigator_date_format);
    private SimpleDateFormat dateFormatter;
    private int textAppearanceTxt = 0;   //  com.google.android.material.R.attr.textAppearanceButton
    private int textAppearanceBtn = 0;   //  com.google.android.material.R.attr.textAppearanceButton

    private final LinearLayout baseLayout;
    private final Button btnTimeRangeNavPrev;
    private final Button btnTimeRangeNavNext;
    private final TextView txtTimeRangeStart;
    private final TextView txtTimeRangeEnd;
    private final TextView txtTimeRangeSeparator;


    // Constructors

    public TimeNavigatorView(Context context) throws ParseException {
        this(context, null, 0);
    }

    public TimeNavigatorView(Context context, AttributeSet attrs) throws ParseException {
        this(context, attrs, 0);
    }

    public TimeNavigatorView(Context context, AttributeSet attrs, int defStyle) throws ParseException {
        super(new ContextThemeWrapper(context, defStyle), attrs, defStyle);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TimeNavigatorView, defStyle, 0);
        String referenceDateStr = a.getString(R.styleable.TimeNavigatorView_navigator_reference_date);
        rangeUnit = a.getInt(R.styleable.TimeNavigatorView_navigator_range_unit, rangeUnit);
        rangeQty = a.getInt(R.styleable.TimeNavigatorView_navigator_range_qty, rangeQty);
        rangeOffset = a.getInt(R.styleable.TimeNavigatorView_navigator_range_offset, rangeOffset);
        if (a.hasValue(R.styleable.TimeNavigatorView_navigator_datetime_format))
            dateTimeFormat = a.getString(R.styleable.TimeNavigatorView_navigator_datetime_format);
        textAppearanceBtn = a.getResourceId(R.styleable.TimeNavigatorView_navigator_text_appearance_btn, textAppearanceBtn);
        textAppearanceTxt = a.getResourceId(R.styleable.TimeNavigatorView_navigator_text_appearance_txt, textAppearanceTxt);
        a.recycle();

        // Adjust attributes
        dateFormatter = newSimpleDateFormat(dateTimeFormat);
        if (referenceDateStr != null && !referenceDateStr.isEmpty())
            referenceDate = dateFormatter.parse(referenceDateStr);

        // Inflate ui
        LayoutInflater.from(context).inflate(LAYOUT, this, true);

        // Get ui components
        baseLayout = findViewById(R.id.baseLayout);
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

    public Date getReferenceDate() {
        if (referenceDate == null)
            return JavaDate.getNowDate();
        return referenceDate;
    }

    public void setReferenceDate(Date referenceDate) {
        this.referenceDate = referenceDate;
        updateUI();
    }

    public int getRangeUnit() {
        return rangeUnit;
    }

    public void setRangeUnit(int rangeUnit) {
        this.rangeUnit = rangeUnit;
        updateUI();
    }

    public int getRangeQty() {
        return rangeQty;
    }

    public void setRangeQty(int rangeQty) {
        this.rangeQty = rangeQty;
        updateUI();
    }

    public int getRangeOffset() {
        return rangeOffset;
    }

    public void setRangeOffset(int rangeOffset) {
        int oldRangeOffset = this.rangeOffset;
        this.rangeOffset = rangeOffset;
        updateUI();
        Log.v("TimeNavigatorView", "Updated TimeRange Offset: " + oldRangeOffset + " => " + rangeOffset);
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
        dateFormatter = newSimpleDateFormat(dateTimeFormat);
        updateUI();
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
        TimeRangeLimits hl = TimeRangeLimits.calculateTimeRangeLimits(getReferenceDate(), rangeUnit, rangeOffset, rangeQty);
        txtTimeRangeStart.setText(dateFormatter.format(hl.getFromDate()));
        txtTimeRangeEnd.setText(dateFormatter.format(hl.getToDate()));
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

    private final OnClickListener onTimeRangeNavClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int newRangeOffset;
            if (v == btnTimeRangeNavPrev)
                newRangeOffset = rangeOffset - 1;
            else if (v == btnTimeRangeNavNext)
                newRangeOffset = rangeOffset + 1;
            else return;

            int oldRangeOffset = rangeOffset;
            setRangeOffset(newRangeOffset);
            emitOnOffsetChanged(rangeOffset, oldRangeOffset);
        }
    };


    // Offset listeners

    private final List<OffsetListener> listeners = new ArrayList<>();

    public interface OffsetListener {

        void onOffsetChanged(int newOffset, int oldOffset);

    }

    public void addOffsetListener(OffsetListener listener) {
        listeners.add(listener);
    }

    public void removeOffsetListener(OffsetListener listener) {
        listeners.remove(listener);
    }

    private void emitOnOffsetChanged(int newOffset, int oldOffset) {
        for (OffsetListener l : listeners)
            try {
                l.onOffsetChanged(newOffset, oldOffset);
            } catch (Throwable t) {
                Log.w("TimeNavigatorView", "Error executing offset listener", t);
            }
    }


    // Static utils

    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat newSimpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

}