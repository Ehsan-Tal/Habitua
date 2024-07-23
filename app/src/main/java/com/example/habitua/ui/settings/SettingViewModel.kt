package com.example.habitua.ui.settings

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitua.data.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.habitua.R

const val TAG = "SettingViewModel"

/**
 * ViewModel for [SettingScreen]
 *
 * @param userPreferencesRepository - used to save and retrieve user preferences
 */
class SettingViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

    private val _hasNotificationPermission = MutableStateFlow(true)
    val hasNotificationPermission: StateFlow<Boolean> = _hasNotificationPermission

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
    }

    //fun createHabitsString(){}

    // I think it'd be fun to share habits, there should be a button to take all the habits and share.
    private fun shareHabitTracking(context: Context, subject: String, habits: String) {
        // createHabitsString() <- requires this

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            // we need to make a string of it
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, habits)
        }
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.new_habit_receipt)
            )
        )
    }

    //Had to check the documentation for this one
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkNotificationPermission(context: Context) {
        viewModelScope.launch {
            _hasNotificationPermission.value = ContextCompat
                .checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_DENIED

        }
    }

}

/**
 * UI state for [SettingScreen]
 */
data class SettingUiState(
    val isDarkMode: Boolean = true
)