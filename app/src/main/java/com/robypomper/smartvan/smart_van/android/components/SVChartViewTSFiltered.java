package com.robypomper.smartvan.smart_van.android.components;

import android.annotation.SuppressLint;

import com.robypomper.java.JavaDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SuppressLint("SimpleDateFormat")
public interface SVChartViewTSFiltered {

    void setFilterTS(int period, int qty, int offset, int partitions);

    int getFilterTSPeriod();

    int getFilterTSQty();

    int getFilterTSOffset();

    int getFilterTSPartitions();

    Date getFilterTSFromDate();

    // TSFiltered Listeners

    interface TSFilteredListener {

        void onFilterChanged(int period, int qty, int offset, int partitions);

    }
    
    void addTSFilteredListener(TSFilteredListener listener);
    
    void removeTSFilteredListener(TSFilteredListener listener);



    // UTILS: calculate from/to dates

    static Date calculateFromDate(int period, int qty, int offset) {
        int fromOffset = qty * (offset - 1);
        if (offset == 0) {
            return JavaDate.getDateAltered(period, fromOffset);
        }
        fromOffset = qty * (offset - 1 + 1);
        return JavaDate.getDateExactAltered(period, period, fromOffset);
    }

    static Date calculateToDate(int period, int qty, int offset) {
        int toOffset = qty * offset;
        if (offset == 0) {
            return JavaDate.getDateAltered(period, toOffset);
        }
        toOffset = qty * (offset + 1);
        return JavaDate.getDateExactAltered(period, period, toOffset);
    }
    

    // UTILS: Periods constants
    
    int PERIOD_MILLIS = Calendar.MILLISECOND;
    int PERIOD_SECONDS = Calendar.SECOND;
    int PERIOD_MINUTES = Calendar.MINUTE;
    int PERIOD_HOURS = Calendar.HOUR_OF_DAY;
    int PERIOD_DAYS = Calendar.DAY_OF_MONTH;
    int PERIOD_MONTHS = Calendar.MONTH;
    int PERIOD_YEARS = Calendar.YEAR;


    // UTILS: Default Time Range values

    int DEFAULT_TIME_RANGE_PERIOD = PERIOD_HOURS;
    int DEFAULT_TIME_RANGE_QTY = 1;
    int DEFAULT_TIME_RANGE_OFFSET = 0;
    int DEFAULT_TIME_RANGE_PARTITIONS = 6;


    // UTILS: Time Range constants and maps

    /** Name for the {@link Calendar#MINUTE} time range period. */
    String TIME_RANGE_PERIOD_MINUTES = "Minutes";
    /** Name for the {@link Calendar#HOUR_OF_DAY} time range period. */
    String TIME_RANGE_PERIOD_HOURS = "Hours";
    /** Name for the {@link Calendar#DAY_OF_MONTH} time range period. */
    String TIME_RANGE_PERIOD_DAYS = "Days";
    /** Name for the {@link Calendar#MONTH} time range period. */
    String TIME_RANGE_PERIOD_MONTHS = "Months";
    /** Name for the {@link Calendar#YEAR} time range period. */
    String TIME_RANGE_PERIOD_YEARS = "Years";

    /**
     * Map Time Range Periods to their common Qtys values.
     */
    Map<Integer, Integer[]> TIME_RANGE_2_QTY_MAP = new HashMap<Integer, Integer[]>() {{
        put(PERIOD_YEARS, new Integer[]{1, 3, 5});
        put(PERIOD_MONTHS, new Integer[]{1, 3, 4, 6, 12});
        put(PERIOD_DAYS, new Integer[]{1, 3, 5, 7, 10, 15, 30});
        put(PERIOD_HOURS, new Integer[]{1, 4, 6, 12, 24});
        put(PERIOD_MINUTES, new Integer[]{5, 15, 30, 60});
    }};

    /**
     * Map Time Range Periods to their string names.
     */
    Map<Integer, String> TIME_RANGE_2_PERIOD_NAMES_MAP = new HashMap<Integer, String>() {{
        put(PERIOD_YEARS, TIME_RANGE_PERIOD_YEARS);
        put(PERIOD_MONTHS, TIME_RANGE_PERIOD_MONTHS);
        put(PERIOD_DAYS, TIME_RANGE_PERIOD_DAYS);
        put(PERIOD_HOURS, TIME_RANGE_PERIOD_HOURS);
        put(PERIOD_MINUTES, TIME_RANGE_PERIOD_MINUTES);

    }};

