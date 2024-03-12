package com.robypomper.smartvan.smart_van.android.storage;

import com.robypomper.smartvan.smart_van.android.R;

import java.util.Collections;
import java.util.List;
import java.util.Set;


/**
 * Main SmartVan storage interface.
 * <p>
 * An instance of SVStorage is provided by the {@link com.robypomper.smartvan.smart_van.android.app.SVApplication}
 * and allows activities and other components to access the SmartVan storage.
 * <p>
 * Except for the current object id, the favourite one and the list of known objects
 * ids; all the other methods refer to a specific object identified by its id.
 * That allows to store data for multiple objects at the same time.
 * @noinspection unused
 */
public interface SVStorage {

    // Constants

    /**
     * The name of the data store used by this class.
     */
    int DATA_STORE_NAME = R.string.pref_group__storage;
    /**
     * The key for the current object id.
     */
    int CURR_OBJ_ID = R.string.pref__storage__curr_obj_id;
    /**
     * The default value for the current object id.
     */
    String DEF_CURR_OBJ_ID = null;
    /**
     * The key for the favourite object id.
     */
    int FAV_OBJ_ID = R.string.pref__storage__fav_obj_id;
    /**
     * The default value for the favourite object id.
     */
    String DEF_FAV_OBJ_ID = null;
    /**
     * The key for the ask for use favourite object id preference.
     */
    int ASK_USE_FAV_OBJ_ID = R.string.pref__storage__ask_use_fav_obj;
    /**
     * The default value for the ask for use favourite object id preference.
     */
    boolean DEF_ASK_USE_FAV_OBJ_ID = true;
    /**
     * The key for the ask for set favourite object id preference.
     */
    int ASK_SET_FAV_OBJ_ID = R.string.pref__storage__ask_set_fav_obj;
    /**
     * The default value for the ask for set favourite object id preference.
     */
    boolean DEF_ASK_SET_FAV_OBJ_ID = true;
    /**
     * The key for the list of known object ids.
     */
    int OBJ_ID_KNOWN = R.string.pref__storage__known_obj_id;
    /**
     * The default value for the list of known object ids.
     */
    Set<String> DEF_OBJ_ID_KNOWN = Collections.emptySet();


    // Current object's id

    /**
     * Get the id of the current object.
     * <p>
     * Current object is selected from the {@link com.robypomper.smartvan.smart_van.android.activities.SVSelectObjectActivity}
     * then, before that this method returns null.
     *
     * @return the currently selected object id, or null if no object has been selected yet.
     */
    String getCurrentObjectId();

    /**
     * Set the id of the current object.
     * <p>
     * This method is called when the user selects a new object from the
     * {@link com.robypomper.smartvan.smart_van.android.activities.SVSelectObjectActivity}
     * activity.
     *
     * @param objectId the id of the object to set as current.
     */
    void setCurrentObjectId(String objectId);


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
    String getFavouriteObjectId();

    /**
     * Set user favourite object id.
     * <p>
     * This method is called when the user selects a new object from the
     * {@link com.robypomper.smartvan.smart_van.android.activities.SVSelectObjectActivity}
     * activity and then answer 'Yes' to the "Set as favourite" dialog box.
     *
     * @param objectId the id of the object to set as favourite.
     */
    void setFavouriteObjectId(String objectId);

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
    boolean askForUseFavouriteObjectId();

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
    void setAskForUseFavouriteObjectId(boolean askForUseFavouriteObjectId);

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
    boolean askForSetFavouriteObjectId();

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
    void setAskForSetFavouriteObjectId(boolean askForSetFavouriteObjectId);


    // Known object ids

    /**
     * Get the list of known object ids.
     * <p>
     * This list contains all object's ids that the user has used in the past
     * and some data is still stored for them.
     *
     * @return the list of known object ids.
     */
    List<String> getKnownObjectIds();

    /**
     * Add an object id to the list of known object ids.
     * <p>
     * This method is called when the user selects a new object from the
     * {@link com.robypomper.smartvan.smart_van.android.activities.SVSelectObjectActivity}
     * activity.
     * <p>
     * Duplicated object ids are ignored.
     * <p>
     * TODO call this method on object's selection
     *
     * @param objectId the id of the object to add to the list of known object ids.
     */
    void addKnownObjectId(String objectId);

    /**
     * Remove an object id from the list of known object ids.
     * <p>
     * This method is called when the user requires to delete all the data
     * for a specific object.
     * <p>
     * If the object id is not in the list, this method does nothing.
     * <p>
     * TODO implement the settings activity to let user wipe all data related to specific object id
     *
     * @param objectId the id of the object to remove from the list of known object ids.
     */
    void removeKnownObjectId(String objectId);

    /**
     * Generate the storage for the given object id.
     * <p>
     * This method is called when the user selects a new object from the
     * {@link com.robypomper.smartvan.smart_van.android.activities.SVSelectObjectActivity}
     * activity.
     * <p>
     * For each sub-component this method calls the {@link SVSubStorage#generateStorage(String)}.
     *
     * @param objectId the id of the object for which to generate the storage.
     */
    void generateStorage(String objectId);

    /**
     * Clear the storage for the given object id.
     * <p>
     * This method is called when the user requires to delete all the data
     * for a specific object.
     * <p>
     * For each sub-component this method calls the {@link SVSubStorage#clearStorage(String)}.
     *
     * @param objectId the id of the object for which to clear the storage.
     */
    void clearStorage(String objectId);


    // Getters for object related storage sub-components

    /**
     * Get the application preferences for the current object id.
     *
     * @return the application preferences for the current object id.
     * @throws IllegalStateException if no current object is set.
     */
    SVPreferences getCurrentPreferencesApp();

    /**
     * Get the preferences services for the current object id.
     *
     * @return the preferences services for the current object id.
     * @throws IllegalStateException if no current object is set.
     */
    SVPreferencesServices getCurrentPreferencesServices();

    /**
     * Get the generic application preferences for the given object id.
     *
     * @param objectId the id of the object for which to get the application preferences.
     * @return the preferences for the given object id.
     */
    SVPreferences getPreferencesApp(String objectId);

    /**
     * Reset the generic application preferences for the given object id.
     *
     * @param objId the id of the object for which to reset the application preferences.
     */
    void resetPreferencesApp(String objId);

    /**
     * Get the preferences services for the given object id.
     *
     * @param objectId the id of the object for which to get the preferences services.
     * @return the preferences services for the given object id.
     */
    SVPreferencesServices getPreferencesServices(String objectId);

    /**
     * Reset the preferences services for the given object id.
     *
     * @param objId the id of the object for which to reset the preferences services.
     */
    void resetPreferencesServices(String objId);

}
