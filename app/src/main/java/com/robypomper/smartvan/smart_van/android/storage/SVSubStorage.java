package com.robypomper.smartvan.smart_van.android.storage;


/**
 * Base interface for SV storage sub-components.
 * <p>
 * All the sub-components are object's specific and are used to store the data
 * related to a specific object. Then this interface is used to define the
 * common methods for all the sub-components to handle the object id and
 * his data.
 */
public interface SVSubStorage {

    /**
     * @return the id of the object for which the storage represents.
     */
    String getObjectId();

    /**
     * Generate the storage for the given object id.
     * <p>
     * This method is called when the user selects a new object from the
     * {@link com.robypomper.smartvan.smart_van.android.activities.SVSelectObjectActivity}
     * activity.
     *
     * @param objectId the id of the object for which to generate the storage.
     */
    void generateStorage(String objectId);

    /**
     * Clear the storage for the given object id.
     * <p>
     * This method is called when the user requires to delete all the data
     * for a specific object.
     *
     * @param objectId the id of the object for which to clear the storage.
     */
    void clearStorage(String objectId);

}
