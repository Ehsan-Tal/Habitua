package com.example.habitua

import android.content.Context
import android.icu.util.Calendar
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.habitua.data.HabitDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReviewWorker(appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        // we add the try catch block to specify success and failure
        return try {

            val database = HabitDatabase.getDatabase(applicationContext)
            val habitDao = database.habitDao()

            // dateString is String? type
            val dateToday = inputData.getLong(KEY_DATE_TODAY, 0L)
            val dateYesterday = inputData.getLong(KEY_DATE_YESTERDAY, 0L)

            habitDao.reviewHabits(dateToday, dateYesterday)
            return Result.success()

        } catch (e: Exception) {
            Result.failure()
        }
    }
    companion object {
        const val KEY_DATE_TODAY = "today"
        const val KEY_DATE_YESTERDAY = "yesterday"
    }


}