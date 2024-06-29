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
class SettingViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
    val uiState: StateFlow<SettingUiState> =
        userPreferencesRepository.isDarkMode.map { isDarkMode ->
            SettingUiState(isDarkMode)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingUiState()
        )

    fun selectThemeMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveThemePreference(isDarkMode)
        }
        Log.d(TAG, "$isDarkMode")
    }

}

data class SettingUiState(
    val isDarkMode: Boolean = true
)