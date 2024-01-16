package com.robypomper.josp.jsl.android.components.charts;

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
import com.robypomper.josp.jsl.android.charts.R;
import com.robypomper.josp.jsl.android.components.charts.utils.TimeRangeLimits;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


/**
 * The fragment used to show the time settings bottom sheet by {@link ChartBaseView}.
 *
 * @noinspection unused
 */
public class TimeSettingsBottomSheet extends BottomSheetDialogFragment {

    // Constants

    public static final String TAG = TimeSettingsBottomSheet.class.getSimpleName();
    private final static int LAYOUT = R.layout.bottom_sheet_time_settings_large;


    // Internal vars

    private int rangePeriod = Calendar.HOUR_OF_DAY;
    private int rangeQty = 1;
    /** Original rangePeriod, set only via {@link #setRangePeriod(int)}. */
    private int rangePeriodOld = Calendar.HOUR_OF_DAY;
    /** Original rangeQty, set only via {@link #setRangeQty(int)}. */
    private int rangeQtyOld = 1;
    private int textAppearanceTitle = 0;   //  com.google.android.material.R.attr.textAppearanceHeadline6
    private int textAppearanceTxt = 0;   //  TODO update value => com.google.android.material.R.attr.textAppearanceHeadline6
    private int textAppearanceTgl = 0;   //  N/A (style: ?attr/materialButtonOutlinedStyle)
    private int textAppearanceBtn = 0;   //  N/A (style: ?android:attr/buttonBarButtonStyle)

    private TextView txtPeriodTitle;
    private TextView txtQtyTitle;
    private MaterialButtonToggleGroup tglPeriod;
    private MaterialButtonToggleGroup tglQty;
    private Button btnCancel;
    private Button btnApply;


    // Constructors

    public TimeSettingsBottomSheet(Context context) {
        this(context, null, 0);
    }

    public TimeSettingsBottomSheet(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeSettingsBottomSheet(Context context, AttributeSet attrs, int defStyle) {
        super();

        // Parse attributes
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimeSettingsBottomSheet, defStyle, 0);
        textAppearanceTitle = a.getResourceId(R.styleable.TimeSettingsBottomSheet_settings_bottom_text_appearance_title, textAppearanceTitle);
        textAppearanceTxt = a.getResourceId(R.styleable.TimeSettingsBottomSheet_settings_bottom_text_appearance_txt, textAppearanceTxt);
        textAppearanceTgl = a.getResourceId(R.styleable.TimeSettingsBottomSheet_settings_bottom_text_appearance_tgl, textAppearanceTgl);
        textAppearanceBtn = a.getResourceId(R.styleable.TimeSettingsBottomSheet_settings_bottom_text_appearance_btn, textAppearanceBtn);
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
        txtQtyTitle = v.findViewById(R.id.txtQtyTitle);

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

    public int getRangePeriod() {
        return rangePeriod;
    }

    public void setRangePeriod(int rangePeriod) {
        if (rangePeriod == this.rangePeriod) return;

        int oldRangePeriod = this.rangePeriod;
        this.rangePeriod = rangePeriod;
        this.rangePeriodOld = rangePeriod;

        if (tglPeriod != null)
            tglPeriod.check(getTimeRangePeriodButton(rangePeriod, tglPeriod));
        if (tglQty != null)
            updateQtyButtonsList(tglQty, rangePeriod, getContext(), textAppearanceTgl);

        Log.v("TimeSettingsBottomSheet", "Updated TimeRange Period: " + oldRangePeriod + " => " + rangePeriod);
    }

    public int getRangeQty() {
        return rangeQty;
    }

    public void setRangeQty(int rangeQty) {
        if (rangeQty == this.rangeQty) return;

        int oldRangQty = this.rangeQty;
        this.rangeQty = rangeQty;
        this.rangeQtyOld = rangeQty;

        if (tglQty != null)
            tglQty.check(getTimeRangeQtyButton(rangeQty, tglQty, rangePeriod));

        Log.v("TimeSettingsBottomSheet", "Updated TimeRange Qty: " + oldRangQty + " => " + rangeQty);
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
        List<Integer> periods = TimeRangeLimits.getTimeRangeUnits();
        Collections.reverse(periods);
        tglPeriod.removeAllViews();

        MaterialButtonToggleGroup.LayoutParams params = new MaterialButtonToggleGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 0);
        for (int period : periods) {
            MaterialButton btn = new MaterialButton(ctx, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
            btn.setText(TimeRangeLimits.getTimeRangeStr(period));
            btn.setLayoutParams(params);
            tglPeriod.addView(btn);
        }
    }

    @SuppressLint("DefaultLocale")
    private static void updateQtyButtonsList(MaterialButtonToggleGroup tglQty, int rangePeriod, Context ctx, int textAppearanceTgl) {
        List<Integer> qtys = TimeRangeLimits.getTimeRangeQtys(rangePeriod);
        tglQty.removeAllViews();

        MaterialButtonToggleGroup.LayoutParams params = new MaterialButtonToggleGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 0);
        for (int qty : qtys) {
            MaterialButton btn = new MaterialButton(ctx, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
            btn.setText(String.format("%d %s", qty, TimeRangeLimits.getTimeRangeStr(rangePeriod)));
            btn.setLayoutParams(params);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                btn.setTextAppearance(textAppearanceTgl);
            tglQty.addView(btn);
        }
    }

    private static @IdRes int getTimeRangePeriodButton(int period, MaterialButtonToggleGroup tglPeriod) {
        int btnIdx = TimeRangeLimits.getTimeRangeUnitIdx(period);
        btnIdx = tglPeriod.getChildCount() - btnIdx - 1;
        return tglPeriod.getChildAt(btnIdx).getId();
    }

    private static @IdRes int getTimeRangeQtyButton(int qty, MaterialButtonToggleGroup tglQty, int rangePeriod) {
        int btnIdx = TimeRangeLimits.getTimeRangeQtyIdx(rangePeriod, qty);
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
            // txtMessage.setTextAppearance(textAppearanceTxt);
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
            rangePeriod = TimeRangeLimits.getTimeRangeUnits().get(pos);

            // update qtys
            updateQtyButtonsList(tglQty, rangePeriod, getContext(), textAppearanceTgl);
            rangeQty = TimeRangeLimits.getTimeRangeQtys(rangePeriod).get(0);
            tglQty.check(getTimeRangeQtyButton(rangeQty, tglQty, rangePeriod));
        }
    };

