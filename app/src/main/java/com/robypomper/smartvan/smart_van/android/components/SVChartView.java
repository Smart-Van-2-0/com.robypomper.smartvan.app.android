package com.robypomper.smartvan.smart_van.android.components;

import com.github.mikephil.charting.components.YAxis;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SVChartView {

    void setEnabled(boolean enabled);


    // JSL Components management

    class ChartComponentInfo {

        // Internal vars

        final JSLRangeState jslRangeComponent;
        final String label;
        final String unit;
        final int color;
        final YAxis.AxisDependency axisDependency;


        // Constructors

        ChartComponentInfo(JSLRangeState jslRangeComponent, String label, String unit, int color, YAxis.AxisDependency axisDependency) {
            this.jslRangeComponent = jslRangeComponent;
            this.label = label;
            this.unit = unit;
            this.color = color;
            this.axisDependency = axisDependency;
        }

    }

    void addComponent(JSLRangeState comp, String compLabel, String compUnit, int compColor, YAxis.AxisDependency axisDependency);

    void removeComponent(JSLRangeState comp);


    // Data management

    class ChartDataSet {

        // Internal vars

        final ChartComponentInfo componentInfo;
        final Map<? extends Comparable<?>, ? extends Number> data;

        // Constructors

        ChartDataSet(ChartComponentInfo componentInfo, Map<? extends Comparable<?>, ? extends Number> data) {
            this.componentInfo = componentInfo;
            this.data = data;
        }

    }

    boolean isFetching();

    void fetchData();

    void clearData();


    // Data Listeners

    interface DataListener {

        void onFetchStarted();

        void onProcessingStarted();

        void onDisplayingStarted();

        void onFetchedTerminated();

    }

    void addDataListener(DataListener listener);

    void removeDataListener(DataListener listener);

}
