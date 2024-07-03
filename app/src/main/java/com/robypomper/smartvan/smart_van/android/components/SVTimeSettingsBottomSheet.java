package com.robypomper.smartvan.smart_van.android.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.StyleRes;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.robypomper.smartvan.smart_van.android.R;

import java.util.Collections;
import java.util.List;


/**
 * The fragment used to show the time settings bottom sheet for {@link SVChartViewTSFiltered}.
 * <p>
 * The chart must implement the {@link SVChartViewTSFiltered} interface.
 *
 * @noinspection unused
 */
public class SVTimeSettingsBottomSheet extends BottomSheetDialogFragment {

    // Constants

    public static final String BTN_SHEET_TAG = SVTimeSettingsBottomSheet.class.getSimpleName();
    private final static int LAYOUT = R.layout.lay_charts_bottom_sheet_time_settings;


    // Internal vars

    private SVChartViewTSFiltered chart = null;
    private int rangePeriod = SVChartViewTSFiltered.DEFAULT_TIME_RANGE_PERIOD;
    private int rangeQty = SVChartViewTSFiltered.DEFAULT_TIME_RANGE_QTY;


    // View args

    private int textAppearanceTitle = 0;   //  com.google.android.material.R.attr.textAppearanceHeadline6
    private int textAppearanceTxt = 0;   //  com.google.android.material.R.attr.textAppearanceSubtitle1
    private int textAppearanceTgl = 0;   //  N/A (style: ?attr/materialButtonOutlinedStyle)
    private int textAppearanceBtn = 0;   //  N/A (style: ?android:attr/buttonBarButtonStyle)

    private TextView txtPeriodTitle;
    private TextView txtPeriodDescription;
    private TextView txtQtyTitle;
    private TextView txtQtyDescription;
    private MaterialButtonToggleGroup tglPeriod;
    private MaterialButtonToggleGroup tglQty;
    private Button btnCancel;
    private Button btnApply;
    private boolean isInternalUpdate = false;


    // Constructors

    public SVTimeSettingsBottomSheet(Context context) {
        this(context, null, 0);
    }

