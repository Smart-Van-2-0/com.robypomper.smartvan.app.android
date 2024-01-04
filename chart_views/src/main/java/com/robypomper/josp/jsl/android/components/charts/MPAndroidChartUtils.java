package com.robypomper.josp.jsl.android.components.charts;

import static java.lang.Math.max;
import static java.lang.Math.round;

import com.github.mikephil.charting.data.BaseEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartBaseFormatter;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartDateTimeFormatter;
import com.robypomper.josp.jsl.android.components.charts.utils.TimeRangeLimits;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Utility class to work with MPAndroidChart.
 * @noinspection unused, ConstantValue
 */
public class MPAndroidChartUtils {

    // Entities and Data sets builders

    public static <T extends BaseEntry> T newEntry(Class<?> c, float x, float y) {
        String msg;
        Throwable t;
        try {
            Constructor<?> constructor = c.getConstructor(Float.TYPE, Float.TYPE);
            Object newInstance = constructor.newInstance(x, y);
            //noinspection unchecked
            return (T) newInstance;
        } catch (NoSuchMethodException e) {
            t = e;
            msg = "Entry '" + c.getSimpleName() + "' class must have a constructor with  2 float params";
        } catch (InvocationTargetException e) {
            t = e;
            //noinspection DataFlowIssue
            msg = "Entry '" + c.getSimpleName() + "'class trows InvocationTargetException: " + e.getCause().getMessage();
        } catch (IllegalAccessException e) {
            t = e;
            msg = "Entry '" + c.getSimpleName() + "' class must have a public constructor with 2 float params";
        } catch (InstantiationException e) {
            t = e;
            msg = "Entry '" + c.getSimpleName() + "'class trows InstantiationException: " + e.getMessage();
        }

        assert false : msg;
        throw new IllegalArgumentException(msg, t);
    }

    public static <T extends DataSet<?>> T newDataSet(T dataSet, List<? extends BaseEntry> newEntryList) {
        Class<?> c = dataSet.getClass();
        return newDataSet(c, dataSet.getLabel(), newEntryList);
    }

    public static <T extends DataSet<?>> T newDataSet(Class<?> c, String dataSetName, List<? extends BaseEntry> newEntryList) {
        String msg;
        Throwable t;
        try {
            Constructor<?> constructor = c.getConstructor(List.class, String.class);
            Object newInstance = constructor.newInstance(newEntryList, dataSetName);
            //noinspection unchecked
            return (T) newInstance;
        } catch (NoSuchMethodException e) {
            t = e;
            msg = "DataSet '" + c.getSimpleName() + "' class must have a constructor with List<Entry> and String params";
        } catch (InvocationTargetException e) {
            t = e;
            //noinspection DataFlowIssue
            msg = "DataSet '" + c.getSimpleName() + "'class trows InvocationTargetException: " + e.getCause().getMessage();
        } catch (IllegalAccessException e) {
            t = e;
            msg = "DataSet '" + c.getSimpleName() + "' class must have a public constructor with List<Entry> and String params";
        } catch (InstantiationException e) {
            t = e;
            msg = "DataSet '" + c.getSimpleName() + "'class trows InstantiationException: " + e.getMessage();
        }

        assert false : msg;
        throw new IllegalArgumentException(msg, t);
    }


    // Data set line modifiers

    /**
     * Sort the data set list by X (date).
     *
     * @param dataSet the list to order
     */
    public static <T extends DataSet<?>> void sortDataSet(T dataSet) {
        Collections.sort(dataSet.getValues(), new EntryXComparator());
    }

    /**
     * Filter the data set list by range time limits.
     *
     * @param dataSet    the list to filter
     * @param timeLimits the range time limits
     * @param xFormatter the x formatter
     * @return the filtered list of data set entries
     */
    public static <T extends DataSet<?>> T filterDataSetByTimeRangeLimits(T dataSet, TimeRangeLimits timeLimits, ChartBaseFormatter xFormatter) {
        //float fromDate = xFormatter.from(timeLimits.getFromDate());
        //float toDate = xFormatter.from(timeLimits.getToDate());
        long fromDate = timeLimits.getFromDate().getTime();
        long toDate = timeLimits.getToDate().getTime();

        // Filter by range time limits
        List<Entry> newEntryList = new ArrayList<>();
        for (int i = 0; i < dataSet.getEntryCount(); i++) {
            Entry entry = dataSet.getEntryForIndex(i);
            Date entryDate = ((ChartDateTimeFormatter) xFormatter).toDate(entry.getX());
            if (fromDate <= entryDate.getTime() && entryDate.getTime() <= toDate)
                newEntryList.add(dataSet.getEntryForIndex(i));
        }

        return newDataSet(dataSet, newEntryList);
    }

