package com.robypomper.smartvan.smart_van.android.components.charts;

import static org.junit.Assert.assertEquals;

import android.graphics.Color;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.robypomper.java.JavaDate;
import com.robypomper.josp.jsl.android.components.charts.MPAndroidChartUtils;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartDateTimeFormatter;
import com.robypomper.josp.jsl.android.components.charts.utils.TimeRangeLimits;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/** @noinspection UnnecessaryLocalVariable*/
@RunWith(MockitoJUnitRunner.class)
public class TestMPAndroidChartUtilsDataSetsModifiers {

    private static final long FIX_1_HOUR = -60 * 60 * 1000;
    @Mock
    private final Color mockColor = new Color();
    private TimeRangeLimits newTimeRangeLimits(Date from, Date to) {
        return new TimeRangeLimits(from, to);
    }
    

    // reduceMiddleValueLineDataSet

    @Test
    public void testReduceMiddleDataSet_count() {
        final int ITEMS_COUNT = 100;
        final int MAX_COUNT = 10;
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();

        DataSet<?> lineDataSet = populateLineDataSet(JavaDate.getNowDate(), ITEMS_COUNT, FORMATTER);
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reduceMiddleValueDataSet(lineDataSet, MAX_COUNT);
        assertEquals(MAX_COUNT, reducedLineDataSet.getEntryCount());
    }

    @Test
    public void testReduceMiddleDataSet_firstMidAndLast() {
        final int ITEMS_COUNT = 102;
        final int MAX_COUNT = 9;
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();

        DataSet<?> lineDataSet = populateLineDataSet(JavaDate.getNowDate(), ITEMS_COUNT, FORMATTER);
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reduceMiddleValueDataSet(lineDataSet, MAX_COUNT);

        float first = lineDataSet.getEntryForIndex(0).getY();
        float firstReduced = reducedLineDataSet.getEntryForIndex(0).getY();
        assertEquals(first, firstReduced, 0);

        float middle = lineDataSet.getEntryForIndex(ITEMS_COUNT / 2).getY();   // -> 51
        float middleReduced = reducedLineDataSet.getEntryForIndex(MAX_COUNT / 2).getY(); // -> 4
        assertEquals(middle, middleReduced, 0);

        float last = lineDataSet.getEntryForIndex(ITEMS_COUNT - 1).getY();
        float lastReduced = reducedLineDataSet.getEntryForIndex(MAX_COUNT - 1).getY();
        assertEquals(last, lastReduced, 0);
    }


    // reduceAvgValueLineDataSet

