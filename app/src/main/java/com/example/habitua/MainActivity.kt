package com.example.habitua

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.habitua.ui.navigation.HabitNavHost
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
                    HabitNavHost(navController = rememberNavController())
                }
            }
        }

    }

    /**
     * WorkerManager's queue is cleared
     *
     * onResume collects the current time and the target time
     * If current time exceeds target time, add one day to the target
     *  the difference marks the initial delay, which is then used to schedule the notification
     *
     *  The notification is of type NotificationWorker and is enqueued using WorkManager
     */
    override fun onResume(){
        super.onResume()

        WorkManager.getInstance(this).cancelAllWork()

        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)

            if (before(currentTime)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val initialDelay = targetTime.timeInMillis - currentTime.timeInMillis

        val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueue(notificationWorkRequest)

    }

}

/**
 * App bar to display title and conditionally display the back navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = { Text(text = title, style = MaterialTheme.typography.displayLarge) },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.content_description_navigate_back)
                )
            }
        }
    )
}