    /**
     * Reduce the number of entries to maxCount.
     * <p>
     * The first and last entry are always included.
     * The intermediate entry are chosen as the closest to the middle of the range.
     * The returned list is always sorted by date.
     *
     * @param dataSet  the list to reduce
     * @param maxCount the maximum number of entries to return
     * @return the reduced list
     */
    public static <T extends DataSet<?>> T reduceMiddleValueDataSet(T dataSet, int maxCount) {
        return reduceMiddleValueDataSet(dataSet, maxCount, true);
    }

    /**
     * Reduce the number of entries to maxCount.
     * <p>
     * The first and last entry are always included.
     * The intermediate entry are chosen as the closest to the middle of the range.
     *
     * @param dataSet     the list to reduce
     * @param maxCount    the maximum number of entries to return
     * @param sortDataSet if true, the data set will be sorted by date
     * @return the reduced list
     */
    public static <T extends DataSet<?>> T reduceMiddleValueDataSet(T dataSet, int maxCount, boolean sortDataSet) {
        maxCount = max(2, maxCount);

        if (sortDataSet) sortDataSet(dataSet);

        if (dataSet.getEntryCount() < maxCount) return dataSet;

        float inc = (float) dataSet.getEntryCount() / (maxCount - 2);

        List<Entry> dataSetFilteredEntries = new ArrayList<>();
        dataSetFilteredEntries.add(dataSet.getEntryForIndex(0));
        for (float f = inc / 2; f < dataSet.getEntryCount(); f = f + inc) {
            int i = round(f);
            if (i >= dataSet.getEntryCount()) continue;
            dataSetFilteredEntries.add(dataSet.getEntryForIndex(i));
        }
        dataSetFilteredEntries.add(dataSet.getEntryForIndex(dataSet.getEntryCount() - 1));

        return newDataSet(dataSet, dataSetFilteredEntries);
    }

    /**
     * Reduce the number of entries to maxCount.
     * <p>
     * The first and last entries are always included.
     * The intermediate entries are calculated as the average of the entries in the range.
     * The returned list is always sorted by date.
     *
     * @param dataSet  the list to reduce
     * @param maxCount the maximum number of entries to return
     * @return the reduced list
     */
    public static <T extends DataSet<?>> T reduceAvgDataSet(T dataSet, Class<?> entryClass, int maxCount, ChartDateTimeFormatter xFormatter) {
        return reduceAvgDataSet(dataSet, entryClass, maxCount, xFormatter, true);
    }

    /**
     * Reduce the number of entries to maxCount.
     * <p>
     * The first and last entries are always included.
     * The intermediate entries are calculated as the average of the entries in the range.
     * <p>
     * Include first entry from original list
     * From original list entry 1 to entry n-1
     * Every n entries
     * Calculate mid date between first and last entry's dates
     * Calculate average value of n entries
     * Include last entry from original list
     *
     * @param dataSet     the list to reduce
     * @param maxCount    the maximum number of entries to return
     * @param sortDataSet if true, the data set will be sorted by date
     * @return the reduced list
     */
    //public static <U extends Entry, T extends DataSet<U>> T reduceAvgDataSet(T dataSet, int maxCount, ChartDateTimeFormatter xFormatter, boolean sortDataSet) {
    public static <T extends DataSet<?>> T reduceAvgDataSet(T dataSet, Class<?> entryClass, int maxCount, ChartDateTimeFormatter xFormatter, boolean sortDataSet) {
        maxCount = max(2, maxCount);

        // sort data set
        if (sortDataSet) sortDataSet(dataSet);

        // if there are less entries than maxCount, return the original list
        if (dataSet.getEntryCount() < maxCount) return dataSet;

        float inc = (float) dataSet.getEntryCount() / (maxCount - 2);

        Date firstDate = null;
        List<Float> values = new ArrayList<>();

        List<Entry> dataSetFilteredEntries = new ArrayList<>();
        // add first status
        dataSetFilteredEntries.add(dataSet.getEntryForIndex(0));

        // add intermediate statuses
        for (int i = 1; i < dataSet.getEntryCount() - 1; i++) {
            // extract current status
            Entry currentEntity = dataSet.getEntryForIndex(i);
            if (firstDate == null) firstDate = xFormatter.toDate(currentEntity.getX());
            values.add(currentEntity.getY());

            // if it's time to add a new status
            if (i % round(inc) == 0 || i == dataSet.getEntryCount() - 2) {
                // calculate new date
                Date lastDate = xFormatter.toDate(currentEntity.getX());
                Date midDate = new Date((firstDate.getTime() + lastDate.getTime()) / 2);
                // calculate new value
                double sumValues = 0;
                for (int j = 0; j < values.size(); j++)
                    sumValues += values.get(j);
                double newValue = sumValues / values.size();
                // create and add new status
                Entry newEntry = newEntry(entryClass, xFormatter.from(midDate), (float) newValue);
                dataSetFilteredEntries.add(newEntry);
                // clean up for next partition
                firstDate = null;
                values.clear();
            }
        }

        // add last status
        Entry lastEntry = dataSet.getEntryForIndex(dataSet.getEntryCount() - 1);
        dataSetFilteredEntries.add(newEntry(entryClass, lastEntry.getX(), lastEntry.getY()));

        return newDataSet(dataSet, dataSetFilteredEntries);
    }

