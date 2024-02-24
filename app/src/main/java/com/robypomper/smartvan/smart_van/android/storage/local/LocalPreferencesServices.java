package com.robypomper.smartvan.smart_van.android.storage.local;

import android.content.Context;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.rxjava3.RxDataStore;

import com.robypomper.smartvan.smart_van.android.storage.SVAbsSubStorage;
import com.robypomper.smartvan.smart_van.android.storage.SVPreferencesServices;
import com.robypomper.smartvan.smart_van.android.utils.DataStoreUtils;

public class LocalPreferencesServices extends SVAbsSubStorage implements SVPreferencesServices {

    // Constants

    private static final String DATA_STORE_NAME_PREFIX = "smart_van_services_"; // + ObjectId
    private static final String NAME_PROP_PREFIX = "NAME_"; // + ComponentPath
    private static final String ICON_PROP_PREFIX = "ICON_"; // + ComponentPath
    private static final String DEF_NAME = null;
    private static final String DEF_ICON_NAME = null;


    // Internal vars

    private final Context ctx;
    private RxDataStore<Preferences> dataStore;


    // Constructors

    public LocalPreferencesServices(Context ctx) {
        this.ctx = ctx;
    }


    // SVSubStorage

    @Override
    public void generateStorage(String objectId) {
        dataStore = DataStoreUtils.initDataStore(ctx, DATA_STORE_NAME_PREFIX + objectId);
        setObjectId(objectId);
    }

    @Override
    public void clearStorage(String objectId) {
        DataStoreUtils.clearDataStore(dataStore);
    }


    @Override
    public void setName(String srvPath, String name) {
        final Preferences.Key<String> PREF = PreferencesKeys.stringKey(NAME_PROP_PREFIX + srvPath);
        DataStoreUtils.setToDataStore(dataStore, PREF, name);
    }

    @Override
    public String getName(String srvPath) {
        final Preferences.Key<String> PREF = PreferencesKeys.stringKey(NAME_PROP_PREFIX + srvPath);
        return DataStoreUtils.getFromDataStore(dataStore, PREF, DEF_NAME);
    }

    @Override
    public void setIconName(String srvPath, String iconName) {
        final Preferences.Key<String> PREF = PreferencesKeys.stringKey(ICON_PROP_PREFIX + srvPath);
        DataStoreUtils.setToDataStore(dataStore, PREF, iconName);
    }

    @Override
    public String getIconName(String srvPath) {
        final Preferences.Key<String> PREF = PreferencesKeys.stringKey(ICON_PROP_PREFIX + srvPath);
        return DataStoreUtils.getFromDataStore(dataStore, PREF, DEF_ICON_NAME);
    }

}
