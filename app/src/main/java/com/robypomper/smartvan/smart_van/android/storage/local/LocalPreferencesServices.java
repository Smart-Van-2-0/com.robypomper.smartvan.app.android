package com.robypomper.smartvan.smart_van.android.storage.local;

import android.content.Context;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.rxjava3.RxDataStore;

import com.robypomper.smartvan.smart_van.android.storage.SVAbsSubStorage;
import com.robypomper.smartvan.smart_van.android.storage.SVPreferencesServices;
import com.robypomper.smartvan.smart_van.android.utils.DataStoreUtils;


/**
 * The local {@link SVPreferencesServices} implementation for the SmartVan application.
 * <p>
 * This implementation use the AndroidX DataStore to store the preferences.
 */
public class LocalPreferencesServices extends SVAbsSubStorage implements SVPreferencesServices {

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
     * Initialize the local preferences services.
     *
     * @param ctx the application's context.
     */
    public LocalPreferencesServices(Context ctx) {
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
     */
    @Override
    public void generateStorage(String objectId) {
        dataStore = DataStoreUtils.initDataStore(ctx, DATA_STORE_NAME_PREFIX + objectId);
        setObjectId(objectId);
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


    // SVPreferencesServices

    /**
     * Set the name of the specified SV Service.
     *
     * @param srvPath the path of the SV Service for which to set the name.
     * @param name    the name to set for the SV Service.
     */
    @Override
    public void setName(String srvPath, String name) {
        final Preferences.Key<String> PREF = PreferencesKeys.stringKey(ctx.getString(NAME_PROP_PREFIX, srvPath));
        DataStoreUtils.setToDataStore(dataStore, PREF, name);
    }

    /**
     * Get the name of the specified SV Service.
     *
     * @param srvPath the path of the SV Service for which to get the name.
     * @return the name of the SV Service.
     */
    @Override
    public String getName(String srvPath) {
        final Preferences.Key<String> PREF = PreferencesKeys.stringKey(ctx.getString(NAME_PROP_PREFIX, srvPath));
        return DataStoreUtils.getFromDataStore(dataStore, PREF, DEF_NAME);
    }

    /**
     * Set the icon of the specified SV Service.
     *
     * @param srvPath  the path of the SV Service for which to set the icon.
     * @param iconName the string corresponding to the icon to set for the SV Service.
     */
    @Override
    public void setIconName(String srvPath, String iconName) {
        final Preferences.Key<String> PREF = PreferencesKeys.stringKey(ctx.getString(ICON_PROP_PREFIX, srvPath));
        DataStoreUtils.setToDataStore(dataStore, PREF, iconName);
    }

    /**
     * Get the icon drawable of the specified SV Service.
     *
     * @param srvPath the path of the SV Service for which to get the icon drawable.
     * @return the string corresponding to the icon drawable of the SV Service.
     */
    @Override
    public String getIconName(String srvPath) {
        final Preferences.Key<String> PREF = PreferencesKeys.stringKey(ctx.getString(ICON_PROP_PREFIX, srvPath));
        return DataStoreUtils.getFromDataStore(dataStore, PREF, DEF_ICON_NAME);
    }

}
