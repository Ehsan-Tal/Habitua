package com.example.habitua

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.habitua.data.AppContainer
import com.example.habitua.data.AppDataContainer
import com.example.habitua.data.UserPreferencesRepository
import com.example.habitua.workers.ReminderWorker
import java.util.Calendar
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
        val workManager =
            WorkManager.getInstance(applicationContext)


        val currentTime = Calendar.getInstance()
        val dueTime = Calendar.getInstance().apply{
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0) // do we need to do this ?
            if (before(currentTime)) {
                add(Calendar.DAY_OF_YEAR, 1)
            } // we can add one day to the instance - to ensure the difference I suppose
            // but is this an inclusive before ? Would == count in this instance ?
        }

        val delay = dueTime.timeInMillis - currentTime.timeInMillis

        val constraints = Constraints.Builder()
            .setTriggerContentUpdateDelay(15, TimeUnit.MINUTES)
            .setTriggerContentMaxDelay(30, TimeUnit.MINUTES)
            .build()

        //val data = Data.Builder()

        val work = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()

        workManager.enqueue(work)

    }
}