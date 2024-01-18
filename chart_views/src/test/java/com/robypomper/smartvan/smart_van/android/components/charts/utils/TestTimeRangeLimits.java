package com.robypomper.smartvan.smart_van.android.components.charts.utils;

import static org.junit.Assert.assertEquals;

import com.robypomper.josp.jsl.android.components.charts.utils.TimeRangeLimits;
import com.robypomper.josp.jsl.android.utils.JavaDateWrapper;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TestTimeRangeLimits {
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");


    // calculateRangeTimeLimits_Rounded

    @Test
    public void calculateRangeTimeLimits_Rounded() throws ParseException {
        Date originDate = createDate("2020-01-01 13:02:03.000");

        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 12:02:03.000"),
                createDate("2020-01-01 13:02:03.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, 0, 1));
    }


    // calculateRangeTimeLimits_Rounded - timeUnit

    @Test
    public void calculateRangeTimeLimits_Rounded_Unitss() throws ParseException {
        Date originDate = createDate("2020-01-01 13:02:03.000");

        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 13:01:00.000"),
                createDate("2020-01-01 13:02:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.MINUTE, -1, 1));
        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 12:00:00.000"),
                createDate("2020-01-01 13:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, -1, 1));
        assertTimeRangeLimitsDateEquals(
                createDate("2019-12-31 00:00:00.000"),
                createDate("2020-01-01 00:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.DAY_OF_MONTH, -1, 1));
        assertTimeRangeLimitsDateEquals(
                createDate("2019-12-01 00:00:00.000"),
                createDate("2020-01-01 00:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.MONTH, -1, 1));
        assertTimeRangeLimitsDateEquals(
                createDate("2019-01-01 00:00:00.000"),
                createDate("2020-01-01 00:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.YEAR, -1, 1));
    }


    // calculateRangeTimeLimits_Rounded - offset

    @Test
    public void calculateRangeTimeLimits_Rounded_OffsetZero() throws ParseException {
        Date originDate = createDate("2020-01-01 13:02:03.000");

        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 12:02:03.000"),
                createDate("2020-01-01 13:02:03.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, 0, 1));
    }

    @Test
    public void calculateRangeTimeLimits_Rounded_OffsetNegative() throws ParseException {
        Date originDate = createDate("2020-01-01 13:02:03.000");

        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 12:00:00.000"),
                createDate("2020-01-01 13:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, -1, 1));
        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 11:00:00.000"),
                createDate("2020-01-01 12:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, -2, 1));
        assertTimeRangeLimitsDateEquals(
                createDate("2019-12-31 13:00:00.000"),
                createDate("2019-12-31 14:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, -24, 1));
    }

    @Test
    public void calculateRangeTimeLimits_Rounded_OffsetPositive() throws ParseException {
        Date originDate = createDate("2020-01-01 13:02:03.000");

        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 13:00:00.000"),
                createDate("2020-01-01 14:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, 1, 1));
        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 14:00:00.000"),
                createDate("2020-01-01 15:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, 2, 1));
        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-02 12:00:00.000"),
                createDate("2020-01-02 13:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, 24, 1));
    }


    // calculateRangeTimeLimits_Rounded - qty

    @Test
    public void calculateRangeTimeLimits_Rounded_Qty() throws ParseException {
        Date originDate = createDate("2020-01-01 13:02:03.000");

        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 12:02:03.000"),
                createDate("2020-01-01 13:02:03.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, 0, 1));
        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 11:02:03.000"),
                createDate("2020-01-01 13:02:03.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, 0, 2));
        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 13:02:03.000"),
                createDate("2020-01-01 13:02:03.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, 0, 0));
    }


    // calculateRangeTimeLimits_Rounded - qty/offset

    @Test
    public void calculateRangeTimeLimits_Rounded_Qty_OffsetNegative_1() throws ParseException {
        Date originDate = createDate("2020-01-01 13:02:03.000");

        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 12:00:00.000"),
                createDate("2020-01-01 13:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, -1, 1));
        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 11:00:00.000"),
                createDate("2020-01-01 13:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, -1, 2));
        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 13:00:00.000"),
                createDate("2020-01-01 13:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, -1, 0));
    }

    @Test
    public void calculateRangeTimeLimits_Rounded_Qty_OffsetNegative_2() throws ParseException {
        Date originDate = createDate("2020-01-01 13:02:03.000");

        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 11:00:00.000"),
                createDate("2020-01-01 12:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, -2, 1));
        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 09:00:00.000"),
                createDate("2020-01-01 11:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, -2, 2));
        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 13:00:00.000"),
                createDate("2020-01-01 13:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, -2, 0));
    }

    @Test
    public void calculateRangeTimeLimits_Rounded_Qty_OffsetNegative_24() throws ParseException {
        Date originDate = createDate("2020-01-01 13:02:03.000");

        assertTimeRangeLimitsDateEquals(
                createDate("2019-12-31 13:00:00.000"),
                createDate("2019-12-31 14:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, -24, 1));
        assertTimeRangeLimitsDateEquals(
                createDate("2019-12-30 13:00:00.000"),
                createDate("2019-12-30 15:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, -24, 2));
        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 13:00:00.000"),
                createDate("2020-01-01 13:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, -24, 0));
    }

    @Test
    public void calculateRangeTimeLimits_Rounded_Qty_OffsetPositive_1() throws ParseException {
        Date originDate = createDate("2020-01-01 13:02:03.000");

        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 13:00:00.000"),
                createDate("2020-01-01 14:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, 1, 1));
        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 13:00:00.000"),
                createDate("2020-01-01 15:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, 1, 2));
        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 13:00:00.000"),
                createDate("2020-01-01 13:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, 1, 0));
    }

    @Test
    public void calculateRangeTimeLimits_Rounded_Qty_OffsetPositive_2() throws ParseException {
        Date originDate = createDate("2020-01-01 13:02:03.000");

        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 14:00:00.000"),
                createDate("2020-01-01 15:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, 2, 1));
        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 15:00:00.000"),
                createDate("2020-01-01 17:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, 2, 2));
        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 13:00:00.000"),
                createDate("2020-01-01 13:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, 2, 0));
    }

    @Test
    public void calculateRangeTimeLimits_Rounded_Qty_OffsetPositive_24() throws ParseException {
        Date originDate = createDate("2020-01-01 13:02:03.000");

        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-02 12:00:00.000"),
                createDate("2020-01-02 13:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, 24, 1));
        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-03 11:00:00.000"),
                createDate("2020-01-03 13:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, 24, 2));
        assertTimeRangeLimitsDateEquals(
                createDate("2020-01-01 13:00:00.000"),
                createDate("2020-01-01 13:00:00.000"),
                TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, Calendar.HOUR_OF_DAY, 24, 0));
    }



    // continuity

    @Test
    public void calculateRangeTimeLimits_Rounded_OffsetContinuity() throws ParseException {
        Date originDate = createDate("2020-01-01 00:15:30.555");
        testCalculateRangeTimeLimits_Continuous_OffsetContinuity(originDate, Calendar.HOUR_OF_DAY, 1, 3);
        testCalculateRangeTimeLimits_Continuous_OffsetContinuity(originDate, Calendar.HOUR_OF_DAY, 1, 25);
    }


    // continuity - timeUnit

    @Test
    public void calculateRangeTimeLimits_Rounded_OffsetContinuity_Units() throws ParseException {
        Date originDate = createDate("2020-01-01 00:15:30.555");
        testCalculateRangeTimeLimits_Continuous_OffsetContinuity(originDate, Calendar.MINUTE, 1, 3);
        testCalculateRangeTimeLimits_Continuous_OffsetContinuity(originDate, Calendar.HOUR_OF_DAY, 1, 3);
        testCalculateRangeTimeLimits_Continuous_OffsetContinuity(originDate, Calendar.DAY_OF_MONTH, 1, 3);
        testCalculateRangeTimeLimits_Continuous_OffsetContinuity(originDate, Calendar.MONTH, 1, 3);
        testCalculateRangeTimeLimits_Continuous_OffsetContinuity(originDate, Calendar.YEAR, 1, 3);
    }


    // continuity - qty

    @Test
    public void calculateRangeTimeLimits_Rounded_OffsetContinuity_Qty() throws ParseException {
        Date originDate = createDate("2020-01-01 00:15:30.555");
        testCalculateRangeTimeLimits_Continuous_OffsetContinuity(originDate, Calendar.HOUR_OF_DAY, 1, 3);
        testCalculateRangeTimeLimits_Continuous_OffsetContinuity(originDate, Calendar.HOUR_OF_DAY, 2, 3);
        testCalculateRangeTimeLimits_Continuous_OffsetContinuity(originDate, Calendar.HOUR_OF_DAY, 0, 3);
    }



    // Utils

    private void assertTimeRangeLimitsDateEquals(Date expectedFrom, Date expectedTo, TimeRangeLimits rangeTimeLimits) {
        Date timeRangeFrom = rangeTimeLimits.getFromDate();
        Date timeRangeTo = rangeTimeLimits.getToDate();
        long diffFrom = expectedFrom.getTime() - timeRangeFrom.getTime();
        long diffTo = expectedTo.getTime() - timeRangeTo.getTime();

        System.out.println("Compare given   : " + sdf.format(timeRangeFrom) + " - " + sdf.format(timeRangeTo));
        System.out.println("   with expected: " + sdf.format(expectedFrom) + " - " + sdf.format(expectedTo));
        System.out.println("   with         : " + diffFrom + " - " + diffTo);
        assertEquals(String.format("DataSet status's fromDate (%s) is not as expected (%s)", sdf.format(timeRangeFrom), sdf.format(expectedFrom)),
                expectedFrom, timeRangeFrom);
        assertEquals(String.format("DataSet status's toDate (%s) is not as expected (%s)", sdf.format(timeRangeTo), sdf.format(expectedTo)),
                expectedTo.getTime(), timeRangeTo.getTime());
    }

    private Date createDate(String strDate) throws ParseException {
        return sdf.parse(strDate);
    }

    private void testCalculateRangeTimeLimits_Continuous_OffsetContinuity(Date originDate, int timeUnit, int qty, int halfRange) {
        testCalculateRangeTimeLimits_Continuous_OffsetContinuity(originDate, timeUnit, qty, halfRange, false);
    }

    private void testCalculateRangeTimeLimits_Continuous_OffsetContinuity(Date originDate, int timeUnit, int qty, int halfRange, boolean skippTest) {
        System.out.println("\n### testOffsetContinuity  " + timeUnit + " - " + qty + " ### " + sdf.format(originDate));
        for (int offset = (halfRange * -1); offset <= halfRange; offset++) {
            System.out.println("Offset: " + offset + " - " + timeUnit + " - " + qty);

            //Date expectedFrom = addAndResetTimeRangeUnitsToDate(originDate, timeUnit, offset < 0 ? offset : offset - 1);
            //Date expectedTo = addAndResetTimeRangeUnitsToDate(expectedFrom, timeUnit, 1);
            Date expectedFrom = addAndResetTimeRangeUnitsToDate(originDate, timeUnit, qty * (offset < 0 ? offset : offset - 1));
            Date expectedTo = addAndResetTimeRangeUnitsToDate(expectedFrom, timeUnit, qty);

            TimeRangeLimits hl = TimeRangeLimits.calculateTimeRangeLimits_Rounded(originDate, timeUnit, offset, qty);
            if (skippTest || offset == 0) {
                System.out.println("Compare given   : " + sdf.format(hl.getFromDate()) + " - " + sdf.format(hl.getToDate()));
                System.out.println("   with expected: " + sdf.format(expectedFrom) + " - " + sdf.format(expectedTo));
            } else
                assertTimeRangeLimitsDateEquals(expectedFrom, expectedTo, hl);
        }
    }

    private Date addAndResetTimeRangeUnitsToDate(Date date, int timeUnit, int units) {
        return resetTimeRangeUnitsToDate(addTimeRangeUnitsToDate(date, timeUnit, units), timeUnit);
    }

    private Date addTimeRangeUnitsToDate(Date date, int timeUnit, int units) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(timeUnit, units);
        return calendar.getTime();
    }

    private Date resetTimeRangeUnitsToDate(Date date, int timeUnit) {
        return JavaDateWrapper.getDateExact(date, timeUnit);
    }

}