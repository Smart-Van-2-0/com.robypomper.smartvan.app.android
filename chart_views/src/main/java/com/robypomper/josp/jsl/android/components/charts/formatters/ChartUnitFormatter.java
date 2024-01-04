package com.robypomper.josp.jsl.android.components.charts.formatters;

import android.annotation.SuppressLint;


/**
 * Formatter that handle double and float values.
 * <p>
 * During the initialization you can set a scale factor that will be used
 * to scale the given values. This is useful when you want to convert a
 * value to another unit of measure.
 * @noinspection unused
 */
public class ChartUnitFormatter extends ChartBaseFormatter {

    // Static constructors

    /**
     * Unit formatter with the no scale factor.
     */
    public static ChartUnitFormatter Y_FORMATTER_UNIT() { return new ChartUnitFormatter(1); }

    /**
     * Unit formatter with the x0.1 scale factor.
     */
    public static ChartUnitFormatter Y_FORMATTER_UNIT_01() { return new ChartUnitFormatter(0.1F); }

    /**
     * Unit formatter with the x0.01 scale factor.
     */
    public static ChartUnitFormatter Y_FORMATTER_UNIT_001() { return new ChartUnitFormatter(0.01F); }

    /**
     * Unit formatter with the x0.001 scale factor.
     */
    public static ChartUnitFormatter Y_FORMATTER_UNIT_0001() { return new ChartUnitFormatter(0.001F); }

    /**
     * Unit formatter with the x10 scale factor.
     */
    public static ChartUnitFormatter Y_FORMATTER_UNIT_10() { return new ChartUnitFormatter(10); }

    /**
     * Unit formatter with the x100 scale factor.
     */
    public static ChartUnitFormatter Y_FORMATTER_UNIT_100() { return new ChartUnitFormatter(100); }

    /**
     * Unit formatter with the x1000 scale factor.
     */
    public static ChartUnitFormatter Y_FORMATTER_UNIT_1000() { return new ChartUnitFormatter(1000); }


    // Internal vars

    private final float scale;


    // Constructors

    /**
     * Create a new UnitFormatter with given scale.
     *
     * @param scale the scale factor to use to scale the given values.
     */
    public ChartUnitFormatter(float scale) {
        this.scale = scale;
    }


    // SVBaseFormatter implementation

    /**
     * {@inheritDoc}
     * <p>
     * Given value is multiplied by the scale factor.
     * <p>
     * If given object is not a Double or a Float, an
     * {@link IllegalArgumentException} is thrown.
     */
    @Override
    public float from(Object obj) {
        float value;
        if (obj instanceof Double) {
            value = ((Double) obj).floatValue();
        } else if (obj instanceof Float) {
            value = (Float) obj;
        } else
            throw new IllegalArgumentException("Object must be a Float or a Double, got " + obj.getClass().getName());

        // scale the range new state and keep only the first 2 digits after the decimal point
        return (float) (Math.round(value * scale * 100) / 100.0);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The resulting string is the given value multiplied by the scale
     * factor.
     */
    @Override
    @SuppressLint("DefaultLocale")
    public String toString(float value) {
        float v = (float) (Math.round(value * 100) / 100.0);
        return String.format("%.2f", v);
    }

}
