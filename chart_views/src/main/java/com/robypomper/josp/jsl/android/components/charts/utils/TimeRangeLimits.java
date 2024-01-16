package com.robypomper.josp.jsl.android.components.charts.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.robypomper.josp.jsl.android.utils.JavaDateWrapper;


/**
 * Class used into chart's classes to specify the chart bounds based on a
 * time range.
 * <p>
 * Those classes are used to calculate the chart time range using a time unit,
 * time unit quantity and an offset. The time unit and his quantity specify how
 * long the range should be (Unit * Qty). The offset define how much the range
 * must be shifted to the reference date. Normally the reference date is set as
 * current date time, so positive offsets refers to the future, and negatives
 * to the past.<br/>
 * This class provides 2 different methods to calculate the time range limits:
 * {@link #calculateTimeRangeLimits_Rounded(Date, int, int, int)} and
 * {@link #calculateTimeRangeLimits_UpperRounded(Date, int, int, int)} the second
 * one is the default one.
 * <p>
 * Moreover this class provides constants, maps and converters for Time Units.
 *
 * @noinspection unused
 */
public class TimeRangeLimits {

    // Internal vars

    private final Date fromDate;
    private final Date toDate;


    // Constructors

    /**
     * Create a new TimeRangeLimits with the given from/to dates.
     *
     * @param fromDate the beginning date of the range.
     * @param toDate  the ending date of the range.
     */
    public TimeRangeLimits(Date fromDate, Date toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }


    // Getters

    /**
     * @return the beginning date of the range.
     */
    public Date getFromDate() {
        return fromDate;
    }

    /**
     * @return the ending date of the range.
     */
    public Date getToDate() {
        return toDate;
    }


    // Time Range Limits calculation

    /**
     * Calculate time range limit for a given offset, qty and time unit,
     * it use the `nowDate` param as reference date.
     * <p>
     * The calculated time range limits are calculated using the given
     * `nowDate` as reference date. That means that the returned from/to
     * dates will be calculated using the given `nowDate` as base date
     * and the `offset` and `qty` as time unit modifiers.
     * <p>
     * When this method calculate the from/to dates, use the `timeUnit`
     * param as base time unit to slide on the time line. This means that
     * if you set the `timeUnit` to Calendar.HOUR_OF_DAY, the returned
     * from/to dates will be calculated using the hours as base time unit.
     * Others values for `timeUnit` can be found into the {@link Calendar}
     * constants.
     * <p>
     * The `offset` is the number of time units to go back (negative) or
     * forward (positive) in time, starting from the given `nowDate`. Except
     * when the offset value is `0`, the dates returned are rounded to the
     * beginning of the time unit.
     * <p>
     * The `qty` is the number of time units to consider in the returned
     * from/to dates. The `qty` is always positive. With `timeUnit` set
     * to HOUR and `qty` set to 24, the returned date range will contains
     * 24 hours.
     *
     * @param nowDate  the date-time to use as reference datetime to calculate the time limit
     * @param timeUnit the time unit to use (use the {@link Calendar} constants)
     * @param offset   the number of timeUnits x qty to move on time
     * @param qty      the number of time units to consider in the returned from/to dates
     * @return the calculated time range limits.
     * @throws IllegalArgumentException if the given time range unit is not valid.
     */
    public static TimeRangeLimits calculateTimeRangeLimits(Date nowDate, int timeUnit, int offset, int qty) {
        //return calculateTimeRangeLimits_Rounded(nowDate, timeUnit, offset, qty);
        return calculateTimeRangeLimits_UpperRounded(nowDate, timeUnit, offset, qty);
    }

