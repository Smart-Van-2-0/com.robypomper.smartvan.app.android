package com.robypomper.smartvan.smart_van.android.storage;

import android.content.Context;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.rxjava3.RxDataStore;

import com.robypomper.smartvan.smart_van.android.storage.local.LocalPreferences;
import com.robypomper.smartvan.smart_van.android.storage.local.LocalPreferencesServices;
import com.robypomper.smartvan.smart_van.android.utils.DataStoreUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Base class for SmartVan storage using AndroidX DataStore.
 * <p>
 * This class provides the basic methods to store and retrieve data from the
 * AndroidX DataStore.
 * <p>
 * As an abstract class it provides only a subset of the methods required by
 * the {@link SVStorage} interface. This class provides the support for:
 * <ul>
 *     <li>the current object id</li>
 *     <li>the favourite object id</li>
 *     <li>and the list of known object ids.</li>
 * </ul>
 */
public abstract class SVStorageBaseDataStore implements SVStorage {

    // Internal vars

    /**
     * The application's context.
     */
    private final Context ctx;
    /**
     * The AndroidX DataStore used by this class.
     */
    private final RxDataStore<Preferences> dataStore;


    // Constructor

    /**
     * Initialize the storage using the given context.
     *
     * @param ctx the application's context.
     */
    public SVStorageBaseDataStore(Context ctx) {
        this.ctx = ctx;
        dataStore = DataStoreUtils.initDataStore(ctx, ctx.getString(DATA_STORE_NAME));
    }


    // Getters

    /**
     * Get the AndroidX DataStore used by this class.
     *
     * @return the AndroidX DataStore used by this class.
     */
    public RxDataStore<Preferences> getDataStore() {
        return dataStore;
    }


    // Current object's id

    /**
     * Get the id of the current object.
     * <p>
     * Current object is selected from the {@link com.robypomper.smartvan.smart_van.android.activities.SVSelectObjectActivity}
     * then, before that this method returns null.
     *
     * @return the currently selected object id, or null if no object has been selected yet.
     */
    @Override
    public String getCurrentObjectId() {
        return DataStoreUtils.getFromDataStore(dataStore, DataStoreUtils.stringKey(ctx, CURR_OBJ_ID), DEF_CURR_OBJ_ID);
    }

    /**
     * Set the id of the current object.
     * <p>
     * This method is called when the user selects a new object from the
     * {@link com.robypomper.smartvan.smart_van.android.activities.SVSelectObjectActivity}
     * activity.
     *
     * @param objectId the id of the object to set as current.
     */
    @Override
    public void setCurrentObjectId(String objectId) {
        DataStoreUtils.setToDataStore(dataStore, DataStoreUtils.stringKey(ctx, CURR_OBJ_ID), objectId);

        if (objectId != null && !getKnownObjectIds().contains(objectId))
            addKnownObjectId(objectId);
    }


    // Favourite object's id

    /**
     * Get user favourite object id.
     * <p>
     * When the corresponding object is available, the favourite object id is
     * used to select the current object. Otherwise, the current object is
     * selected from the {@link com.robypomper.smartvan.smart_van.android.activities.SVSelectObjectActivity}
     * activity.
     * <p>
     * If the user didn't select a favourite object yet, this method returns null.
     * And the app must show the {@link com.robypomper.smartvan.smart_van.android.activities.SVSelectObjectActivity}
     * activity to let the user select a current object.
     *
     * @return the user favourite object id, or null if the user didn't select a favourite object yet.
     */
    @Override
    public String getFavouriteObjectId() {
        return DataStoreUtils.getFromDataStore(dataStore, DataStoreUtils.stringKey(ctx, FAV_OBJ_ID), DEF_FAV_OBJ_ID);
    }

    /**
     * Set user favourite object id.
     * <p>
     * This method is called when the user selects a new object from the
     * {@link com.robypomper.smartvan.smart_van.android.activities.SVSelectObjectActivity}
     * activity and then answer 'Yes' to the "Set as favourite" dialog box.
     *
     * @param objectId the id of the object to set as favourite.
     */
    @Override
    public void setFavouriteObjectId(String objectId) {
        DataStoreUtils.setToDataStore(dataStore, DataStoreUtils.stringKey(ctx, FAV_OBJ_ID), objectId);
    }

    /**
     * During application startup, when the favourite object id is set and
     * corresponding object is available, ask the user if he wants to use the
     * favourite object id.
     * <p>
     * Default value is true.
     * <p>
     *
     * @return true if it must ask the user if he wants to use the favourite
     * object, false otherwise.
     */
    @Override
    public boolean askForUseFavouriteObjectId() {
        return DataStoreUtils.getFromDataStore(dataStore, DataStoreUtils.booleanKey(ctx, ASK_USE_FAV_OBJ_ID), DEF_ASK_USE_FAV_OBJ_ID);
    }

    /**
     * Set the ask for use favourite object id.
     * <p>
     * During application startup, when the favourite object id is set and
     * corresponding object is available, ask the user if he wants to use the
     * favourite object id.
     * <p>
     *
     * @param askForUseFavouriteObjectId true if it must ask the user if he wants
     *                                   to use the favourite object, false otherwise.
     */
    @Override
    public void setAskForUseFavouriteObjectId(boolean askForUseFavouriteObjectId) {
        DataStoreUtils.setToDataStore(dataStore, DataStoreUtils.booleanKey(ctx, ASK_USE_FAV_OBJ_ID), askForUseFavouriteObjectId);
    }

