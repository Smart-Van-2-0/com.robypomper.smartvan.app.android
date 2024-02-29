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


    // Object's id management


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
        SVPreferences locPreferences = new LocalPreferences(ctx);
        locPreferences.generateStorage(objectId);
        preferencesApp.put(objectId, locPreferences);

        SVPreferencesServices locPreferencesServices = new LocalPreferencesServices(ctx);
        locPreferencesServices.generateStorage(objectId);
        preferencesServices.put(objectId, locPreferencesServices);

        // TODO uncomment when the history is implemented
        // SVHistory locHistory = new LocalHistory();
        // locHistory.generateStorage(objectId);
        // history.put(objectId, locHistory);

        // TODO uncomment when the automations are implemented
        // SVAutomations locAutomations = new LocalAutomations();
        // locAutomations.generateStorage(objectId);
        // automations.put(objectId, locAutomations);
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
        SVPreferences locPreferences = preferencesApp.remove(objectId);
        if (locPreferences != null) locPreferences.clearStorage(objectId);

        SVPreferencesServices locPreferencesServices = preferencesServices.remove(objectId);
        if (locPreferencesServices != null) locPreferencesServices.clearStorage(objectId);

        // TODO uncomment when the history is implemented
        // SVHistory locHistory = history.remove(objectId);
        // if (locHistory != null) locHistory.clearStorage(objectId);

        // TODO uncomment when the automations are implemented
        // SVAutomations locAutomations = automations.remove(objectId);
        // if (locAutomations != null) locAutomations.clearStorage(objectId);
    }


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