    /**
     * Calculate time range limit for a given offset, qty and time unit,
     * it use the `nowDate` param as reference date. The limits ar rounded
     * to the beginning of the time unit.
     * <p>
     * This offset management results in the following behavior (with
     * `timeUnit` set to MINUTES and `qty` set to 5):
     * <ul>
     *     <li>offset = -2: start = now exact minute - 10 minutes, end = now exact minute - 5</li>
     *     <li>offset = -1: start = now exact minute - 5 minutes,  end = now exact minute</li>
     *     <li>offset = 0 : start = <b>now - 5 minutes</b>,        end = <b>now</b></li>
     *     <li>offset = 1:  start = now exact minute,              end = now exact minute + 1</li>
     *     <li>offset = 1:  start = now exact minute + 1,          end = now exact minute + 2</li>
     * </ul>
     * <p>
     * When the offset is not 0, the returned from/to dates are rounded
     * to the beginning of the time unit.
     *
     * @param nowDate  the date-time to use as current time to calculate the time limit
     * @param timeUnit the time unit to use (use the {@link Calendar} constants)
     * @param offset   the number of time units to move on time
     * @param qty      the number of time units to consider in the returned from/to dates
     * @return the calculated time range limits
     * @throws IllegalArgumentException if the given time range unit is not valid.
     */
    public static TimeRangeLimits calculateTimeRangeLimits_Rounded(Date nowDate, int timeUnit, int offset, int qty) {
        assert nowDate != null: "nowDate cannot be null";
        checkTimeRangeUnitValue(timeUnit);

        if (offset == 0) {
            Date start = JavaDateWrapper.getDateAltered(nowDate, timeUnit, qty * -1);
            return new TimeRangeLimits(start, nowDate);
        }

        int offsetStart, offsetEnd;
        if (offset > 0) {
            offset -= 1;
            offsetStart = offset * qty;
            offsetEnd = offset * qty + qty;
        } else {
            offset += 1;
            int nQty = qty * -1;
            offsetStart = offset * qty + nQty;
            offsetEnd = offset * qty;
        }
        Date start = JavaDateWrapper.getDateExactAltered(nowDate, timeUnit, timeUnit, offsetStart);
        Date end = JavaDateWrapper.getDateExactAltered(nowDate, timeUnit, timeUnit, offsetEnd);
        return new TimeRangeLimits(start, end);
    }

