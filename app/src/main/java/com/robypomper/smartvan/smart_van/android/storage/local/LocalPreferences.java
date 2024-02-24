package com.robypomper.smartvan.smart_van.android.storage.local;

import android.content.Context;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.rxjava3.RxDataStore;

import com.robypomper.smartvan.smart_van.android.storage.SVPreferences;
import com.robypomper.smartvan.smart_van.android.utils.DataStoreUtils;


/**
 * The local {@link SVPreferences} implementation for the SmartVan application.
 * <p>
 * This implementation use the AndroidX DataStore to store the preferences.
 */
public class LocalPreferences implements SVPreferences {

    // Constants

    /**
     * The name of the data store used by this class.
     */
    private static final String DATA_STORE_NAME = "smart_van_preferences";
    /**
     * The key for the ask for use favourite object id preference.
     */
    private static final Preferences.Key<Boolean> ASK_USE_FAV_OBJ_ID = PreferencesKeys.booleanKey("ASK_USE_FAV_OBJ_ID");
    /**
     * The default value for the ask for use favourite object id preference.
     */
    private static final boolean DEF_ASK_USE_FAV_OBJ_ID = true;
    /**
     * The key for the ask for set favourite object id preference.
     */
    private static final Preferences.Key<Boolean> ASK_SET_FAV_OBJ_ID = PreferencesKeys.booleanKey("ASK_SET_FAV_OBJ_ID");
    /**
     * The default value for the ask for set favourite object id preference.
     */
    private static final boolean DEF_ASK_SET_FAV_OBJ_ID = true;
    /**
     * The key for the timeout in seconds for the chart.
     */
    private static final Preferences.Key<Integer> CHARTS_TIMEOUT = PreferencesKeys.intKey("CHARTS_TIMEOUT");
    /**
     * The default value for the timeout in seconds for the chart.
     */
    private static final int DEF_CHARTS_TIMEOUT = 15;


    // Internal vars

    /**
     * The DataStore used by this class.
     */
    private final RxDataStore<Preferences> dataStore;


    // Constructor

    /**
     * Initialize the local preferences.
     *
     * @param ctx the application's context.
     */
    public LocalPreferences(Context ctx) {
        dataStore = DataStoreUtils.initDataStore(ctx, DATA_STORE_NAME);
    }


    // SVPreferences

    /**
     * During application startup, when the favourite object id is set and
     * corresponding object is available, ask the user if he wants to use the
     * favourite object id.
     * <p>
     * Default value is true.
     * <p>
     * @return true if it must ask the user if he wants to use the favourite
     * object, false otherwise.
     */
    @Override
    public boolean askForUseFavouriteObjectId() {
        return DataStoreUtils.getFromDataStore(dataStore, ASK_USE_FAV_OBJ_ID, DEF_ASK_USE_FAV_OBJ_ID);
    }

    /**
     * Set the ask for use favourite object id.
     * <p>
     * During application startup, when the favourite object id is set and
     * corresponding object is available, ask the user if he wants to use the
     * favourite object id.
     * <p>
     * @param askForUseFavouriteObjectId true if it must ask the user if he wants
     * to use the favourite object, false otherwise.
     */
    @Override
    public void setAskForUseFavouriteObjectId(boolean askForUseFavouriteObjectId) {
        DataStoreUtils.setToDataStore(dataStore, ASK_USE_FAV_OBJ_ID, askForUseFavouriteObjectId);
    }

    /**
     * During application startup, when the user selected an object from the
     * {@link com.robypomper.smartvan.smart_van.android.activities.SVSelectObjectActivity}
     * activity and the favourite object id is not set, ask the user if he
     * wants to set the selected object as favourite.
     * <p>
     * Default value is true.
     * <p>
     * @return true if it must ask the user to set the favourite object id, false
     * otherwise.
     */
    @Override
    public boolean askForSetFavouriteObjectId() {
        return DataStoreUtils.getFromDataStore(dataStore, ASK_SET_FAV_OBJ_ID, DEF_ASK_SET_FAV_OBJ_ID);
    }

    /**
     * Set the ask for set favourite object id.
     * <p>
     * During application startup, when the user selected an object from the
     * {@link com.robypomper.smartvan.smart_van.android.activities.SVSelectObjectActivity}
     * activity and the favourite object id is not set, ask the user if he
     * wants to set the selected object as favourite.
     * <p>
     * @param askForSetFavouriteObjectId true if it must ask the user to set the
     * favourite object id, false otherwise.
     */
    @Override
    public void setAskForSetFavouriteObjectId(boolean askForSetFavouriteObjectId) {
        DataStoreUtils.setToDataStore(dataStore, ASK_SET_FAV_OBJ_ID, askForSetFavouriteObjectId);
    }

    /**
     * Get the timeout in seconds for the chart.
     * <p>
     * Default value is 15.
     * <p>
     * @return the timeout in seconds for the chart.
     */
    @Override
    public int getChartTimeoutSeconds() {
        return DataStoreUtils.getFromDataStore(dataStore, CHARTS_TIMEOUT, DEF_CHARTS_TIMEOUT);
    }

    /**
     * Set the timeout in seconds for the chart.
     * <p>
     * @param chartTimeoutSeconds the timeout in seconds for the chart.
     */
    @Override
    public void setChartTimeoutSeconds(int chartTimeoutSeconds) {
        DataStoreUtils.setToDataStore(dataStore, CHARTS_TIMEOUT, chartTimeoutSeconds);
    }

}
