package com.example.habitua.data

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

//TODO: move this to a container so that we can make this a singleton
// also, we can probably more easily access this and it's keys this way
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object{
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val LAST_REVIEWED_KEY = stringPreferencesKey("reviewed")

        const val TAG = "UserPreferencesRepository"
    }

    // we "expose" a flow when doing this
    // A flow and catch attempting to read the preferences
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

    suspend fun saveThemePreference(isDarkMode: Boolean) {
        dataStore.edit { prefers ->
            prefers[IS_DARK_MODE] = isDarkMode
        }
    }
/*
    suspend fun writeLanguagePreference(context: Context, langCode: String) {
        context.dataStore.edit { settings ->
            settings[LANGUAGE_KEY] = langCode
        }
    }

    suspend fun readLanguagePreference(context: Context): String? {
        val preferences = context.dataStore.data.firstOrNull()[LANGUAGE_KEY] ?: null
        return preferences
    }
*/

}
