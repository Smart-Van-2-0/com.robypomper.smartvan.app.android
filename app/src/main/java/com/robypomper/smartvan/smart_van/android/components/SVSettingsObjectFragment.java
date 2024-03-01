package com.robypomper.smartvan.smart_van.android.components;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.kizitonwose.colorpreferencecompat.ColorPreferenceCompat;
import com.robypomper.smartvan.smart_van.android.R;
import com.robypomper.smartvan.smart_van.android.activities.SVSettingsActivity;
import com.robypomper.smartvan.smart_van.android.storage.SVStorageSingleton;
import com.robypomper.smartvan.smart_van.android.storage.local.LocalPreferences;

import java.util.List;

public class SVSettingsObjectFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        loadData();

        // Inflate the preferences from the XML resource
        setPreferencesFromResource(R.xml.preferences_object, rootKey);

        setupViews();
    }

    private void loadData() {
        // Bind the backend data store from LocalPreferences to the preference manager
        assert SVStorageSingleton.getInstance().getCurrentPreferencesApp() instanceof LocalPreferences;
        RxDataStore<Preferences> dataStore = ((LocalPreferences) SVStorageSingleton.getInstance().getCurrentPreferencesApp()).getDataStore();
        getPreferenceManager().setPreferenceDataStore(new SVSettingsActivity.RxDataStorePreference(dataStore));
    }

    private void setupViews() {
        // EditTextPreference version for the activity_svsettings_txt_svbox_color_key preference
        /*EditTextPreference prefSVBoxColor = findPreference(getString(R.string.activity_svsettings_txt_svbox_color_key));
        assert prefSVBoxColor != null;
        prefSVBoxColor.setDefaultValue(SVStorageSingleton.getInstance().getCurrentPreferencesApp().getSVBoxColor());
        prefSVBoxColor.setOnBindEditTextListener(prefSVBoxColorBindListener);
        prefSVBoxColor.setOnPreferenceChangeListener(prefSVBoxColorChangeListener);*/

        // ColorPreferenceCompat version for the activity_svsettings_txt_svbox_color_key preference
        ColorPreferenceCompat prefSVBoxColor = findPreference(getString(R.string.activity_svsettings_txt_svbox_color_key));
        assert prefSVBoxColor != null;
        prefSVBoxColor.setDefaultValue(SVStorageSingleton.getInstance().getCurrentPreferencesApp().getSVBoxColor());
        prefSVBoxColor.setOnPreferenceChangeListener(prefSVBoxColorChangeListener);

        EditTextPreference prefChartsTimeout = findPreference(getString(R.string.activity_svsettings_txt_charts_timeout_key));
        assert prefChartsTimeout != null;
        prefChartsTimeout.setDefaultValue(SVStorageSingleton.getInstance().getCurrentPreferencesApp().getChartTimeoutSeconds());
        prefChartsTimeout.setOnBindEditTextListener(prefChartsTimeoutBindListener);
        prefChartsTimeout.setOnPreferenceChangeListener(prefChartsTimeoutChangeListener);

        Preference prefResetObjectServices = findPreference(getString(R.string.activity_svsettings_txt_reset_obj_id_services_key));
        assert prefResetObjectServices != null;
        prefResetObjectServices.setOnPreferenceClickListener(prefResetObjectServicesListener);
    }

    private final EditTextPreference.OnBindEditTextListener prefSVBoxColorBindListener = new EditTextPreference.OnBindEditTextListener() {
        @Override
        public void onBindEditText(@NonNull EditText editText) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    };

    private final Preference.OnPreferenceChangeListener prefSVBoxColorChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
            // parse string values from EditTextPreference
            if (newValue instanceof String) {
                String value = (String) newValue;
                // if empty string, reset to default
                if (value.isEmpty()) {
                    SVStorageSingleton.getInstance().getCurrentPreferencesApp().setSVBoxColor(LocalPreferences.DEF_SVBOX_COLOR);
                    Toast.makeText(preference.getContext(), R.string.activity_svsettings_txt_svbox_color_set_default, Toast.LENGTH_SHORT).show();
                    return true;
                }
                // try to parse the value as hex
                if (value.length() == 6) {
                    try {
                        SVStorageSingleton.getInstance().getCurrentPreferencesApp().setSVBoxColor(Integer.parseInt(value, 16));
                        return true;
                    } catch (NumberFormatException ignore) {
                    }
                }
                // try to parse the value as dec
                try {
                    SVStorageSingleton.getInstance().getCurrentPreferencesApp().setSVBoxColor(Integer.parseInt(value));
                    return true;
                } catch (NumberFormatException ignore) {
                }
            }

            // parse int values from ColorPreferenceCompat
            if (newValue instanceof Integer) {
                int value = (int) newValue;
                SVStorageSingleton.getInstance().getCurrentPreferencesApp().setSVBoxColor(value);
                return true;
            }

            Toast.makeText(preference.getContext(), R.string.activity_svsettings_txt_svbox_color_set_invalid, Toast.LENGTH_SHORT).show();
            return false;
        }
    };

    private final EditTextPreference.OnBindEditTextListener prefChartsTimeoutBindListener = new EditTextPreference.OnBindEditTextListener() {
        @Override
        public void onBindEditText(@NonNull EditText editText) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    };

    private final Preference.OnPreferenceChangeListener prefChartsTimeoutChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
            if (newValue instanceof String) {
                String value = (String) newValue;
                // if empty string, reset to default
                if (value.isEmpty()) {
                    SVStorageSingleton.getInstance().getCurrentPreferencesApp().setSVBoxColor(LocalPreferences.DEF_SVBOX_COLOR);
                    Toast.makeText(preference.getContext(), getString(R.string.activity_svsettings_txt_charts_timeout_set_default, LocalPreferences.DEF_CHARTS_TIMEOUT), Toast.LENGTH_SHORT).show();
                    return true;
                }
                // try to parse the value as int
                try {
                    int timeout = Integer.parseInt(value);
                    if (timeout > 0) {
                        SVStorageSingleton.getInstance().getCurrentPreferencesApp().setChartTimeoutSeconds(timeout);
                        return true;
                    }
                } catch (NumberFormatException ignore) {
                }
            }
            Toast.makeText(preference.getContext(), R.string.activity_svsettings_txt_charts_timeout_set_invalid, Toast.LENGTH_SHORT).show();
            return false;
        }
    };

    private final Preference.OnPreferenceClickListener prefResetObjectServicesListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(@NonNull Preference preference) {
            // Show warning dialog, if accepted reset the favourite object id
            AlertDialog.Builder builder = new AlertDialog.Builder(preference.getContext());
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
    };

}