    /**
     * During application startup, when the user selected an object from the
     * {@link com.robypomper.smartvan.smart_van.android.activities.SVSelectObjectActivity}
     * activity and the favourite object id is not set, ask the user if he
     * wants to set the selected object as favourite.
     * <p>
     * Default value is true.
     * <p>
     *
     * @return true if it must ask the user to set the favourite object id, false
     * otherwise.
     */
    @Override
    public boolean askForSetFavouriteObjectId() {
        return DataStoreUtils.getFromDataStore(dataStore, DataStoreUtils.booleanKey(ctx, ASK_SET_FAV_OBJ_ID), DEF_ASK_SET_FAV_OBJ_ID);
    }

    /**
     * Set the ask for set favourite object id.
     * <p>
     * During application startup, when the user selected an object from the
     * {@link com.robypomper.smartvan.smart_van.android.activities.SVSelectObjectActivity}
     * activity and the favourite object id is not set, ask the user if he
     * wants to set the selected object as favourite.
     * <p>
     *
     * @param askForSetFavouriteObjectId true if it must ask the user to set the
     *                                   favourite object id, false otherwise.
     */
    @Override
    public void setAskForSetFavouriteObjectId(boolean askForSetFavouriteObjectId) {
        DataStoreUtils.setToDataStore(dataStore, DataStoreUtils.booleanKey(ctx, ASK_SET_FAV_OBJ_ID), askForSetFavouriteObjectId);
    }


    // Known object ids

    /**
     * Get the list of known object ids.
     * <p>
     * This list contains all object's ids that the user has used in the past
     * and some data is still stored for them.
     *
     * @return the list of known object ids.
     */
    @Override
    public List<String> getKnownObjectIds() {
        Set<String> knownIds = DataStoreUtils.getFromDataStore(dataStore, DataStoreUtils.stringSetKey(ctx, OBJ_ID_KNOWN), DEF_OBJ_ID_KNOWN);
        return new ArrayList<>(knownIds);
    }

    /**
     * Add a known object id to the list.
     * <p>
     * This method is called when the user selects a new object from the
     * {@link com.robypomper.smartvan.smart_van.android.activities.SVSelectObjectActivity}
     * activity.
     *
     * @param objectId the id of the object to add to the list of known object ids.
     */
    @Override
    public void addKnownObjectId(String objectId) {
        Set<String> knownIds = DataStoreUtils.getFromDataStore(dataStore, DataStoreUtils.stringSetKey(ctx, OBJ_ID_KNOWN), DEF_OBJ_ID_KNOWN);
        List<String> knownIdsList = new ArrayList<>(knownIds);
        if (!knownIdsList.add(objectId))
            return;
        knownIds = new HashSet<>(knownIdsList);
        DataStoreUtils.setToDataStore(dataStore, DataStoreUtils.stringSetKey(ctx, OBJ_ID_KNOWN), knownIds);
        generateStorage(objectId);
    }

    /**
     * Remove a known object id from the list.
     * <p>
     * This method is called when the user requires to delete all the data
     * for a specific object.
     *
     * @param objectId the id of the object to remove from the list of known object ids.
     */
    @Override
    public void removeKnownObjectId(String objectId) {
        Set<String> knownIds = DataStoreUtils.getFromDataStore(dataStore, DataStoreUtils.stringSetKey(ctx, OBJ_ID_KNOWN), DEF_OBJ_ID_KNOWN);
        List<String> knownIdsList = new ArrayList<>(knownIds);
        if (!knownIdsList.remove(objectId))
            return;
        knownIds = new HashSet<>(knownIdsList);
        DataStoreUtils.setToDataStore(dataStore, DataStoreUtils.stringSetKey(ctx, OBJ_ID_KNOWN), knownIds);
        clearStorage(objectId);
    }


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
        generatePreferenceApp(objectId);
        generatePreferenceServices(objectId);
        // TODO uncomment when the history is implemented
        // generateHistory(objectId);
        // TODO uncomment when the automations are implemented
        // generateAutomations(objectId);
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
        clearPreferenceApp(objectId);
        clearPreferenceServices(objectId);
        // TODO uncomment when the history is implemented
        // clearHistory(objectId);
        // TODO uncomment when the automations are implemented
        // clearAutomations(objectId);
    }


    // Storage mngm implementation

    protected abstract void generatePreferenceApp(String objectId);

    protected abstract void generatePreferenceServices(String objectId);

    //protected abstract void generateHistory(String objectId);

    //protected abstract void generateAutomations(String objectId);

    protected abstract void clearPreferenceApp(String objectId);

    protected abstract void clearPreferenceServices(String objectId);

    //protected abstract void clearHistory(String objectId);

    //protected abstract void clearAutomations(String objectId);


    // Getters for object related storage sub-components

    /**
     * Get the application preferences for the current object id.
     *
     * @return the application preferences for the current object id.
     * @throws IllegalStateException if no current object is set.
     */
    @Override
    public SVPreferences getCurrentPreferencesApp() {
        if (getCurrentObjectId() == null)
            throw new IllegalStateException("No current object is set");
        return getPreferencesApp(getCurrentObjectId());
    }


    /**
     * Get the preferences services for the current object id.
     *
     * @return the preferences services for the current object id.
     * @throws IllegalStateException if no current object is set.
     */
    @Override
    public SVPreferencesServices getCurrentPreferencesServices() {
        if (getCurrentObjectId() == null)
            throw new IllegalStateException("No current object is set");
        return getPreferencesServices(getCurrentObjectId());
    }

    /**
     * Reset the generic application preferences for the given object id.
     *
     * @param objId the id of the object for which to reset the application preferences.
     */
    @Override
    public void resetPreferencesApp(String objId) {
        clearPreferenceApp(objId);
        generatePreferenceApp(objId);
    }

    /**
     * Reset the preferences services for the given object id.
     *
     * @param objId the id of the object for which to reset the preferences services.
     */
    @Override
    public void resetPreferencesServices(String objId) {
        clearPreferenceServices(objId);
        generatePreferenceServices(objId);
    }

}
