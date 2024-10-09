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
import com.example.habitua.data.AppRepository
import com.example.habitua.data.Habit
import com.example.habitua.data.Principle
import com.example.habitua.ui.test_data_HabitList
import com.example.habitua.ui.test_data_PrincipleList
import com.example.habitua.ui.test_data_principleListToday
import kotlinx.coroutines.flow.first
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

const val TAG = "SettingViewModel"

/**
 * ViewModel for [SettingScreen]
 *
 * @param userPreferencesRepository - used to save and retrieve user preferences
 */
class SettingViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val appRepository: AppRepository
): ViewModel() {

    private val _hasNotificationPermission = MutableStateFlow(true)
    val hasNotificationPermission: StateFlow<Boolean> = _hasNotificationPermission

    private val _canCreateTestData = MutableStateFlow(true)
    val canCreateTestData: StateFlow<Boolean> = _canCreateTestData

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
    init {
        viewModelScope.launch {
            _canCreateTestData.value = appRepository.getAllHabitsStream().first().isEmpty()
        }
    }

    fun createTestData(){

        viewModelScope.launch {
            appRepository.createAllHabits(test_data_HabitList)
            _canCreateTestData.value = true
        }

        viewModelScope.launch {
            appRepository.createTestPrinciples(test_data_PrincipleList)
        }

    }

    fun deleteAllHabits() {
        viewModelScope.launch {
            appRepository.deleteAllHabits()
            _canCreateTestData.value = false
        }

        viewModelScope.launch {
            appRepository.deleteAllPrinciples()
        }
    }

    fun downloadData(){
        // requires permissions
        // handle errors too
        // launch a background thread for this`

        // how to download data ?
        // get the habit list, convert into a string
        // create a file
        // write data to the file
        // create a download request
        // enqueue the download
/*
        viewModelScope.launch {
            val data = appRepository.getAllHabitsStream()
            val csvData = data.joinToString("\n") { entity ->
                "${entity.id},${entity.name},${entity.value}"
            }
            val file = File(context.getExternalFilesDir(null), "data.csv")
            file.writeText(csvData)
            val request = androidx.media3.exoplayer.offline.DownloadManager.Request(Uri.fromFile(file))
                .setTitle("My Data")
                .setDescription("Downloading data from Room")
                .setNotificationVisibility(androidx.media3.exoplayer.offline.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            downloadManager.enqueue(request)
        }

 */
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