package com.robypomper.smartvan.smart_van.android.storage;

/**
 * This class is used to define the abstract implementation of a sub storage.
 * @noinspection unused
 */
public abstract class SVAbsSubStorage implements SVSubStorage {

    /**
     * The objectId of the storage.
     */
    private String objectId = null;


    /**
     * This method is used to check if the storage is generated.
     *
     * @return true if the storage is generated, false otherwise.
     */
    protected boolean isStorageGenerated() {
        return objectId != null;
    }

    /**
     * This method is used to get the objectId of the storage.
     *
     * @return the objectId of the storage.
     */
    @Override
    public String getObjectId() {
        return objectId;
    }

    /**
     * This method is used to set the objectId of the storage.
     * <p>
     * The object id can be set only from a subclass during the generation of
     * the storage.
     *
     * @param objectId the objectId of the storage.
     */
    protected void setObjectId(String objectId) {
        this.objectId = objectId;
    }

}
