package com.robypomper.smartvan.smart_van.android.components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import com.github.mikephil.charting.data.DataSet;

import java.util.List;
import java.util.Map;

public interface SVChartViewExportable {

    // Exportable methods

    List<SVChartView.ChartDataSet> getDataSetsRaw();

    List<SVChartView.ChartDataSet> getDataSetsProcessed();

    List<SVChartView.ChartDataSet> getDataSetsDisplayed();

    View getChartView();


    // Export methods from ChartExportable

    static Bitmap loadBitmapFromView(View v) {
        int width = v.getLayoutParams().width > 0 ? v.getLayoutParams().width : v.getWidth();
        int height = v.getLayoutParams().height > 0 ? v.getLayoutParams().height : v.getHeight();
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

}