    /**
     * @return the default Time Range Periods list (as {@link Calendar} values).
     */
    static List<Integer> getTimeRangePeriods() {
        return new ArrayList<>(TIME_RANGE_2_QTY_MAP.keySet());
    }

    /**
     * @return the default Time Range Periods names.
     */
    static List<String> getTimeRangePeriodsStr() {
        return new ArrayList<>(TIME_RANGE_2_PERIOD_NAMES_MAP.values());
    }

    /**
     * @param period the time range period
     * @return the default Qtys list for given period.
     * @throws IllegalArgumentException if the given time range period is not valid.
     */
    static List<Integer> getTimeRangeQtys(int period) {
        Integer[] qtys = TIME_RANGE_2_QTY_MAP.get(period);
        if (qtys == null)
            throw new IllegalArgumentException(String.format(EXCEPTION_MSG_FORMAT, period));
        return Arrays.asList(qtys);
    }

    /**
     * @param period the time range period
     * @return the default name for given time range period.
     * @throws IllegalArgumentException if the given time range period is not valid.
     */
    static String getTimeRangeStr(int period) {
        String name = TIME_RANGE_2_PERIOD_NAMES_MAP.get(period);
        if (name == null)
            throw new IllegalArgumentException(String.format(EXCEPTION_MSG_FORMAT, period));
        return name;
    }

    /**
     * @param period the time range period
     * @return the periods list's index for given time range period.
     *         `-1` if the given time range period is not valid.
     */
    static int getTimeRangePeriodIdx(int period) {
        return getTimeRangePeriods().indexOf(period);
    }

    /**
     * @param period the time range period's name
     * @return the periods list's index for given time range period.
     *         `-1` if the given time range period is not valid.
     */
    static int getTimeRangePeriodIdx(String period) {
        return getTimeRangePeriodsStr().indexOf(period);
    }

    /**
     * @param period the time range period
     * @param qty the time range qty
     * @return the qtys list's index for given qty on given time range period.
     *         `-1` if the given time range period is not valid.
     * @throws IllegalArgumentException if the given time range period is not valid.
     */
    static int getTimeRangeQtyIdx(int period, int qty) {
        return getTimeRangeQtys(period).indexOf(qty);
    }

    /**
     * Check if the given time period is valid. If given value is not valid, it
     * throws an {@link IllegalArgumentException}.
     * <p>
     * Valid values are constants from {@link Calendar} class.
     *
     * @param timePeriod the time period to check.
     * @throws IllegalArgumentException if the given time range period is not valid.
     */
    static void checkTimeRangePeriodValue(int timePeriod) {
        if (timePeriod != PERIOD_MILLIS &&
                timePeriod != PERIOD_SECONDS &&
                timePeriod != PERIOD_MINUTES &&
                timePeriod != PERIOD_HOURS &&
                timePeriod != PERIOD_DAYS &&
                timePeriod != PERIOD_MONTHS &&
                timePeriod != PERIOD_YEARS)
            throw new IllegalArgumentException(String.format(EXCEPTION_MSG_FORMAT, timePeriod));
    }

    static long periodDurationMS(int filterTSPeriod) {
        switch (filterTSPeriod) {
            case PERIOD_MILLIS:
                return TimeUnit.MILLISECONDS.toMillis(1);
            case PERIOD_SECONDS:
                return TimeUnit.SECONDS.toMillis(1);
            case PERIOD_MINUTES:
                return TimeUnit.MINUTES.toMillis(1);
            case PERIOD_HOURS:
                return TimeUnit.HOURS.toMillis(1);
            case PERIOD_DAYS:
                return TimeUnit.DAYS.toMillis(1);
            case PERIOD_MONTHS:
                return TimeUnit.DAYS.toMillis(30);
            case PERIOD_YEARS:
                return TimeUnit.DAYS.toMillis(365);
            default:
                throw new IllegalArgumentException("Invalid period: " + filterTSPeriod);
        }
    }

    String EXCEPTION_MSG_FORMAT = "Invalid time period: %s";


    // UTILS: Date formatters

    /**
     * Formatters for each time period:
     * <pre>
     * Period     Nav	        Axis		    Detail
     * ------------------------------------------------
     * Minute	19/04	    14:51:30	    14:51:24.322
     * 			14:51
     * Hour		19/04	    14:30		    19/04 14:51:24.322
     * 			14:00
     * Day		19/04	    19/04 14:00	    19/04 14:51:24.322
     * Month	04/24	    19/04/24	    19/04/24 14:51:24.322
     * Year		2024	    04/24		    19/04/24 14:51:24.322
     * </pre>
     */

