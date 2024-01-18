package com.robypomper.josp.jsl.android.components.charts.formatters;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;


/**
 * Formatter that handle double and float values and display their percentage
 * based on the min and max values.
 * <p>
 * This formatter is inherited from {@link ChartUnitFormatter}, so it provide
 * also the scale factor.
 * <p>
 * The min and max values do not invalidate previous generated values. Because
 * they are used only to calculate the percentage of the given value into the
 * {@link #toString(float)} method.<br/>
 * But, if you have multiple data sets and you would use different min and max
 * values for each data set, you must create a new formatter for each data set.
 * <p>
 * Warning: actually the LineChartView supports at least 2 formatters for the
 * Y axis: left and right.
 * @noinspection unused
 */
public class ChartPercentFormatter extends ChartUnitFormatter {

    // Internal vars

    private float minValue;
    private float maxValue;


    // Constructors

    /**
     * Create a new Percent using given min and max values.
     *
     * @param minValue the min value corresponding to 0%.
     * @param maxValue the max value corresponding to 100%.
     */
    public ChartPercentFormatter(float minValue, float maxValue) {
        this(minValue, maxValue, 1);
    }

    /**
     * Create a new Percent using given min and max values and scale factor.
     *
     * @param minValue the min value corresponding to 0%.
     * @param maxValue the max value corresponding to 100%.
     * @param scale    the scale factor to use to scale the given values.
     */
    public ChartPercentFormatter(float minValue, float maxValue, float scale) {
        super(scale);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }


    // Getters and setters

    /**
     * @return the min value corresponding to 0%.
     */
    public float getMinValue() {
        return minValue;
    }

    /**
     * Set the min value corresponding to 0%.
     * <p>
     * NB: this do not invalidate previous generated values.
     *
     * @param minValue the min value corresponding to 0%.
     */
    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    /**
     * @return the max value corresponding to 100%.
     */
    public float getMaxValue() {
        return maxValue;
    }

    /**
     * Set the max value corresponding to 100%.
     * <p>
     * NB: this do not invalidate previous generated values.
     *
     * @param maxValue the max value corresponding to 100%.
     */
    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }


    // SVBaseFormatter re-implementation

    /**
     * {@inheritDoc}
     * <p>
     * The resulting Date from the given value is formatted using the
     * {@link SimpleDateFormat} initialized in the constructor.
     */
    @SuppressLint("DefaultLocale")
    @Override
    public String toString(float value) {
        float valRelative = value - minValue;
        float valRange = maxValue - minValue;
        float valPercent = valRelative / valRange * 100;

        return super.toString(valPercent) + "%";
    }

}
