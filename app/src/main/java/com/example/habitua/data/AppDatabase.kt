package com.example.habitua.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Habit::class], version = 1, exportSchema = false)
abstract class HabitDatabase: RoomDatabase() {
    abstract fun habitDao(): HabitDao

    // where the streak dao would go

    companion object {
        // so, in this companion object, we establish a volatile nullable variable
        // that represents the database
        @Volatile
        private var Instance: HabitDatabase? = null

        fun getDatabase(context: Context): HabitDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context, HabitDatabase::class.java, "habit_database"
                )
                    .build().also { Instance = it }

            }
        }
    }
}
