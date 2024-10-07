package com.example.habitua.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map

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

    override suspend fun deleteAllHabits(): Void = habitDao.deleteAllHabits()

    override suspend fun updateHabit(habit: Habit) = habitDao.update(habit)

    override suspend fun createAllHabits(habitList: List<Habit>) = habitDao.createTestHabits(habitList)

    override fun getHabitStream(habitId: Int): Flow<Habit?> =
        habitDao.getHabit(habitId)

    override fun getAllHabitsStream(): Flow<List<Habit>> = habitDao.getAllHabits()

    override fun getAllHabitsAcquiredStream():
            Flow<List<Habit>> = habitDao.getAllHabitsAcquired()
    override fun getAllHabitsNotAcquiredStream():
            Flow<List<Habit>> = habitDao.getAllHabitsNotAcquired()
    override fun getAllHabitsNotStreakingStream():
            Flow<List<Habit>> = habitDao.getAllHabitsNotStreaking()
    override fun getAllHabitsTODOStream(dateToday: Long, dateYesterday: Long):
            Flow<List<Habit>> = habitDao.getAllHabitsTODO(dateToday, dateYesterday)

    override fun getAllHabitsAtRiskStream(dateYesterday: Long):
            Flow<List<Habit>> = habitDao.getAllHabitsAtRisk(dateYesterday)

    override suspend fun reviewHabits(dateToday: Long, dateYesterday: Long)
            = habitDao.reviewHabits(dateToday, dateYesterday)


    override fun countAllHabitsStream(): Flow<Int>
    = habitDao.countAllHabits()
    override fun countAllHabitsAcquiredStream(): Flow<Int>
    = habitDao.countAllHabitsAcquired()
    override fun countAllHabitsNotAcquiredStream(): Flow<Int>
    = habitDao.countAllHabitsNotAcquired()
    override fun countAllHabitsNotStreakingStream(): Flow<Int>
    = habitDao.countAllHabitsNotStreaking()
    override fun countAllHabitsTODOStream(dateToday: Long, dateYesterday: Long): Flow<Int>
    = habitDao.countAllHabitsTODO(dateToday, dateYesterday)
    override fun countAllHabitsAtRiskStream(dateYesterday: Long): Flow<Int>
    = habitDao.countAllHabitsAtRisk(dateYesterday)



    // principles
    /**
     * Collects principles and existing principle dates of :date
     * Creates principle date records by :date for all missing principles of that date
     *
     * We want to create a record for each principle at :date in the principles_date entity
     *
     * We first collect the result of a LEFT JOIN between principles and principles_dates
     * Then, we map and assign the variable as a combinedPrinciple
     *
     * then we collect the existing principleId's from combinedPrinciples
     *
     * then we gather all existent principles. As of now, I do not know what first() does,
     * but it seems to fix a few bugs.
     *
     * We then filter out from that all principleIds that were acquired from the successful
     * LEFT JOIN query
     *
     * For those that aren't, the next step maps them and creates a PrincipleDate object that
     * is then used in a forEach loop with a simple insert query.
     *
     * we need to leave combined principles at the end as that functions as a return statement
     */
    override suspend fun getPrinciplesAndPrincipleDates(date: Long): Flow<List<PrincipleDetails>> =
        habitDao.getPrinciplesDetails(date).map { combinedPrinciples ->
            val principles = habitDao.getAllPrinciples().first()

            val missingPrincipleIds = principles
                .filterNot { combinedPrinciples.map { principleDate -> principleDate.principleId }.contains(it.principleId) }
                .map {
                    PrincipleDate(
                        principleId = it.principleId,
                        date = date,
                        value = false
                    )
                }
            missingPrincipleIds.forEach {habitDao.insertPrincipleDate(it) }

            combinedPrinciples
    }

    /*
        habitDao.getPrinciplesAndPrincipleDates(date).map { combinedPrinciples ->

            val principles = habitDao.getAllPrinciples().first()

            val principleDates = habitDao.getPrinciplesDatesByDate(date).first()

            val missingPrincipleIds = principles
                .filterNot { principleDates.map { principleDate -> principleDate.principleId }.contains(it.principleId) }
                .map {
                    PrincipleDate(
                        principleId = it.principleId,
                        date = date,
                        value = false
                    )
                }
            missingPrincipleIds.forEach {habitDao.insertPrincipleDate(it) }

            combinedPrinciples
    }

     */

    override suspend fun getPrinciplesAndPrincipleDatesWithoutCreating(date: Long): Flow<List<PrincipleDetails>> =
        habitDao.getPrinciplesDetails(date)

    override suspend fun createTestPrinciples(principleList: List<Principle>): Void
    = habitDao.createTestPrinciples(principleList)
    override suspend fun deleteAllPrinciples() = habitDao.deleteAllPrinciples()

    override suspend fun insertPrinciple(principle: Principle)
    = habitDao.insertPrinciple(principle)
    override suspend fun updatePrinciple(principle: Principle)
    = habitDao.updatePrinciple(principle)
    override suspend fun deletePrinciple(principle: Principle)
    = habitDao.deletePrinciple(principle)
    override suspend fun updatePrincipleOrigin(date: Long, principleId: Int)
    = habitDao.updatePrincipleOrigin(date, principleId)


    override fun getAllPrinciplesStream(): Flow<List<Principle>>
    = habitDao.getAllPrinciples()
    override fun getAllPrinciplesDatesStream(): Flow<List<PrincipleDate>>
    = habitDao.getAllPrinciplesDates()

    override fun getPrinciplesByDateStream(date: Long): Flow<List<PrincipleDate>>
    = habitDao.getPrinciplesDatesByDate(date)
    override fun getPrinciplesByDateRangeStream(dateStartInclusive: Long, dateEndInclusive: Long): Flow<List<PrincipleDate>>
    = habitDao.getPrinciplesByDateRange(dateStartInclusive, dateEndInclusive)

    override suspend fun insertPrincipleDate(principleDate: PrincipleDate)
    = habitDao.insertPrincipleDate(principleDate)
    override suspend fun updatePrincipleDate(date: Long, principleId: Int, value: Boolean)
    = habitDao.updatePrincipleDate(date, principleId, value)
    override suspend fun deletePrincipleDate(principleDate: PrincipleDate)
    = habitDao.deletePrincipleDate(principleDate)
}