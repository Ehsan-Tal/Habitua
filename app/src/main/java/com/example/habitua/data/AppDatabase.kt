package com.example.habitua.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The HabitDatabase class is an abstract class that extends RoomDatabase.
 *
 * Contains the [Habit] entity and provides access to the [HabitDao]
 *
 * When altering the entities, you'll have to edit the version and handle migration.
 * Currently `.fallbackToDestructiveMigration()` is used as I'd rather not handle
 * migration
 */
@Database(entities = [Habit::class, Principle::class, PrincipleDate::class], version =5, exportSchema = false)
abstract class HabitDatabase: RoomDatabase() {

    abstract fun habitDao(): HabitDao

    // where the streak dao would go

    companion object {
        // so, in this companion object, we establish a volatile nullable variable
        // that represents the database

        /**
         * Singleton instance of the [HabitDatabase].
         *
         * Ensures that only one instance of the database is created for the app.
         */
        @Volatile
        private var Instance: HabitDatabase? = null

        /**
         * Returns the singleton instance of [HabitDatabase].
         *
         * Creates the database if it doesn't exist, using the provided [Context].
         *
         * @param context The application context.
         * @return The instance of [HabitDatabase].
         */
        fun getDatabase(context: Context): HabitDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context, HabitDatabase::class.java, "habit_database"
                )
                .fallbackToDestructiveMigration()
                .build().also { Instance = it }
                .also { return it}

            }
        }
    }
}