    public static <T extends DataSet<?>> T reducePartitionDataSet(T dataSet, Class<?> entryClass, int maxCount, ChartDateTimeFormatter xFormatter, TimeRangeLimits timeLimits) {
        return reducePartitionDataSet(dataSet, entryClass, maxCount, xFormatter, timeLimits, true);
    }

    /**
     * Reduce the number of entries to maxCount.
     * <p>
     * The last entry is always included.
     * The intermediate entries are calculated as the average of the entries in the range.
     * <p>
     * From original list entry 1 to entry n-1
     * Every n entries
     * Calculate mid date between first and last entry's dates
     * Calculate average value of n entries
     * Include last entry from original list
     *
     * @param dataSet     the list to reduce
     * @param maxCount    the maximum number of entries to return
     * @param sortDataSet if true, the data set will be sorted by date
     * @return the reduced list
     * @noinspection ConstantValue
     */
    public static <T extends DataSet<?>> T reducePartitionDataSet(T dataSet, Class<?> entryClass, int maxCount, ChartDateTimeFormatter xFormatter, TimeRangeLimits timeLimits, boolean sortDataSet) {
        boolean LOG = false;
        maxCount = max(3, maxCount);

        if (sortDataSet) sortDataSet(dataSet);

        long rangeLengthMs = timeLimits.getToDate().getTime() - timeLimits.getFromDate().getTime();
        long partitionMs = rangeLengthMs / (maxCount - 2);

        Map<Date, List<Float>> partitions = new HashMap<>();
        // Create empty partitions
        // Map<MidDate, Values list>
        partitions.put(timeLimits.getFromDate(), new ArrayList<>());
        float firstPartitionDateFloat = xFormatter.from(timeLimits.getFromDate());
        if (LOG)
            System.out.println("Created partition \t" + xFormatter.toString(firstPartitionDateFloat) + "\t\t from " + xFormatter.toString(firstPartitionDateFloat) + " - " + xFormatter.toString(firstPartitionDateFloat));
        Date currentDate = timeLimits.getFromDate();
        while (currentDate.getTime() < timeLimits.getToDate().getTime()) {
            Date partitionStartDate = currentDate;
            Date partitionMidDate = new Date(partitionStartDate.getTime() + (partitionMs / 2));
            partitions.put(partitionMidDate, new ArrayList<>());
            currentDate = new Date(currentDate.getTime() + partitionMs);
            Date partitionEndDate = currentDate;
            float partitionStartDateFloat = xFormatter.from(partitionStartDate);
            float partitionMidDateFloat = xFormatter.from(partitionMidDate);
            float partitionEndDateFloat = xFormatter.from(partitionEndDate);
            if (LOG)
                System.out.println("Created partition \t" + xFormatter.toString(partitionMidDateFloat) + "\t\t from " + xFormatter.toString(partitionStartDateFloat) + " - " + xFormatter.toString(partitionEndDateFloat));
        }
        partitions.put(timeLimits.getToDate(), new ArrayList<>());
        float lastPartitionDateFloat = xFormatter.from(timeLimits.getToDate());
        if (LOG)
            System.out.println("Created partition \t" + xFormatter.toString(lastPartitionDateFloat) + "\t\t from " + xFormatter.toString(lastPartitionDateFloat) + " - " + xFormatter.toString(lastPartitionDateFloat));

        int startIdx = 0;
        // Copy first item from original data set as first partition
        // Only if original data set is not empty, and first element date is "near" to FROM date
        List<Float> firstPartition = new ArrayList<>();
        Date startMidDate = new Date(timeLimits.getFromDate().getTime() + (partitionMs / 2));
        if (dataSet.getEntryCount() != 0) {
            Entry firstEntry = dataSet.getEntryForIndex(0);
            if (xFormatter.from(timeLimits.getFromDate()) <= firstEntry.getX() && firstEntry.getX() < xFormatter.from(startMidDate)) {
                firstPartition.add(firstEntry.getY());
                if (LOG)
                    System.out.println("Populated partition " + xFormatter.toString(firstPartitionDateFloat) + "\t from " + xFormatter.toString(firstPartitionDateFloat) + " - " + xFormatter.toString(firstPartitionDateFloat) + "\t values: " + firstEntry.getY());
                startIdx = 1;
            }
        }
        partitions.put(timeLimits.getFromDate(), firstPartition);        // is it correct use range time limits' FROM date?

        int endIdxOffset = 0;
        // Copy last item from original data set as last partition
        // Only if original data set is not empty, and last element date is "near" to TO date
        List<Float> lastPartition = new ArrayList<>();
        Date endMidDate = new Date(timeLimits.getToDate().getTime() - (partitionMs / 2));
        if (dataSet.getEntryCount() != 0) {
            Entry lastEntry = dataSet.getEntryForIndex(dataSet.getEntryCount() - 1);
            if (xFormatter.from(endMidDate) < lastEntry.getX() && lastEntry.getX() <= xFormatter.from(timeLimits.getToDate())) {
                lastPartition.add(lastEntry.getY());
                if (LOG)
                    System.out.println("Populated partition " + xFormatter.toString(lastPartitionDateFloat) + "\t from " + xFormatter.toString(lastPartitionDateFloat) + " - " + xFormatter.toString(lastPartitionDateFloat) + "\t values: " + lastEntry.getY());
                endIdxOffset = 1;
            }
        }
        partitions.put(timeLimits.getToDate(), lastPartition);        // is it correct use range time limits' TO date?

        // Calculate intermediate partitions
        Date cP_StartDate = new Date(timeLimits.getFromDate().getTime());
        Date cP_MidDate = new Date(timeLimits.getFromDate().getTime() + (partitionMs / 2));
        Date cP_EndDate = new Date(cP_StartDate.getTime() + partitionMs);
        float cP_StartDateFloat = xFormatter.from(cP_StartDate);
        float cP_MidFloat = xFormatter.from(cP_MidDate);
        float cP_EndDateFloat = xFormatter.from(cP_EndDate);
        for (int i = startIdx; i < dataSet.getEntryCount() - endIdxOffset; i++) {
            Entry currentEntity = dataSet.getEntryForIndex(i);

            // current entry is in next partition (including cP_EndDateFloat)
            if (currentEntity.getX() >= cP_EndDateFloat) {
                // switch partition
                if (LOG)
                    System.out.println("Populated partition " + xFormatter.toString(cP_MidFloat) + "\t from " + xFormatter.toString(cP_StartDateFloat) + " - " + xFormatter.toString(cP_EndDateFloat) + "\t values: " + partitions.get(cP_MidDate));
                cP_StartDate = cP_EndDate;
                cP_MidDate = new Date(cP_EndDate.getTime() + (partitionMs / 2));
                cP_EndDate = new Date(cP_EndDate.getTime() + partitionMs);
                cP_StartDateFloat = xFormatter.from(cP_StartDate);
                cP_MidFloat = xFormatter.from(cP_MidDate);
                cP_EndDateFloat = xFormatter.from(cP_EndDate);

                // skip partitions with no data
                while (currentEntity.getX() >= cP_EndDateFloat) {
                    if (LOG)
                        System.out.println("Skipped partition " + xFormatter.toString(cP_MidFloat) + "\t from " + xFormatter.toString(cP_StartDateFloat) + " - " + xFormatter.toString(cP_EndDateFloat) + " (no data)");
                    partitions.put(cP_MidDate, new ArrayList<>());
                    cP_MidDate = new Date(cP_EndDate.getTime() + (partitionMs / 2));
                    cP_EndDate = new Date(cP_EndDate.getTime() + partitionMs);
                    cP_EndDateFloat = xFormatter.from(cP_EndDate);
                }
            }

            // Add entry to current partition
            List<Float> partitionEntry = partitions.get(cP_MidDate);
            if (partitionEntry == null) {
                partitionEntry = new ArrayList<>();
                partitions.put(cP_MidDate, partitionEntry);
            }
            partitionEntry.add(currentEntity.getY());
        }
        if (LOG)
            System.out.println("Populated partition " + xFormatter.toString(cP_MidFloat) + "\t from " + xFormatter.toString(cP_StartDateFloat) + " - " + xFormatter.toString(cP_EndDateFloat) + "\t values: " + partitions.get(cP_MidDate));

        // Convert Map<Date,List<Float>> to List<Entry>
        T reducedDataSet = mapPartitionsToDataSet(dataSet, entryClass, partitions, xFormatter);
        if (sortDataSet) sortDataSet(reducedDataSet);

        return reducedDataSet;
    }

