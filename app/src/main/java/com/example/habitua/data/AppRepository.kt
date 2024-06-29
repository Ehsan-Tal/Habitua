package com.example.habitua.data

import kotlinx.coroutines.flow.Flow

interface AppRepository {

    // these would connect to the data source

    fun getAllHabitsByAcquiredStream(hasBeenAcquired: Boolean): Flow<List<Habit>>

    fun getAllHabitsStream(): Flow<List<Habit>>
    fun getHabitStream(habitId: Int): Flow<Habit?>

    suspend fun insertHabit(habit: Habit)

    suspend fun deleteHabit(habit: Habit)

    suspend fun updateHabit(habit: Habit)

    //TODO: need to create a string entity
//    suspend fun endStreak(habit: Habit)
}
