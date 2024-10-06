package com.example.habitua.data

import kotlinx.coroutines.flow.Flow

interface AppRepository {

    suspend fun insertHabit(habit: Habit)
    suspend fun deleteHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)

    suspend fun deleteAllHabits(): Void
    suspend fun createAllHabits(habitList: List<Habit>): Void

    fun getHabitStream(habitId: Int): Flow<Habit?>
    fun getAllHabitsStream(): Flow<List<Habit>>
    fun getAllHabitsAcquiredStream(): Flow<List<Habit>>
    fun getAllHabitsNotAcquiredStream(): Flow<List<Habit>>
    fun getAllHabitsNotStreakingStream(): Flow<List<Habit>>
    fun getAllHabitsTODOStream(dateToday: Long, dateYesterday: Long): Flow<List<Habit>>
    fun getAllHabitsAtRiskStream(dateYesterday: Long): Flow<List<Habit>>

    fun countAllHabitsStream(): Flow<Int>
    fun countAllHabitsAcquiredStream(): Flow<Int>
    fun countAllHabitsNotAcquiredStream(): Flow<Int>
    fun countAllHabitsNotStreakingStream(): Flow<Int>
    fun countAllHabitsTODOStream(dateToday: Long, dateYesterday: Long): Flow<Int>
    fun countAllHabitsAtRiskStream(dateYesterday: Long): Flow<Int>

    suspend fun reviewHabits(dateToday: Long, dateYesterday: Long)


    // principles
    suspend fun createTestPrinciples(principleList: List<Principle>): Void
    suspend fun deleteAllPrinciples()

    suspend fun getPrinciplesAndPrincipleDates(date: Long): Flow<List<PrincipleDetails>>
    suspend fun getPrinciplesAndPrincipleDatesWithoutCreating(date: Long): Flow<List<PrincipleDetails>>

    fun getAllPrinciplesStream(): Flow<List<Principle>>
    fun getAllPrinciplesDatesStream(): Flow<List<PrincipleDate>>
    fun getPrinciplesByDateStream(date: Long): Flow<List<PrincipleDate>>
    fun getPrinciplesByDateRangeStream(dateStartInclusive: Long, dateEndInclusive: Long): Flow<List<PrincipleDate>>

    suspend fun insertPrinciple(principle: Principle)
    suspend fun updatePrinciple(principle: Principle)
    suspend fun deletePrinciple(principle: Principle)

    suspend fun insertPrincipleDate(principleDate: PrincipleDate)
    suspend fun deletePrincipleDate(principleDate: PrincipleDate)
    suspend fun updatePrincipleDate(date: Long, principleId: Int, value: Boolean)

}