    public static <T extends DataSet<?>> T reduceEqualsPartitionDataSet(T dataSet, Class<?> entryClass, int maxCount, boolean post, ChartDateTimeFormatter xFormatter, TimeRangeLimits timeLimits) {
        return reduceEqualsPartitionDataSet(dataSet, entryClass, maxCount, post, xFormatter, timeLimits, true);
    }

    public static <T extends DataSet<?>> T reduceEqualsPartitionDataSet(T dataSet, Class<?> entryClass, int maxCount, boolean post, ChartDateTimeFormatter xFormatter, TimeRangeLimits timeLimits, boolean sortDataSet) {
        assert !post : "Parameter 'post'='true' not implemented.";

        boolean LOG = false;
        maxCount = max(3, maxCount);

        if (sortDataSet) sortDataSet(dataSet);

        long rangeLengthMs = timeLimits.getToDate().getTime() - timeLimits.getFromDate().getTime();
        long partitionMs = rangeLengthMs / (maxCount - 1);

        Map<Date, List<Float>> partitions = new HashMap<>();
        // Create empty partitions, based on StartDate
        Date currentDate = timeLimits.getFromDate();
        while (currentDate.getTime() <= timeLimits.getToDate().getTime()) {
            Date partitionStartDate = currentDate;
            Date partitionMidDate = new Date(partitionStartDate.getTime() + (partitionMs / 2));
            partitions.put(partitionStartDate, new ArrayList<>());
            currentDate = new Date(currentDate.getTime() + partitionMs);
            Date partitionEndDate = currentDate;
            float partitionStartDateFloat = xFormatter.from(partitionStartDate);
            float partitionMidDateFloat = xFormatter.from(partitionMidDate);
            float partitionEndDateFloat = xFormatter.from(partitionEndDate);
            if (LOG)
                System.out.println("Created partition \t" + xFormatter.toString(partitionStartDateFloat) + "\t\t from " + xFormatter.toString(partitionStartDateFloat) + " - " + xFormatter.toString(partitionEndDateFloat));
        }

        while (currentDate.getTime() < timeLimits.getToDate().getTime()) {
            Date partitionStartDate = currentDate;
            Date partitionMidDate = new Date(partitionStartDate.getTime() + (partitionMs / 2));
            partitions.put(partitionMidDate, new ArrayList<>());
            currentDate = new Date(currentDate.getTime() + partitionMs);
            Date partitionEndDate = currentDate;
            float partitionStartDateFloat = xFormatter.from(partitionStartDate);
            float partitionMidDateFloat = xFormatter.from(partitionMidDate);
            float partitionEndDateFloat = xFormatter.from(partitionEndDate);
            if (LOG)
                System.out.println("Created partition \t" + xFormatter.toString(partitionMidDateFloat) + "\t\t from " + xFormatter.toString(partitionStartDateFloat) + " - " + xFormatter.toString(partitionEndDateFloat));
        }

        // Calculate partitions
        Date cP_StartDate = new Date(timeLimits.getFromDate().getTime());
        Date cP_EndDate = new Date(cP_StartDate.getTime() + partitionMs);
        float cP_StartDateFloat = xFormatter.from(cP_StartDate);
        float cP_EndDateFloat = xFormatter.from(cP_EndDate);
        for (int i = 0; i < dataSet.getEntryCount(); i++) {
            Entry currentEntity = dataSet.getEntryForIndex(i);

            // current entry is in next partition (including cP_EndDateFloat)
            if (currentEntity.getX() >= cP_EndDateFloat) {
                // switch partition
                if (LOG)
                    System.out.println("Populated partition " + xFormatter.toString(cP_StartDateFloat) + "\t from " + xFormatter.toString(cP_StartDateFloat) + " - " + xFormatter.toString(cP_EndDateFloat) + "\t values: " + partitions.get(cP_StartDate));
                cP_StartDate = cP_EndDate;
                cP_EndDate = new Date(cP_EndDate.getTime() + partitionMs);
                cP_StartDateFloat = xFormatter.from(cP_StartDate);
                cP_EndDateFloat = xFormatter.from(cP_EndDate);

                // skip partitions with no data
                while (currentEntity.getX() >= cP_EndDateFloat) {
                    if (LOG)
                        System.out.println("Skipped partition " + xFormatter.toString(cP_StartDateFloat) + "\t from " + xFormatter.toString(cP_StartDateFloat) + " - " + xFormatter.toString(cP_EndDateFloat) + "\t values: " + partitions.get(cP_StartDate));
                    partitions.put(cP_StartDate, new ArrayList<>());
                    cP_StartDate = cP_EndDate;
                    cP_EndDate = new Date(cP_EndDate.getTime() + partitionMs);
                    cP_EndDateFloat = xFormatter.from(cP_EndDate);
                }
            }

            // Add entry to current partition
            List<Float> partitionEntry = partitions.get(cP_StartDate);
            if (partitionEntry == null) {
                partitionEntry = new ArrayList<>();
                partitions.put(cP_StartDate, partitionEntry);
            }
            partitionEntry.add(currentEntity.getY());
        }
        if (LOG)
            System.out.println("Populated partition " + xFormatter.toString(cP_StartDateFloat) + "\t from " + xFormatter.toString(cP_StartDateFloat) + " - " + xFormatter.toString(cP_EndDateFloat) + "\t values: " + partitions.get(cP_StartDate));

        // Convert Map<Date,List<Float>> to List<Entry
        T reducedDataSet = mapPartitionsToDataSet(dataSet, entryClass, partitions, xFormatter);
        if (sortDataSet) sortDataSet(reducedDataSet);

        return reducedDataSet;
    }

