package com.example.habitua.data

import kotlinx.coroutines.flow.Flow

class AppRepositoryImplementation(
    private val habitDao: HabitDao
    ):  AppRepository {

        //TODO: add the StreaksDao
        override fun getAllHabitsByAcquiredStream(hasBeenAcquired: Boolean): Flow<List<Habit>> =
            habitDao.getAllHabitsByAcquired(hasBeenAcquired)

        override fun getAllHabitsStream(): Flow<List<Habit>> = habitDao.getAllHabits()
        override fun getHabitStream(habitId: Int): Flow<Habit?> =
            habitDao.getHabit(habitId)

        override suspend fun insertHabit(habit: Habit) = habitDao.insert(habit)

        override suspend fun deleteHabit(habit: Habit) = habitDao.delete(habit)

        override suspend fun updateHabit(habit: Habit) = habitDao.update(habit)

        //override suspend fun createStreak(habitId: Int, streakDate: Long) = streakDao.createStreak(habitId, streakDate)
        //override suspend fun endStreak(habitId: Int) = streakDao.endStreak(habitId)
    }
