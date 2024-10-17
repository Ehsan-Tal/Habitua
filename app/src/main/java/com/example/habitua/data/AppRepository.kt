package com.example.habitua.data

import kotlinx.coroutines.flow.Flow

interface AppRepository {

    suspend fun insertHabit(habit: Habit): Long
    suspend fun deleteHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)

    suspend fun deleteAllHabits(): Void
    suspend fun createAllHabits(habitList: List<Habit>): Void

    suspend fun insertHabitDate(habitDate: HabitDate)
    suspend fun deleteHabitDate(habitDate: HabitDate)
    suspend fun updateHabitDate(date: Long, id: Int)

    fun getAllHabitsDatesStream(): Flow<List<HabitDate>>
    fun getHabitsByDateStream(date: Long): Flow<List<HabitDate>>
    fun getHabitsDetailsByDateCreated(date: Long): Flow<List<Habit>>

    suspend fun getHabitsAndHabitDates(date: Long): Flow<List<HabitDetails>>
    fun countHabitsDetailsByDateCreated(date: Long): Flow<Int>


    suspend fun deleteHabitById(id: Int)
    suspend fun deleteHabitDateById(id: Int)


    fun getHabitStream(habitId: Int): Flow<Habit?>
    fun getAllHabitsStream(): Flow<List<Habit>>


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
