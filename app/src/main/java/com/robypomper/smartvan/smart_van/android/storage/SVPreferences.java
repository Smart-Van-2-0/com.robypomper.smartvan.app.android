package com.robypomper.smartvan.smart_van.android.storage;


/**
 * This interface is used to define the generic preferences of the SmartVan.
 */
public interface SVPreferences {

    /**
     * During application startup, when the favourite object id is set and
     * corresponding object is available, ask the user if he wants to use the
     * favourite object id.
     * <p>
     * Default value is true.
     * <p>
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
     * @param askForUseFavouriteObjectId true if it must ask the user if he wants
     * to use the favourite object, false otherwise.
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
     * @param askForSetFavouriteObjectId true if it must ask the user to set the
     * favourite object id, false otherwise.
     */
    void setAskForSetFavouriteObjectId(boolean askForSetFavouriteObjectId);

    /**
     * Get the timeout in seconds for the chart.
     * <p>
     * Default value is 15.
     * <p>
     * @return the timeout in seconds for the chart.
     */
    int getChartTimeoutSeconds();

    /**
     * Set the timeout in seconds for the chart.
     * <p>
     * @param chartTimeoutSeconds the timeout in seconds for the chart.
     */
    void setChartTimeoutSeconds(int chartTimeoutSeconds);

}