    /**
     * Calculate time range limit for a given offset, qty and time unit,
     * it use the `nowDate` param as reference date. The limits ar rounded
     * to the beginning of the time unit.
     * <p>
     * This offset management results in the following behavior (with
     * `timeUnit` set to MINUTES and `qty` set to 5):
     * <ul>
     *     <li>offset = -2: start = now exact minute URF - 10 minutes, end = now exact minute URF - 5</li>
     *     <li>offset = -1: start = now exact minute URF- 5 minutes,   end = now exact minute URF</li>
     *     <li>offset = 0 : start = <b>now - 5 minutes</b>,                      end = <b>now</b></li>
     *     <li>offset = 1:  start = now exact minute URF,              end = now exact minute URF + 1</li>
     *     <li>offset = 1:  start = now exact minute URF + 1,          end = now exact minute URF + 2</li>
     * </ul>
     * <p>
     * Where `URF` means Upper Rounded Fraction.<br/>
     * When the offset is not 0, the returned from/to dates are rounded
     * not to the beginning of the time unit but at the beginning of a fraction
     * of upper time unit. That means if the time unit is MINUTE and the qty is 5,
     * the returned from/to dates will be rounded to the beginning of the 5 minutes
     * fraction. P.e. if the current time is 17:23:45, the returned from/to dates
     * will be rounded to 17:20:00 - 17:25:00.
     *
     * @param nowDate  the date-time to use as current time to calculate the time limit
     * @param timeUnit the time unit to use (use the {@link Calendar} constants)
     * @param offset   the number of time units to move on time
     * @param qty      the number of time units to consider in the returned from/to dates
     * @return the calculated time range limits
     * @throws IllegalArgumentException if the given time range unit is not valid.
     */
    public static TimeRangeLimits calculateTimeRangeLimits_UpperRounded(Date nowDate, int timeUnit, int offset, int qty) {
        assert nowDate != null: "nowDate cannot be null";
        checkTimeRangeUnitValue(timeUnit);

        if (offset == 0) {
            Date start = JavaDateWrapper.getDateAltered(nowDate, timeUnit, qty * -1);
            return new TimeRangeLimits(start, nowDate);
        }
        offset = offset + (offset > 0 ? 1 : -1);

        // JavaDateWrapper.getNowDate() => 27/12/2023 17:23:45
        // timeUnit = Calendar.MINUTE, qty = 30, offset = 0 => 17:00:00 - 17:29:59
        // timeUnit = Calendar.MINUTE, qty = 30, offset = -1 => 16:30:00 - 16:59:59       <-- note: slide to previous upper range
        // timeUnit = Calendar.MINUTE, qty = 5, offset = 0 => 17:20:00 - 17:24:59
        // timeUnit = Calendar.MINUTE, qty = 5, offset = -1 => 17:15:00 - 17:19:59
        // timeUnit = Calendar.MINUTE, qty = 5, offset = -2 => 17:10:00 - 17:14:59
        // timeUnit = Calendar.MINUTE, qty = 7, offset = 0 => 17:21:00 - 17:27:59
        // timeUnit = Calendar.MINUTE, qty = 7, offset = -1 => 17:14:00 - 17:20:59
        // timeUnit = Calendar.MINUTE, qty = 7, offset = -2 => 17:07:00 - 17:13:59
        // timeUnit = Calendar.MINUTE, qty = 7, offset = -3 => 17:00:00 - 17:06:59
        // timeUnit = Calendar.MINUTE, qty = 7, offset = -4 => 16:56:00 - 17:02:59         <-- note: overlap with previous range
        // timeUnit = Calendar.MINUTE, qty = 7, offset = -5 => 16:49:00 - 16:55:59
        // timeUnit = Calendar.MINUTE, qty = 7, offset = -6 => 16:42:00 - 16:48:59
        // timeUnit = Calendar.MINUTE, qty = 7, offset = -7 => 16:35:00 - 16:41:59
        // timeUnit = Calendar.MINUTE, qty = 7, offset = -8 => 16:28:00 - 16:34:59
        // timeUnit = Calendar.MINUTE, qty = 7, offset = -9 => 16:21:00 - 16:27:59         <-- note: same minutes (but 1 hour before) as offset = 0
        // timeUnit = Calendar.MINUTE, qty = 7, offset = -13 => 15:56:00 - 16:02:59        <-- note: overlap with previous range 2
        // timeUnit = Calendar.MINUTE, qty = 7, offset = -18 => 15:21:00 - 15:27:59        <-- note: same minutes (but 2 hour before) as offset = 0

        // timeUnit = Calendar.HOUR, qty = 6, offset = 0 => 12:00:00 - 17:59:59
        // timeUnit = Calendar.HOUR, qty = 6, offset = -1 => 06:00:00 - 11:59:59
        // timeUnit = Calendar.HOUR, qty = 6, offset = -2 => 00:00:00 - 05:59:59
        // timeUnit = Calendar.HOUR, qty = 6, offset = -3 => 18:00:00 - 23:59:59
        // timeUnit = Calendar.HOUR, qty = 5, offset = 0 => 15:00:00 - 19:59:59
        // timeUnit = Calendar.HOUR, qty = 5, offset = -1 => 10:00:00 - 14:59:59
        // timeUnit = Calendar.HOUR, qty = 5, offset = -2 => 05:00:00 - 09:59:59
        // timeUnit = Calendar.HOUR, qty = 5, offset = -3 => 00:00:00 - 04:59:59
        // timeUnit = Calendar.HOUR, qty = 5, offset = -4 => 26/12/23 20:00:00 - 01:59:59  <-- note: from date switch previous to previous DAY


        // get now + offset
        // timeUnit = MINUTE, qty = 30, offset = 0 => 17:23:45
        // timeUnit = MINUTE, qty = 30, offset = -1 => 16:53:45           <-- note: slide to previous upper range
        // timeUnit = MINUTE, qty = 5, offset = 0 => 17:23:45
        // timeUnit = MINUTE, qty = 5, offset = -1 => 17:18:45
        // timeUnit = MINUTE, qty = 7, offset = 0 => 17:23:45
        // timeUnit = MINUTE, qty = 7, offset = -1 => 17:16:45
        // timeUnit = MINUTE, qty = 7, offset = -2 => 17:09:45
        // timeUnit = MINUTE, qty = 7, offset = -3 => 17:02:45
        // timeUnit = MINUTE, qty = 7, offset = -4 => 16:55:45            <-- note: overlap with previous range
        // timeUnit = MINUTE, qty = 7, offset = -5 => 16:48:45
        // timeUnit = MINUTE, qty = 7, offset = -6 => 16:41:45
        // timeUnit = MINUTE, qty = 7, offset = -7 => 16:34:45
        // timeUnit = MINUTE, qty = 7, offset = -8 => 16:27:45
        // timeUnit = MINUTE, qty = 7, offset = -9 => 16:20:45            <-- note: same minutes (but 1 hour before) as offset = 0
        // timeUnit = MINUTE, qty = 7, offset = -13 => 15:52:45           <-- note: overlap with previous range 2
        // timeUnit = MINUTE, qty = 7, offset = -18 => 15:17:45           <-- note: same minutes (but 2 hour before) as offset = 0
        // timeUnit = HOUR, qty = 6, offset = 0 => 17:23:45
        // timeUnit = HOUR, qty = 6, offset = -1 => 11:23:45
        // timeUnit = HOUR, qty = 5, offset = 0 => 17:23:45
        // timeUnit = HOUR, qty = 5, offset = -1 => 12:23:45
        // timeUnit = HOUR, qty = 5, offset = -4 => 26/12/23 21:23:45    <-- note: from date switch previous to previous DAY
        Date nowOffset = JavaDateWrapper.getDateAltered(nowDate, timeUnit, offset * qty);

        // get start of upper unit
        // timeUnit = MINUTE, nowOffset = 17:23:45 => rangeUpperUnit = HOUR => 17:00:00
        // timeUnit = MINUTE, nowOffset = 16:53:45 => rangeUpperUnit = HOUR => 16:00:00                     <-- note: slide to previous upper range
        // timeUnit = MINUTE, nowOffset = 16:55:45 => rangeUpperUnit = HOUR => 16:00:00                     <-- note: will overlap with previous range
        // timeUnit = MINUTE, nowOffset = 16:20:45 => rangeUpperUnit = HOUR => 16:00:00                     <-- note: same minutes (but 1 hour before) as offset = 0
        // timeUnit = MINUTE, nowOffset = 15:52:45 => rangeUpperUnit = HOUR => 15:00:00                     <-- note: overlap with previous range 2
        // timeUnit = MINUTE, nowOffset = 15:17:45 => rangeUpperUnit = HOUR => 15:00:00                     <-- note: same minutes (but 2 hour before) as offset = 0
        // timeUnit = HOUR, nowOffset = 17:23:45 => rangeUpperUnit = DAY => 27/12/2023 00:00:00
        // timeUnit = HOUR, nowOffset = 11:23:45 => rangeUpperUnit = DAY => 27/12/2023 00:00:00
        // timeUnit = HOUR, nowOffset = 26/12/23 21:23:45 => rangeUpperUnit = DAY => 26/12/2023 00:00:00    <-- note: from date switch previous to previous DAY
        int rangeUpperUnit = getUpperTimeUnit(timeUnit);
        Date fromUpperDate = JavaDateWrapper.getDateExact(nowOffset, rangeUpperUnit);

        // slide until from date
        // timeUnit = MINUTE, qty = 30, nowOffset = 17:23:45, fromUpperDate = 17:00:00 => 17:00:00
        // timeUnit = MINUTE, qty = 30, nowOffset = 16:53:45, fromUpperDate = 16:00:00 => 16:30:00            <-- note: slide to previous upper range
        // timeUnit = MINUTE, qty = 5, nowOffset = 16:55:45, fromUpperDate = 16:00:00 => 16:55:00             <-- note: will overlap with previous range
        // timeUnit = MINUTE, qty = 7, nowOffset = 16:20:45, fromUpperDate = 16:00:00 => 16:14:00             <-- note: same minutes (but 1 hour before) as offset = 0
        // timeUnit = MINUTE, qty = 7, nowOffset = 15:52:45, fromUpperDate = 15:00:00 => 15:49:00             <-- note: overlap with previous range 2
        // timeUnit = MINUTE, qty = 7, nowOffset = 15:17:45, fromUpperDate = 15:00:00 => 15:14:00             <-- note: same minutes (but 2 hour before) as offset = 0
        // timeUnit = HOUR, qty = 6, nowOffset = 17:23:45, fromUpperDate = 27/12/2023 00:00:00 => 12:00:00
        // timeUnit = HOUR, qty = 6, nowOffset = 11:23:45, fromUpperDate = 27/12/2023 00:00:00 => 06:00:00
        // timeUnit = HOUR, qty = 5, nowOffset = 26/12/23 21:23:45, fromUpperDate = 26/12/2023 00:00:00 => 20:00:00    <-- note: from date switch previous to previous DAY
        while (nowOffset.getTime() > fromUpperDate.getTime())
            fromUpperDate = JavaDateWrapper.getDateAltered(fromUpperDate, timeUnit, qty);
        Date fromDate = fromUpperDate;

        // get to date
        // timeUnit = MINUTE, qty = 30, fromDate = 17:00:00 => 17:29:59
        // timeUnit = MINUTE, qty = 30, fromDate = 16:30:00 => 16:59:59       <-- note: slide to previous upper range
        // timeUnit = MINUTE, qty = 5, fromDate = 16:55:00 => 16:59:59        <-- note: will overlap with previous range
        // timeUnit = MINUTE, qty = 7, fromDate = 16:14:00 => 16:20:59        <-- note: same minutes (but 1 hour before) as offset = 0
        // timeUnit = MINUTE, qty = 7, fromDate = 15:49:00 => 15:55:59        <-- note: overlap with previous range 2
        // timeUnit = MINUTE, qty = 7, fromDate = 15:14:00 => 15:20:59        <-- note: same minutes (but 2 hour before) as offset = 0
        // timeUnit = HOUR, qty = 6, fromDate = 12:00:00 => 17:59:59
        // timeUnit = HOUR, qty = 6, fromDate = 06:00:00 => 11:59:59
        // timeUnit = HOUR, qty = 5, fromDate = 20:00:00 => 00:59:59          <-- note: from date switch previous to previous DAY
        Date toDate = JavaDateWrapper.getDateAltered(fromDate, timeUnit, qty);

        return new TimeRangeLimits(fromDate, toDate);
    }