    @Test
    public void testReduceAvgDataSet_count() {
        final int ITEMS_COUNT = 100;
        final int MAX_COUNT = 10;
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        final Class<?> ENTRY_CLASS = Entry.class;

        DataSet<?> lineDataSet = populateLineDataSet(JavaDate.getNowDate(), ITEMS_COUNT, FORMATTER);
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reduceAvgDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, FORMATTER);
        assertEquals(MAX_COUNT, reducedLineDataSet.getEntryCount());
    }

    @Test
    public void testReduceAvgDataSet_firstMidAndLast() {
        final int ITEMS_COUNT = 102;
        final int MAX_COUNT = 9;
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        final Class<?> ENTRY_CLASS = Entry.class;

        DataSet<?> lineDataSet = populateLineDataSet(JavaDate.getNowDate(), ITEMS_COUNT, FORMATTER);
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reduceAvgDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, FORMATTER);
        for (Entry h : reducedLineDataSet.getValues())
            System.out.println("id: " + h.getX() + " value: " + h.getY());

        float firstValue = lineDataSet.getEntryForIndex(0).getY();
        float firstReduced = reducedLineDataSet.getEntryForIndex(0).getY();
        assertEquals(firstValue, firstReduced, 0);

        float last = lineDataSet.getEntryForIndex(ITEMS_COUNT - 1).getY();
        float lastReduced = reducedLineDataSet.getEntryForIndex(MAX_COUNT - 1).getY();
        assertEquals(last, lastReduced, 0);

        /*
         [1.0] ==> 1.0
         [2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0] ==> 9.0
         [17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0] ==> 24.0
         [32.0, 33.0, 34.0, 35.0, 36.0, 37.0, 38.0, 39.0, 40.0, 41.0, 42.0, 43.0, 44.0, 45.0, 46.0] ==> 39.0
         [47.0, 48.0, 49.0, 50.0, 51.0, 52.0, 53.0, 54.0, 55.0, 56.0, 57.0, 58.0, 59.0, 60.0, 61.0] ==> 54.0
         [62.0, 63.0, 64.0, 65.0, 66.0, 67.0, 68.0, 69.0, 70.0, 71.0, 72.0, 73.0, 74.0, 75.0, 76.0] ==> 69.0
         [77.0, 78.0, 79.0, 80.0, 81.0, 82.0, 83.0, 84.0, 85.0, 86.0, 87.0, 88.0, 89.0, 90.0, 91.0] ==> 84.0
         [92.0, 93.0, 94.0, 95.0, 96.0, 97.0, 98.0, 99.0, 100.0, 101.0] ==> 96.5
         [102.0] ==> 102.0
         */
        float value;
        float[] expectedValues = {1.0F, 9.0F, 24.0F, 39.0F, 54.0F, 69.0F, 84.0F, 96.5F, 102.0F};
        for (int i = 0; i < MAX_COUNT; i++) {
            value = reducedLineDataSet.getEntryForIndex(i).getY();
            assertEquals(expectedValues[i], value, 0);
        }
    }


    // reducePartitionLineDataSet

    /*
     *  0.00    1.00    2.00    3.00    4.00    <- First and last
     *      1.30    2.30    3.30    4.30        <- Intermediate
     *
     * 00:00 ==> 0.0       1
     * 00:20 ==> 1.0       2
     * 00:40 ==> 2.0       2
     * 01:00 ==> 3.0       3
     * 01:20 ==> 4.0       3
     * 01:40 ==> 5.0       3
     * 02:00 ==> 6.0       4
     * 02:20 ==> 7.0       4
     * 02:40 ==> 8.0       4
     * 03:00 ==> 9.0       5
     * 03:20 ==> 10.0      5
     * 03:40 ==> 11.0      5
     * 04:00 ==> 12.0      6
     */
    
    @Test
    public void testReducePartitionDataSet_count() {
        final int MAX_COUNT = 6;        // 1st + 4 intermediate + Last

        // FROM: 2000-01-01 00:00:00, TO: +4 Hours, NOW = FROM
        long Y2000 = 946684800000L + FIX_1_HOUR; // 2000-01-01 00:00:00
        final Date FROM = new Date(Y2000);
        final Date TO = new Date(Y2000 + (4 * 60 * 60 * 1000)); // 4 Hours
        final TimeRangeLimits TIME_LIMITS = newTimeRangeLimits(FROM, TO);
        final long SAMPLE_RATE = 20 * 60 * 1000;  // 20 minutes -> 3 samples per hour -> 12 samples
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        FORMATTER.setRelativeDate(FROM);
        final Class<?> ENTRY_CLASS = Entry.class;

        // 13 samples:
        //  00:00 > 0.0
        //  ...
        //  04:00 > 12.0
        DataSet<?> lineDataSet = populateLineDataSetTime(FROM, TO, FORMATTER, SAMPLE_RATE);

        // Reduce by partition
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reducePartitionDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, FORMATTER, TIME_LIMITS);

        // Test reduced data set's size
        assertEquals(MAX_COUNT, reducedLineDataSet.getEntryCount());
    }

    @Deprecated
    @Test
    public void testReducePartitionDataSet_firstMidAndLast() {
        final int MAX_COUNT = 6;        // 1st + 4 intermediate + Last

        // FROM: 2000-01-01 00:00:00, TO: +4 Hours, NOW = FROM
        long Y2000 = 946684800000L + FIX_1_HOUR; // 2000-01-01 00:00:00
        final Date FROM = new Date(Y2000);
        final Date TO = new Date(Y2000 + (4 * 60 * 60 * 1000)); // 4 Hours
        final TimeRangeLimits TIME_LIMITS = newTimeRangeLimits(FROM, TO);
        final long SAMPLE_RATE = 20 * 60 * 1000;  // 20 minutes -> 3 samples per hour -> 12 samples
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        FORMATTER.setRelativeDate(FROM);
        final Class<?> ENTRY_CLASS = Entry.class;

        // 13 samples:
        //  00:00 > 0.0
        //  ...
        //  04:00 > 12.0
        DataSet<?> lineDataSet = populateLineDataSetTime(FROM, TO, FORMATTER, SAMPLE_RATE);

        // Reduce by partition
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reducePartitionDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, FORMATTER, TIME_LIMITS);
        for (Entry h : reducedLineDataSet.getValues())
            System.out.println("id: " + FORMATTER.toString(h.getX()) + " value: " + h.getY());

        // Test first partition against first value
        float firstValue = lineDataSet.getEntryForIndex(0).getY();
        float firstReduced = reducedLineDataSet.getEntryForIndex(0).getY();
        assertEquals(firstValue, firstReduced, 0);

        // Test last partition against last value
        float last = lineDataSet.getEntryForIndex(lineDataSet.getEntryCount() - 1).getY();
        float lastReduced = reducedLineDataSet.getEntryForIndex(MAX_COUNT - 1).getY();
        assertEquals(last, lastReduced, 0);

        // Test intermediate values
        /*
        Partition 00:00 -  0.0:  0.0,
        Partition 00:30 -  1.5:  1.0,  2.0,
        Partition 01:30 -  4.0:  3.0,  4.0,  5.0,
        Partition 02:30 -  7.0:  6.0,  7.0,  8.0,
        Partition 03:30 - 10.0:  9.0, 10.0, 11.0
        Partition 04:00 - 12.0: 12.0,
         */
        float value;
        float[] expectedValues = {0.0F, 1.5F, 4.0F, 7.0F, 10.0F, 12.0F};
        for (int i = 0; i < MAX_COUNT; i++) {
            value = reducedLineDataSet.getEntryForIndex(i).getY();
            assertEquals(expectedValues[i], value, 0);
        }
    }

    @Test
    public void testReducePartitionDataSet_Complete() {
        final int MAX_COUNT = 6;        // 1st + 4 intermediate + Last

        // FROM: 2000-01-01 00:00:00, TO: +4 Hours, NOW = FROM
        long Y2000 = 946684800000L + FIX_1_HOUR; // 2000-01-01 00:00:00
        final Date FROM = new Date(Y2000);
        final Date TO = new Date(Y2000 + (4 * 60 * 60 * 1000)); // 4 Hours
        final TimeRangeLimits TIME_LIMITS = newTimeRangeLimits(FROM, TO);
        final long SAMPLE_RATE = 20 * 60 * 1000;  // 20 minutes -> 3 samples per hour -> 12 samples
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        FORMATTER.setRelativeDate(FROM);
        final Class<?> ENTRY_CLASS = Entry.class;

        // 13 samples:
        //  00:00 > 0.0
        //  ...
        //  04:00 > 12.0
        DataSet<?> lineDataSet = populateLineDataSetTime(FROM, TO, FORMATTER, SAMPLE_RATE);

        // Reduce by partition
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reducePartitionDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, FORMATTER, TIME_LIMITS);
        for (Entry h : reducedLineDataSet.getValues())
            System.out.println("id: " + FORMATTER.toString(h.getX()) + " value: " + h.getY());

        // Test first partition against first value
        float firstValue = lineDataSet.getEntryForIndex(0).getY();
        float firstReduced = reducedLineDataSet.getEntryForIndex(0).getY();
        assertEquals(firstValue, firstReduced, 0);

        // Test last partition against last value
        float last = lineDataSet.getEntryForIndex(lineDataSet.getEntryCount() - 1).getY();
        float lastReduced = reducedLineDataSet.getEntryForIndex(MAX_COUNT - 1).getY();
        assertEquals(last, lastReduced, 0);

        // Test intermediate values
        /*
        Partition 00:00 -  0.0:  0.0,
        Partition 00:30 -  1.5:  1.0,  2.0,
        Partition 01:30 -  4.0:  3.0,  4.0,  5.0,
        Partition 02:30 -  7.0:  6.0,  7.0,  8.0,
        Partition 03:30 - 10.0:  9.0, 10.0, 11.0
        Partition 04:00 - 12.0: 12.0,
         */
        float value;
        float[] expectedValues = {0.0F, 1.5F, 4.0F, 7.0F, 10.0F, 12.0F};
        for (int i = 0; i < MAX_COUNT; i++) {
            value = reducedLineDataSet.getEntryForIndex(i).getY();
            assertEquals(expectedValues[i], value, 0);
        }
    }

    @Test
    public void testReducePartitionDataSet_CompletePartialStart() {
        final int MAX_COUNT = 6;        // 1st + 4 intermediate + Last

        // FROM: 2000-01-01 00:00:00, TO: +4 Hours, NOW = FROM
        long Y2000 = 946684800000L + FIX_1_HOUR; // 2000-01-01 00:00:00
        final Date FROM = new Date(Y2000);
        final Date TO = new Date(Y2000 + (4 * 60 * 60 * 1000)); // 4 Hours
        final TimeRangeLimits TIME_LIMITS = newTimeRangeLimits(FROM, TO);
        final long SAMPLE_RATE = 20 * 60 * 1000;  // 20 minutes -> 3 samples per hour -> 12 samples
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        FORMATTER.setRelativeDate(FROM);
        final Class<?> ENTRY_CLASS = Entry.class;

        // 13 samples:
        final Date REMOVE_FROM = FROM;
        final Date REMOVE_TO = new Date(FROM.getTime() + (60 * 1000)); // 1 Minute
        //                  MISSING 00:00 ==> 0.0
        //  00:20 ==> 1.0
        //  ...
        //  04:00 ==> 12.0
        DataSet<?> lineDataSet = populateLineDataSetTime(FROM, TO, FORMATTER, SAMPLE_RATE);
        lineDataSet = removeEntries(lineDataSet, REMOVE_FROM, REMOVE_TO, FORMATTER);
        print(lineDataSet, FORMATTER);

        // Reduce by partition
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reducePartitionDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, FORMATTER, TIME_LIMITS);
        for (Entry h : reducedLineDataSet.getValues())
            System.out.println("id: " + FORMATTER.toString(h.getX()) + " value: " + h.getY());

        // Test first partition against first value
        float firstValue = lineDataSet.getEntryForIndex(0).getY();
        float firstReduced = reducedLineDataSet.getEntryForIndex(0).getY();
        assertEquals(firstValue, firstReduced, 0);

        // Test last partition against last value
        float last = lineDataSet.getEntryForIndex(lineDataSet.getEntryCount() - 1).getY();
        float lastReduced = reducedLineDataSet.getEntryForIndex(MAX_COUNT - 1).getY();
        assertEquals(last, lastReduced, 0);

        // Test intermediate values
        /*
        Partition 00:00 -  1.0:  1.0,
        Partition 00:30 -  2.0:  2.0,
        Partition 01:30 -  4.0:  3.0,  4.0,  5.0,
        Partition 02:30 -  7.0:  6.0,  7.0,  8.0,
        Partition 03:30 - 10.0:  9.0, 10.0, 11.0
        Partition 04:00 - 12.0: 12.0,
         */
        float value;
        float[] expectedValues = {1.0F, 2.0F, 4.0F, 7.0F, 10.0F, 12.0F};
        for (int i = 0; i < MAX_COUNT; i++) {
            value = reducedLineDataSet.getEntryForIndex(i).getY();
            assertEquals(expectedValues[i], value, 0);
        }
    }

    @Test
    public void testReducePartitionDataSet_CompletePartialMiddle() {
        final int MAX_COUNT = 6;        // 1st + 4 intermediate + Last

        // FROM: 2000-01-01 00:00:00, TO: +4 Hours, NOW = FROM
        long Y2000 = 946684800000L + FIX_1_HOUR; // 2000-01-01 00:00:00
        final Date FROM = new Date(Y2000);
        final Date TO = new Date(Y2000 + (4 * 60 * 60 * 1000)); // 4 Hours
        final TimeRangeLimits TIME_LIMITS = newTimeRangeLimits(FROM, TO);
        final long SAMPLE_RATE = 20 * 60 * 1000;  // 20 minutes -> 3 samples per hour -> 12 samples
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        FORMATTER.setRelativeDate(FROM);
        final Class<?> ENTRY_CLASS = Entry.class;

        // 13 samples:
        final Date REMOVE_FROM = new Date(FROM.getTime() + (70 * 60 * 1000)); // 1 Hour + 10 Minute;
        final Date REMOVE_TO = new Date(FROM.getTime() + (90 * 60 * 1000)); // 1 Hour + 30 Minute
        //  00:00 ==> 0.0
        //  00:20 ==> 1.0
        //  ...
        //  01:00 ==> 3.0
        //                  MISSING 01:20 ==> 4.0
        //  01:40 ==> 5.0
        //  ...
        //  04:00 ==> 12.0
        DataSet<?> lineDataSet = populateLineDataSetTime(FROM, TO, FORMATTER, SAMPLE_RATE);
        lineDataSet = removeEntries(lineDataSet, REMOVE_FROM, REMOVE_TO, FORMATTER);
        print(lineDataSet, FORMATTER);

        // Reduce by partition
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reducePartitionDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, FORMATTER, TIME_LIMITS);
        for (Entry h : reducedLineDataSet.getValues())
            System.out.println("id: " + FORMATTER.toString(h.getX()) + " value: " + h.getY());

        // Test first partition against first value
        float firstValue = lineDataSet.getEntryForIndex(0).getY();
        float firstReduced = reducedLineDataSet.getEntryForIndex(0).getY();
        assertEquals(firstValue, firstReduced, 0);

        // Test last partition against last value
        float last = lineDataSet.getEntryForIndex(lineDataSet.getEntryCount() - 1).getY();
        float lastReduced = reducedLineDataSet.getEntryForIndex(MAX_COUNT - 1).getY();
        assertEquals(last, lastReduced, 0);

        // Test intermediate values
        /*
        Partition 00:00 -  0.0:  0.0,
        Partition 00:30 -  1.5:  1.0,  2.0,
        Partition 01:30 -  4.0:  3.0,  5.0,
        Partition 02:30 -  7.0:  6.0,  7.0,  8.0,
        Partition 03:30 - 10.0:  9.0, 10.0, 11.0
        Partition 04:00 - 12.0: 12.0,
         */
        float value;
        float[] expectedValues = {0.0F, 1.5F, 4.0F, 7.0F, 10.0F, 12.0F};
        for (int i = 0; i < MAX_COUNT; i++) {
            value = reducedLineDataSet.getEntryForIndex(i).getY();
            assertEquals(expectedValues[i], value, 0);
        }
    }

    @Test
    public void testReducePartitionDataSet_CompletePartialEnd() {
        final int MAX_COUNT = 6;        // 1st + 4 intermediate + Last

        // FROM: 2000-01-01 00:00:00, TO: +4 Hours, NOW = FROM
        long Y2000 = 946684800000L + FIX_1_HOUR; // 2000-01-01 00:00:00
        final Date FROM = new Date(Y2000);
        final Date TO = new Date(Y2000 + (4 * 60 * 60 * 1000)); // 4 Hours
        final TimeRangeLimits TIME_LIMITS = newTimeRangeLimits(FROM, TO);
        final long SAMPLE_RATE = 20 * 60 * 1000;  // 20 minutes -> 3 samples per hour -> 12 samples
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        FORMATTER.setRelativeDate(FROM);
        final Class<?> ENTRY_CLASS = Entry.class;

        // 13 samples:
        final Date REMOVE_FROM = new Date(TO.getTime() + (-60 * 1000)); // -1 Minute
        final Date REMOVE_TO = TO;
        //  00:00 ==> 0.0
        //  ...
        //  03:40 ==> 11.0
        //                  MISSING 04:00 ==> 12.0
        DataSet<?> lineDataSet = populateLineDataSetTime(FROM, TO, FORMATTER, SAMPLE_RATE);
        lineDataSet = removeEntries(lineDataSet, REMOVE_FROM, REMOVE_TO, FORMATTER);
        print(lineDataSet, FORMATTER);

        // Reduce by partition
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reducePartitionDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, FORMATTER, TIME_LIMITS);
        for (Entry h : reducedLineDataSet.getValues())
            System.out.println("id: " + FORMATTER.toString(h.getX()) + " value: " + h.getY());

        // Test first partition against first value
        float firstValue = lineDataSet.getEntryForIndex(0).getY();
        float firstReduced = reducedLineDataSet.getEntryForIndex(0).getY();
        assertEquals(firstValue, firstReduced, 0);

        // Test last partition against last value
        float last = lineDataSet.getEntryForIndex(lineDataSet.getEntryCount() - 1).getY();
        float lastReduced = reducedLineDataSet.getEntryForIndex(MAX_COUNT - 1).getY();
        assertEquals(last, lastReduced, 0);

        // Test intermediate values
        /*
        Partition 00:00 -  0.0:  0.0,
        Partition 00:30 -  1.5:  1.0,  2.0,
        Partition 01:30 -  4.0:  3.0,  4.0,  5.0,
        Partition 02:30 -  7.0:  6.0,  7.0,  8.0,
        Partition 03:30 -  9.5:  9.0, 10.0
        Partition 04:00 - 11.0: 11.0,
         */
        float value;
        float[] expectedValues = {0.0F, 1.5F, 4.0F, 7.0F, 9.5F, 11.0F};
        for (int i = 0; i < MAX_COUNT; i++) {
            value = reducedLineDataSet.getEntryForIndex(i).getY();
            assertEquals(expectedValues[i], value, 0);
        }
    }

    @Test
    public void testReducePartitionDataSet_MissingStart() {
        final int MAX_COUNT = 6;        // 1st + 4 intermediate + Last

        // FROM: 2000-01-01 00:00:00, TO: +4 Hours, NOW = FROM
        long Y2000 = 946684800000L + FIX_1_HOUR; // 2000-01-01 00:00:00
        final Date FROM = new Date(Y2000);
        final Date TO = new Date(Y2000 + (4 * 60 * 60 * 1000)); // 4 Hours
        final TimeRangeLimits TIME_LIMITS = newTimeRangeLimits(FROM, TO);
        final long SAMPLE_RATE = 20 * 60 * 1000;  // 20 minutes -> 3 samples per hour -> 12 samples
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        FORMATTER.setRelativeDate(FROM);
        final Class<?> ENTRY_CLASS = Entry.class;

        // 13 samples:
        final Date REMOVE_FROM = FROM;
        final Date REMOVE_TO = new Date(FROM.getTime() + (30 * 60 * 1000)); // 30 Minutes
        //                  MISSING 00:00 ==> 0.0
        //                  MISSING 00:20 ==> 1.0
        //  00:40 ==> 2.0   Can't be into 1st Partition, to "far" from FROM date
        //  ...
        //  04:00 ==> 12.0
        DataSet<?> lineDataSet = populateLineDataSetTime(FROM, TO, FORMATTER, SAMPLE_RATE);
        lineDataSet = removeEntries(lineDataSet, REMOVE_FROM, REMOVE_TO, FORMATTER);
        print(lineDataSet, FORMATTER);

        // Reduce by partition
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reducePartitionDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, FORMATTER, TIME_LIMITS);
        for (Entry h : reducedLineDataSet.getValues())
            System.out.println("id: " + FORMATTER.toString(h.getX()) + " value: " + h.getY());

        // Test first partition against first value
        float firstReduced = reducedLineDataSet.getEntryForIndex(0).getY();
        assertEquals(0.0, firstReduced, 0);

        // Test last partition against last value
        float last = lineDataSet.getEntryForIndex(lineDataSet.getEntryCount() - 1).getY();
        float lastReduced = reducedLineDataSet.getEntryForIndex(MAX_COUNT - 1).getY();
        assertEquals(last, lastReduced, 0);

        // Test intermediate values
        /*
        Partition 00:00 -  0.0:
        Partition 00:30 -  2.0:  2.0,
        Partition 01:30 -  4.0:  3.0,  4.0,  5.0,
        Partition 02:30 -  7.0:  6.0,  7.0,  8.0,
        Partition 03:30 - 10.0:  9.0, 10.0, 11.0
        Partition 04:00 - 12.0: 12.0,
         */
        float value;
        float[] expectedValues = {0.0F, 2.0F, 4.0F, 7.0F, 10.0F, 12.0F};
        for (int i = 0; i < MAX_COUNT; i++) {
            value = reducedLineDataSet.getEntryForIndex(i).getY();
            assertEquals(expectedValues[i], value, 0);
        }
    }

    @Test
    public void testReducePartitionDataSet_MissingMiddle() {
        final int MAX_COUNT = 6;        // 1st + 4 intermediate + Last

        // FROM: 2000-01-01 00:00:00, TO: +4 Hours, NOW = FROM
        long Y2000 = 946684800000L + FIX_1_HOUR; // 2000-01-01 00:00:00
        final Date FROM = new Date(Y2000);
        final Date TO = new Date(Y2000 + (4 * 60 * 60 * 1000)); // 4 Hours
        final TimeRangeLimits TIME_LIMITS = newTimeRangeLimits(FROM, TO);
        final long SAMPLE_RATE = 20 * 60 * 1000;  // 20 minutes -> 3 samples per hour -> 12 samples
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        FORMATTER.setRelativeDate(FROM);
        final Class<?> ENTRY_CLASS = Entry.class;

        // 13 samples:
        final Date REMOVE_FROM = new Date(FROM.getTime() + (55 * 60 * 1000)); // 55 Minutes;
        final Date REMOVE_TO = new Date(FROM.getTime() + (105 * 60 * 1000)); // 1 Hour + 45 Minutes
        //  00:00 ==> 0.0
        //  00:20 ==> 1.0
        //  ...
        //  00:40 ==> 2.0
        //                  MISSING 01:00 ==> 3.0
        //                  MISSING 01:20 ==> 4.0
        //                  MISSING 01:40 ==> 5.0
        //  02:00 ==> 6.0
        //  ...
        //  04:00 ==> 12.0
        DataSet<?> lineDataSet = populateLineDataSetTime(FROM, TO, FORMATTER, SAMPLE_RATE);
        lineDataSet = removeEntries(lineDataSet, REMOVE_FROM, REMOVE_TO, FORMATTER);
        print(lineDataSet, FORMATTER);

        // Reduce by partition
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reducePartitionDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, FORMATTER, TIME_LIMITS);
        for (Entry h : reducedLineDataSet.getValues())
            System.out.println("id: " + FORMATTER.toString(h.getX()) + " value: " + h.getY());

        // Test first partition against first value
        float firstValue = lineDataSet.getEntryForIndex(0).getY();
        float firstReduced = reducedLineDataSet.getEntryForIndex(0).getY();
        assertEquals(firstValue, firstReduced, 0);

        // Test last partition against last value
        float last = lineDataSet.getEntryForIndex(lineDataSet.getEntryCount() - 1).getY();
        float lastReduced = reducedLineDataSet.getEntryForIndex(MAX_COUNT - 1).getY();
        assertEquals(last, lastReduced, 0);

        // Test intermediate values
        /*
        Partition 00:00 -  0.0:  0.0,
        Partition 00:30 -  1.5:  1.0,  2.0,
        Partition 01:30 -  0.0:
        Partition 02:30 -  7.0:  6.0,  7.0,  8.0,
        Partition 03:30 - 10.0:  9.0, 10.0, 11.0
        Partition 04:00 - 12.0: 12.0,
         */
        float value;
        float[] expectedValues = {0.0F, 1.5F, 0.0F, 7.0F, 10.0F, 12.0F};
        for (int i = 0; i < MAX_COUNT; i++) {
            value = reducedLineDataSet.getEntryForIndex(i).getY();
            assertEquals(expectedValues[i], value, 0);
        }
    }

    @Test
    public void testReducePartitionDataSet_MissingEnd() {
        final int MAX_COUNT = 6;        // 1st + 4 intermediate + Last

        // FROM: 2000-01-01 00:00:00, TO: +4 Hours, NOW = FROM
        long Y2000 = 946684800000L + FIX_1_HOUR; // 2000-01-01 00:00:00
        final Date FROM = new Date(Y2000);
        final Date TO = new Date(Y2000 + (4 * 60 * 60 * 1000)); // 4 Hours
        final TimeRangeLimits TIME_LIMITS = newTimeRangeLimits(FROM, TO);
        final long SAMPLE_RATE = 20 * 60 * 1000;  // 20 minutes -> 3 samples per hour -> 12 samples
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        FORMATTER.setRelativeDate(FROM);
        final Class<?> ENTRY_CLASS = Entry.class;

        // 13 samples:
        final Date REMOVE_FROM = new Date(TO.getTime() + (-30 * 60 * 1000)); // -30 Minutes
        final Date REMOVE_TO = TO;
        //  00:00 ==> 0.0
        //  ...
        //  03:20 ==> 10.0  Can't be into Last Partition, to "far" from TO date
        //                  MISSING 03:40 ==> 11.0
        //                  MISSING 04:00 ==> 12.0
        DataSet<?> lineDataSet = populateLineDataSetTime(FROM, TO, FORMATTER, SAMPLE_RATE);
        lineDataSet = removeEntries(lineDataSet, REMOVE_FROM, REMOVE_TO, FORMATTER);
        print(lineDataSet, FORMATTER);

        // Reduce by partition
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reducePartitionDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, FORMATTER, TIME_LIMITS);
        for (Entry h : reducedLineDataSet.getValues())
            System.out.println("id: " + FORMATTER.toString(h.getX()) + " value: " + h.getY());

        // Test first partition against first value
        float firstValue = lineDataSet.getEntryForIndex(0).getY();
        float firstReduced = reducedLineDataSet.getEntryForIndex(0).getY();
        assertEquals(firstValue, firstReduced, 0);

        // Test last partition against last value
        float lastReduced = reducedLineDataSet.getEntryForIndex(MAX_COUNT - 1).getY();
        assertEquals(0.0, lastReduced, 0);

        // Test intermediate values
        /*
        Partition 00:00 -  0.0:  0.0,
        Partition 00:30 -  1.5:  1.0,  2.0,
        Partition 01:30 -  4.0:  3.0,  4.0,  5.0,
        Partition 02:30 -  7.0:  6.0,  7.0,  8.0,
        Partition 03:30 -  9.5:  9.0, 10.0
        Partition 04:00 -  0.0:
         */
        float value;
        float[] expectedValues = {0.0F, 1.5F, 4.0F, 7.0F, 9.5F, 0.0F};
        for (int i = 0; i < MAX_COUNT; i++) {
            value = reducedLineDataSet.getEntryForIndex(i).getY();
            assertEquals(expectedValues[i], value, 0);
        }
    }

    @Test
    public void testReducePartitionDataSet_noData() {
        final int MAX_COUNT = 6;        // 1st + 4 intermediate + Last

        // FROM: 2000-01-01 00:00:00, TO: +4 Hours, NOW = FROM
        long Y2000 = 946684800000L + FIX_1_HOUR; // 2000-01-01 00:00:00
        final Date FROM = new Date(Y2000);
        final Date TO = new Date(Y2000 + (4 * 60 * 60 * 1000)); // 4 Hours
        final TimeRangeLimits TIME_LIMITS = newTimeRangeLimits(FROM, TO);
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        FORMATTER.setRelativeDate(FROM);
        final Class<?> ENTRY_CLASS = Entry.class;

        // 0 samples: Empty data set
        DataSet<?> lineDataSet = MPAndroidChartUtils.newDataSet(LineDataSet.class, "Empty", new ArrayList<>());

        // Reduce by partition
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reducePartitionDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, FORMATTER, TIME_LIMITS);
        for (Entry h : reducedLineDataSet.getValues())
            System.out.println("id: " + FORMATTER.toString(h.getX()) + " value: " + h.getY());

        // Test reduced data set's size
        assertEquals(MAX_COUNT, reducedLineDataSet.getEntryCount());

        // Test intermediate values
        /*
        Partition 00:00 - 0.0: 0.0,
        Partition 00:30 - 0.0: 0.0,,
        Partition 01:30 - 0.0: 0.0,
        Partition 02:30 - 0.0: 0.0,
        Partition 03:30 - 0.0: 0.0,
        Partition 04:00 - 0.0: 0.0,
         */
        float value;
        float[] expectedValues = {0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F,};
        for (int i = 0; i < MAX_COUNT; i++) {
            value = reducedLineDataSet.getEntryForIndex(i).getY();
            assertEquals(expectedValues[i], value, 0);
        }
    }


    // reduceEqualsPartitionLineDataSet

    /*
     *  0.00    1.00    2.00    3.00    4.00    <- First and last
     *      1.30    2.30    3.30    4.30        <- Intermediate
     *
     *                    PRE  POST
     * 00:00 ==> 0.0       1    1
     * 00:20 ==> 1.0       1    2
     * 00:40 ==> 2.0       1    2
     * 01:00 ==> 3.0       2    2
     * 01:20 ==> 4.0       2    3
     * 01:40 ==> 5.0       2    3
     * 02:00 ==> 6.0       3    3
     * 02:20 ==> 7.0       3    4
     * 02:40 ==> 8.0       3    4
     * 03:00 ==> 9.0       4    4
     * 03:20 ==> 10.0      4    5
     * 03:40 ==> 11.0      4    5
     * 04:00 ==> 12.0      5    5
     *
     *
     */

    @Test
    public void testReduceEqualsPartitionDataSet_count() {
        final int MAX_COUNT = 5;        // 1st + 4 intermediate + Last

        // FROM: 2000-01-01 00:00:00, TO: +4 Hours, NOW = FROM
        long Y2000 = 946684800000L + FIX_1_HOUR; // 2000-01-01 00:00:00
        final Date FROM = new Date(Y2000);
        final Date TO = new Date(Y2000 + (4 * 60 * 60 * 1000)); // 4 Hours
        final TimeRangeLimits TIME_LIMITS = newTimeRangeLimits(FROM, TO);
        final long SAMPLE_RATE = 20 * 60 * 1000;  // 20 minutes -> 3 samples per hour -> 12 samples
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        FORMATTER.setRelativeDate(FROM);
        boolean POST = false;
        final Class<?> ENTRY_CLASS = Entry.class;

        // 13 samples:
        //  00:00 > 0.0
        //  ...
        //  04:00 > 12.0
        DataSet<?> lineDataSet = populateLineDataSetTime(FROM, TO, FORMATTER, SAMPLE_RATE);

        // Reduce by partition
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reduceEqualsPartitionDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, POST, FORMATTER, TIME_LIMITS);

        // Test reduced data set's size
        assertEquals(MAX_COUNT, reducedLineDataSet.getEntryCount());
    }

    @Deprecated
    @Test
    public void testReduceEqualsPartitionDataSet_firstMidAndLast() {
        final int MAX_COUNT = 4 + 1;        // 4 partitions + ToDate

        // FROM: 2000-01-01 00:00:00, TO: +4 Hours, NOW = FROM
        long Y2000 = 946684800000L + FIX_1_HOUR; // 2000-01-01 00:00:00
        final Date FROM = new Date(Y2000);
        final Date TO = new Date(Y2000 + (4 * 60 * 60 * 1000)); // 4 Hours
        final TimeRangeLimits TIME_LIMITS = newTimeRangeLimits(FROM, TO);
        final long SAMPLE_RATE = 20 * 60 * 1000;  // 20 minutes -> 3 samples per hour -> 12 samples
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        FORMATTER.setRelativeDate(FROM);
        boolean POST = false;
        final Class<?> ENTRY_CLASS = Entry.class;

        // 13 samples:
        //  00:00 > 0.0
        //  
        //  ...
        //  04:00 > 12.0
        DataSet<?> lineDataSet = populateLineDataSetTime(FROM, TO, FORMATTER, SAMPLE_RATE);

        // Reduce by partition
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reduceEqualsPartitionDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, POST, FORMATTER, TIME_LIMITS);
        for (Entry h : reducedLineDataSet.getValues())
            System.out.println("id: " + FORMATTER.toString(h.getX()) + " value: " + h.getY());

        // Test first partition against first value - todo segnare che reduceEqualsPartitionDataSet non mantiene il primo e lultimo elemento
        //float firstValue = lineDataSet.getEntryForIndex(0).getY();
        //float firstReduced = reducedLineDataSet.getEntryForIndex(0).getY();
        //assertEquals(firstValue, firstReduced, 0);

        // Test last partition against last value - todo segnare che reduceEqualsPartitionDataSet non mantiene il primo e lultimo elemento
        //float last = lineDataSet.getEntryForIndex(lineDataSet.getEntryCount() - 1).getY();
        //float lastReduced = reducedLineDataSet.getEntryForIndex(MAX_COUNT - 1).getY();
        //assertEquals(last, lastReduced, 0);

        // Test intermediate values
        /*
        Partition 00:00 -  1.0:  0.0,  1.0,  2.0,
        Partition 01:00 -  4.0:  3.0,  4.0,  5.0,
        Partition 02:00 -  7.0:  6.0,  7.0,  8.0,
        Partition 03:00 - 10.0:  9.0, 10.0, 11.0
        Partition 04:00 - 12.0: 12.0,
         */
        float value;
        float[] expectedValues = {1.0F, 4.0F, 7.0F, 10.0F, 12.0F};
        for (int i = 0; i < MAX_COUNT; i++) {
            value = reducedLineDataSet.getEntryForIndex(i).getY();
            assertEquals(expectedValues[i], value, 0);
        }
    }

    @Test
    public void testReduceEqualsPartitionDataSet_Complete() {
        final int MAX_COUNT = 4 + 1;        // 4 partitions + ToDate

        // FROM: 2000-01-01 00:00:00, TO: +4 Hours, NOW = FROM
        long Y2000 = 946684800000L + FIX_1_HOUR; // 2000-01-01 00:00:00
        final Date FROM = new Date(Y2000);
        final Date TO = new Date(Y2000 + (4 * 60 * 60 * 1000)); // 4 Hours
        final TimeRangeLimits TIME_LIMITS = newTimeRangeLimits(FROM, TO);
        final long SAMPLE_RATE = 20 * 60 * 1000;  // 20 minutes -> 3 samples per hour -> 12 samples
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        FORMATTER.setRelativeDate(FROM);
        boolean POST = false;
        final Class<?> ENTRY_CLASS = Entry.class;

        // 13 samples:
        //  00:00 > 0.0
        //  ...
        //  04:00 > 12.0
        DataSet<?> lineDataSet = populateLineDataSetTime(FROM, TO, FORMATTER, SAMPLE_RATE);

        // Reduce by partition
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reduceEqualsPartitionDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, POST, FORMATTER, TIME_LIMITS);
        for (Entry h : reducedLineDataSet.getValues())
            System.out.println("id: " + FORMATTER.toString(h.getX()) + " value: " + h.getY());

        // Test first partition against first value - todo segnare che reduceEqualsPartitionDataSet non mantiene il primo e lultimo elemento - todo segnare che reduceEqualsPartitionDataSet non mantiene il primo e lultimo elemento
        //float firstValue = lineDataSet.getEntryForIndex(0).getY();
        //float firstReduced = reducedLineDataSet.getEntryForIndex(0).getY();
        //assertEquals(firstValue, firstReduced, 0);

        // Test last partition against last value - todo segnare che reduceEqualsPartitionDataSet non mantiene il primo e lultimo elemento - todo segnare che reduceEqualsPartitionDataSet non mantiene il primo e lultimo elemento
        //float last = lineDataSet.getEntryForIndex(lineDataSet.getEntryCount() - 1).getY();
        //float lastReduced = reducedLineDataSet.getEntryForIndex(MAX_COUNT - 1).getY();
        //assertEquals(last, lastReduced, 0);

        // Test intermediate values
        /*
        Partition 00:00 -  1.0:  0.0,  1.0,  2.0,
        Partition 01:00 -  4.0:  3.0,  4.0,  5.0,
        Partition 02:00 -  7.0:  6.0,  7.0,  8.0,
        Partition 03:00 - 10.0:  9.0, 10.0, 11.0
        Partition 04:00 - 12.0: 12.0,
         */
        float value;
        float[] expectedValues = {1.0F, 4.0F, 7.0F, 10.0F, 12.0F};
        for (int i = 0; i < MAX_COUNT; i++) {
            value = reducedLineDataSet.getEntryForIndex(i).getY();
            assertEquals(expectedValues[i], value, 0);
        }
    }

    @Test
    public void testReduceEqualsPartitionDataSet_CompletePartialStart_PRE() {
        final int MAX_COUNT = 4 + 1;        // 4 partitions + ToDate

        // FROM: 2000-01-01 00:00:00, TO: +4 Hours, NOW = FROM
        long Y2000 = 946684800000L + FIX_1_HOUR; // 2000-01-01 00:00:00
        final Date FROM = new Date(Y2000);
        final Date TO = new Date(Y2000 + (4 * 60 * 60 * 1000)); // 4 Hours
        final TimeRangeLimits TIME_LIMITS = newTimeRangeLimits(FROM, TO);
        final long SAMPLE_RATE = 20 * 60 * 1000;  // 20 minutes -> 3 samples per hour -> 12 samples
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        FORMATTER.setRelativeDate(FROM);
        boolean POST = false;
        final Class<?> ENTRY_CLASS = Entry.class;

        // 13 samples:
        final Date REMOVE_FROM = FROM;
        final Date REMOVE_TO = new Date(FROM.getTime() + (60 * 1000)); // 1 Minute
        //                  MISSING 00:00 ==> 0.0
        //  00:20 ==> 1.0
        //  ...
        //  04:00 ==> 12.0
        DataSet<?> lineDataSet = populateLineDataSetTime(FROM, TO, FORMATTER, SAMPLE_RATE);
        lineDataSet = removeEntries(lineDataSet, REMOVE_FROM, REMOVE_TO, FORMATTER);
        print(lineDataSet, FORMATTER);

        // Reduce by partition
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reduceEqualsPartitionDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, POST, FORMATTER, TIME_LIMITS);
        for (Entry h : reducedLineDataSet.getValues())
            System.out.println("id: " + FORMATTER.toString(h.getX()) + " value: " + h.getY());

        // Test first partition against first value - todo segnare che reduceEqualsPartitionDataSet non mantiene il primo e lultimo elemento
        //float firstValue = lineDataSet.getEntryForIndex(0).getY();
        //float firstReduced = reducedLineDataSet.getEntryForIndex(0).getY();
        //assertEquals(firstValue, firstReduced, 0);

        // Test last partition against last value - todo segnare che reduceEqualsPartitionDataSet non mantiene il primo e lultimo elemento
        //float last = lineDataSet.getEntryForIndex(lineDataSet.getEntryCount() - 1).getY();
        //float lastReduced = reducedLineDataSet.getEntryForIndex(MAX_COUNT - 1).getY();
        //assertEquals(last, lastReduced, 0);

        // Test intermediate values
        /*
        Partition 00:00 -  1.5:  1.0,  2.0,
        Partition 01:00 -  4.0:  3.0,  4.0,  5.0,
        Partition 02:00 -  7.0:  6.0,  7.0,  8.0,
        Partition 03:00 - 10.0:  9.0, 10.0, 11.0
        Partition 04:00 - 12.0: 12.0,
         */
        float value;
        float[] expectedValues = {1.5F, 4.0F, 7.0F, 10.0F, 12.0F};
        for (int i = 0; i < MAX_COUNT; i++) {
            value = reducedLineDataSet.getEntryForIndex(i).getY();
            assertEquals(expectedValues[i], value, 0);
        }
    }

    @Test
    public void testReduceEqualsPartitionDataSet_CompletePartialMiddle() {
        final int MAX_COUNT = 4 + 1;        // 4 partitions + ToDate

        // FROM: 2000-01-01 00:00:00, TO: +4 Hours, NOW = FROM
        long Y2000 = 946684800000L + FIX_1_HOUR; // 2000-01-01 00:00:00
        final Date FROM = new Date(Y2000);
        final Date TO = new Date(Y2000 + (4 * 60 * 60 * 1000)); // 4 Hours
        final TimeRangeLimits TIME_LIMITS = newTimeRangeLimits(FROM, TO);
        final long SAMPLE_RATE = 20 * 60 * 1000;  // 20 minutes -> 3 samples per hour -> 12 samples
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        FORMATTER.setRelativeDate(FROM);
        boolean POST = false;
        final Class<?> ENTRY_CLASS = Entry.class;

        // 13 samples:
        final Date REMOVE_FROM = new Date(FROM.getTime() + (70 * 60 * 1000)); // 1 Hour + 10 Minute;
        final Date REMOVE_TO = new Date(FROM.getTime() + (90 * 60 * 1000)); // 1 Hour + 30 Minute
        //  00:00 ==> 0.0
        //  00:20 ==> 1.0
        //  ...
        //  01:00 ==> 3.0
        //                  MISSING 01:20 ==> 4.0
        //  01:40 ==> 5.0
        //  ...
        //  04:00 ==> 12.0
        DataSet<?> lineDataSet = populateLineDataSetTime(FROM, TO, FORMATTER, SAMPLE_RATE);
        lineDataSet = removeEntries(lineDataSet, REMOVE_FROM, REMOVE_TO, FORMATTER);
        print(lineDataSet, FORMATTER);

        // Reduce by partition
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reduceEqualsPartitionDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, POST, FORMATTER, TIME_LIMITS);
        for (Entry h : reducedLineDataSet.getValues())
            System.out.println("id: " + FORMATTER.toString(h.getX()) + " value: " + h.getY());

        // Test first partition against first value - todo segnare che reduceEqualsPartitionDataSet non mantiene il primo e lultimo elemento
        //float firstValue = lineDataSet.getEntryForIndex(0).getY();
        //float firstReduced = reducedLineDataSet.getEntryForIndex(0).getY();
        //assertEquals(firstValue, firstReduced, 0);

        // Test last partition against last value - todo segnare che reduceEqualsPartitionDataSet non mantiene il primo e lultimo elemento
        //float last = lineDataSet.getEntryForIndex(lineDataSet.getEntryCount() - 1).getY();
        //float lastReduced = reducedLineDataSet.getEntryForIndex(MAX_COUNT - 1).getY();
        //assertEquals(last, lastReduced, 0);

        // Test intermediate values
        /*
        Partition 00:00 -  1.0:  0.0,  1.0,  2.0,
        Partition 01:00 -  4.0:  3.0,  5.0,
        Partition 02:00 -  7.0:  6.0,  7.0,  8.0,
        Partition 03:00 - 10.0:  9.0, 10.0, 11.0
        Partition 04:00 - 12.0: 12.0,
         */
        float value;
        float[] expectedValues = {1.0F, 4.0F, 7.0F, 10.0F, 12.0F};
        for (int i = 0; i < MAX_COUNT; i++) {
            value = reducedLineDataSet.getEntryForIndex(i).getY();
            assertEquals(expectedValues[i], value, 0);
        }
    }

    @Test
    public void testReduceEqualsPartitionDataSet_CompletePartialEnd_POST() {
        final int MAX_COUNT = 4 + 1;        // 4 partitions + ToDate

        // FROM: 2000-01-01 00:00:00, TO: +4 Hours, NOW = FROM
        long Y2000 = 946684800000L + FIX_1_HOUR; // 2000-01-01 00:00:00
        final Date FROM = new Date(Y2000);
        final Date TO = new Date(Y2000 + (4 * 60 * 60 * 1000)); // 4 Hours
        final TimeRangeLimits TIME_LIMITS = newTimeRangeLimits(FROM, TO);
        final long SAMPLE_RATE = 20 * 60 * 1000;  // 20 minutes -> 3 samples per hour -> 12 samples
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        FORMATTER.setRelativeDate(FROM);
        boolean POST = true;
        final Class<?> ENTRY_CLASS = Entry.class;

        // 13 samples:
        final Date REMOVE_FROM = new Date(TO.getTime() + (-60 * 1000)); // -1 Minute
        final Date REMOVE_TO = TO;
        //  00:00 ==> 0.0
        //  ...
        //  03:40 ==> 11.0
        //                  MISSING 04:00 ==> 12.0
        DataSet<?> lineDataSet = populateLineDataSetTime(FROM, TO, FORMATTER, SAMPLE_RATE);
        lineDataSet = removeEntries(lineDataSet, REMOVE_FROM, REMOVE_TO, FORMATTER);
        print(lineDataSet, FORMATTER);

        // Reduce by partition
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reduceEqualsPartitionDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, POST, FORMATTER, TIME_LIMITS);
        for (Entry h : reducedLineDataSet.getValues())
            System.out.println("id: " + FORMATTER.toString(h.getX()) + " value: " + h.getY());

        // Test first partition against first value - todo segnare che reduceEqualsPartitionDataSet non mantiene il primo e lultimo elemento
        //float firstValue = lineDataSet.getEntryForIndex(0).getY();
        //float firstReduced = reducedLineDataSet.getEntryForIndex(0).getY();
        //assertEquals(firstValue, firstReduced, 0);

        // Test last partition against last value - todo segnare che reduceEqualsPartitionDataSet non mantiene il primo e lultimo elemento
        //float last = lineDataSet.getEntryForIndex(lineDataSet.getEntryCount() - 1).getY();
        //float lastReduced = reducedLineDataSet.getEntryForIndex(MAX_COUNT - 1).getY();
        //assertEquals(last, lastReduced, 0);

        // Test intermediate values
        /*
        Partition 00:00 -  0.0:  0.0,
        Partition 01:00 -  2.0:  1.0,  2.0,  3.0,
        Partition 02:00 -  5.0:  4.0,  5.0,  6.0,
        Partition 03:00 -  8.0:  7.0,  8.0,  9.0,
        Partition 04:00 - 10.5:  10.0, 11.0
         */
        float value;
        float[] expectedValues = {0.0F, 2.0F, 5.0F, 8.0F, 10.5F};
        for (int i = 0; i < MAX_COUNT; i++) {
            value = reducedLineDataSet.getEntryForIndex(i).getY();
            assertEquals(expectedValues[i], value, 0);
        }
    }

    @Test
    public void testReduceEqualsPartitionDataSet_MissingStart() {
        final int MAX_COUNT = 4 + 1;        // 4 partitions + ToDate

        // FROM: 2000-01-01 00:00:00, TO: +4 Hours, NOW = FROM
        long Y2000 = 946684800000L + FIX_1_HOUR; // 2000-01-01 00:00:00
        final Date FROM = new Date(Y2000);
        final Date TO = new Date(Y2000 + (4 * 60 * 60 * 1000)); // 4 Hours
        final TimeRangeLimits TIME_LIMITS = newTimeRangeLimits(FROM, TO);
        final long SAMPLE_RATE = 20 * 60 * 1000;  // 20 minutes -> 3 samples per hour -> 12 samples
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        FORMATTER.setRelativeDate(FROM);
        boolean POST = false;
        final Class<?> ENTRY_CLASS = Entry.class;

        // 13 samples:
        final Date REMOVE_FROM = FROM;
        final Date REMOVE_TO = new Date(FROM.getTime() + (50 * 60 * 1000)); // 50 Minutes
        //                  MISSING 00:00 ==> 0.0
        //                  MISSING 00:20 ==> 1.0
        //                  MISSING 00:40 ==> 2.0
        //  01:00 ==> 3.0
        //  ...
        //  04:00 ==> 12.0
        DataSet<?> lineDataSet = populateLineDataSetTime(FROM, TO, FORMATTER, SAMPLE_RATE);
        lineDataSet = removeEntries(lineDataSet, REMOVE_FROM, REMOVE_TO, FORMATTER);
        print(lineDataSet, FORMATTER);

        // Reduce by partition
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reduceEqualsPartitionDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, POST, FORMATTER, TIME_LIMITS);
        for (Entry h : reducedLineDataSet.getValues())
            System.out.println("id: " + FORMATTER.toString(h.getX()) + " value: " + h.getY());

        // Test first partition against first value - todo segnare che reduceEqualsPartitionDataSet non mantiene il primo e lultimo elemento
        float firstReduced = reducedLineDataSet.getEntryForIndex(0).getY();
        assertEquals(0.0, firstReduced, 0);

        // Test last partition against last value - todo segnare che reduceEqualsPartitionDataSet non mantiene il primo e lultimo elemento
        float last = lineDataSet.getEntryForIndex(lineDataSet.getEntryCount() - 1).getY();
        float lastReduced = reducedLineDataSet.getEntryForIndex(MAX_COUNT - 1).getY();
        assertEquals(last, lastReduced, 0);

        // Test intermediate values
        /*
        Partition 00:00 -  0.0:  0.0,
        Partition 01:00 -  4.0:  3.0,  4.0,  5.0,
        Partition 02:00 -  7.0:  6.0,  7.0,  8.0,
        Partition 03:00 - 10.0:  9.0, 10.0, 11.0
        Partition 04:00 - 12.0: 12.0,
         */
        float value;
        float[] expectedValues = {0.0F, 4.0F, 7.0F, 10.0F, 12.0F};
        for (int i = 0; i < MAX_COUNT; i++) {
            value = reducedLineDataSet.getEntryForIndex(i).getY();
            assertEquals(expectedValues[i], value, 0);
        }
    }

    @Test
    public void testReduceEqualsPartitionDataSet_MissingMiddle() {
        final int MAX_COUNT = 4 + 1;        // 4 partitions + ToDate

        // FROM: 2000-01-01 00:00:00, TO: +4 Hours, NOW = FROM
        long Y2000 = 946684800000L + FIX_1_HOUR; // 2000-01-01 00:00:00
        final Date FROM = new Date(Y2000);
        final Date TO = new Date(Y2000 + (4 * 60 * 60 * 1000)); // 4 Hours
        final TimeRangeLimits TIME_LIMITS = newTimeRangeLimits(FROM, TO);
        final long SAMPLE_RATE = 20 * 60 * 1000;  // 20 minutes -> 3 samples per hour -> 12 samples
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        FORMATTER.setRelativeDate(FROM);
        boolean POST = false;
        final Class<?> ENTRY_CLASS = Entry.class;

        // 13 samples:
        final Date REMOVE_FROM = new Date(FROM.getTime() + (55 * 60 * 1000)); // 55 Minutes;
        final Date REMOVE_TO = new Date(FROM.getTime() + (105 * 60 * 1000)); // 1 Hour + 45 Minutes
        //  00:00 ==> 0.0
        //  00:20 ==> 1.0
        //  ...
        //  00:40 ==> 2.0
        //                  MISSING 01:00 ==> 3.0
        //                  MISSING 01:20 ==> 4.0
        //                  MISSING 01:40 ==> 5.0
        //  02:00 ==> 6.0
        //  ...
        //  04:00 ==> 12.0
        DataSet<?> lineDataSet = populateLineDataSetTime(FROM, TO, FORMATTER, SAMPLE_RATE);
        lineDataSet = removeEntries(lineDataSet, REMOVE_FROM, REMOVE_TO, FORMATTER);
        print(lineDataSet, FORMATTER);

        // Reduce by partition
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reduceEqualsPartitionDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, POST, FORMATTER, TIME_LIMITS);
        for (Entry h : reducedLineDataSet.getValues())
            System.out.println("id: " + FORMATTER.toString(h.getX()) + " value: " + h.getY());

        // Test first partition against first value - todo segnare che reduceEqualsPartitionDataSet non mantiene il primo e lultimo elemento
        //float firstValue = lineDataSet.getEntryForIndex(0).getY();
        //float firstReduced = reducedLineDataSet.getEntryForIndex(0).getY();
        //assertEquals(firstValue, firstReduced, 0);

        // Test last partition against last value - todo segnare che reduceEqualsPartitionDataSet non mantiene il primo e lultimo elemento
        //float last = lineDataSet.getEntryForIndex(lineDataSet.getEntryCount() - 1).getY();
        //float lastReduced = reducedLineDataSet.getEntryForIndex(MAX_COUNT - 1).getY();
        //assertEquals(last, lastReduced, 0);

        // Test intermediate values
        /*
        Partition 00:00 -  1.0:  0.0,  1.0,  2.0,
        Partition 01:00 -  0.0:
        Partition 02:00 -  7.0:  6.0,  7.0,  8.0,
        Partition 03:00 - 10.0:  9.0, 10.0, 11.0
        Partition 04:00 - 12.0: 12.0,
         */
        float value;
        float[] expectedValues = {1.0F, 0.0F, 7.0F, 10.0F, 12.0F};
        for (int i = 0; i < MAX_COUNT; i++) {
            value = reducedLineDataSet.getEntryForIndex(i).getY();
            assertEquals(expectedValues[i], value, 0);
        }
    }

    @Test
    public void testReduceEqualsPartitionDataSet_MissingEnd() {
        final int MAX_COUNT = 4 + 1;        // 4 partitions + ToDate

        // FROM: 2000-01-01 00:00:00, TO: +4 Hours, NOW = FROM
        long Y2000 = 946684800000L + FIX_1_HOUR; // 2000-01-01 00:00:00
        final Date FROM = new Date(Y2000);
        final Date TO = new Date(Y2000 + (4 * 60 * 60 * 1000)); // 4 Hours
        final TimeRangeLimits TIME_LIMITS = newTimeRangeLimits(FROM, TO);
        final long SAMPLE_RATE = 20 * 60 * 1000;  // 20 minutes -> 3 samples per hour -> 12 samples
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        FORMATTER.setRelativeDate(FROM);
        boolean POST = false;
        final Class<?> ENTRY_CLASS = Entry.class;

        // 13 samples:
        final Date REMOVE_FROM = new Date(TO.getTime() + (-15 * 60 * 1000)); // -15 Minutes
        final Date REMOVE_TO = TO;
        //  00:00 ==> 0.0
        //  ...
        //  03:40 ==> 11.0
        //                  MISSING 04:00 ==> 12.0
        DataSet<?> lineDataSet = populateLineDataSetTime(FROM, TO, FORMATTER, SAMPLE_RATE);
        lineDataSet = removeEntries(lineDataSet, REMOVE_FROM, REMOVE_TO, FORMATTER);
        print(lineDataSet, FORMATTER);

        // Reduce by partition
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reduceEqualsPartitionDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, POST, FORMATTER, TIME_LIMITS);
        for (Entry h : reducedLineDataSet.getValues())
            System.out.println("id: " + FORMATTER.toString(h.getX()) + " value: " + h.getY());

        // Test first partition against first value - todo segnare che reduceEqualsPartitionDataSet non mantiene il primo e lultimo elemento
        //float firstValue = lineDataSet.getEntryForIndex(0).getY();
        //float firstReduced = reducedLineDataSet.getEntryForIndex(0).getY();
        //assertEquals(firstValue, firstReduced, 0);

        // Test last partition against last value - todo segnare che reduceEqualsPartitionDataSet non mantiene il primo e lultimo elemento
        //float lastReduced = reducedLineDataSet.getEntryForIndex(MAX_COUNT - 1).getY();
        //assertEquals(0.0, lastReduced, 0);

        // Test intermediate values
        /*
        Partition 00:00 -  1.0:  0.0,  1.0,  2.0,
        Partition 01:00 -  4.0:  3.0,  4.0,  5.0,
        Partition 02:00 -  7.0:  6.0,  7.0,  8.0,
        Partition 03:00 - 10.0:  9.0, 10.0, 11.0,
        Partition 04:00 -  0.0:
         */
        float value;
        float[] expectedValues = {1.0F, 4.0F, 7.0F, 10.0F, 0.0F};
        for (int i = 0; i < MAX_COUNT; i++) {
            value = reducedLineDataSet.getEntryForIndex(i).getY();
            assertEquals(expectedValues[i], value, 0);
        }
    }

    @Test
    public void testReduceEqualsPartitionDataSet_noData() {
        final int MAX_COUNT = 4 + 1;        // 4 partitions + ToDate

        // FROM: 2000-01-01 00:00:00, TO: +4 Hours, NOW = FROM
        long Y2000 = 946684800000L + FIX_1_HOUR; // 2000-01-01 00:00:00
        final Date FROM = new Date(Y2000);
        final Date TO = new Date(Y2000 + (4 * 60 * 60 * 1000)); // 4 Hours
        final TimeRangeLimits TIME_LIMITS = newTimeRangeLimits(FROM, TO);
        final long SAMPLE_RATE = 20 * 60 * 1000;  // 20 minutes -> 3 samples per hour -> 12 samples
        final ChartDateTimeFormatter FORMATTER = ChartDateTimeFormatter.X_FORMATTER_MINUTES();
        FORMATTER.setRelativeDate(FROM);
        boolean POST = false;
        final Class<?> ENTRY_CLASS = Entry.class;

        // 0 samples: Empty data set
        DataSet<?> lineDataSet = MPAndroidChartUtils.newDataSet(LineDataSet.class, "Empty", new ArrayList<>());

        // Reduce by partition
        DataSet<?> reducedLineDataSet = MPAndroidChartUtils.reduceEqualsPartitionDataSet(lineDataSet, ENTRY_CLASS, MAX_COUNT, POST, FORMATTER, TIME_LIMITS);
        for (Entry h : reducedLineDataSet.getValues())
            System.out.println("id: " + FORMATTER.toString(h.getX()) + " value: " + h.getY());

        // Test reduced data set's size
        assertEquals(MAX_COUNT, reducedLineDataSet.getEntryCount());

        // Test intermediate values
        /*
        Partition 00:00 - 0.0: 0.0,
        Partition 00:30 - 0.0: 0.0,,
        Partition 01:30 - 0.0: 0.0,
        Partition 02:30 - 0.0: 0.0,
        Partition 03:30 - 0.0: 0.0,
        Partition 04:00 - 0.0: 0.0,
         */
        float value;
        float[] expectedValues = {0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F,};
        for (int i = 0; i < MAX_COUNT; i++) {
            value = reducedLineDataSet.getEntryForIndex(i).getY();
            assertEquals(expectedValues[i], value, 0);
        }
    }


    // utils

    private DataSet<?> populateLineDataSet(Date nowDate, int itemCount, ChartDateTimeFormatter xFormatter) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            Date date = JavaDate.getDateAltered(nowDate, Calendar.SECOND, i);
            float dateFloat = xFormatter.fromDate(date);
            float value = i + 1;
            entries.add(new Entry(dateFloat, value));
        }

        return MPAndroidChartUtils.newDataSet(LineDataSet.class, "data set", entries);
    }

    private DataSet<?> populateLineDataSetTime(Date from, Date to, ChartDateTimeFormatter xFormatter, long sampleRateMs) {
        List<Entry> entries = new ArrayList<>();
        Date current = from;
        int count = 0;
        while (current.getTime() <= to.getTime()) {
            float dateFloat = xFormatter.fromDate(current);
            entries.add(new Entry(dateFloat, (float)count++));
            current = new Date(current.getTime() + sampleRateMs);
        }
        DataSet<?> reducedLineDataSet =  MPAndroidChartUtils.newDataSet(LineDataSet.class, "data set", entries);
        MPAndroidChartUtils.sortDataSet(reducedLineDataSet);

        return reducedLineDataSet;
    }

    private DataSet<?> removeEntries(DataSet<?> lineDataSet, Date from, Date to, ChartDateTimeFormatter xFormatter) {
        List<Entry> newEntries = new ArrayList<>();
        for (Entry entry : lineDataSet.getValues()) {
            Date date = xFormatter.toDate(entry.getX());
            if (date.getTime() < from.getTime() || date.getTime() > to.getTime())
                newEntries.add(entry);
        }

        return MPAndroidChartUtils.newDataSet(lineDataSet, newEntries);
    }

    private void print(DataSet<?> lineDataSet, ChartDateTimeFormatter xFormatter) {
        for (Entry entry : lineDataSet.getValues())
            System.out.println(xFormatter.toString(entry.getX()) + " ==> " + entry.getY());
    }

}