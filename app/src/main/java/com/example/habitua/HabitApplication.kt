package com.example.habitua

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.habitua.data.AppContainer
import com.example.habitua.data.AppDataContainer
import com.example.habitua.data.UserPreferencesRepository
import com.example.habitua.workers.ReminderWorker
import java.util.concurrent.TimeUnit

private const val PREFERENCES_NAME = "preference_settings"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES_NAME
)

class HabitApplication: Application() {

    // so, we instance this so that the rest can obtain dependencies
    // black box
    // I think this thing doesn't work
    // very complicated - will learn in 3-5 business months

    lateinit var container: AppContainer
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        userPreferencesRepository = UserPreferencesRepository(dataStore)

        scheduleDailyNotification()
    }

    // this creates a schedule to send out the defined notification
    private fun scheduleDailyNotification() {
        val dailyWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                // Add any necessary constraints here - what are they ?
                .build())
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_reminder_work",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }
}