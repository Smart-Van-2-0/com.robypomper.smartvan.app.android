package com.robypomper.josp.jsl.android.components.charts;

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

import com.robypomper.josp.jsl.android.charts.R;
import com.robypomper.josp.jsl.android.components.charts.utils.TimeRangeLimits;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * TODO: document your custom view class.
 * <p>
 * TimeSettingsView
 * <= Time Range Unit       setRangeUnit(unit)
 * <= Time Range Qty        setRangeQty(qty)
 * <p>
 *
 * @noinspection unused
 */
public class TimeSettingsView extends LinearLayout {

    // Constants

    private final static int LAYOUT = R.layout.view_time_settings;
    private static final int LAYOUT_ITEMS = R.layout.view_time_settings_item;


    // Internal vars

    private int rangeUnit = Calendar.HOUR_OF_DAY;
    private int rangeQty = 1;
    private int textAppearanceTxt = 0;   //  com.google.android.material.R.attr.textAppearanceButton
    private int textAppearanceFlds = 0;   //  com.google.android.material.R.attr.textAppearanceButton

    private final LinearLayout baseLayout;
    private final TextView txtLabel;
    private final Spinner spnUnit;
    private final Spinner spnQty;


    // Constructors

    public TimeSettingsView(Context context) throws ParseException {
        this(context, null, 0);
    }

    public TimeSettingsView(Context context, AttributeSet attrs) throws ParseException {
        this(context, attrs, 0);
    }

    public TimeSettingsView(Context context, AttributeSet attrs, int defStyle) throws ParseException {
        super(new ContextThemeWrapper(context, defStyle), attrs, defStyle);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TimeSettingsView, defStyle, 0);
        rangeUnit = a.getInt(R.styleable.TimeSettingsView_settings_range_unit, rangeUnit);
        rangeQty = a.getInt(R.styleable.TimeSettingsView_settings_range_qty, rangeQty);
        textAppearanceFlds = a.getResourceId(R.styleable.TimeSettingsView_settings_text_appearance_flds, textAppearanceFlds);
        textAppearanceTxt = a.getResourceId(R.styleable.TimeSettingsView_settings_text_appearance_txt, textAppearanceTxt);
        a.recycle();

        // Inflate ui
        LayoutInflater.from(context).inflate(LAYOUT, this, true);

        // Get ui components
        baseLayout = findViewById(R.id.baseLayout);
        txtLabel = findViewById(R.id.txtLabel);
        updateTxtTextAppearance();
        spnUnit = findViewById(R.id.spnUnit);
        spnQty = findViewById(R.id.spnQty);
        updateFldsTextAppearance();

        // set unit selection listener
        spnUnit.setOnItemSelectedListener(onUnitItemClick);
        spnQty.setOnItemSelectedListener(onQtyItemClick);
        setupUnitAdapter();
        setupQtyAdapter();

