package com.example.habitua.workers

import android.Manifest
import android.annotation.SuppressLint
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


const val CHANNEL_ID = "reminder_habitua_1"
const val NOTIFICATION_REMINDER_ID = 1
const val KEY_NOTIFICATION_PERMISSION_GRANTED = "notification_permission_granted"

class ReminderWorker(
    context: Context, workerParameters: WorkerParameters
): Worker(context, workerParameters) {

    // we define a class to create the reminder worker thing
    // we then want to define its logic and behaviour in doWork
    override fun doWork(): Result {
        createNotificationChannel()
        showNotification()

        return Result.success()
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