    // Time Range Unit converters

    /**
     * Return the number of milliseconds in a time unit.
     *
     * @param timeUnit the time unit
     * @return the number of milliseconds in a time unit
     * @throws IllegalArgumentException if the given time range unit is not valid.
     */
    public static long timeUnitToMs(int timeUnit) {
        switch (timeUnit) {
            case Calendar.SECOND:
                return 1000L;
            case Calendar.MINUTE:
                return 60 * 1000L;
            case Calendar.HOUR_OF_DAY:
                return 60 * 60 * 1000L;
            case Calendar.DAY_OF_MONTH:
                return 24 * 60 * 60 * 1000L;
            case Calendar.MONTH:
                return 30 * 24 * 60 * 60 * 1000L;
            case Calendar.YEAR:
                return 365 * 24 * 60 * 60 * 1000L;
            default:
                throw newInvalidTimeUnitException(timeUnit);
        }
    }

    /**
     * Return the upper time unit for a given time unit.
     *
     * @param timeUnit the time unit
     * @return the upper time unit
     * @throws IllegalArgumentException if the given time range unit is not valid.
     */
    public static int getUpperTimeUnit(int timeUnit) {
        switch (timeUnit) {
            case Calendar.MILLISECOND:
                return Calendar.SECOND;
            case Calendar.SECOND:
                return Calendar.MINUTE;
            case Calendar.MINUTE:
                return Calendar.HOUR_OF_DAY;
            case Calendar.HOUR_OF_DAY:
                return Calendar.DAY_OF_MONTH;
            case Calendar.DAY_OF_MONTH:
                return Calendar.MONTH;
            case Calendar.MONTH:
            case Calendar.YEAR:
                return Calendar.YEAR;
            default:
                throw newInvalidTimeUnitException(timeUnit);
        }
    }