        // Update UI with initial values
        updateUI();
    }

    private void setupUnitAdapter() {
        // set unit adapter
        List<String> unitsList = TimeRangeLimits.getTimeRangeUnitsStr();
        ArrayAdapter<String> unitListAdapter = new ArrayAdapter<>(getContext(), LAYOUT_ITEMS, R.id.txtLabel, unitsList);
        spnUnit.setAdapter(unitListAdapter);
    }

    private void setupQtyAdapter() {
        // set rangeQty adapter depending on rangePeriod
        List<Integer> qtys = TimeRangeLimits.getTimeRangeQtys(rangeUnit);
        ArrayAdapter<Integer> qtyListAdapter = new ArrayAdapter<>(getContext(), LAYOUT_ITEMS, R.id.txtLabel, qtys);
        spnQty.setAdapter(qtyListAdapter);
    }


    // Getters/Setters

    public int getRangeUnit() {
        return rangeUnit;
    }

    public void setRangeUnit(int rangeUnit) {
        if (rangeUnit == this.rangeUnit) return;

        int oldRangeUnit = this.rangeUnit;
        this.rangeUnit = rangeUnit;

        // from unit to idx
        int idxUnit = TimeRangeLimits.getTimeRangeUnitIdx(rangeUnit);
        spnUnit.setSelection(idxUnit);

        setupQtyAdapter();

        Log.v("TimeSettingsView", "Updated TimeRange Unit: " + oldRangeUnit + " => " + rangeUnit);
    }

    public int getRangeQty() {
        return rangeQty;
    }

    public void setRangeQty(int rangeQty) {
        if (rangeQty == this.rangeQty) return;

        int oldRangQty = this.rangeQty;
        this.rangeQty = rangeQty;

        // from rangeQty/rangePeriod to idx
        int idxQty = TimeRangeLimits.getTimeRangeQtys(rangeUnit).indexOf(rangeQty);
        spnQty.setSelection(idxQty);

        Log.v("TimeSettingsView", "Updated TimeRange Qty: " + oldRangQty + " => " + rangeQty);
    }

    public void setFldsTextAppearance(@StyleRes int textAppearanceFlds) {
        this.textAppearanceFlds = textAppearanceFlds;
        updateFldsTextAppearance();
    }

    public void setTxtTextAppearance(@StyleRes int textAppearanceTxt) {
        this.textAppearanceTxt = textAppearanceTxt;
        updateTxtTextAppearance();
    }


    // UI

    private void updateUI() {
        // from unit to idx
        //int idxUnit = new ArrayList<>(MPAndroidChartUtils.TIME_RANGE_MAP.keySet()).indexOf(rangeUnit);
        int idxUnit = TimeRangeLimits.getTimeRangeUnitIdx(rangeUnit);
        spnUnit.setSelection(idxUnit);

        // from rangeQty/rangePeriod to idx
        int idxQty = TimeRangeLimits.getTimeRangeQtys(rangeUnit).indexOf(rangeQty);
        spnQty.setSelection(idxQty);
    }

    private void updateFldsTextAppearance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //spnUnit.setTextAppearance(textAppearanceBtn);
            //spnQty.setTextAppearance(textAppearanceBtn);
        }
    }

    private void updateTxtTextAppearance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            txtLabel.setTextAppearance(textAppearanceTxt);
        }
    }

    private final AdapterView.OnItemSelectedListener onUnitItemClick = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int unit = TimeRangeLimits.getTimeRangeUnits().get(position);
            if (unit == -1) {
                Log.w("TimeSettingsView", "Invalid unit selected, got " + unit);
                return;
            }

            if (unit == rangeUnit) return;

            Log.e("TimeSettingsView", "onItemSelected-Unit: " + unit);
            int oldRangeUnit = rangeUnit;
            setRangeUnit(unit);
            emitOnUnitChanged(rangeUnit, oldRangeUnit);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private final AdapterView.OnItemSelectedListener onQtyItemClick = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int qty = (int) parent.getItemAtPosition(position);   // short form
            //int qty = getTimeRangeQtys(rangePeriod).get(position);    // long form
            if (qty == -1) {
                Log.w("TimeSettingsView", "Invalid quantity selected, got " + qty);
                return;
            }

            if (qty == rangeQty) return;

            Log.e("TimeSettingsView", "onItemSelected-Qty: " + qty);
            int oldRangeQty = rangeQty;
            setRangeQty(qty);
            emitOnQtyChanged(rangeQty, oldRangeQty);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    // Offset listeners

    private final List<UnitListener> listenersUnit = new ArrayList<>();
    private final List<QtyListener> listenersQty = new ArrayList<>();

    public interface UnitListener {

        void onUnitChanged(int newUnit, int oldUnit);

    }

    public interface QtyListener {

        void onQtyChanged(int newQty, int oldQty);

    }

    public void addUnitListener(UnitListener listener) {
        listenersUnit.add(listener);
    }

    public void removeUnitListener(UnitListener listener) {
        listenersUnit.remove(listener);
    }


    public void addQtyListener(QtyListener listener) {
        listenersQty.add(listener);
    }

    public void removeQtyListener(QtyListener listener) {
        listenersQty.remove(listener);
    }

    private void emitOnUnitChanged(int newUnit, int oldUnit) {
        for (UnitListener l : listenersUnit)
            try {
                l.onUnitChanged(newUnit, oldUnit);
            } catch (Throwable t) {
                Log.w("TimeSettingsView", "Error executing unit listener", t);
            }
    }

    private void emitOnQtyChanged(int newQty, int oldQty) {
        for (QtyListener l : listenersQty)
            try {
                l.onQtyChanged(newQty, oldQty);
            } catch (Throwable t) {
                Log.w("TimeSettingsView", "Error executing qty listener", t);
            }
    }

}