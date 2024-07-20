package com.example.habitua

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

const val notificationId = 1
const val notificationChannelId = "reminder_notification_channel"
const val titleExtra = "Reviewer Reminder-er"
const val messageExtra = "Review your habits ?"

class NotificationWorker(private val context: Context, workerParams: WorkerParameters):
    Worker(context, workerParams){

    override fun doWork(): Result {

        // build the notification
        val reminder = NotificationCompat.Builder(context, notificationChannelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titleExtra)
            .setContentText(messageExtra)
            .build()

        // get the notification manager service
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // show the notification using the manager
        manager.notify(notificationId, reminder)

        return Result.success()
    }
}