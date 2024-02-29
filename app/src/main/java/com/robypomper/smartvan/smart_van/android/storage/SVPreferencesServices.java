package com.robypomper.smartvan.smart_van.android.storage;


import com.robypomper.smartvan.smart_van.android.R;

/**
 * Interface for SV Services' preferences.
 * <p>
 * This interface is used to store and retrieve preferences for a specific
 * service based on his component's path. For each SV service the user can
 * set a custom name and icon.
 */
public interface SVPreferencesServices extends SVSubStorage {

    // Constants

    int DATA_STORE_NAME_PREFIX = R.string.pref_group__services_prefix; // + ObjectId
    int NAME_PROP_PREFIX = R.string.pref__services__name_prefix; // + ComponentPath
    String DEF_NAME = null;
    int ICON_PROP_PREFIX = R.string.pref__services__icon_prefix; // + ComponentPath
    String DEF_ICON_NAME = null;


    // SVPreferencesServices

    /**
     * Set the name of the specified SV Service.
     *
     * @param srvPath the path of the SV Service for which to set the name.
     * @param name    the name to set for the SV Service.
     */
    void setName(String srvPath, String name);

    /**
     * Get the name of the specified SV Service.
     *
     * @param srvPath the path of the SV Service for which to get the name.
     * @return the name of the SV Service.
     */
    String getName(String srvPath);

    /**
     * Set the icon of the specified SV Service.
     *
     * @param srvPath  the path of the SV Service for which to set the icon.
     * @param iconName the string corresponding to the icon to set for the SV Service.
     */
    void setIconName(String srvPath, String iconName);

    /**
     * Get the icon drawable of the specified SV Service.
     *
     * @param srvPath the path of the SV Service for which to get the icon drawable.
     * @return the string corresponding to the icon drawable of the SV Service.
     */
    String getIconName(String srvPath);

}
