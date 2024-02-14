package com.robypomper.josp.jsl.android.components.charts.utils;

import com.github.mikephil.charting.data.BaseEntry;
import com.github.mikephil.charting.data.DataSet;
import com.robypomper.josp.jsl.android.components.charts.adapters.ChartViewAdapter;

/**
 * Observer interface used by ChartViewAdapter to notify the chart about data set changes
 * and other events.
 * <p>
 * Internal interface used by {@link com.robypomper.josp.jsl.android.components.charts.adapters.ChartViewAdapter}
 * to communicate with main chart.
 */
public interface ChartAdapterObserver {

    /**
     * Called by {@link ChartViewAdapter} when it needs to fetch
     * data set.
     *
     * @param dataSetName name of the data set to fetch.
     */
    void fetch(String dataSetName);

    /**
     * Add/update specified data set to the chart.
     * <p>
     * Method called by the {@link ChartViewAdapter} when a data set is
     * fetched from the adapter.
     *
     * @param dataSetName   name of the data set to add/update.
     * @param dataSet       data set to add/update.
     * @param historyLimits limits of the history.
     */
    void processFetchedDataSet(String dataSetName, DataSet<?> dataSet, TimeRangeLimits historyLimits);

    /**
     * Called by {@link ChartViewAdapter} when occurs an error while fetching
     * data set.
     *
     * @param dataSetName name of the data set that caused the error.
     * @param timeRangeLimits limits of the history.
     * @param errorMsg error message.
     * @param error error occurred while fetching data set, optional.
     */
    void processErrorDataSet(String dataSetName, TimeRangeLimits timeRangeLimits, String errorMsg, Throwable error);

    /**
     * Remove specified data set from the chart.
     * <p>
     * Method called by the {@link ChartViewAdapter} when a data set is
     * removed from the adapter.
     *
     * @param dataSetName name of the data set to remove.
     */
    void removeDataSetFromChart(String dataSetName, boolean invalidate);

    Class<? extends DataSet<?>> getChartDataSetClass();

    Class<? extends BaseEntry> getChartEntryClass();

}
