package com.robypomper.smartvan.smart_van.android.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;

import com.robypomper.smartvan.smart_van.android.R;
import com.robypomper.smartvan.smart_van.android.components.SVSettingsMainFragment;
import com.robypomper.smartvan.smart_van.android.utils.DataStoreUtils;

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
        assert pref.getFragment() != null;
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(getClassLoader(), pref.getFragment());
        fragment.setArguments(args);
        getSupportFragmentManager().setFragmentResultListener("reqKey", caller.getViewLifecycleOwner(), (requestKey, result) -> {});

        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frmConfigs, fragment)
                .addToBackStack(null)
                .commit();
        setTitle(pref.getTitle());
        return true;
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