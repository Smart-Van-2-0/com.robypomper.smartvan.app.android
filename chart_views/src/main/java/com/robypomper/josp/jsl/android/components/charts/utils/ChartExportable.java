package com.robypomper.josp.jsl.android.components.charts.utils;

import android.graphics.Bitmap;

import com.github.mikephil.charting.data.DataSet;

import java.util.Map;

public interface ChartExportable {

    Bitmap exportImg();

    Map<String, DataSet<?>> exportChartData();

    Map<String, DataSet<?>> exportRAW();

}
