package com.robypomper.smartvan.smart_van.android.storage.local;

import android.content.Context;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.rxjava3.RxDataStore;

import com.robypomper.smartvan.smart_van.android.storage.SVAbsSubStorage;
import com.robypomper.smartvan.smart_van.android.storage.SVPreferences;
import com.robypomper.smartvan.smart_van.android.utils.DataStoreUtils;


/**
 * The local {@link SVPreferences} implementation for the SmartVan application.
 * <p>
 * This implementation use the AndroidX DataStore to store the preferences.
 */
public class LocalPreferences extends SVAbsSubStorage implements SVPreferences {

    // Internal vars

    /**
     * The application's context.
     */
    private final Context ctx;
    /**
     * The DataStore used by this class.
     */
    private RxDataStore<Preferences> dataStore;


    // Constructors

    /**
     * Initialize the local preferences.
     *
     * @param ctx the application's context.
     */
    public LocalPreferences(Context ctx) {
        this.ctx = ctx;
    }


    // SVSubStorage

    /**
     * Generate the storage for the given object id.
     * <p>
     * This method is called when the user selects a new object from the
     * {@link com.robypomper.smartvan.smart_van.android.activities.SVSelectObjectActivity}
     * activity.
     *
     * @param objectId the id of the object for which to generate the storage.
     */    @Override
    public void generateStorage(String objectId) {
        dataStore = DataStoreUtils.initDataStore(ctx, ctx.getString(DATA_STORE_NAME_PREFIX, objectId));
    }

    /**
     * Clear the storage for the given object id.
     * <p>
     * This method is called when the user requires to delete all the data
     * for a specific object.
     *
     * @param objectId the id of the object for which to clear the storage.
     */
    @Override
    public void clearStorage(String objectId) {
        DataStoreUtils.clearDataStore(dataStore);
    }


    // SVPreferences

    /**
     * Get the SV box color.
     * <p>
     * Default value is `R.color.smartvan_origin`.
     * <p>
     *
     * @return the SV box color.
     */
    @Override
    public int getSVBoxColor() {
        return DataStoreUtils.getFromDataStore(dataStore, DataStoreUtils.intKey(ctx, SVBOX_COLOR), DEF_SVBOX_COLOR);
    }

    /**
     * Set the SV box color.
     * <p>
     *
     * @param color the SV box color.
     */
    @Override
    public void setSVBoxColor(int color) {
        DataStoreUtils.setToDataStore(dataStore, DataStoreUtils.intKey(ctx, SVBOX_COLOR), color);
    }

    /**
     * Get the timeout in seconds for the chart.
     * <p>
     * Default value is 15.
     * <p>
     *
     * @return the timeout in seconds for the chart.
     */
    @Override
    public int getChartTimeoutSeconds() {
        return DataStoreUtils.getFromDataStore(dataStore, DataStoreUtils.intKey(ctx, CHARTS_TIMEOUT), DEF_CHARTS_TIMEOUT);
    }

    /**
     * Set the timeout in seconds for the chart.
     * <p>
     *
     * @param chartTimeoutSeconds the timeout in seconds for the chart.
     */
    @Override
    public void setChartTimeoutSeconds(int chartTimeoutSeconds) {
        DataStoreUtils.setToDataStore(dataStore, DataStoreUtils.intKey(ctx, CHARTS_TIMEOUT), chartTimeoutSeconds);
    }

}
