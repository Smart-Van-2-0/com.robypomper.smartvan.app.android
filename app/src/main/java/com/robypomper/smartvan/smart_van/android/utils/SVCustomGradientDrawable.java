package com.robypomper.smartvan.smart_van.android.utils;


import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;


/**
 * Simple class to generate a gradient drawable.
 * @noinspection unused
 */
public class SVCustomGradientDrawable extends GradientDrawable {

    public SVCustomGradientDrawable(int topColor) {
        super(Orientation.TOP_BOTTOM, new int[]{topColor, Color.TRANSPARENT});
        setGradientType(LINEAR_GRADIENT);
        setGradientRadius(90);
    }

    public SVCustomGradientDrawable(int topColor, int bottomColor) {
        super(Orientation.TOP_BOTTOM, new int[]{topColor, bottomColor});
        setGradientType(LINEAR_GRADIENT);
        setGradientRadius(90);
    }

    public SVCustomGradientDrawable(int topColor, int centerColor, int bottomColor) {
        super(Orientation.TOP_BOTTOM, new int[]{topColor, centerColor, bottomColor});
        setGradientType(LINEAR_GRADIENT);
        setGradientRadius(90);
    }

}