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
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.habitua.ReviewWorker.Companion.KEY_DATE_TODAY
import com.example.habitua.ReviewWorker.Companion.KEY_DATE_YESTERDAY
import com.example.habitua.ui.navigation.HabitNavHost
import com.example.habitua.ui.theme.HabituaTheme
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
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

        // For notifications to review
        val instantNowObject = Instant.now()
        val currentTime = LocalDateTime.ofInstant(instantNowObject, ZoneId.systemDefault())
        val notificationLocalDateTimeHour = 20
        val notificationLocalDateTimeMinute = 0

        val notificationLocalDateTime = LocalDateTime.of(
            currentTime.year, currentTime.month, currentTime.dayOfMonth,
            notificationLocalDateTimeHour, notificationLocalDateTimeMinute, 0
        )


        val targetTimes = listOf(
            // targetTimeToday
            notificationLocalDateTime
                .let { if (it.isBefore(currentTime)) it.plusDays(1) else it }
                .toInstant(ZoneOffset.UTC),

            // targetTimeTomorrow
            notificationLocalDateTime
                .plusDays(1)
                .let { if (it.isBefore(currentTime)) it.plusDays(1) else it }
                .toInstant(ZoneOffset.UTC),

            // targetTimeDayAfterDayAfter
            notificationLocalDateTime
                .plusDays(4)
                .toInstant(ZoneOffset.UTC),

            // targetTimeNextWeek
            notificationLocalDateTime
                .plusDays(7)
                .toInstant(ZoneOffset.UTC)
        )

        targetTimes.forEach { targetTime ->
            val initialDelay = targetTime.toEpochMilli() - instantNowObject.toEpochMilli()

            val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(this).enqueue(notificationWorkRequest)
        }

        // For making an auto-reviewer
        // we only need it for "this day" and tomorrow
        // daily - only 2 necessary
        // bi-daily - only 3 necessary
        // weekly - only 8 necessary
        // should weekly allow one week's worth of affordance ? Nah.
        // eight - not safe. If today is tuesday and the habit is at friday,
        // 8 days does not cycle past two fridays.
        // worst case is today-tuesday and habit-monday.
        // 14 is max, let's do 14 ones !

        //  THE REVIEW INITIAL DELAY FUNCTIONS
        val reviewTimeComparisonHour = 1

        val reviewTimeComparison = LocalDateTime.of(
            currentTime.year, currentTime.month, currentTime.dayOfMonth,
            reviewTimeComparisonHour, 0, 0)

        val startDay = if (currentTime.isAfter(reviewTimeComparison)) 1 else 0

        // for checking
        for( i in startDay until 14 + startDay) {
            val reviewTime = reviewTimeComparison
                .plusDays(i.toLong())

            val dateToday = reviewTime.toInstant(ZoneOffset.UTC).toEpochMilli()
            val dateYesterday = reviewTime.minusDays(1).toInstant(ZoneOffset.UTC).toEpochMilli()

            val inputData = workDataOf(
                KEY_DATE_TODAY to dateToday,
                KEY_DATE_YESTERDAY to dateYesterday
                )

            val reviewWorkRequest = OneTimeWorkRequestBuilder<ReviewWorker>()
                .setInitialDelay(dateToday, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(this).enqueue(reviewWorkRequest)
        }

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