package com.example.habitua.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.temporal.ChronoUnit

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
    override suspend fun getPrinciplesAndPrincipleDates(date: Long): Flow<List<PrincipleDetails>> = flow {

        // gemini gave me this, I hate it <- it autocomplete this !
        // I don't hate you Gemini, <- It gave me a suggestion to pass it off to Kotlin !
        // I don't hate any of these, I'm just frustrated with myself

        val combinedPrinciples = habitDao.getPrinciplesDetails(date).first() // Get initial list
        val principles = habitDao.getAllPrinciples().first()

        val missingPrinciples = principles
            .filterNot { p -> combinedPrinciples.map { it.principleId }.contains(p.principleId) }
            .filterNot { p -> Instant.ofEpochMilli(p.dateCreated).isAfter(Instant.ofEpochMilli(date)) }
            .map {
                PrincipleDate(
                    principleId = it.principleId,
                    date = date,
                    value = false
                )
            }

        missingPrinciples.forEach { habitDao.insertPrincipleDate(it) } // Insert missing data

        emit(habitDao.getPrinciplesDetails(date).first()) // Emit the updated list
    }
    override fun countPrinciplesDetailsByDateCreated(date: Long): Flow<Int> =
        habitDao.countPrinciplesDetailsByDateCreated(date)


    override fun getPrinciplesDetailsByDateCreated(date: Long): Flow<List<Principle>> =
        habitDao.getPrinciplesDetailsByDateCreated(date)

    override suspend fun createTestPrinciples(principleList: List<Principle>): Void
    = habitDao.createTestPrinciples(principleList)
    override suspend fun deleteAllPrinciples() = habitDao.deleteAllPrinciples()
    override suspend fun deleteAllPrinciplesDates() = habitDao.deleteAllPrincipleDates()


    override suspend fun insertPrinciple(principle: Principle): Long
    = habitDao.insertPrinciple(principle)
    override suspend fun updatePrinciple(principle: Principle)
    = habitDao.updatePrinciple(principle)
    override suspend fun deletePrinciple(principle: Principle)
    = habitDao.deletePrinciple(principle)
    override suspend fun updatePrincipleOrigin(date: Long, principleId: Int)
    = habitDao.updatePrincipleOrigin(date, principleId)


    override fun getPrincipleStream(id: Int): Flow<Principle?>
    = habitDao.getPrinciple(id)
    override fun getAllPrinciplesStream(): Flow<List<Principle>>
    = habitDao.getAllPrinciples()
    override fun getAllPrinciplesDatesStream(): Flow<List<PrincipleDate>>
    = habitDao.getAllPrinciplesDates()
    override fun getPrinciplesByDateStream(date: Long): Flow<List<PrincipleDate>>
    = habitDao.getPrinciplesDatesByDate(date)
    override fun getPrinciplesByDateRangeStream(dateStart: Long, dateEnd: Long): Flow<List<PrincipleDate>>
    = habitDao.getPrinciplesByDateRange(dateStart, dateEnd)

    override suspend fun insertPrincipleDate(principleDate: PrincipleDate)
    = habitDao.insertPrincipleDate(principleDate)
    override suspend fun updatePrincipleDate(date: Long, principleId: Int)
    = habitDao.updatePrincipleDate(date, principleId)
    override suspend fun deletePrincipleDate(principleDate: PrincipleDate)
    = habitDao.deletePrincipleDate(principleDate)


    override suspend fun deletePrincipleById(principleId: Int) =
        habitDao.deletePrincipleById(principleId)

    override suspend fun deletePrincipleDateById(principleId: Int) =
        habitDao.deletePrincipleDateById(principleId)

}