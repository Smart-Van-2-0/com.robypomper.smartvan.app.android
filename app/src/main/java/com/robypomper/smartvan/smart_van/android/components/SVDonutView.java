package com.robypomper.smartvan.smart_van.android.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robypomper.smartvan.smart_van.android.R;

import app.futured.donut.DonutProgressView;


public class SVDonutView extends LinearLayout {

    // Constants

    private final static int LAYOUT = R.layout.view_sv_donut;
    private final static String DEF_SECTION_NAME = "UniqueSectionName";


    // Internal vars

    private float value;
    private String valueStr = null;
    private int valueAppearance = 1;
    private float valueMax = 100;
    private String unit = null;
    private int unitAppearance = 1;
    private String label = null;
    private int labelAppearance = 1;
    private float txtVerticalOffset = 0;
    private int color = Color.rgb(0, 255, 0);


    // UI Components

    private DonutProgressView donutValue;
    private LinearLayout layFront;
    private TextView txtValue;
    private TextView txtUnit;
    private TextView txtLabel;


    // Constructors

    public SVDonutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SVDonutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }


    // UI Methods

    @SuppressLint("DefaultLocale")
    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Parse attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SVDonutView, defStyle, 0);

        if (a.hasValue(R.styleable.SVDonutView_sv_donut_value)) {
            try {
                value = a.getFloat(R.styleable.SVDonutView_sv_donut_value, Integer.MIN_VALUE);
            } catch (NumberFormatException ignore) { value = Integer.MIN_VALUE; }
            if (value != Integer.MIN_VALUE) {
                valueStr = String.format("%.0f", value);
            } else {
                try {
                    value = a.getFloat(R.styleable.SVDonutView_sv_donut_value, Float.MIN_VALUE);
                } catch (NumberFormatException ignore) { value = Float.MIN_VALUE; }
                if (value != Float.MIN_VALUE) {
                    valueStr = String.format("%.2f", value);
                }
                else value = 0;
            }
        }
        if (a.hasValue(R.styleable.SVDonutView_sv_donut_value_max)) {
            try {
                valueMax = a.getInt(R.styleable.SVDonutView_sv_donut_value_max, Integer.MIN_VALUE);
            } catch (NumberFormatException ignore) { value = Integer.MIN_VALUE; }
            if (valueMax == Integer.MIN_VALUE) {
                try {
                    valueMax = a.getFloat(R.styleable.SVDonutView_sv_donut_value_max, Float.MIN_VALUE);
                } catch (NumberFormatException ignore) { value = Float.MIN_VALUE; }
                if (valueMax == Float.MIN_VALUE)
                    valueMax = 0;
            }
        }
        if (a.hasValue(R.styleable.SVDonutView_sv_donut_value_appearance))
            valueAppearance = a.getResourceId(R.styleable.SVDonutView_sv_donut_value_appearance, valueAppearance);
        if (a.hasValue(R.styleable.SVDonutView_sv_donut_value))
            unit = a.getString(R.styleable.SVDonutView_sv_donut_unit);
        if (a.hasValue(R.styleable.SVDonutView_sv_donut_unit_appearance))
            unitAppearance = a.getResourceId(R.styleable.SVDonutView_sv_donut_unit_appearance, unitAppearance);
        if (a.hasValue(R.styleable.SVDonutView_sv_donut_label))
            label = a.getString(R.styleable.SVDonutView_sv_donut_label);
        if (a.hasValue(R.styleable.SVDonutView_sv_donut_label_appearance))
            labelAppearance = a.getResourceId(R.styleable.SVDonutView_sv_donut_label_appearance, labelAppearance);
        if (a.hasValue(R.styleable.SVDonutView_sv_donut_text_vertical_offset))
            txtVerticalOffset = a.getDimension(R.styleable.SVDonutView_sv_donut_text_vertical_offset, txtVerticalOffset);
        if (a.hasValue(R.styleable.SVDonutView_sv_donut_color))
            color = a.getColor(R.styleable.SVDonutView_sv_donut_color, color);
        a.recycle();

        // Inflate ui
        LayoutInflater.from(context).inflate(LAYOUT, this, true);

        // Get UI comps
        donutValue = findViewById(R.id.donutValue);
        layFront = findViewById(R.id.layFront);
        txtValue = findViewById(R.id.txtValue);
        txtUnit = findViewById(R.id.txtUnit);
        txtLabel = findViewById(R.id.txtLabel);

        // Update UI
        updateUI();
    }

    private void updateUI() {
        // Update texts
        if (valueStr != null) txtValue.setText(valueStr);
        if (unit != null) txtUnit.setText(unit);
        if (label != null) txtLabel.setText(label);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (valueAppearance != -1)  txtValue.setTextAppearance(valueAppearance);
            if (unitAppearance != -1) txtUnit.setTextAppearance(unitAppearance);
            if (labelAppearance != -1) txtLabel.setTextAppearance(labelAppearance);
        }

        // add bottom margin to front layout
        layFront.setPadding(0, 0, 0, (int)txtVerticalOffset);

        // Setup donut
        donutValue.addAmount(DEF_SECTION_NAME, value, color);
        donutValue.setCap(valueMax);
    }


    // Getters and Setters

    @SuppressLint("DefaultLocale")
    public void setValue(Object value) {
        float valueFloat;
        if (value instanceof Integer) {
            valueStr = String.format("%d", (Integer) value);
            valueFloat = ((Integer) value).floatValue();

        } else if (value instanceof Float) {
            valueStr = String.format("%.2f", (Float) value);
            valueFloat = ((Float) value);

        } else if (value instanceof Double) {
            valueStr = String.format("%.2f", (Double) value);
            valueFloat = ((Double) value).floatValue();

        } else
            throw new IllegalArgumentException("Unsupported value type: " + value.getClass().getName());

        this.value = valueFloat;
        donutValue.setAmount(DEF_SECTION_NAME, this.value);
        txtValue.setText(valueStr);
    }

    public void setUnit(String unit) {
        this.unit = unit;
        txtUnit.setText(unit);
    }

    public void setLabel(String label) {
        this.label = label;
        txtLabel.setText(label);
    }

    public void setColor(int color) {
        this.color = color;
        donutValue.removeAmount(DEF_SECTION_NAME, value);
        donutValue.addAmount(DEF_SECTION_NAME, value, color);
    }

}