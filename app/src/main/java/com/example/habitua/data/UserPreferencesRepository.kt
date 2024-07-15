package com.example.habitua.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Class that handles saving and retrieving user preferences.
 * @param dataStore - the method by which we store user preferences
 */
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {

    /**
     * A companion object to hold keys
     */
    private companion object{
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val LAST_REVIEWED_KEY = stringPreferencesKey("reviewed")

        const val TAG = "UserPreferencesRepository"
    }

    /**
     * A flow that emits a boolean value indicating whether dark mode is enabled.
     * Default return is false
     */
    val isDarkMode: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_DARK_MODE] ?: false
        }

    /**
     * A suspend function to save the theme preference.
     */
    suspend fun saveThemePreference(isDarkMode: Boolean) {
        dataStore.edit { prefers ->
            prefers[IS_DARK_MODE] = isDarkMode
        }
    }

    //TODO: Change this to a boolean that checks if today's date matches what's stored in
    //TODO: Change the view model to init one single current date !
    /**
     * SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
     * */
    val lastReviewedFlow: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[LAST_REVIEWED_KEY] ?: "2023-12-08"
        }

    suspend fun saveDateReviewed(date: String) {
        if (date.matches(Regex(
                "\\d{4}-\\d{2}-\\d{2}"
            ))) {
            dataStore.edit {prefs ->

                prefs[LAST_REVIEWED_KEY] = date
            }
        }
    }


    /**
     * A flow that emits a string value indicating the language preference.
     * @return a Flow of a string value - default is "en"
     */
    val readLangPref: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[LANGUAGE_KEY] ?: "en"
        }

    /**
     * A suspend function to save the language preference.
     */
    suspend fun writeLangPref(langCode: String) {
        dataStore.edit { settings ->
            settings[LANGUAGE_KEY] = langCode
        }
    }
}
