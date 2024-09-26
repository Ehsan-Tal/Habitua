package com.example.habitua.data

import android.icu.util.Calendar
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Update
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * The Data Access Object for the Habit class. The HabitDao.
 * Controls CRUD operations on the habits table.
 */
@Dao
interface HabitDao {

    @Insert
    suspend fun insert(habit: Habit)

    @Delete
    suspend fun delete(habit: Habit)


    @Update
    suspend fun update(habit: Habit) // <- update habits, reference ?

    // NEW
    @Query("""
        SELECT *
        FROM habits
        WHERE dateAcquired IS NOT NULL
        """)
    fun getAllHabitsAcquired(): Flow<List<Habit>>

    // NEW
    @Query("""
        SELECT *
        FROM habits
        WHERE dateAcquired IS NULL
        """)
    fun getAllHabitsNotAcquired(): Flow<List<Habit>>


    // NEW
    @Query("""
        SELECT *
        FROM habits
        WHERE dateAcquired IS NULL
        AND  currentStreakOrigin IS NOT NULL
        """)
    fun getAllHabitsStreaking(): Flow<List<Habit>>

    // NEW
    @Query("""
        SELECT *
        FROM habits
        WHERE dateAcquired IS NULL
        AND currentStreakOrigin IS NULL
        """)
    fun getAllHabitsNotStreaking(): Flow<List<Habit>>

    // NEW
    @Query("""
        SELECT *
        FROM habits
        WHERE dateAcquired IS NULL
        AND currentStreakOrigin IS NOT NULL
        AND (
            DATE(nextReviewedDate / 1000, 'unixepoch') != DATE(:dateToday / 1000, 'unixepoch') OR 
            DATE(nextReviewedDate / 1000, 'unixepoch') != DATE(:dateYesterday / 1000, 'unixepoch')
        )
        """)
    fun getAllHabitsTODO(dateToday: Long, dateYesterday: Long): Flow<List<Habit>>

    // NEW
    @Query("""
        SELECT *
        FROM habits
        WHERE dateAcquired IS NULL
        AND currentStreakOrigin IS NOT NULL
        AND DATE(nextReviewedDate / 1000, 'unixepoch') != DATE(:dateYesterday / 1000, 'unixepoch')
        """)
    fun getAllHabitsAtRisk(dateYesterday: Long): Flow<List<Habit>>


       // TODO: this needs a history
    //    @Query("SELECT * from habits WHERE dateAcquired IS NULL AND currentStreakOrigin IS NOT NULL AND curr")
    //    fun getAllHabitsStreakingFromDateRange(dateToday:String, dateThen: String): Flow<List<Habit>>


    /**
     * Returns a Flow of all the habits in the database.
     * @return a Flow of a List of all Habit objects in the database.
     */
    @Query ("SELECT * from habits")
    fun getAllHabits(): Flow<List<Habit>>

    /**
     * Returns a Flow of a single [Habit] object with the specified [habitId].
     * @param habitId Integer
     * @return Flow of a Habit object with the specified [habitId].
     */
    @Query("SELECT * from habits WHERE id = :habitId")
    fun getHabit(habitId: Int): Flow<Habit>

    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits(): Void

    //Flow as return returns notifications whenever data source changes.
    //Meaning you only need to explicitly get it once (with Room).
    // also, since suspend ensures it runs on a different thread
    // we do not need suspend as Room performs this in co-routine scope already -
    // due to the Flow return.

    // Review Logic
    @Query(
        """
         UPDATE habits 		
         SET isActive = 0,
            currentStreakOrigin = :date,
            nextReviewedDate = :date 
         WHERE 
            DATE(daysUntilAcquisition  / 1000, 'unixepoch') = DATE(:medianAcquisitionDays / 1000, 'unixepoch')  IS NULL AND 
            currentStreakOrigin IS NULL AND 
            isActive = 1 
         """
    )
    fun startStreaks(date: Long, medianAcquisitionDays: Long)

