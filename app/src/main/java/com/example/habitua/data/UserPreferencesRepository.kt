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

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object{
        //val nightmode
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val LANGUAGE_KEY = stringPreferencesKey("language")

        const val TAG = "UserPreferencesRepository"
    }

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