    public SVTimeSettingsBottomSheet(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SVTimeSettingsBottomSheet(Context context, AttributeSet attrs, int defStyle) {
        super();

        // Parse attributes
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SVTimeSettingsBottomSheet, defStyle, 0);
        textAppearanceTitle = a.getResourceId(R.styleable.SVTimeSettingsBottomSheet_settings_bottom_text_appearance_title, textAppearanceTitle);
        textAppearanceTxt = a.getResourceId(R.styleable.SVTimeSettingsBottomSheet_settings_bottom_text_appearance_txt, textAppearanceTxt);
        textAppearanceTgl = a.getResourceId(R.styleable.SVTimeSettingsBottomSheet_settings_bottom_text_appearance_tgl, textAppearanceTgl);
        textAppearanceBtn = a.getResourceId(R.styleable.SVTimeSettingsBottomSheet_settings_bottom_text_appearance_btn, textAppearanceBtn);
        a.recycle();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(LAYOUT, container, false);

        // Get UI components
        tglPeriod = v.findViewById(R.id.tglPeriod);
        tglQty = v.findViewById(R.id.tglQty);
        btnCancel = v.findViewById(R.id.btnCancel);
        btnApply = v.findViewById(R.id.btnApply);
        txtPeriodTitle = v.findViewById(R.id.txtPeriodTitle);
        txtPeriodDescription = v.findViewById(R.id.txtPeriodDescription);
        txtQtyTitle = v.findViewById(R.id.txtQtyTitle);
        txtQtyDescription = v.findViewById(R.id.txtQtyDescription);

        // setup periods buttons
        updatePeriodButtonsList(tglPeriod, getContext());
        tglPeriod.check(getTimeRangePeriodButton(rangePeriod, tglPeriod));
        tglPeriod.addOnButtonCheckedListener(onTglPeriodCheckedListener);

        // setup qtys buttons
        updateQtyButtonsList(tglQty, rangePeriod, getContext(), textAppearanceTgl);
        tglQty.check(getTimeRangeQtyButton(rangeQty, tglQty, rangePeriod));
        tglQty.addOnButtonCheckedListener(onTglQtyCheckedListener);

        // setup Apply|Cancel buttons
        btnCancel.setOnClickListener(onBtnCancelClickListener);
        btnApply.setOnClickListener(onBtnApplyClickListener);

        updateTitleTextAppearance();
        updateTxtTextAppearance();
        updateTglTextAppearance();
        updateBtnTextAppearance();

        return v;
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

    public void setTitleTextAppearance(@StyleRes int textAppearanceTitle) {
        this.textAppearanceTitle = textAppearanceTitle;
        updateTitleTextAppearance();
    }

    public void setTxtTextAppearance(@StyleRes int textAppearanceTxt) {
        this.textAppearanceTxt = textAppearanceTxt;
        updateTxtTextAppearance();
    }

    public void setToggleTextAppearance(@StyleRes int textAppearanceTgl) {
        this.textAppearanceTgl = textAppearanceTgl;
        updateTglTextAppearance();
    }

    public void setBtnTextAppearance(@StyleRes int textAppearanceBtn) {
        this.textAppearanceBtn = textAppearanceBtn;
        updateBtnTextAppearance();
    }


    // UI

    private static void updatePeriodButtonsList(MaterialButtonToggleGroup tglPeriod, Context ctx) {
        if (ctx == null) return;

        List<Integer> periods = SVChartViewTSFiltered.getTimeRangePeriods();
        Collections.reverse(periods);
        tglPeriod.removeAllViews();

        MaterialButtonToggleGroup.LayoutParams params = new MaterialButtonToggleGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 0);
        for (int period : periods) {
            MaterialButton btn = new MaterialButton(ctx, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
            btn.setText(SVChartViewTSFiltered.getTimeRangeStr(period));
            btn.setLayoutParams(params);
            tglPeriod.addView(btn);
        }
    }

    @SuppressLint("DefaultLocale")
    private static void updateQtyButtonsList(MaterialButtonToggleGroup tglQty, int rangePeriod, Context ctx, int textAppearanceTgl) {
        if (ctx == null) return;

        List<Integer> qtys = SVChartViewTSFiltered.getTimeRangeQtys(rangePeriod);
        tglQty.removeAllViews();

        MaterialButtonToggleGroup.LayoutParams params = new MaterialButtonToggleGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 0);
        for (int qty : qtys) {
            MaterialButton btn = new MaterialButton(ctx, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
            btn.setText(String.format("%d %s", qty, SVChartViewTSFiltered.getTimeRangeStr(rangePeriod)));
            btn.setLayoutParams(params);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                btn.setTextAppearance(textAppearanceTgl);
            tglQty.addView(btn);
        }
    }

    private static @IdRes int getTimeRangePeriodButton(int period, MaterialButtonToggleGroup tglPeriod) {
        int btnIdx = SVChartViewTSFiltered.getTimeRangePeriodIdx(period);
        btnIdx = tglPeriod.getChildCount() - btnIdx - 1;
        return tglPeriod.getChildAt(btnIdx).getId();
    }

    private static @IdRes int getTimeRangeQtyButton(int qty, MaterialButtonToggleGroup tglQty, int rangePeriod) {
        int btnIdx = SVChartViewTSFiltered.getTimeRangeQtyIdx(rangePeriod, qty);
        return tglQty.getChildAt(btnIdx).getId();
    }

    private void updateTitleTextAppearance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            txtPeriodTitle.setTextAppearance(textAppearanceTitle);
            txtQtyTitle.setTextAppearance(textAppearanceTitle);
        }
    }

    private void updateTxtTextAppearance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            txtPeriodDescription.setTextAppearance(textAppearanceTxt);
            txtQtyDescription.setTextAppearance(textAppearanceTxt);
        }
    }

    private void updateTglTextAppearance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < tglPeriod.getChildCount(); i++)
                ((MaterialButton) tglPeriod.getChildAt(i)).setTextAppearance(textAppearanceTgl);
            for (int i = 0; i < tglQty.getChildCount(); i++)
                ((MaterialButton) tglQty.getChildAt(i)).setTextAppearance(textAppearanceTgl);
        }
    }

    private void updateBtnTextAppearance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btnCancel.setTextAppearance(textAppearanceBtn);
            btnApply.setTextAppearance(textAppearanceBtn);
        }
    }

    private final MaterialButtonToggleGroup.OnButtonCheckedListener onTglPeriodCheckedListener = new MaterialButtonToggleGroup.OnButtonCheckedListener() {
        @Override
        public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
            if (!isChecked) return;

            // set rangePeriod
            int pos = tglPeriod.indexOfChild(tglPeriod.findViewById(checkedId));
            pos = tglPeriod.getChildCount() - pos - 1;
            rangePeriod = SVChartViewTSFiltered.getTimeRangePeriods().get(pos);

            // update qtys
            updateQtyButtonsList(tglQty, rangePeriod, getContext(), textAppearanceTgl);
            rangeQty = SVChartViewTSFiltered.getTimeRangeQtys(rangePeriod).get(0);
            tglQty.check(getTimeRangeQtyButton(rangeQty, tglQty, rangePeriod));
        }
    };

    private final MaterialButtonToggleGroup.OnButtonCheckedListener onTglQtyCheckedListener = new MaterialButtonToggleGroup.OnButtonCheckedListener() {
        @Override
        public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
            if (!isChecked) return;

            // set rangeQty
            int pos = tglQty.indexOfChild(tglQty.findViewById(checkedId));
            rangeQty = SVChartViewTSFiltered.getTimeRangeQtys(rangePeriod).get(pos);
        }
    };

    private final View.OnClickListener onBtnCancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    private final View.OnClickListener onBtnApplyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isInternalUpdate = true;
            try {
                chart.setFilterTS(rangePeriod, rangeQty, chart.getFilterTSOffset(), chart.getFilterTSPartitions());
            } catch (IllegalStateException e) {
                Log.d("TimeSettingsBottomSheet", e.getMessage());
            }
            dismiss();
        }
    };

    private final SVChartViewTSFiltered.TSFilteredListener chartFilterListener = new SVChartViewTSFiltered.TSFilteredListener() {
        @Override
        public void onFilterChanged(int period, int qty, int offset, int partitions) {
            if (isInternalUpdate) { isInternalUpdate = false; return; }
            int rangePeriodOld = rangePeriod;
            rangePeriod = period;
            rangeQty = qty;

            if (tglPeriod == null || tglQty == null) return;

            tglPeriod.check(getTimeRangePeriodButton(period, tglPeriod));
            tglQty.check(getTimeRangeQtyButton(qty, tglQty, period));

            if (rangePeriodOld != rangePeriod)
                updateQtyButtonsList(tglQty, rangePeriod, getContext(), textAppearanceTgl);
        }
    };

}