    /**
     * Return the lower time unit for a given time unit.
     *
     * @param timeUnit the time unit
     * @return the lower time unit
     * @throws IllegalArgumentException if the given time range unit is not valid.
     */
    public static int getLowerTimeUnit(int timeUnit) {
        switch (timeUnit) {
            case Calendar.MILLISECOND:
            case Calendar.SECOND:
                return Calendar.MILLISECOND;
            case Calendar.MINUTE:
                return Calendar.SECOND;
            case Calendar.HOUR_OF_DAY:
                return Calendar.MINUTE;
            case Calendar.DAY_OF_MONTH:
                return Calendar.HOUR_OF_DAY;
            case Calendar.MONTH:
                return Calendar.DAY_OF_MONTH;
            case Calendar.YEAR:
                return Calendar.MONTH;
            default:
                throw newInvalidTimeUnitException(timeUnit);
        }
    }


    // Time Range constants and maps
    // TODO rename unit to period

    /** Name for the {@link Calendar#MINUTE} time range unit. */
    public static final String TIME_RANGE_UNIT_MINUTES = "Minutes";
    /** Name for the {@link Calendar#HOUR_OF_DAY} time range unit. */
    public static final String TIME_RANGE_UNIT_HOURS = "Hours";
    /** Name for the {@link Calendar#DAY_OF_MONTH} time range unit. */
    public static final String TIME_RANGE_UNIT_DAYS = "Days";
    /** Name for the {@link Calendar#MONTH} time range unit. */
    public static final String TIME_RANGE_UNIT_MONTHS = "Months";
    /** Name for the {@link Calendar#YEAR} time range unit. */
    public static final String TIME_RANGE_UNIT_YEARS = "Years";

