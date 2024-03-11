package com.robypomper.smartvan.smart_van.android.activities;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;

import com.robypomper.smartvan.smart_van.android.R;
import com.robypomper.smartvan.smart_van.android.components.SVSettingsMainFragment;
import com.robypomper.smartvan.smart_van.android.components.SVSettingsObjectFragment;
import com.robypomper.smartvan.smart_van.android.storage.SVStorageBaseDataStore;
import com.robypomper.smartvan.smart_van.android.storage.SVStorageSingleton;
import com.robypomper.smartvan.smart_van.android.storage.local.LocalPreferences;
import com.robypomper.smartvan.smart_van.android.utils.DataStoreUtils;

import java.util.List;
import java.util.Set;

public class SVSettingsActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    // Constants

    private static final String LOG_TAG = "JSLA.Actvt.Settings";


    // Internal vars


    // Constructor


    // Android

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_svsettings);

        // setup fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frmConfigs, new SVSettingsMainFragment())
                .commit();
        /*getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frmObjectConfigs, new SVSettingsObjectFragment())
                .commit();*/

        // setup title update on back button click
        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        if (getSupportFragmentManager().getBackStackEntryCount() == 0)
                            setTitle(R.string.activity_svsettings__title);
                    }
                });

        // set up action bar
        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
        else if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        else
            Log.w(LOG_TAG, "No ActionBar available for this activity");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getParentActivityIntent() == null) {
                Log.w(LOG_TAG, "You have forgotten to specify the parentActivityName in the AndroidManifest!");
                //onBackPressed();
                getOnBackPressedDispatcher().onBackPressed();
            } else
                NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceStartFragment(@NonNull PreferenceFragmentCompat caller, Preference pref) {
        // Instantiate the new Fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(getClassLoader(), pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);

        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frmConfigs, fragment)
                .addToBackStack(null)
                .commit();
        setTitle(pref.getTitle());
        return true;
    }










    public static class GenericFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            // Bind the backend data store from SVStorageBaseDataStore to the preference manager
            assert SVStorageSingleton.getInstance() instanceof SVStorageBaseDataStore;
            RxDataStore<Preferences> dataStore = ((SVStorageBaseDataStore) SVStorageSingleton.getInstance()).getDataStore();
            getPreferenceManager().setPreferenceDataStore(new RxDataStorePreference(dataStore));
            // Get current SV Box
            String currObjId = SVStorageSingleton.getInstance().getCurrentObjectId();
            String currObjName = "A"; // TODO assign this
            String favObjId = SVStorageSingleton.getInstance().getFavouriteObjectId();
            String favObjName = "B"; // TODO assign this
            List<String> knowObjIds = SVStorageSingleton.getInstance().getKnownObjectIds();

            // Inflate the preferences from the XML resource
            setPreferencesFromResource(R.xml.preferences_generic, rootKey);


            // Customize the preferences views
            Preference prefShowCurrentObjConfigs = findPreference(getResources().getString(R.string.activity_svsettings_txt_show_current_obj_configs_key));
            assert prefShowCurrentObjConfigs != null;
            prefShowCurrentObjConfigs.setSummary(
                    getResources().getString(R.string.activity_svsettings_txt_show_current_obj_configs_summary,
                            currObjName, currObjId));

            Preference prefResetFavouriteSVBox = findPreference(getResources().getString(R.string.activity_svsettings_txt_reset_favourite_obj_id_key));
            assert prefResetFavouriteSVBox != null;
            prefResetFavouriteSVBox.setSummary(
                    getResources().getString(R.string.activity_svsettings_txt_reset_favourite_obj_id_summary,
                            favObjName, favObjId));
            prefResetFavouriteSVBox.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(@NonNull Preference preference) {
                        // Show warning dialog, if accepted reset the favourite object id
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(R.string.activity_svsettings_txt_reset_favourite_obj_id_title)
                                .setMessage(R.string.activity_svsettings_txt_reset_favourite_obj_id_message)
                                .setPositiveButton(R.string.activity_svsettings_txt_reset_favourite_obj_id_yes,
                                        (dialog, which) -> {
                                            SVStorageSingleton.getInstance().setFavouriteObjectId(null);
                                            prefResetFavouriteSVBox.setSummary(
                                                    getResources().getString(R.string.activity_svsettings_txt_reset_favourite_obj_id_summary,
                                                            "N", "A"));
                                        })
                                .setNegativeButton(R.string.activity_svsettings_txt_reset_favourite_obj_id_no,
                                        (dialog, which) -> {
                                            // Do nothing
                                        })
                                .show();
                        return true;
                    }
                });

            Preference prefShowKnownObjectIds = findPreference(getResources().getString(R.string.activity_svsettings_txt_show_known_obj_id_key));
            assert prefShowKnownObjectIds != null;
            prefShowKnownObjectIds.setSummary(
                    getResources().getString(R.string.activity_svsettings_txt_show_known_obj_id_summary,
                            knowObjIds.size()));
            View prefShowKnownObjectIdsDialogView = null;   // TODO assign this
            prefShowKnownObjectIds.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(@NonNull Preference preference) {
                        // Show warning dialog, if accepted reset the favourite object id
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(R.string.activity_svsettings_txt_show_known_obj_id_title)
                                .setView(prefShowKnownObjectIdsDialogView)
                                .setPositiveButton(R.string.activity_svsettings_txt_show_known_obj_id_yes,
                                        (dialog, which) -> {
                                            List<String> updatedKnowObjIds = SVStorageSingleton.getInstance().getKnownObjectIds();
                                            prefShowKnownObjectIds.setSummary(
                                                    getResources().getString(R.string.activity_svsettings_txt_show_known_obj_id_summary,
                                                            updatedKnowObjIds.size()));
                                        })
                                .show();
                        return true;
                    }
                });

            Preference prefResetKnownObjectIds = findPreference(getResources().getString(R.string.activity_svsettings_txt_clean_known_obj_id_storage_key));
            assert prefResetKnownObjectIds != null;
            prefResetKnownObjectIds.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(@NonNull Preference preference) {
                        // Show warning dialog, if accepted reset the favourite object id
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(R.string.activity_svsettings_txt_clean_known_obj_id_storage_title)
                                .setMessage(R.string.activity_svsettings_txt_clean_known_obj_id_storage_message)
                                .setPositiveButton(R.string.activity_svsettings_txt_clean_known_obj_id_storage_yes,
                                        (dialog, which) -> {
                                            // remove known objects' ids and clean their storage
                                            List<String> updatedKnowObjIds = SVStorageSingleton.getInstance().getKnownObjectIds();
                                            for (String objId : updatedKnowObjIds)
                                                SVStorageSingleton.getInstance().removeKnownObjectId(objId);

                                            // recreate current object id
                                            SVStorageSingleton.getInstance().addKnownObjectId(currObjId);

                                            // update preferences' summaries
                                            prefShowKnownObjectIds.setSummary(
                                                    getResources().getString(R.string.activity_svsettings_txt_show_known_obj_id_summary,
                                                            SVStorageSingleton.getInstance().getKnownObjectIds().size()));
                                        })
                                .setNegativeButton(R.string.activity_svsettings_txt_clean_known_obj_id_storage_no,
                                        (dialog, which) -> {
                                            // Do nothing
                                        })
                                .show();
                        return true;
                    }
                });
        }

    }

    public static class ObjectFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            // Bind the backend data store from LocalPreferences to the preference manager
            assert SVStorageSingleton.getInstance().getCurrentPreferencesApp() instanceof LocalPreferences;
            RxDataStore<Preferences> dataStore = ((LocalPreferences) SVStorageSingleton.getInstance().getCurrentPreferencesApp()).getDataStore();
            getPreferenceManager().setPreferenceDataStore(new RxDataStorePreference(dataStore));

            // Inflate the preferences from the XML resource
            setPreferencesFromResource(R.xml.preferences_object, rootKey);

            // Customize the preferences views
            EditTextPreference prefChartsTimeout = findPreference(getString(LocalPreferences.CHARTS_TIMEOUT));
            assert prefChartsTimeout != null;
            prefChartsTimeout.setDefaultValue(LocalPreferences.DEF_CHARTS_TIMEOUT);
            prefChartsTimeout.setOnBindEditTextListener(
                new EditTextPreference.OnBindEditTextListener() {
                    @Override
                    public void onBindEditText(@NonNull EditText editText) {
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }
                });

            Preference prefResetObjectServices = findPreference(getResources().getString(R.string.activity_svsettings_txt_reset_obj_id_services_key));
            assert prefResetObjectServices != null;
            prefResetObjectServices.setOnPreferenceClickListener(
                    new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(@NonNull Preference preference) {
                            // Show warning dialog, if accepted reset the favourite object id
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle(R.string.activity_svsettings_txt_reset_obj_id_services_title)
                                    .setMessage(R.string.activity_svsettings_txt_reset_obj_id_services_message)
                                    .setPositiveButton(R.string.activity_svsettings_txt_reset_obj_id_services_yes,
                                            (dialog, which) -> {
                                                // reset all services preference from storage
                                                List<String> updatedKnowObjIds = SVStorageSingleton.getInstance().getKnownObjectIds();
                                                for (String objId : updatedKnowObjIds)
                                                    SVStorageSingleton.getInstance().resetPreferencesServices(objId);
                                            })
                                    .setNegativeButton(R.string.activity_svsettings_txt_reset_obj_id_services_no,
                                            (dialog, which) -> {
                                                // Do nothing
                                            })
                                    .show();
                            return true;
                        }
                    });
        }

    }



    public static class RxDataStorePreference extends PreferenceDataStore {

        // Internal vars

        private final RxDataStore<Preferences> dataStore;


        // Constructor

        public RxDataStorePreference(RxDataStore<Preferences> dataStore) {
            this.dataStore = dataStore;
        }


        // Setters

        /**
         * Sets a {@link String} value to the data store.
         *
         * <p>Once the value is set the data store is responsible for holding it.
         *
         * @param key   The name of the preference to modify
         * @param value The new value for the preference
         * @see #getString(String, String)
         */
        @Override
        public void putString(String key, String value) {
            DataStoreUtils.setToDataStore(dataStore, PreferencesKeys.stringKey(key), value);
        }

        /**
         * Sets a set of {@link String}s to the data store.
         *
         * <p>Once the value is set the data store is responsible for holding it.
         *
         * @param key    The name of the preference to modify
         * @param values The set of new values for the preference
         * @see #getStringSet(String, Set)
         */
        public void putStringSet(String key, @Nullable Set<String> values) {
            DataStoreUtils.setToDataStore(dataStore, PreferencesKeys.stringSetKey(key), values);
        }

        /**
         * Sets an {@link Integer} value to the data store.
         *
         * <p>Once the value is set the data store is responsible for holding it.
         *
         * @param key   The name of the preference to modify
         * @param value The new value for the preference
         * @see #getInt(String, int)
         */
        @Override
        public void putInt(String key, int value) {
            DataStoreUtils.setToDataStore(dataStore, PreferencesKeys.intKey(key), value);
        }

        /**
         * Sets a {@link Long} value to the data store.
         *
         * <p>Once the value is set the data store is responsible for holding it.
         *
         * @param key   The name of the preference to modify
         * @param value The new value for the preference
         * @see #getLong(String, long)
         */
        public void putLong(String key, long value) {
            DataStoreUtils.setToDataStore(dataStore, PreferencesKeys.longKey(key), value);
        }

        /**
         * Sets a {@link Float} value to the data store.
         *
         * <p>Once the value is set the data store is responsible for holding it.
         *
         * @param key   The name of the preference to modify
         * @param value The new value for the preference
         * @see #getFloat(String, float)
         */
        public void putFloat(String key, float value) {
            DataStoreUtils.setToDataStore(dataStore, PreferencesKeys.floatKey(key), value);
        }

        /**
         * Sets a {@link Boolean} value to the data store.
         *
         * <p>Once the value is set the data store is responsible for holding it.
         *
         * @param key   The name of the preference to modify
         * @param value The new value for the preference
         * @see #getBoolean(String, boolean)
         */
        public void putBoolean(String key, boolean value) {
            DataStoreUtils.setToDataStore(dataStore, PreferencesKeys.booleanKey(key), value);
        }


        // Getters

        /**
         * Retrieves a {@link String} value from the data store.
         *
         * @param key      The name of the preference to retrieve
         * @param defValue Value to return if this preference does not exist in the storage
         * @return The value from the data store or the default return value
         * @see #putString(String, String)
         */
        @Override
        public String getString(String key, String defValue) {
            return DataStoreUtils.getFromDataStore(dataStore, PreferencesKeys.stringKey(key), defValue);
        }

        /**
         * Retrieves a set of Strings from the data store.
         *
         * @param key       The name of the preference to retrieve
         * @param defValues Values to return if this preference does not exist in the storage
         * @return The values from the data store or the default return values
         * @see #putStringSet(String, Set)
         */
        @Nullable
        public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
            return DataStoreUtils.getFromDataStore(dataStore, PreferencesKeys.stringSetKey(key), defValues);
        }

        /**
         * Retrieves an {@link Integer} value from the data store.
         *
         * @param key      The name of the preference to retrieve
         * @param defValue Value to return if this preference does not exist in the storage
         * @return The value from the data store or the default return value
         * @see #putInt(String, int)
         */
        @Override
        public int getInt(String key, int defValue) {
            return DataStoreUtils.getFromDataStore(dataStore, PreferencesKeys.intKey(key), defValue);
        }

        /**
         * Retrieves a {@link Long} value from the data store.
         *
         * @param key      The name of the preference to retrieve
         * @param defValue Value to return if this preference does not exist in the storage
         * @return The value from the data store or the default return value
         * @see #putLong(String, long)
         */
        public long getLong(String key, long defValue) {
            return DataStoreUtils.getFromDataStore(dataStore, PreferencesKeys.longKey(key), defValue);
        }

        /**
         * Retrieves a {@link Float} value from the data store.
         *
         * @param key      The name of the preference to retrieve
         * @param defValue Value to return if this preference does not exist in the storage
         * @return The value from the data store or the default return value
         * @see #putFloat(String, float)
         */
        public float getFloat(String key, float defValue) {
            return DataStoreUtils.getFromDataStore(dataStore, PreferencesKeys.floatKey(key), defValue);
        }

        /**
         * Retrieves a {@link Boolean} value from the data store.
         *
         * @param key      The name of the preference to retrieve
         * @param defValue Value to return if this preference does not exist in the storage
         * @return the value from the data store or the default return value
         * @see #getBoolean(String, boolean)
         */
        public boolean getBoolean(String key, boolean defValue) {
            return DataStoreUtils.getFromDataStore(dataStore, PreferencesKeys.booleanKey(key), defValue);
        }

    }

}