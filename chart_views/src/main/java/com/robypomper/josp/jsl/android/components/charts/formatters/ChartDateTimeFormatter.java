package com.robypomper.josp.jsl.android.components.charts.formatters;

import android.annotation.SuppressLint;

import com.robypomper.java.JavaDate;
import com.robypomper.josp.jsl.android.components.charts.utils.TimeRangeLimits;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Formatter that handle Date objects.
 * <p>
 * It use the {@link SimpleDateFormat} to convert Date to String and it use
 * the pattern given in the constructor.
 * <p>
 * Because not all dates (as milliseconds) can be stored into a float value,
 * this class use a relative date to calculate the difference between the
 * date to convert and the relative date. The difference is then converted
 * to float using the given time unit. That means if the time unit is set
 * to {@link Calendar#HOUR_OF_DAY} and the date to convert is 1 hour before
 * (any minutes and seconds) the relative date, the resulting float value
 * will be -1.0 (1 time unit before relative date).
 * So, any float generated using the {@link #fromDate(Date)} method is
 * linked to the relative date and the time unit. Then if you change them,
 * you cannot use the same float value to convert it back to a Date.
 * <p>
 * The {@link #setRelativeDate(Date)} method store the given date but it
 * also round it to the time unit. This is done to avoid errors when
 * calculating the difference between the date to convert and the relative
 * date.
 * @noinspection unused
 */
public class ChartDateTimeFormatter extends ChartBaseFormatter {

    // Constants

    /**
     * Pattern used to convert Date to String into {@link #X_FORMATTER_MINUTES}
     * and {@link #X_FORMATTER_HOURS}.
     */
    public static final String TIMESTAMP_HOUR_FORMAT = "HH:mm";
    /**
     * Pattern used to convert Date to String into {@link #X_FORMATTER_DATE}.
     */
    public static final String TIMESTAMP_DATE_FORMAT = "dd/MM";
    /**
     * Pattern used to convert Date to String into {@link #X_FORMATTER_MONTH}.
     */
    public static final String TIMESTAMP_MONTH_FORMAT = "MMM";
    /**
     * Pattern used to convert Date to String into {@link #X_FORMATTER_YEAR}.
     */
    public static final String TIMESTAMP_YEAR_FORMAT = "YYYY";


    // Static constructors

    /**
     * Formatter for minutes that use the {@link #TIMESTAMP_HOUR_FORMAT}.
     */
    public static ChartDateTimeFormatter X_FORMATTER_MINUTES() { return new ChartDateTimeFormatter(TIMESTAMP_HOUR_FORMAT, Calendar.SECOND); }
    /**
     * Formatter for hours that use the {@link #TIMESTAMP_HOUR_FORMAT}.
     */
    public static ChartDateTimeFormatter X_FORMATTER_HOURS() { return new ChartDateTimeFormatter(TIMESTAMP_HOUR_FORMAT, Calendar.MINUTE); }
    /**
     * Formatter for days that use the {@link #TIMESTAMP_DATE_FORMAT}.
     */
    public static ChartDateTimeFormatter X_FORMATTER_DATE() { return new ChartDateTimeFormatter(TIMESTAMP_DATE_FORMAT, Calendar.HOUR_OF_DAY); }
    /**
     * Formatter for months that use the {@link #TIMESTAMP_MONTH_FORMAT}.
     */
    public static ChartDateTimeFormatter X_FORMATTER_MONTH() { return new ChartDateTimeFormatter(TIMESTAMP_MONTH_FORMAT, Calendar.DAY_OF_MONTH); }
    /**
     * Formatter for years that use the {@link #TIMESTAMP_YEAR_FORMAT}.
     */
    public static ChartDateTimeFormatter X_FORMATTER_YEAR() { return new ChartDateTimeFormatter(TIMESTAMP_YEAR_FORMAT, Calendar.MONTH); }

    // Internal vars

    private SimpleDateFormat simpleDateFormat;
    private int timeUnit;
    private Date relativeDateRounded;
    private Date relativeDate;


    // Constructors

    /**
     * Create a new DateTimeFormatter using given pattern and current date.
     *
     * @param pattern the pattern to use to convert Date to String.
     */
    public ChartDateTimeFormatter(String pattern) {
        this(pattern, JavaDate.getNowDate(), Calendar.SECOND);
    }

    /**
     * Create a new DateTimeFormatter using given pattern and current date.
     *
     * @param pattern  the pattern to use to convert Date to String.
     * @param timeUnit the time unit to use to convert Date to float.
     */
    public ChartDateTimeFormatter(String pattern, int timeUnit) {
        this(pattern, JavaDate.getNowDate(), timeUnit);
    }

    /**
     * Create a new DateTimeFormatter using given pattern and relative date.
     * The relative date is used to calculate the difference between the
     * date to convert and the relative date. The difference is then
     * converted to float using the given time unit.
     *
     * @param pattern      the pattern to use to convert Date to String.
     * @param relativeDate the relative date to use to convert Date to float.
     * @param timeUnit     the time unit to use to convert Date to float.
     */
    @SuppressLint("SimpleDateFormat")
    public ChartDateTimeFormatter(String pattern, Date relativeDate, int timeUnit) {
        this.simpleDateFormat = new SimpleDateFormat(pattern);
        this.timeUnit = timeUnit;
        setRelativeDate(relativeDate);
    }


    // Getters and setters

    /**
     * @return the relative date used to calculate the difference between the
     * date to convert and the relative date.
     */
    public Date getRelativeDate() {
        return relativeDate;
    }

    /**
     * Set the relative date used to calculate the difference between the
     * date to convert and the relative date.
     * <p>
     * NB: all previous generated values must be invalidated.
     *
     * @param relativeDate the relative date to use to convert Date to float.
     */
    public void setRelativeDate(Date relativeDate) {
        this.relativeDate = relativeDate;
        this.relativeDateRounded = JavaDate.getDateExact(relativeDate, timeUnit);
    }

    /**
     * @return the time unit used to convert Date to float.
     */
    public int getTimeUnit() {
        return timeUnit;
    }

    /**
     * Set the time unit used to convert Date to float.
     * <p>
     * NB: all previous generated values must be invalidated.
     *
     * @param timeUnit the time unit to use to convert Date to float.
     */
    public void setTimeUnit(int timeUnit) {
        this.timeUnit = timeUnit;
        setRelativeDate(relativeDate);
    }

    /**
     * @return the SimpleDateFormat used to convert Date to String.
     */
    public SimpleDateFormat getDateFormatter() {
        return simpleDateFormat;
    }

    /**
     * Set the SimpleDateFormat used to convert Date to String.
     *
     * @param pattern the pattern used to initialize the internal
     *                SimpleDateFormat.
     */
    @SuppressLint("SimpleDateFormat")
    public void setDateFormat(String pattern) {
        simpleDateFormat = new SimpleDateFormat(pattern);
    }


    // Conversion methods

    /**
     * Convert a Date to a float value.
     * <p>
     * After rounding the given date to the time unit, it makes the difference
     * between the rounded date and the relative date (also rounded to the
     * time unit). Then the result is converted to float dividing it by the
     * count of milliseconds in the time unit.
     *
     * @param date the date to convert.
     * @return a float value representing given date based on relative date
     * and time unit.
     */
    public float fromDate(Date date) {
        long ms = JavaDate.getDateExact(date, timeUnit).getTime() - relativeDateRounded.getTime();
        return (float) (ms / TimeRangeLimits.timeUnitToMs(timeUnit));
    }

    /**
     * Convert a float value to a Date.
     * <p>
     * The given value is multiplied by the count of milliseconds in the
     * time unit. Then the result is added to the relative date.
     * <p>
     * NB: the concatenation of the {@link #fromDate(Date)} and
     * toDate(float) methods do not return the original value. This
     * concatenation will return the original value rounded to the time
     * unit.
     *
     * @param value the value to convert.
     * @return a Date representing given value based on relative date
     * and time unit.
     */
    public Date toDate(float value) {
        long valueL = (long) value * TimeRangeLimits.timeUnitToMs(timeUnit);
        long ms = valueL + relativeDateRounded.getTime();
        return new Date(ms);
    }

    /**
     * Convert a Date to a formatted string.
     * <p>
     * This method use the {@link #fromDate(Date)} method to convert the
     * given date to a float value and then it use the {@link #toString(float)}
     * method to convert the float value to a string.
     *
     * @param date the date to convert.
     * @return a string representing given date based on current formatter.
     */
    public String fromDateToStr(Date date) {
        float tmpVal = fromDate(date);
        return toString(tmpVal);
    }


    // SVBaseFormatter implementation

    /**
     * {@inheritDoc}
     * <p>
     * If given object is not a Date, an {@link IllegalArgumentException}
     * is thrown.
     */
    @Override
    public float from(Object obj) {
        if (!(obj instanceof Date))
            throw new IllegalArgumentException("Object must be a Date, got " + obj.getClass().getName());
        return fromDate((Date) obj);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The resulting Date from the given value is formatted using the
     * {@link SimpleDateFormat} initialized in the constructor.
     */
    @Override
    public String toString(float value) {
        Date date = toDate(value);
        return simpleDateFormat.format(date);
    }

}
