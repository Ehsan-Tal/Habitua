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
    suspend fun deleteAllPrinciplesDates()

    suspend fun getPrinciplesAndPrincipleDates(date: Long): Flow<List<PrincipleDetails>>


    fun getPrincipleStream(id: Int): Flow<Principle?>
    fun getAllPrinciplesStream(): Flow<List<Principle>>
    fun getAllPrinciplesDatesStream(): Flow<List<PrincipleDate>>
    fun getPrinciplesByDateStream(date: Long): Flow<List<PrincipleDate>>
    fun getPrinciplesByDateRangeStream(dateStart: Long, dateEnd: Long): Flow<List<PrincipleDate>>
    fun getPrinciplesDetailsByDateCreated(date: Long): Flow<List<Principle>>
    fun countPrinciplesDetailsByDateCreated(date: Long): Flow<Int>

    suspend fun insertPrinciple(principle: Principle): Long
    suspend fun updatePrinciple(principle: Principle)
    suspend fun deletePrinciple(principle: Principle)
    suspend fun updatePrincipleOrigin(date: Long, principleId: Int)

    suspend fun insertPrincipleDate(principleDate: PrincipleDate)
    suspend fun deletePrincipleDate(principleDate: PrincipleDate)
    suspend fun updatePrincipleDate(date: Long, principleId: Int)


    suspend fun deletePrincipleById(principleId: Int)
    suspend fun deletePrincipleDateById(principleId: Int)
}
