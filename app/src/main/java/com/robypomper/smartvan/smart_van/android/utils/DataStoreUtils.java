package com.robypomper.smartvan.smart_van.android.utils;

import android.content.Context;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import java.util.Set;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;


/**
 * Utility class for AndroidX DataStore.
 * <p>
 * This class provides the basic methods to store and retrieve data from the
 * AndroidX DataStore.
 */
public class DataStoreUtils {

    // Static DataStore utils

    /**
     * Initialize the DataStore.
     * <p>
     * This method is used to initialize the DataStore for the SmartVan storage.
     *
     * @param ctx           the context used to initialize the DataStore.
     * @param dataStoreName the name of the DataStore to initialize.
     * @return the initialized DataStore.
     */
    public static RxDataStore<Preferences> initDataStore(Context ctx, String dataStoreName) {
        return new RxPreferenceDataStoreBuilder(ctx, dataStoreName).build();
    }

    /**
     * Clear the DataStore.
     * <p>
     * This method is used to clear the DataStore for the SmartVan storage.
     *
     * @param dataStore the DataStore to clear.
     */
    public static void clearDataStore(RxDataStore<Preferences> dataStore) {
        dataStore.updateDataAsync(new Function<Preferences, Single<Preferences>>() {
            @Override
            public Single<Preferences> apply(Preferences preferences) {
                MutablePreferences mutablePreferences = preferences.toMutablePreferences();
                mutablePreferences.clear();
                dataStore.dispose();
                return Single.just(mutablePreferences);
            }
        }).subscribe();
    }

    /**
     * Get a value from the DataStore.
     * <p>
     * This method is used to get a value from the DataStore.
     *
     * @param dataStore  the DataStore to get the value from.
     * @param preference the preference key to get the value for.
     * @param defValue   the default value to return if the preference is not set.
     * @param <T>        the type of the value to get.
     * @return the value from the DataStore, or the default value if the preference is not set.
     */
    public static <T> T getFromDataStore(RxDataStore<Preferences> dataStore, Preferences.Key<T> preference, T defValue) {
        Flowable<T> preferenceFlowable = dataStore.data().map(prefs -> prefs.get(preference));
        try {
            return preferenceFlowable.firstElement().blockingGet();
        } catch (NullPointerException e) {
            return defValue;
        }
    }

    /**
     * Set a value to the DataStore.
     * <p>
     * This method is used to set a value to the DataStore.
     *
     * @param dataStore  the DataStore to set the value to.
     * @param preference the preference key to set the value for.
     * @param value      the value to set.
     * @param <T>        the type of the value to set.
     */
    public static <T> void setToDataStore(RxDataStore<Preferences> dataStore, Preferences.Key<T> preference, T value) {
        dataStore.updateDataAsync(new Function<Preferences, Single<Preferences>>() {
            @Override
            public Single<Preferences> apply(Preferences preferences) {
                MutablePreferences mutablePreferences = preferences.toMutablePreferences();
                mutablePreferences.set(preference, value);
                return Single.just(mutablePreferences);
            }
        }).subscribe();
    }


    // Preference key generators

    public static Preferences.Key<Boolean> booleanKey(Context ctx, int propKeyAsRes) {
        return booleanKey(ctx.getString(propKeyAsRes));
    }
    public static Preferences.Key<Boolean> booleanKey(String propKey) {
        return PreferencesKeys.booleanKey(propKey);
    }

    public static Preferences.Key<Integer> intKey(Context ctx, int propKeyAsRes) {
        return intKey(ctx.getString(propKeyAsRes));
    }
    public static Preferences.Key<Integer> intKey(String propKey) {
        return PreferencesKeys.intKey(propKey);
    }

    public static Preferences.Key<Long> longKey(Context ctx, int propKeyAsRes) {
        return longKey(ctx.getString(propKeyAsRes));
    }
    public static Preferences.Key<Long> longKey(String propKey) {
        return PreferencesKeys.longKey(propKey);
    }

    public static Preferences.Key<Float> floatKey(Context ctx, int propKeyAsRes) {
        return floatKey(ctx.getString(propKeyAsRes));
    }
    public static Preferences.Key<Float> floatKey(String propKey) {
        return PreferencesKeys.floatKey(propKey);
    }

    public static Preferences.Key<Double> doubleKey(Context ctx, int propKeyAsRes) {
        return doubleKey(ctx.getString(propKeyAsRes));
    }
    public static Preferences.Key<Double> doubleKey(String propKey) {
        return PreferencesKeys.doubleKey(propKey);
    }

    public static Preferences.Key<String> stringKey(Context ctx, int propKeyAsRes) {
        return stringKey(ctx.getString(propKeyAsRes));
    }
    public static Preferences.Key<String> stringKey(String propKey) {
        return PreferencesKeys.stringKey(propKey);
    }

    public static Preferences.Key<Set<String>> stringSetKey(Context ctx, int propKeyAsRes) {
        return stringSetKey(ctx.getString(propKeyAsRes));
    }
    public static Preferences.Key<Set<String>> stringSetKey(String propKey) {
        return PreferencesKeys.stringSetKey(propKey);
    }
}
