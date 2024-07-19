package com.example.habitua.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitua.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

const val TAG = "SettingViewModel"

/**
 * ViewModel for [SettingScreen]
 *
 * @param userPreferencesRepository - used to save and retrieve user preferences
 */
class SettingViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

    /**
     * UI state exposed to the UI
     *
     * Uses a StateFlow to hold the current setting
     */
    val uiState: StateFlow<SettingUiState> =
        userPreferencesRepository.isDarkMode.map { isDarkMode ->
            SettingUiState(isDarkMode)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingUiState()
        )

    /**
     * Saves the new theme preference
     */
    fun selectThemeMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveThemePreference(isDarkMode)
        }
        //Log.d(TAG, "$isDarkMode")
    }

}

/**
 * UI state for [SettingScreen]
 */
data class SettingUiState(
    val isDarkMode: Boolean = true
)