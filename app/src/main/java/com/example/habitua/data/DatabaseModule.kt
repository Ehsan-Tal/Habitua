package com.example.habitua.data

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application):
            HabituaLocalDatabase {
        return Room.databaseBuilder(
            application,
            HabituaLocalDatabase::class.java,
            "HabituaDB"
        ).build()
    }

    @Provides
    fun provideHabitDao(database: HabituaLocalDatabase): HabitDao {
        return database.habitDao()
    }

    @Provides
    fun provideStreakLogsDao(database: HabituaLocalDatabase): StreakLogsDao {
        return database.streakLogsDao()
    }

    @Provides
    fun provideConditionLogsDao(database: HabituaLocalDatabase): ConditionLogsDao {
        return database.conditionLogsDao()
    }
}