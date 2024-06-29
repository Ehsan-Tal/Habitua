package com.example.habitua
import android.Manifest
import android.annotation.SuppressLint
import android.app.LocaleConfig
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.habitua.ui.theme.HabituaTheme
import android.app.LocaleManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.LocaleList
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.habitua.workers.KEY_NOTIFICATION_PERMISSION_GRANTED
import java.util.Locale
import androidx.work.Data
import androidx.work.ForegroundUpdater
import androidx.work.ProgressUpdater
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.habitua.workers.ReminderWorker
import java.util.UUID
import java.util.concurrent.Executor

class MainActivity : ComponentActivity() {

    // we need to declare this in the mainActivity
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        val data = Data.Builder()
            .putBoolean(
                KEY_NOTIFICATION_PERMISSION_GRANTED,
                isGranted
            )
        .build()
        scheduleReminderWorker(data)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // requesting notification permission
        // TODO: maybe keep this enablement in a button in the settings screen

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Permission already granted, schedule worker
                scheduleReminderWorker(null)
            }
        } else {
            // For devices below Android 13, permission is granted by default
            scheduleReminderWorker(null)
        }

        enableEdgeToEdge()
        setContent {
            HabituaTheme {
                Surface (
                    modifier = Modifier
                        .fillMaxSize()
                ){
                    HabitApp()
                }
            }
        }
    }

    private fun scheduleReminderWorker(inputData: Data?) {
        // scheduling logic

    }
}

/// this is kept empty - the navigation is in HabitApp (HabitApp.kt)
// the individual pages are kept in .ui/





/*
// these localisation implementations keep forcing the SDK upwards.
//For setOverrideLocaleConfig
val localeManager = applicationContext
    .getSystemService(LocaleManager::class.java)
localeManager.overrideLocaleConfig = LocaleConfig(
    LocaleList.forLanguageTags("en-US,ja-JP,zh-Hans-SG")
)

//For getOverrideLocaleConfig
// The app calls the API to get the override LocaleConfig
val overrideLocaleConfig = localeManager.overrideLocaleConfig
// If the returned overrideLocaleConfig isn't equal to NULL, then the app calls the API to get the supported Locales
val supportedLocales = overrideLocaleConfig.supportedLocales()

// now we need the current locale of our app
*/
