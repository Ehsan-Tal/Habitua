package com.example.habitua.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import javax.inject.Singleton

@Database(entities = [Habit::class, StreakLogs::class, ConditionLogs::class], version = 1)
@Singleton
abstract class HabituaLocalDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun streakLogsDao(): StreakLogsDao
    abstract fun conditionLogsDao(): ConditionLogsDao
}