    /**
     * Map Time Range Units to their common Qtys values.
     */
    public static final Map<Integer, Integer[]> TIME_RANGE_2_QTY_MAP = new HashMap<>();
    static {
        TIME_RANGE_2_QTY_MAP.put(Calendar.YEAR, new Integer[]{1, 3, 5, 7, 10, 15});
        TIME_RANGE_2_QTY_MAP.put(Calendar.MONTH, new Integer[]{1, 3, 4, 6, 12, 24, 48});
        TIME_RANGE_2_QTY_MAP.put(Calendar.DAY_OF_MONTH, new Integer[]{1, 3, 5, 7, 10, 15, 30, 60, 120});
        TIME_RANGE_2_QTY_MAP.put(Calendar.HOUR_OF_DAY, new Integer[]{1, 4, 6, 12, 24, 48, 96});
        TIME_RANGE_2_QTY_MAP.put(Calendar.MINUTE, new Integer[]{5, 15, 30, 60, 120, 240});
    }

    /**
     * Map Time Range Units to their string names.
     */
    public static final Map<Integer, String> TIME_RANGE_2_UNIT_NAMES_MAP = new HashMap<>();
    static {
        // WARNING: order is important
        TIME_RANGE_2_UNIT_NAMES_MAP.put(Calendar.YEAR, TIME_RANGE_UNIT_YEARS);
        TIME_RANGE_2_UNIT_NAMES_MAP.put(Calendar.MONTH, TIME_RANGE_UNIT_MONTHS);
        TIME_RANGE_2_UNIT_NAMES_MAP.put(Calendar.DAY_OF_MONTH, TIME_RANGE_UNIT_DAYS);
        TIME_RANGE_2_UNIT_NAMES_MAP.put(Calendar.HOUR_OF_DAY, TIME_RANGE_UNIT_HOURS);
        TIME_RANGE_2_UNIT_NAMES_MAP.put(Calendar.MINUTE, TIME_RANGE_UNIT_MINUTES);
    }

