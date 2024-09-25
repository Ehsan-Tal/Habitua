package com.example.habitua.data

import kotlinx.coroutines.flow.Flow

/**
 * Implementation of [AppRepository] interface - provides habit data to the app
 *
 * Interacts with [HabitDao] to perform & control CRUD operations on data.
 */
class AppRepositoryImplementation(
    private val habitDao: HabitDao
): AppRepository {


    override suspend fun insertHabit(habit: Habit) = habitDao.insert(habit)

    override suspend fun deleteHabit(habit: Habit) = habitDao.delete(habit)

    override suspend fun updateHabit(habit: Habit) = habitDao.update(habit)

    override fun getHabitStream(habitId: Int): Flow<Habit?> =
        habitDao.getHabit(habitId)

    override fun getAllHabitsStream(): Flow<List<Habit>> = habitDao.getAllHabits()

    override fun getAllHabitsAcquiredStream():
            Flow<List<Habit>> = habitDao.getAllHabitsAcquired()

    override fun getAllHabitsNotAcquiredStream():
            Flow<List<Habit>> = habitDao.getAllHabitsNotAcquired()

    override fun getAllHabitsStreakingStream():
            Flow<List<Habit>> = habitDao.getAllHabitsStreaking()

    override fun getAllHabitsNotStreakingStream():
            Flow<List<Habit>> = habitDao.getAllHabitsNotStreaking()


    override fun getAllHabitsTODOStream(dateToday: Long, dateYesterday: Long):
            Flow<List<Habit>> = habitDao.getAllHabitsTODO(dateToday, dateYesterday)

    override fun getAllHabitsAtRiskStream(dateYesterday: Long):
            Flow<List<Habit>> = habitDao.getAllHabitsAtRisk(dateYesterday)

    override suspend fun reviewHabits(dateToday: Long, dateYesterday: Long)
            = habitDao.reviewHabits(dateToday, dateYesterday)


    // principles
    override suspend fun insertPrinciple(principle: Principle) = habitDao.insertPrinciple(principle)

    override suspend fun insertPrincipleDate(principleDate: PrincipleDate) = habitDao.insertPrincipleDate(principleDate)

    override suspend fun deletePrinciple(principle: Principle) = habitDao.deletePrinciple(principle)

    override suspend fun deletePrincipleDate(principleDate: PrincipleDate) = habitDao.deletePrincipleDate(principleDate)

    override fun getAllPrinciplesStream(): Flow<List<Principle>> = habitDao.getAllPrinciples()

    override fun getAllPrinciplesDatesStream(): Flow<List<PrincipleDate>> = habitDao.getAllPrinciplesDates()


    override fun getPrinciplesByDateStream(date: Long): Flow<List<PrincipleDate>> = habitDao.getPrinciplesByDate(date)

    override fun getPrinciplesByDateRangeStream(dateStartInclusive: Long, dateEndInclusive: Long): Flow<List<PrincipleDate>> = habitDao.getPrinciplesByDateRange(dateStartInclusive, dateEndInclusive)

    override suspend fun insertPrincipleDateEntry(principleId: Int, date: Long, value: Boolean) = habitDao.insertPrincipleDateEntry(principleId, date, value)

}