    SimpleDateFormat SDF_MILLIS_NAV = new SimpleDateFormat("ss.SSS");
    SimpleDateFormat SDF_MILLIS_AXIS = new SimpleDateFormat("SSS");
    SimpleDateFormat SDF_MILLIS_DETAILS = new SimpleDateFormat("HH:mm:ss.SSS");
    SimpleDateFormat SDF_SECONDS_NAV = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat SDF_SECONDS_AXIS = new SimpleDateFormat("ss.SSS");
    SimpleDateFormat SDF_SECONDS_DETAILS = new SimpleDateFormat("HH:mm:ss.SSS");
    SimpleDateFormat SDF_MINUTES_NAV = new SimpleDateFormat("dd/MM HH:mm");
    SimpleDateFormat SDF_MINUTES_AXIS = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat SDF_MINUTES_DETAILS = new SimpleDateFormat("HH:mm:ss.SSS");
    SimpleDateFormat SDF_HOURS_NAV = new SimpleDateFormat("dd/MM HH:mm");
    SimpleDateFormat SDF_HOURS_AXIS = new SimpleDateFormat("HH:mm");
    SimpleDateFormat SDF_HOURS_DETAILS = new SimpleDateFormat("dd/MM HH:mm:ss.SSS");
    SimpleDateFormat SDF_DAYS_NAV = new SimpleDateFormat("dd/MM");
    SimpleDateFormat SDF_DAYS_AXIS = new SimpleDateFormat("dd/MM HH:mm");
    SimpleDateFormat SDF_DAYS_DETAILS = new SimpleDateFormat("dd/MM HH:mm:ss.SSS");
    SimpleDateFormat SDF_MONTHS_NAV = new SimpleDateFormat("MM/yy");
    SimpleDateFormat SDF_MONTHS_AXIS = new SimpleDateFormat("dd/MM/yy");
    SimpleDateFormat SDF_MONTHS_DETAILS = new SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS");
    SimpleDateFormat SDF_YEARS_NAV = new SimpleDateFormat("yyyy");
    SimpleDateFormat SDF_YEARS_AXIS = new SimpleDateFormat("MM/yy");
    SimpleDateFormat SDF_YEARS_DETAILS = new SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS");

    static SimpleDateFormat periodFormatterNavigator(int filterTSPeriod, int filterTSQty) {
        switch (filterTSPeriod) {
            case PERIOD_MILLIS:
                return SDF_MILLIS_NAV;
            case PERIOD_SECONDS:
                return SDF_SECONDS_NAV;
            case PERIOD_MINUTES:
                return SDF_MINUTES_NAV;
            case PERIOD_HOURS:
                return SDF_HOURS_NAV;
            case PERIOD_DAYS:
                return SDF_DAYS_NAV;
            case PERIOD_MONTHS:
                return SDF_MONTHS_NAV;
            case PERIOD_YEARS:
                return SDF_YEARS_NAV;
            default:
                throw new IllegalArgumentException("Invalid period: " + filterTSPeriod);
        }
    }

    static SimpleDateFormat periodFormatterAxis(int filterTSPeriod, int filterTSQty) {
        switch (filterTSPeriod) {
            case PERIOD_MILLIS:
                return SDF_MILLIS_AXIS;
            case PERIOD_SECONDS:
                return SDF_SECONDS_AXIS;
            case PERIOD_MINUTES:
                return SDF_MINUTES_AXIS;
            case PERIOD_HOURS:
                return SDF_HOURS_AXIS;
            case PERIOD_DAYS:
                return SDF_DAYS_AXIS;
            case PERIOD_MONTHS:
                return SDF_MONTHS_AXIS;
            case PERIOD_YEARS:
                return SDF_YEARS_AXIS;
            default:
                throw new IllegalArgumentException("Invalid period: " + filterTSPeriod);
        }
    }

    static SimpleDateFormat periodFormatterDetails(int filterTSPeriod, int filterTSQty) {
        switch (filterTSPeriod) {
            case PERIOD_MILLIS:
                return SDF_MILLIS_DETAILS;
            case PERIOD_SECONDS:
                return SDF_SECONDS_DETAILS;
            case PERIOD_MINUTES:
                return SDF_MINUTES_DETAILS;
            case PERIOD_HOURS:
                return SDF_HOURS_DETAILS;
            case PERIOD_DAYS:
                return SDF_DAYS_DETAILS;
            case PERIOD_MONTHS:
                return SDF_MONTHS_DETAILS;
            case PERIOD_YEARS:
                return SDF_YEARS_DETAILS;
            default:
                throw new IllegalArgumentException("Invalid period: " + filterTSPeriod);
        }
    }

}
