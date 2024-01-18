package com.robypomper.josp.jsl.android.components.charts.adapters;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartBaseFormatter;
import com.robypomper.josp.jsl.android.components.charts.utils.TimeRangeLimits;

import java.util.List;


/** @noinspection unused*/
public interface ChartViewAdapter {

    // Data sets getters

    List<String> getDataSetNames();

    String getDataSetLabel(String dataSetName);

    int getDataSetColor(String dataSetName);

    YAxis.AxisDependency getDataSetYAxisDep(String dataSetName);


    // Formatters getters

    ChartBaseFormatter getXFormatter();

    ChartBaseFormatter getYLeftFormatter();

    ChartBaseFormatter getYRightFormatter();

    /**
     * Return the formatter (left or right) assigned to the specified data set.
     *
     * @param dataSet the data set name.
     * @return the formatter (left or right) assigned to the specified data set.
     */
    ChartBaseFormatter getDataSetYFormatter(String dataSet);


    // Fetching methods

    /**
     * The method's implementations must call the updateDataSetToChart() method to update the chart
     * with fetched data.
     */
    void doFetch(String dataSetName, TimeRangeLimits timeRangeLimits);


    // Style methods

    void setupChartStyle(Chart<?> chart);

    void setupDataStyle(ChartData<?> chartData);

    void setupDataSetStyle(String dataSetName, DataSet<?> dataSet);

}