    public static <T extends DataSet<?>> T removeZeroValueEntries(T dataSet) {
        List<Entry> dataSetFilteredEntries = new ArrayList<>();
        if (dataSet.getEntryCount() > 0) dataSetFilteredEntries.add(dataSet.getEntryForIndex(0));
        for (int i = 1; i < dataSet.getEntryCount() - 1; i++) {
            Entry entryPrev = dataSet.getEntryForIndex(i - 1);
            Entry entry = dataSet.getEntryForIndex(i);
            Entry entryNext = dataSet.getEntryForIndex(i + 1);
            if (!(entryPrev.getY() != 0 && entry.getY() == 0 && entryNext.getY() != 0))
                dataSetFilteredEntries.add(entry);
        }
        if (dataSet.getEntryCount() > 1)
            dataSetFilteredEntries.add(dataSet.getEntryForIndex(dataSet.getEntryCount() - 1));
        return newDataSet(dataSet, dataSetFilteredEntries);
    }

    private static <T extends DataSet<?>> T mapPartitionsToDataSet(T dataSet, Class<?> entryClass, Map<Date, List<Float>> partitions, ChartDateTimeFormatter xFormatter) {
        List<Entry> dataSetFilteredEntries = new ArrayList<>();
        for (Map.Entry<Date, List<Float>> partition : partitions.entrySet()) {
            float avgValue = 0;
            StringBuilder log = new StringBuilder();
            if (partition.getValue().size() > 0) {
                float sumValues = 0;
                for (Float value : partition.getValue()) {
                    sumValues += value;
                    log.append(value).append(", ");
                }
                avgValue = sumValues / partition.getValue().size();
            }
            float date = xFormatter.from(partition.getKey());
            dataSetFilteredEntries.add(newEntry(entryClass, date, avgValue));
        }

        return newDataSet(dataSet, dataSetFilteredEntries);
    }

}
