package com.robypomper.smartvan.smart_van.android.storage;

import android.content.Context;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.rxjava3.RxDataStore;

import com.robypomper.smartvan.smart_van.android.utils.DataStoreUtils;

import java.util.ArrayList;
import java.util.Collections;
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

    // Constants

    /**
     * The name of the data store used by this class.
     */
    private static final String DATA_STORE_NAME = "smart_van_storage";
    /**
     * The key for the current object id.
     */
    private static final Preferences.Key<String> OBJ_ID_CURRENT = PreferencesKeys.stringKey("OBJ_ID_CURRENT");
    /**
     * The default value for the current object id.
     */
    private static final String DEF_OBJ_ID_CURRENT = null;
    /**
     * The key for the favourite object id.
     */
    private static final Preferences.Key<String> OBJ_ID_FAVOURITE = PreferencesKeys.stringKey("OBJ_ID_FAVOURITE");
    /**
     * The default value for the favourite object id.
     */
    private static final String DEF_OBJ_ID_FAVOURITE = null;
    /**
     * The key for the list of known object ids.
     */
    private static final Preferences.Key<Set<String>> OBJ_ID_KNOWN = PreferencesKeys.stringSetKey("OBJ_ID_KNOWN");
    /**
     * The default value for the list of known object ids.
     */
    private static final Set<String> DEF_OBJ_ID_KNOWN = Collections.emptySet();


    // Internal vars

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
        dataStore = DataStoreUtils.initDataStore(ctx, DATA_STORE_NAME);
    }


    // Object's id management

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
        return DataStoreUtils.getFromDataStore(dataStore, OBJ_ID_CURRENT, DEF_OBJ_ID_CURRENT);
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
        DataStoreUtils.setToDataStore(dataStore, OBJ_ID_CURRENT, objectId);

        if (objectId != null && !getKnownObjectIds().contains(objectId))
            addKnownObjectId(objectId);
    }

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
        return DataStoreUtils.getFromDataStore(dataStore, OBJ_ID_FAVOURITE, DEF_OBJ_ID_FAVOURITE);
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
        DataStoreUtils.setToDataStore(dataStore, OBJ_ID_FAVOURITE, objectId);
    }

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
        Set<String> knownIds = DataStoreUtils.getFromDataStore(dataStore, OBJ_ID_KNOWN, DEF_OBJ_ID_KNOWN);
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
        Set<String> knownIds = DataStoreUtils.getFromDataStore(dataStore, OBJ_ID_KNOWN, DEF_OBJ_ID_KNOWN);
        List<String> knownIdsList = new ArrayList<>(knownIds);
        if (!knownIdsList.add(objectId))
            return;
        knownIds = new HashSet<>(knownIdsList);
        DataStoreUtils.setToDataStore(dataStore, OBJ_ID_KNOWN, knownIds);
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
        Set<String> knownIds = DataStoreUtils.getFromDataStore(dataStore, OBJ_ID_KNOWN, DEF_OBJ_ID_KNOWN);
        List<String> knownIdsList = new ArrayList<>(knownIds);
        if (!knownIdsList.remove(objectId))
            return;
        knownIds = new HashSet<>(knownIdsList);
        DataStoreUtils.setToDataStore(dataStore, OBJ_ID_KNOWN, knownIds);
        clearStorage(objectId);
    }

}
