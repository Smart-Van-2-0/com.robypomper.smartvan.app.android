package com.robypomper.smartvan.smart_van.android.components.charts.formatters;

import static org.junit.Assert.assertEquals;

import com.robypomper.java.JavaDate;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartDateTimeFormatter;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestChartDateTimeFormatter {

    @Test
    public void testBase() {
        final SimpleDateFormat LOG_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        String pattern = "yyyy-MM-dd HH:mm:ss.SSS";
        int timeUnit = Calendar.SECOND;
        ChartDateTimeFormatter formatter
                = new ChartDateTimeFormatter(pattern, timeUnit);

        Date originalDate = JavaDate.getNowDate();
        float valueDate = formatter.from(originalDate);
        Date resultDate = formatter.toDate(valueDate);

        originalDate = new Date(JavaDate.getDateExact(originalDate, timeUnit).getTime());
        System.out.printf("origin      : %s%n", LOG_SDF.format(originalDate));
        System.out.printf("median      : %f%n", valueDate);
        System.out.printf("result      : %s%n", LOG_SDF.format(resultDate));
        assertEquals(originalDate.getTime(), resultDate.getTime());

        Date originalDatePast = JavaDate.getDateExactAltered(timeUnit, timeUnit, -1);
        float valueDatePast = formatter.from(originalDatePast);
        Date resultDatePast = formatter.toDate(valueDatePast);
        System.out.printf("origin      : %s%n", LOG_SDF.format(originalDatePast));
        System.out.printf("median      : %f%n", valueDatePast);
        System.out.printf("result      : %s%n", LOG_SDF.format(resultDatePast));
        assertEquals(originalDatePast.getTime(), resultDatePast.getTime());

        Date originalDateFuture = JavaDate.getDateExactAltered(timeUnit, timeUnit, 1);
        float valueDateFuture = formatter.from(originalDateFuture);
        Date resultDateFuture = formatter.toDate(valueDateFuture);
        System.out.printf("origin      : %s%n", LOG_SDF.format(originalDateFuture));
        System.out.printf("median      : %f%n", valueDateFuture);
        System.out.printf("result      : %s%n", LOG_SDF.format(resultDateFuture));
        assertEquals(originalDateFuture, resultDateFuture);
    }

    @Test
    public void testSwitchTimeUnit() {
        final SimpleDateFormat LOG_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        String pattern = "yyyy-MM-dd HH:mm:ss.SSS";
        int timeUnit = Calendar.SECOND;
        ChartDateTimeFormatter formatter
                = new ChartDateTimeFormatter(pattern, timeUnit);

        Date originalDate = JavaDate.getNowDate();
        float valueDate = formatter.from(originalDate);
        Date resultDate = formatter.toDate(valueDate);

        originalDate = new Date(JavaDate.getDateExact(originalDate, timeUnit).getTime());
        System.out.printf("origin      : %s%n", LOG_SDF.format(originalDate));
        System.out.printf("median      : %f%n", valueDate);
        System.out.printf("result      : %s%n", LOG_SDF.format(resultDate));
        assertEquals(originalDate.getTime(), resultDate.getTime());


        timeUnit = Calendar.HOUR_OF_DAY;
        formatter.setTimeUnit(timeUnit);

        originalDate = JavaDate.getNowDate();
        valueDate = formatter.from(originalDate);
        resultDate = formatter.toDate(valueDate);

        originalDate = new Date(JavaDate.getDateExact(originalDate, timeUnit).getTime());
        System.out.printf("origin      : %s%n", LOG_SDF.format(originalDate));
        System.out.printf("median      : %f%n", valueDate);
        System.out.printf("result      : %s%n", LOG_SDF.format(resultDate));
        assertEquals(originalDate.getTime(), resultDate.getTime());
    }

    @Test
    public void test_LimitsSeconds() {
        /**
         timeUnit:  13, timeUnitTest:   1, count:        9, lastDate: 2009-01-01 00:00:00.000 (1230764400000)
         timeUnit:  13, timeUnitTest:   2, count:      106, lastDate: 2008-11-01 00:00:00.000 (1225494000000)
         timeUnit:  13, timeUnitTest:   5, count:     3222, lastDate: 2008-10-27 00:00:00.000 (1225062000000)
         timeUnit:  13, timeUnitTest:  11, count:    74568, lastDate: 2008-07-04 01:00:00.000 (1215126000000)
         timeUnit:  13, timeUnitTest:  12, count:  1118483, lastDate: 2002-02-15 18:23:00.000 (1013793780000)
         timeUnit:  13, timeUnitTest:  13, count: 16780817, lastDate: 2000-07-13 06:20:17.000 (963462017000)
         */
        final SimpleDateFormat LOG_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        long Y2000 = 946684800000L; // 2000-01-01 00:00:00
        Date now = new Date(Y2000);
        System.out.println(String.format("now: %s", LOG_SDF.format(now)));

        String pattern = "yyyy-MM-dd HH:mm:ss.SSS";
        int timeUnit = Calendar.SECOND;
        Map<Integer, Integer> timeUnitTests = new HashMap<>();
        timeUnitTests.put(Calendar.SECOND,          16780817);  // 2000-07-13 06:20:17.000 963462017000
        timeUnitTests.put(Calendar.MINUTE,          1118483);   // 2002-02-15 18:23:00.000 1013793780000
        timeUnitTests.put(Calendar.HOUR_OF_DAY,     74568);     // 2008-07-04 01:00:00.000 1215126000000
        timeUnitTests.put(Calendar.DAY_OF_MONTH,    3222);      // 2008-10-27 00:00:00.000 1225062000000
        timeUnitTests.put(Calendar.MONTH,           106);       // 2008-11-01 00:00:00.000 1225494000000
        timeUnitTests.put(Calendar.YEAR,            9);         // 2009-01-01 00:00:00.000 1230764400000
        ChartDateTimeFormatter formatter
                = new ChartDateTimeFormatter(pattern, now, timeUnit);

        for (Map.Entry<Integer, Integer> timeUnitTest : timeUnitTests.entrySet()) {
            int count = -1;
            boolean ok = true;
            Date originalDate = null, resultDate;
            while (ok) {
                count++;
                originalDate = JavaDate.getDateExactAltered(now, timeUnitTest.getKey(), timeUnitTest.getKey(), (int)count);
                float valueDate = formatter.from(originalDate);
                resultDate = formatter.toDate(valueDate);
                ok = originalDate.getTime() == resultDate.getTime();
            }
            System.out.println(String.format("timeUnit: %3d, timeUnitTest: %3d, count: %8d, lastDate: %s (%d)",
                    timeUnit, timeUnitTest.getKey(), count, LOG_SDF.format(originalDate), originalDate.getTime()));
            assertEquals((int)timeUnitTest.getValue(), count);
        }

    }

    @Test
    public void test_LimitsSeconds_DoubleError() {
        final SimpleDateFormat LOG_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        long Y2000 = 946684800000L; // 2000-01-01 00:00:00
        Date now = new Date(Y2000);
        System.out.println(String.format("now: %s", LOG_SDF.format(now)));

        String pattern = "yyyy-MM-dd HH:mm:ss.SSS";
        int timeUnit = Calendar.SECOND;
        Map<Integer, Integer> timeUnitTests = new HashMap<>();
        timeUnitTests.put(Calendar.SECOND,          16780817+2);  // 2000-07-13 06:20:17.000 963462017000
        timeUnitTests.put(Calendar.MINUTE,          1118483+2);   // 2002-02-15 18:23:00.000 1013793780000
        timeUnitTests.put(Calendar.HOUR_OF_DAY,     74568+2);     // 2008-07-04 01:00:00.000 1215126000000
        timeUnitTests.put(Calendar.DAY_OF_MONTH,    3222+1);      // 2008-10-27 00:00:00.000 1225062000000
        timeUnitTests.put(Calendar.MONTH,           106+1);       // 2008-11-01 00:00:00.000 1225494000000
        timeUnitTests.put(Calendar.YEAR,            9+1);         // 2009-01-01 00:00:00.000 1230764400000
        ChartDateTimeFormatter formatter
                = new ChartDateTimeFormatter(pattern, now, timeUnit);

        for (Map.Entry<Integer, Integer> timeUnitTest : timeUnitTests.entrySet()) {
            int count = -1;
            int count_err = 0;
            Date originalDate = null, resultDate;
            while (count_err < 2) {
                count++;
                originalDate = JavaDate.getDateExactAltered(now, timeUnitTest.getKey(), timeUnitTest.getKey(), (int)count);
                float valueDate = formatter.from(originalDate);
                resultDate = formatter.toDate(valueDate);
                if (originalDate.getTime() != resultDate.getTime()) {
                    count_err++;
                    System.out.println("Error " + count + "# at " + LOG_SDF.format(originalDate) + " " + originalDate.getTime());
                    System.out.println("      actual " + LOG_SDF.format(resultDate) + " " + resultDate.getTime());
                }
            }
            System.out.println(LOG_SDF.format(originalDate) + " " + originalDate.getTime());
            assertEquals((int)timeUnitTest.getValue(), count);
        }
    }

    @Test
    public void test_LimitsHour() {
        /**
         timeUnit:  11, timeUnitTest:   1, count:      9, lastDate: 2009-01-01 00:00:00.000 (1230764400000)
         timeUnit:  11, timeUnitTest:   2, count:    106, lastDate: 2008-11-01 00:00:00.000 (1225494000000)
         timeUnit:  11, timeUnitTest:   5, count:   3222, lastDate: 2008-10-27 00:00:00.000 (1225062000000)
         timeUnit:  11, timeUnitTest:  11, count:  74568, lastDate: 2008-07-04 01:00:00.000 (1215126000000)
         timeUnit:  11, timeUnitTest:  12, count:      1, lastDate: 2000-01-01 01:01:00.000 (946684860000)
         timeUnit:  11, timeUnitTest:  13, count:      1, lastDate: 2000-01-01 01:00:01.000 (946684801000)
         */
        final SimpleDateFormat LOG_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        long Y2000 = 946684800000L; // 2000-01-01 00:00:00
        Date now = new Date(Y2000);
        System.out.println(String.format("now: %s", LOG_SDF.format(now)));

        String pattern = "yyyy-MM-dd HH:mm:ss.SSS";
        int timeUnit = Calendar.HOUR_OF_DAY;
        Map<Integer, Integer> timeUnitTests = new HashMap<>();
        timeUnitTests.put(Calendar.SECOND,          1);         // 2000-07-13 06:20:17.000 963462017000
        timeUnitTests.put(Calendar.MINUTE,          1);         // 2002-02-15 18:23:00.000 1013793780000
        timeUnitTests.put(Calendar.HOUR_OF_DAY,     16777217);  // 3913-12-08 18:00:00.000 (61344666000000)
        timeUnitTests.put(Calendar.DAY_OF_MONTH,    699051);    // 3913-12-09 00:00:00.000 (61344687600000)
        timeUnitTests.put(Calendar.MONTH,           22968);     // 3914-01-01 00:00:00.000 (61346674800000)
        timeUnitTests.put(Calendar.YEAR,            1914);      // 3914-01-01 00:00:00.000 (61346674800000)
        ChartDateTimeFormatter formatter
                = new ChartDateTimeFormatter(pattern, now, timeUnit);

        for (Map.Entry<Integer, Integer> timeUnitTest : timeUnitTests.entrySet()) {
            int count = -1;
            boolean ok = true;
            Date originalDate = null, resultDate;
            while (ok) {
                count++;
                originalDate = JavaDate.getDateExactAltered(now, timeUnitTest.getKey(), timeUnitTest.getKey(), (int)count);
                float valueDate = formatter.from(originalDate);
                resultDate = formatter.toDate(valueDate);
                ok = originalDate.getTime() == resultDate.getTime();
            }
            System.out.println(String.format("timeUnit: %3d, timeUnitTest: %3d, count: %8d, lastDate: %s (%d)",
                    timeUnit, timeUnitTest.getKey(), count, LOG_SDF.format(originalDate), originalDate.getTime()));
            assertEquals((int)timeUnitTest.getValue(), count);
        }

    }

}