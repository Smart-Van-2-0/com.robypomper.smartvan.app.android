package com.robypomper.smartvan.smart_van.android.storage;


import androidx.annotation.ColorInt;

import com.robypomper.smartvan.smart_van.android.R;

/**
 * This interface is used to define the generic preferences of the SmartVan.
 */
public interface SVPreferences extends SVSubStorage {

    // Constants

    /**
     * The name of the data store used by this class.
     */
    int DATA_STORE_NAME_PREFIX = R.string.pref_group__pref_prefix; // + ObjectId
    /**
     * The key for the SV box color.
     */
    int SVBOX_COLOR = R.string.pref__pref__svbox_color;
    /**
     * The default value for the SV box color.
     */
    @ColorInt int DEF_SVBOX_COLOR = 0xFF0BB2B2; // R.color.smartvan_origin, can't access to getResources()
    /**
     * The key for the timeout in seconds for the chart.
     */
    int CHARTS_TIMEOUT = R.string.pref__pref__charts_timeout;
    /**
     * The default value for the timeout in seconds for the chart.
     */
    int DEF_CHARTS_TIMEOUT = 15;


    // SVPreferences

    /**
     * Get the SV box color.
     * <p>
     * Default value is `R.color.smartvan_origin`.
     * <p>
     * @return the SV box color.
     */
    @ColorInt int getSVBoxColor();

    /**
     * Set the SV box color.
     * <p>
     * @param color the SV box color.
     */
    void setSVBoxColor(@ColorInt int color);

    /**
     * Get the timeout in seconds for the chart.
     * <p>
     * Default value is 15.
     * <p>
     * @return the timeout in seconds for the chart.
     */
    int getChartTimeoutSeconds();

    /**
     * Set the timeout in seconds for the chart.
     * <p>
     * @param chartTimeoutSeconds the timeout in seconds for the chart.
     */
    void setChartTimeoutSeconds(int chartTimeoutSeconds);

}
