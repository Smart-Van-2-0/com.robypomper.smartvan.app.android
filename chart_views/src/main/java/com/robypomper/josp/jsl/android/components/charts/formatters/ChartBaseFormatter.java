package com.robypomper.josp.jsl.android.components.charts.formatters;

import com.github.mikephil.charting.formatter.ValueFormatter;


/**
 * Base class for all formatters.
 * <p>
 * It is a {@link ValueFormatter} that define {@link #from(Object)} and
 * {@link #toString(float)} methods that must be implemented by subclasses.
 * <p>
 * This class re-implements also the {@link #getFormattedValue(float)} method
 * from the {@link ValueFormatter} class, using the {@link #toString(float)}
 * method.
 */
public abstract class ChartBaseFormatter extends ValueFormatter {

    // BaseFormatter definitions

    /**
     * Convert an object to a float value.
     *
     * @param obj the object to convert.
     * @return a float value representing given object
     * @throws IllegalArgumentException if given object is not valid or
     *                                  of wrong value.
     */
    public abstract float from(Object obj) throws IllegalArgumentException;

    /**
     * Return a string representing the original object from which the
     * given value was converted.
     *
     * @param value the value (generated using the {@link #from(Object)}
     *              method) to convert to string.
     * @return a string representing the original object from which the
     * given value was converted.
     */
    public abstract String toString(float value);


    // ValueFormatter implementation

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormattedValue(float value) {
        return toString(value);
    }

}