    private final MaterialButtonToggleGroup.OnButtonCheckedListener onTglQtyCheckedListener = new MaterialButtonToggleGroup.OnButtonCheckedListener() {
        @Override
        public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
            if (!isChecked) return;

            // set rangeQty
            int pos = tglQty.indexOfChild(tglQty.findViewById(checkedId));
            rangeQty = TimeRangeLimits.getTimeRangeQtys(rangePeriod).get(pos);
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
            emitOnPeriodChanged(rangePeriod, rangePeriodOld);
            emitOnQtyChanged(rangeQty, rangeQtyOld);
            dismiss();
        }
    };


    // Offset listeners

    private final List<PeriodListener> listenersPeriod = new ArrayList<>();
    private final List<QtyListener> listenersQty = new ArrayList<>();

    public interface PeriodListener {

        void onPeriodChanged(int newPeriod, int oldPeriod);

    }

    public interface QtyListener {

        void onQtyChanged(int newQty, int oldQty);

    }

    public void addPeriodListener(PeriodListener listener) {
        listenersPeriod.add(listener);
    }

    public void removePeriodListener(PeriodListener listener) {
        listenersPeriod.remove(listener);
    }

    private void emitOnPeriodChanged(int newPeriod, int oldPeriod) {
        for (PeriodListener l : listenersPeriod)
            try {
                l.onPeriodChanged(newPeriod, oldPeriod);
            } catch (Throwable t) {
                Log.w("TimeSettingsBottomSheet", "Error executing period listener", t);
            }
    }

    public void addQtyListener(QtyListener listener) {
        listenersQty.add(listener);
    }

    public void removeQtyListener(QtyListener listener) {
        listenersQty.remove(listener);
    }

    private void emitOnQtyChanged(int newQty, int oldQty) {
        for (QtyListener l : listenersQty)
            try {
                l.onQtyChanged(newQty, oldQty);
            } catch (Throwable t) {
                Log.w("TimeSettingsBottomSheet", "Error executing qty listener", t);
            }
    }

}
