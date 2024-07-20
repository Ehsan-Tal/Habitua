package com.example.habitua

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.habitua.ui.theme.HabituaTheme
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            HabituaTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    HabitApp()
                }
            }
        }

    }

    override fun onResume(){
        super.onResume()

        WorkManager.getInstance(this).cancelAllWork()

        val nowTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)

            if (before(nowTime)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val initialDelay = targetTime.timeInMillis - nowTime.timeInMillis

        val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueue(notificationWorkRequest)

    }

}