package com.robypomper.josp.jsl.android.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

public class ThemeUtils {

    public static @ColorInt int colorFromThemeAttribute(@AttrRes int attr, Resources.Theme theme) {
        TypedValue resolvedAttr = new TypedValue();
        theme.resolveAttribute(attr, resolvedAttr, true);
        return resolvedAttr.data;
    }

    /**
     *
     * colorOriginal = ThemeUtils.getAppColor(this, com.google.android.material.R.attr.colorPrimary);
     * colorOriginal = ThemeUtils.getAppColor(this, android.R.attr.textColorPrimary);
     * colorOriginal = ThemeUtils.getAppColor(this, android.R.attr.colorError);
     * colorError = ThemeUtils.getAppColor(this, com.google.android.material.R.attr.colorError);
     * @param context
     * @param colorAttr
     * @return
     */
    @ColorInt
    public static int getAppColor(Context context, @AttrRes int colorAttr) {
        TypedValue resolvedAttr = resolveThemeAttr(context, colorAttr);
        if (resolvedAttr.resourceId != 0)
            return ContextCompat.getColor(context, resolvedAttr.resourceId);
        return resolvedAttr.data;
    }

    @ColorRes
    public static int getAppColorRes(Context context, @AttrRes int colorAttr) {
        TypedValue resolvedAttr = resolveThemeAttr(context, colorAttr);
        // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
        if (resolvedAttr.resourceId != 0)
            return resolvedAttr.resourceId;
        return 0;
    }

    public static TypedValue resolveThemeAttr(Context context, @AttrRes int attrRes) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrRes, typedValue, true);
        return typedValue;
    }

}
