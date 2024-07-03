package com.robypomper.smartvan.smart_van.android.components;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.robypomper.smartvan.smart_van.android.R;

public class SVOverlayView extends LinearLayout {

    // Constants

    private final static int LAYOUT = R.layout.view_sv_charts_overlay;


    // Internal vars

    private SVChartView chart = null;
    private String msgTitle = "N/A";
    private String msgDescription = "N/A";
    private int textAppearanceTxt = 0;   //  com.google.android.material.R.attr.textAppearanceButton

    private final TextView txtTitle;
    private final TextView txtDescription;


    // Constructors

    public SVOverlayView(Context context) {
        this(context, null);
    }

    public SVOverlayView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SVOverlayView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SVOverlayView, defStyle, 0);
        textAppearanceTxt = a.getResourceId(R.styleable.SVOverlayView_overlay_text_appearance_txt, textAppearanceTxt);
        a.recycle();

        // Inflate ui
        LayoutInflater.from(context).inflate(LAYOUT, this, true);

        // Get ui components
        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        updateTxtTextAppearance();

        // Update UI with initial values
        updateUI();
    }


    // Getters and setters

    public void setChart(SVChartView chartView) {
        if (chart != null)
            chart.removeDataListener(chartDataListener);

        this.chart = chartView;

        if (chart != null)
            chart.addDataListener(chartDataListener);
        // TODO update current View status according to chart status
    }

    public SVChartView getChart() {
        return chart;
    }

    public void setMsgs(String msgTitle, String msgDescription) {
        this.msgTitle = msgTitle;
        this.msgDescription = msgDescription;
        updateUI();
    }


    // UI

    private void updateUI() {
        if (chart == null) return;

        // runOnMainYhread
        ((Activity)getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtTitle.setText(msgTitle);
                txtDescription.setText(msgDescription);
            }
        });
    }

    private void updateTxtTextAppearance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            txtTitle.setTextAppearance(textAppearanceTxt);
            txtDescription.setTextAppearance(textAppearanceTxt);
        }
    }

    public void show(boolean show) {
        ((Activity)getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setVisibility(show ? VISIBLE : GONE);
            }
        });
    }


    // Listeners

    private final SVChartView.DataListener chartDataListener = new SVChartView.DataListener() {

        @Override
        public void onFetchStarted() {
            assert chart.isFetching();
            if (chart instanceof SVChartViewExportable) {
                SVChartViewExportable chartExportable = (SVChartViewExportable) chart;
                assert chartExportable.getDataSetsRaw().isEmpty();
                assert chartExportable.getDataSetsProcessed().isEmpty();
                assert chartExportable.getDataSetsDisplayed().isEmpty();
            }

            setMsgs("Fetching data...", "1/3 Please wait...");
            show(true);
        }

        @Override
        public void onProcessingStarted() {
            assert chart.isFetching();
            if (chart instanceof SVChartViewExportable) {
                SVChartViewExportable chartExportable = (SVChartViewExportable) chart;
                assert !chartExportable.getDataSetsRaw().isEmpty();
                assert chartExportable.getDataSetsProcessed().isEmpty();
                assert chartExportable.getDataSetsDisplayed().isEmpty();
            }

            setMsgs("Processing data...", "2/3 Please wait...");
            show(true);
        }

        @Override
        public void onDisplayingStarted() {
            assert chart.isFetching();
            if (chart instanceof SVChartViewExportable) {
                SVChartViewExportable chartExportable = (SVChartViewExportable) chart;
                assert !chartExportable.getDataSetsRaw().isEmpty();
                assert !chartExportable.getDataSetsProcessed().isEmpty();
                assert chartExportable.getDataSetsDisplayed().isEmpty();
            }

            setMsgs("Displaying data...", "3/3 Please wait...");
            show(true);
        }

        @Override
        public void onFetchedTerminated() {
            assert !chart.isFetching();
            if (chart instanceof SVChartViewExportable) {
                SVChartViewExportable chartExportable = (SVChartViewExportable) chart;
                assert !chartExportable.getDataSetsRaw().isEmpty();
                assert !chartExportable.getDataSetsProcessed().isEmpty();
                assert !chartExportable.getDataSetsDisplayed().isEmpty();
            }

            show(false);
        }

    };
}
