package com.robypomper.smartvan.smart_van.android.storage;


/**
 * Singleton for SVStorage.
 * <p>
 * The singleton instance hosted by this class must be set before any other
 * class tries to access it.
 * <p>
 * Actually, the initialization of the singleton instance is done by the
 * {@link SVStorage} implementation classes into their constructors. Then the
 * {@link com.robypomper.smartvan.smart_van.android.app.SVApplication} class
 * initialize the right SVStorage implementation at application's startup.
 */
public class SVStorageSingleton {


    // Singleton

    /**
     * The singleton instance.
     */
    private static SVStorage instance;


    // Getters and Setters

    /**
     * @return the singleton instance
     */
    public static SVStorage getInstance() {
        return instance;
    }

    /**
     * Set the singleton instance.
     * <p>
     * This method can be called only once, otherwise an
     * {@link IllegalStateException} is thrown.
     *
     * @param instance the singleton instance
     */
    public static void setInstance(SVStorage instance) {
        if (SVStorageSingleton.instance != null)
            throw new IllegalStateException("Instance for SVStorageSingleton already set");
        SVStorageSingleton.instance = instance;
    }

}
