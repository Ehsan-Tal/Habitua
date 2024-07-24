package com.example.habitua

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

const val notificationId = 1
const val notificationChannelId = "reminder_notification_channel"
const val titleExtra = "Reviewer Reminder-er"
const val messageExtra = "Review your habits ?"

const val TAG = "NotificationWorker"
/**
 * Worker class for scheduling notifications
 */
class NotificationWorker(private val context: Context, workerParams: WorkerParameters):
    Worker(context, workerParams){

    /**
     * Method for scheduling notifications
     *
     * makes an intent to open MainActivity
     * builds the notification according to some specification
     * sends it to the NotificationManager
     */
    override fun doWork(): Result {

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        // build the notification
        val reminder = NotificationCompat.Builder(context, notificationChannelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titleExtra)
            .setContentText(messageExtra)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // get the notification manager service
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            notificationChannelId,
            "Reminder Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifies user to review their habits - once at 2000"
        }

        manager.createNotificationChannel(channel)

        //val channelExists = manager.getNotificationChannel(notificationChannelId) != null

        // show the notification using the manager
        manager.notify(notificationId, reminder)

        return Result.success()
    }
}