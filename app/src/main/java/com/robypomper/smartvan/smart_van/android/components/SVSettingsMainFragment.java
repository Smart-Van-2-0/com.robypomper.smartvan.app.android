package com.robypomper.smartvan.smart_van.android.components;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.robypomper.smartvan.smart_van.android.R;
import com.robypomper.smartvan.smart_van.android.activities.SVSettingsActivity;
import com.robypomper.smartvan.smart_van.android.storage.SVStorageBaseDataStore;
import com.robypomper.smartvan.smart_van.android.storage.SVStorageSingleton;

import java.util.List;

public class SVSettingsMainFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        loadData();

        // Inflate the preferences from the XML resource
        setPreferencesFromResource(R.xml.preferences_generic, rootKey);

        setupViews();
    }

    private void loadData() {
        // Bind the backend data store from SVStorageBaseDataStore to the preference manager
        assert SVStorageSingleton.getInstance() instanceof SVStorageBaseDataStore;
        RxDataStore<Preferences> dataStore = ((SVStorageBaseDataStore) SVStorageSingleton.getInstance()).getDataStore();
        getPreferenceManager().setPreferenceDataStore(new SVSettingsActivity.RxDataStorePreference(dataStore));
    }

    private void setupViews() {
        Preference prefShowCurrentObjConfigs = findPreference(getString(R.string.activity_svsettings_txt_show_current_obj_configs_key));
        assert prefShowCurrentObjConfigs != null;
        String currObjId = SVStorageSingleton.getInstance().getCurrentObjectId();
        String currObjName = "A"; // TODO assign this
        prefShowCurrentObjConfigs.setSummary(
                getString(R.string.activity_svsettings_txt_show_current_obj_configs_summary,
                        currObjName, currObjId));

        Preference prefResetFavouriteSVBox = findPreference(getString(R.string.activity_svsettings_txt_reset_favourite_obj_id_key));
        assert prefResetFavouriteSVBox != null;
        String favObjId = SVStorageSingleton.getInstance().getFavouriteObjectId();
        String favObjName = "B"; // TODO assign this
        if (favObjId != null) {
            favObjId = "N";
            favObjName = "A";
        }
        prefResetFavouriteSVBox.setSummary(
                getString(R.string.activity_svsettings_txt_reset_favourite_obj_id_summary,
                        favObjName, favObjId));
        prefResetFavouriteSVBox.setOnPreferenceClickListener(prefResetFavouriteSVBoxListener);

        Preference prefShowKnownObjectIds = findPreference(getString(R.string.activity_svsettings_txt_show_known_obj_id_key));
        assert prefShowKnownObjectIds != null;
        List<String> knowObjIds = SVStorageSingleton.getInstance().getKnownObjectIds();
        prefShowKnownObjectIds.setSummary(
                getString(R.string.activity_svsettings_txt_show_known_obj_id_summary,
                        knowObjIds.size()));
        prefShowKnownObjectIds.setOnPreferenceClickListener(prefShowKnownObjectIdsListener);
        
        Preference prefCleanKnownObjectIds = findPreference(getString(R.string.activity_svsettings_txt_clean_known_obj_id_storage_key));
        assert prefCleanKnownObjectIds != null;
        prefCleanKnownObjectIds.setOnPreferenceClickListener(prefCleanKnownObjectIdsListener);
    }

    private final Preference.OnPreferenceClickListener prefResetFavouriteSVBoxListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(@NonNull Preference preference) {
            // Show warning dialog, if accepted reset the favourite object id
            AlertDialog.Builder builder = new AlertDialog.Builder(preference.getContext());
            builder.setTitle(R.string.activity_svsettings_txt_reset_favourite_obj_id_title)
                    .setMessage(R.string.activity_svsettings_txt_reset_favourite_obj_id_message)
                    .setPositiveButton(R.string.activity_svsettings_txt_reset_favourite_obj_id_yes,
                            (dialog, which) -> {
                                SVStorageSingleton.getInstance().setFavouriteObjectId(null);
                                preference.setSummary(getString(R.string.activity_svsettings_txt_reset_favourite_obj_id_summary, "N", "A"));
                            })
                    .setNegativeButton(R.string.activity_svsettings_txt_reset_favourite_obj_id_no,
                            (dialog, which) -> {
                                // Do nothing
                            })
                    .show();
            return true;
        }
    };

    private final Preference.OnPreferenceClickListener prefShowKnownObjectIdsListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(@NonNull Preference preference) {
            View prefShowKnownObjectIdsDialogView = null;   // TODO assign this 'prefShowKnownObjectIdsDialogView'

            // Show warning dialog, if accepted reset the favourite object id
            AlertDialog.Builder builder = new AlertDialog.Builder(preference.getContext());
            builder.setTitle(R.string.activity_svsettings_txt_show_known_obj_id_title)
                    .setView(prefShowKnownObjectIdsDialogView)
                    .setPositiveButton(R.string.activity_svsettings_txt_show_known_obj_id_yes,
                            (dialog, which) -> {
                                List<String> updatedKnowObjIds = SVStorageSingleton.getInstance().getKnownObjectIds();
                                preference.setSummary(getString(R.string.activity_svsettings_txt_show_known_obj_id_summary, updatedKnowObjIds.size()));
                            })
                    .show();
            return true;
        }
    };

    private final Preference.OnPreferenceClickListener prefCleanKnownObjectIdsListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(@NonNull Preference preference) {
            // Show warning dialog, if accepted reset the favourite object id
            AlertDialog.Builder builder = new AlertDialog.Builder(preference.getContext());
            builder.setTitle(R.string.activity_svsettings_txt_clean_known_obj_id_storage_title)
                    .setMessage(R.string.activity_svsettings_txt_clean_known_obj_id_storage_message)
                    .setPositiveButton(R.string.activity_svsettings_txt_clean_known_obj_id_storage_yes,
                            (dialog, which) -> {
                                // remove known objects' ids and clean their storage
                                List<String> updatedKnowObjIds = SVStorageSingleton.getInstance().getKnownObjectIds();
                                for (String objId : updatedKnowObjIds)
                                    SVStorageSingleton.getInstance().removeKnownObjectId(objId);

                                // recreate current object id       // TODO update according to SVStorageBaseDataStore fix (see also SVSelectObjectActivity::doGoToNextActivity())
                                String currObjId = SVStorageSingleton.getInstance().getCurrentObjectId();
                                SVStorageSingleton.getInstance().addKnownObjectId(currObjId);

                                // update preferences' summaries
                                preference.setSummary(getString(R.string.activity_svsettings_txt_show_known_obj_id_summary, SVStorageSingleton.getInstance().getKnownObjectIds().size()));
                            })
                    .setNegativeButton(R.string.activity_svsettings_txt_clean_known_obj_id_storage_no,
                            (dialog, which) -> {
                                // Do nothing
                            })
                    .show();
            return true;
        }
    };

}