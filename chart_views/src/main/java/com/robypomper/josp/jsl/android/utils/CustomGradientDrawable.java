package com.robypomper.josp.jsl.android.utils;


import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;


/**
 * Simple class to generate a gradient drawable.
 */
public class CustomGradientDrawable extends GradientDrawable {

    public CustomGradientDrawable(int topColor) {
        super(Orientation.TOP_BOTTOM, new int[]{topColor, Color.TRANSPARENT});
        setGradientType(LINEAR_GRADIENT);
        setGradientRadius(90);
    }

    public CustomGradientDrawable(int topColor, int bottomColor) {
        super(Orientation.TOP_BOTTOM, new int[]{topColor, bottomColor});
        setGradientType(LINEAR_GRADIENT);
        setGradientRadius(90);
    }

    public CustomGradientDrawable(int topColor, int centerColor, int bottomColor) {
        super(Orientation.TOP_BOTTOM, new int[]{topColor, centerColor, bottomColor});
        setGradientType(LINEAR_GRADIENT);
        setGradientRadius(90);
    }

}