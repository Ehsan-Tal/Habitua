package com.example.habitua.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.habitua.MainActivity
import com.example.habitua.R
import com.example.habitua.data.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


const val CHANNEL_ID = "reminder_habitua_1"
const val NOTIFICATION_REMINDER_ID = 1

class ReminderWorker(
    context: Context, workerParameters: WorkerParameters,
    private val preferencesRepository: UserPreferencesRepository
): Worker(context, workerParameters) {

    // we define a class to create the reminder worker thing
    // we then want to define its logic and behaviour in doWork
    override fun doWork(): Result {

        // we need to create a coroutineScope - tied to this thing's lifecycle
        // I somewhat get Scope and lifecycles, but the context provided is black box x2
        val scope = CoroutineScope(SupervisorJob()  + Dispatchers.IO)

        //TODO: figure out a neat way to test this - you know, editing the time of the AVD
        // through the command line or somethin'
        //TODO: figure out a way to stop this from launching when the app is open

        scope.launch {
            val lastReviewedString =  preferencesRepository.lastReviewedFlow.first()

            // this is a scuffed way to compare dates, but it's the way I choose.
            val converter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            val lastReviewedDate = LocalDate.parse(lastReviewedString, converter)
            val currentDate = LocalDate.now()

            // we want to add more conditions so we don't come close to over-notifying
            if (currentDate.isAfter(lastReviewedDate)) {
                createNotificationChannel()
                showNotification()
            }

        }

        // this is a black box too
        return Result.retry()
    }

    // taken from https://developer.android.com/develop/ui/views/notifications/build-notification
    // now, what do we pass for the name and description ?
    // I do not understand how we'd create a CHANNEL_ID - though, I have to thank the naming
    // scheme as I can see it is a CONST
    // I have set a const for it

    // anyway, I'd like the tap action for this to open the app
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "placeholder name"
            val descriptionText = "placeholder description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                ContextCompat.getSystemService(applicationContext, NotificationManager::class.java )
                        as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    // this would result in a notification that would launch/open the application
    // we can suppress the missing permission warning as we check for the permission in doWork()

    private fun showNotification(){
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext,
            0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext.getString(R.string.worker_reminder_content_title))
            .setContentText(applicationContext.getString(R.string.worker_reminder_content_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notify(NOTIFICATION_REMINDER_ID, builder.build())
        }

    }
}