    /**
     * frequency is a long that represents days to add for the next date for the habit
     * we have to convert them into milliseconds and add that product to the date
     *
     * In the where clause - we only care about the date portion and thus we use DATE.
     * Before that, we convert away the milliseconds
     * Listen, I don't like refactoring.
     */
    @Query(
        """
            UPDATE habits 
            SET isActive = 0, 
                nextReviewedDate = nextReviewedDate + frequency * 24 * 60 * 60 * 1000
            WHERE dateAcquired IS NULL AND 
                currentStreakOrigin IS NOT NULL AND 
                isActive = 1 AND 
                ( 
            DATE(nextReviewedDate / 1000, 'unixepoch') != DATE(:dateToday / 1000, 'unixepoch') OR 
            DATE(nextReviewedDate / 1000, 'unixepoch') != DATE(:dateYesterday / 1000, 'unixepoch') 
                )
            """
    )
    fun streakSatisfied(dateToday: Long, dateYesterday: Long)

    @Query(
        """
		UPDATE habits 
		SET isActive = 0, currentStreakOrigin = NULL, nextReviewedDate = NULL, daysUntilAcquisition = NUll 
		WHERE dateAcquired is NULL AND 
            currentStreakOrigin IS NOT NULL AND 
            DATE(nextReviewedDate / 1000, 'unixepoch') != DATE(:date / 1000, 'unixepoch') AND 
            isActive = 0
    	"""
    )
    fun breakStreaks(date: Long)

    @Transaction
    fun reviewHabits(dateToday: Long, dateYesterday: Long) {

        val instantObject = Instant.ofEpochMilli(dateToday)

        //TODO: UNMAGIC THE SIXTY SIX SOON
        val acquiredDateInMilli = instantObject.plus(66, ChronoUnit.DAYS).toEpochMilli()

        //val daily_habit_acquisition_date = date + 66
        // bi-daily and bi-weekly cannot be determined by research
        // so instead, just ask the user every Saturday if they acquired the habit
        // also offer them the option to manually mark it in the edit screen

        startStreaks(dateToday, acquiredDateInMilli)
        streakSatisfied(dateToday, dateYesterday)
        breakStreaks(dateToday)

    }


    // principles

    @Insert
    suspend fun insertPrinciple(principle: Principle)

    @Insert
    suspend fun insertPrincipleDate(principleDate: PrincipleDate)

    //TODO RE-ADD THE FOREIGN KEY CASCADING
    @Delete
    suspend fun deletePrinciple(principle: Principle)

    @Delete
    suspend fun deletePrincipleDate(principleDate: PrincipleDate)

    @Query("DELETE FROM principles")
    suspend fun deleteAllPrinciples()


    @Query("SELECT * FROM principles")
    fun getAllPrinciples(): Flow<List<Principle>>

    @Query("SELECT * FROM principles_dates")
    fun getAllPrinciplesDates(): Flow<List<PrincipleDate>>

    @Query("SELECT * FROM principles_dates WHERE DATE(date / 1000, 'unixepoch') = DATE(:date / 1000, 'unixepoch')")
    fun getPrinciplesByDate(date: Long): Flow<List<PrincipleDate>>

    @Query("""
        SELECT * 
        FROM principles_dates 
        WHERE DATE(date / 1000, 'unixepoch')
            BETWEEN 
            DATE(:dateStartInclusive / 1000, 'unixepoch') AND
            DATE(:dateEndInclusive / 1000, 'unixepoch') 
        """)
    fun getPrinciplesByDateRange(dateStartInclusive: Long, dateEndInclusive: Long): Flow<List<PrincipleDate>>

    @Query("""
        INSERT INTO principles_dates 
            (principleId, date, value) 
        VALUES (:principleId, :date, :value)
        """)
    suspend fun insertPrincipleDateEntry(principleId: Int, date: Long, value: Boolean)



}