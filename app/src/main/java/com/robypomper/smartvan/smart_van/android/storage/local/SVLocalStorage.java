package com.robypomper.smartvan.smart_van.android.storage.local;

import android.content.Context;

import com.robypomper.smartvan.smart_van.android.storage.SVPreferences;
import com.robypomper.smartvan.smart_van.android.storage.SVPreferencesServices;
import com.robypomper.smartvan.smart_van.android.storage.SVStorage;
import com.robypomper.smartvan.smart_van.android.storage.SVStorageBaseDataStore;
import com.robypomper.smartvan.smart_van.android.storage.SVStorageSingleton;

import java.util.HashMap;
import java.util.Map;


/**
 * The local {@link SVStorage} implementation for the SmartVan application.
 * <p>
 * This implementation use implementations for sub-components that use local
 * storage: {@link LocalPreferences}.
 * <p>
 * This class inherits from {@link SVStorageBaseDataStore} that uses the
 * Android's {@link androidx.datastore.core.DataStore} to store the data.
 */
public class SVLocalStorage extends SVStorageBaseDataStore implements SVStorage {

    // Internal vars

    /**
     * The application's context.
     */
    private final Context ctx;
    /**
     * The application's generic preferences object.
     */
    private final Map<String, SVPreferences> preferencesApp;
    /**
     * SV Services preferences for each known object id.
     */
    private final Map<String, SVPreferencesServices> preferencesServices;


    // Constructor

    /**
     * Initialize the local storage.
     *
     * @param ctx the application's context.
     */
    public SVLocalStorage(Context ctx) {
        super(ctx);
        this.ctx = ctx;
        this.preferencesApp = new HashMap<>();
        this.preferencesServices = new HashMap<>();

        SVStorageSingleton.setInstance(this);
    }


    // Storage mngm implementation

    protected void generatePreferenceApp(String objectId) {
        if (preferencesApp.containsKey(objectId)) return;
        SVPreferences locPreferences = new LocalPreferences(ctx);
        locPreferences.generateStorage(objectId);
        preferencesApp.put(objectId, locPreferences);
    }

    protected void generatePreferenceServices(String objectId) {
        if (preferencesServices.containsKey(objectId)) return;
        SVPreferencesServices locPreferencesServices = new LocalPreferencesServices(ctx);
        locPreferencesServices.generateStorage(objectId);
        preferencesServices.put(objectId, locPreferencesServices);
    }

    // TODO uncomment when the history is implemented
    //protected void generateHistory(String objectId) {
    // if (history.containsKey(objectId)) return;
    // SVHistory locHistory = new LocalHistory();
    // locHistory.generateStorage(objectId);
    // history.put(objectId, locHistory);
    //}

    // TODO uncomment when the automations are implemented
    //protected void generateAutomations(String objectId) {
    // if (automations.containsKey(objectId)) return;
    // SVAutomations locAutomations = new LocalAutomations();
    // locAutomations.generateStorage(objectId);
    // automations.put(objectId, locAutomations);
    //}

    protected void clearPreferenceApp(String objectId) {
        SVPreferences locPreferences = preferencesApp.remove(objectId);
        if (locPreferences != null) locPreferences.clearStorage(objectId);
    }

    protected void clearPreferenceServices(String objectId) {
        SVPreferencesServices locPreferencesServices = preferencesServices.remove(objectId);
        if (locPreferencesServices != null) locPreferencesServices.clearStorage(objectId);
    }

    // TODO uncomment when the history is implemented
    //protected void clearHistory(String objectId) {
    // SVHistory locHistory = history.remove(objectId);
    // if (locHistory != null) locHistory.clearStorage(objectId);
    //}

    // TODO uncomment when the automations are implemented
    //protected void clearAutomations(String objectId) {
    // SVAutomations locAutomations = automations.remove(objectId);
    // if (locAutomations != null) locAutomations.clearStorage(objectId);
    //}


    // Getters for storage sub-components

    /**
     * Get the generic application preferences for the given object id.
     *
     * @param objectId the id of the object for which to get the application preferences.
     * @return the preferences for the given object id.
     */
    @Override
    public SVPreferences getPreferencesApp(String objectId) {
        return preferencesApp.get(objectId);
    }

    /**
     * Get the preferences services for the given object id.
     *
     * @param objectId the id of the object for which to get the preferences services.
     * @return the preferences services for the given object id.
     */
    @Override
    public SVPreferencesServices getPreferencesServices(String objectId) {
        return preferencesServices.get(objectId);
    }

}