    /**
     * @return the default Time Range Units list (as {@link Calendar} values).
     */
    public static List<Integer> getTimeRangeUnits() {
        return new ArrayList<>(TIME_RANGE_2_QTY_MAP.keySet());
    }

    /**
     * @return the default Time Range Units names.
     */
    public static List<String> getTimeRangeUnitsStr() {
        return new ArrayList<>(TIME_RANGE_2_UNIT_NAMES_MAP.values());
    }

    /**
     * @param unit the time range unit
     * @return the default Qtys list for given unit.
     * @throws IllegalArgumentException if the given time range unit is not valid.
     */
    public static List<Integer> getTimeRangeQtys(int unit) {
        Integer[] qtys = TIME_RANGE_2_QTY_MAP.get(unit);
        if (qtys == null)
            throw newInvalidTimeUnitException(unit);
        return Arrays.asList(qtys);
    }

    /**
     * @param unit the time range unit
     * @return the default name for given time range unit.
     * @throws IllegalArgumentException if the given time range unit is not valid.
     */
    public static String getTimeRangeStr(int unit) {
        String name = TIME_RANGE_2_UNIT_NAMES_MAP.get(unit);
        if (name == null)
            throw newInvalidTimeUnitException(unit);
        return name;
    }

    /**
     * @param unit the time range unit
     * @return the units list's index for given time range unit.
     *         `-1` if the given time range unit is not valid.
     */
    public static int getTimeRangeUnitIdx(int unit) {
        return getTimeRangeUnits().indexOf(unit);
    }

    /**
     * @param unit the time range unit's name
     * @return the units list's index for given time range unit.
     *         `-1` if the given time range unit is not valid.
     */
    public static int getTimeRangeUnitIdx(String unit) {
        return getTimeRangeUnitsStr().indexOf(unit);
    }

    /**
     * @param unit the time range unit
     * @param qty the time range qty
     * @return the qtys list's index for given qty on given time range unit.
     *         `-1` if the given time range unit is not valid.
     * @throws IllegalArgumentException if the given time range unit is not valid.
     */
    public static int getTimeRangeQtyIdx(int unit, int qty) {
        return getTimeRangeQtys(unit).indexOf(qty);
    }

    /**
     * Check if the given time unit is valid. If given value is not valid, it
     * throws an {@link IllegalArgumentException}.
     * <p>
     * Valid values are constants from {@link Calendar} class.
     *
     * @param timeUnit the time unit to check.
     * @throws IllegalArgumentException if the given time range unit is not valid.
     */
    public static void checkTimeRangeUnitValue(int timeUnit) {
        if (timeUnit != Calendar.MILLISECOND &&
            timeUnit != Calendar.SECOND &&
            timeUnit != Calendar.MINUTE &&
            timeUnit != Calendar.HOUR_OF_DAY &&
            timeUnit != Calendar.DAY_OF_MONTH &&
            timeUnit != Calendar.MONTH &&
            timeUnit != Calendar.YEAR)
            throw newInvalidTimeUnitException(timeUnit);
    }

    /**
     * @param timeUnit the invalid time unit.
     * @return generate a new {@link IllegalArgumentException} for given time unit.
     */
    private static IllegalArgumentException newInvalidTimeUnitException(int timeUnit) {
        return new IllegalArgumentException("Invalid time unit: " + timeUnit);
    }

}
