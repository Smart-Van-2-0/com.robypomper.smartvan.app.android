package com.robypomper.smartvan.smart_van.android.storage;

import java.util.List;


/**
 * Main SmartVan storage interface.
 * <p>
 * An instance of SVStorage is provided by the {@link com.robypomper.smartvan.smart_van.android.app.SVApplication}
 * and allows activities and other components to access the SmartVan storage.
 * <p>
 * Except for the current, the favourite object id and the list of known objects
 * ids, all the other methods refer to a specific object identified by its id.
 * That allows to store data for multiple objects at the same time.
 */
public interface SVStorage {

    // Object's id management

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


    // Getters for storage sub-components

    /**
     * Get the generic application preferences.
     * <p>
     * NB: this sub-component is generic and NOT related to a specific object.
     *
     * @return the preferences for the given object id.
     */
    SVPreferences getAppPreferences();

}
