package com.example.habitua.data

import kotlinx.coroutines.flow.Flow

interface AppRepository {

    suspend fun insertHabit(habit: Habit)

    suspend fun deleteHabit(habit: Habit)

    suspend fun updateHabit(habit: Habit)

    fun getHabitStream(habitId: Int): Flow<Habit?>

    fun getAllHabitsStream(): Flow<List<Habit>>

    fun getAllHabitsAcquiredStream(): Flow<List<Habit>>

    fun getAllHabitsNotAcquiredStream(): Flow<List<Habit>>

    fun getAllHabitsStreakingStream(): Flow<List<Habit>>

    fun getAllHabitsNotStreakingStream(): Flow<List<Habit>>

    fun getAllHabitsTODOStream(dateToday: Long, dateYesterday: Long): Flow<List<Habit>>

    fun getAllHabitsAtRiskStream(dateYesterday: Long): Flow<List<Habit>>

    suspend fun reviewHabits(dateToday: Long, dateYesterday: Long)


    // principles
    fun getAllPrinciplesStream(): Flow<List<Principle>>

    fun getAllPrinciplesDatesStream(): Flow<List<PrincipleDate>>

    fun getPrinciplesByDateStream(date: Long): Flow<List<PrincipleDate>>

    fun getPrinciplesByDateRangeStream(dateStartInclusive: Long, dateEndInclusive: Long): Flow<List<PrincipleDate>>

    suspend fun insertPrinciple(principle: Principle)

    suspend fun insertPrincipleDate(principleDate: PrincipleDate)

    suspend fun deletePrinciple(principle: Principle)

    suspend fun deletePrincipleDate(principleDate: PrincipleDate)

    suspend fun insertPrincipleDateEntry(principleId: Int, date: Long, value: Boolean)

}
