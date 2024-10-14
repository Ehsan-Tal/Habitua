package com.example.habitua.data

import android.icu.util.Calendar
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.OnConflictStrategy
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

    // COUNT of get functions

    // NEW
    @Query("""
        SELECT COUNT(id)    
        FROM habits
        WHERE dateAcquired IS NOT NULL
        """)
    fun countAllHabitsAcquired(): Flow<Int>

    // NEW
    @Query("""
        SELECT COUNT(id)    
        FROM habits
        WHERE dateAcquired IS NULL
        """)
    fun countAllHabitsNotAcquired(): Flow<Int>

    // NEW
    @Query("""
        SELECT COUNT(id)    
        FROM habits
        WHERE dateAcquired IS NULL
        AND currentStreakOrigin IS NULL
        """)
    fun countAllHabitsNotStreaking(): Flow<Int>

    // NEW
    @Query("""
        SELECT COUNT(id)
        FROM habits
        WHERE dateAcquired IS NULL
        AND currentStreakOrigin IS NOT NULL
        AND (
            DATE(nextReviewedDate / 1000, 'unixepoch') = DATE(:dateToday / 1000, 'unixepoch') OR 
            DATE(nextReviewedDate / 1000, 'unixepoch') = DATE(:dateYesterday / 1000, 'unixepoch')
        )
        """)
    fun countAllHabitsTODO(dateToday: Long, dateYesterday: Long): Flow<Int>

    // NEW
    @Query("""
        SELECT COUNT(id)        
        FROM habits
        WHERE dateAcquired IS NULL
        AND currentStreakOrigin IS NOT NULL
        AND DATE(nextReviewedDate / 1000, 'unixepoch') = DATE(:dateYesterday / 1000, 'unixepoch')
        """)
    fun countAllHabitsAtRisk(dateYesterday: Long): Flow<Int>

    // TODO: this needs a history
    //    @Query("SELECT * from habits WHERE dateAcquired IS NULL AND currentStreakOrigin IS NOT NULL AND curr")
    //    fun getAllHabitsStreakingFromDateRange(dateToday:String, dateThen: String): Flow<List<Habit>>


    /**
     * Returns a Flow of all the habits in the database.
     * @return a Flow of a List of all Habit objects in the database.
     */
    @Query ("SELECT * from habits")
    fun getAllHabits(): Flow<List<Habit>>

    @Query ("SELECT COUNT(id) from habits")
    fun countAllHabits(): Flow<Int>

    /**
     * Returns a Flow of a single [Habit] object with the specified [habitId].
     * @param habitId Integer
     * @return Flow of a Habit object with the specified [habitId].
     */
    @Query("SELECT * from habits WHERE id = :habitId")
    fun getHabit(habitId: Int): Flow<Habit>

    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits(): Void

    @Insert
    suspend fun createTestHabits(habitList: List<Habit>): Void

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
            nextReviewedDate = :date,
            daysUntilAcquisition = :medianAcquisitionDays
         WHERE 
            dateAcquired IS NULL AND
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
        UPDATE 
            habits 
        SET 
            isActive = 0, 
            nextReviewedDate = nextReviewedDate + frequency * 24 * 60 * 60 * 1000
        WHERE 
            dateAcquired IS NULL AND 
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
		UPDATE 
            habits 
		SET 
            isActive = 0, 
            currentStreakOrigin = NULL, 
            nextReviewedDate = NULL, 
            daysUntilAcquisition = NUll 
		WHERE 
            dateAcquired is NULL AND 
            currentStreakOrigin IS NOT NULL AND 
            DATE(nextReviewedDate / 1000, 'unixepoch') != DATE(:date / 1000, 'unixepoch') AND 
            isActive = 0
    	"""
    )
    fun breakStreaks(date: Long)

    @Query(
        """
        UPDATE 
            habits
        SET 
            dateAcquired = :date, 
            isActive = 0, 
            currentStreakOrigin = NULL,  
            nextReviewedDate = NULL,  
            daysUntilAcquisition = NULL
        WHERE 
            currentStreakOrigin IS NOT NULL AND
            isActive = 1 AND
            DATE(daysUntilAcquisition / 1000, 'unixepoch') >= DATE(:date / 1000, 'unixepoch')
        """
    )
    fun checkAcquired(date: Long)

    @Transaction
    fun reviewHabits(dateToday: Long, dateYesterday: Long) {

        val instantObject = Instant.ofEpochMilli(dateToday)

        //TODO: UNMAGIC THE SIXTY SIX SOON
        val acquiredDateInMilli = instantObject.plus(66, ChronoUnit.DAYS).toEpochMilli()

        //val daily_habit_acquisition_date = date + 66
        // bi-daily and bi-weekly cannot be determined by research
        // so instead, just ask the user every Saturday if they acquired the habit
        // also offer them the option to manually mark it in the edit screen

        checkAcquired(dateToday)
        startStreaks(dateToday, acquiredDateInMilli)
        streakSatisfied(dateToday, dateYesterday)
        breakStreaks(dateToday)

    }


    // principles
    @Insert
    suspend fun insertPrinciple(principle: Principle)

    @Update
    suspend fun updatePrinciple(principle: Principle)

    @Delete
    suspend fun deletePrinciple(principle: Principle)

    @Insert
    suspend fun createTestPrinciples(principleList: List<Principle>): Void

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPrincipleDate(principleDate: PrincipleDate)


    @Query("""
        SELECT
            principles.principleId,
            principles.name,
            principles.description,
            COALESCE(principles_dates.date, :date) AS date,
            principles.dateCreated,
            principles.dateFirstActive,
            principles_dates.value
        FROM 
            principles_dates
        INNER JOIN 
            principles
        ON 
            principles.principleId = principles_dates.principleId
        WHERE
            DATE(principles_dates.date / 1000, 'unixepoch') = DATE(:date / 1000, 'unixepoch')
    """)
    fun getPrinciplesDetails(date: Long): Flow<List<PrincipleDetails>>


    @Query("""
        SELECT 
            *
        FROM
            principles
        WHERE
            DATE(principles.dateCreated / 1000, 'unixepoch') < DATE(:date/ 1000, 'unixepoch')
    """)
    fun getPrinciplesDetailsByDateCreated(date: Long): Flow<List<Principle>>


    // NEW
    @Query("""
        SELECT 
            COUNT(principles.principleId)    
        FROM
            principles
        WHERE
            DATE(principles.dateCreated / 1000, 'unixepoch') <= DATE(:date/ 1000, 'unixepoch')
        """)
    fun countPrinciplesDetailsByDateCreated(date: Long): Flow<Int>


    @Query ("DELETE FROM principles")
    suspend fun deleteAllPrinciples()

    @Query("""
        UPDATE 
            principles_dates 
        SET 
            value = CASE WHEN value = 0 THEN 1 ELSE 0 END 
        WHERE 
            principleId = :principleId
            AND DATE(date / 1000, 'unixepoch') = DATE(:date / 1000, 'unixepoch') 
    """)
    suspend fun updatePrincipleDate(date: Long, principleId: Int)

    @Query("""
        UPDATE 
            principles
        SET 
            dateFirstActive = :date
        WHERE 
            principleId = :principleId
    """)
    suspend fun updatePrincipleOrigin(date: Long, principleId: Int)

    @Delete
    suspend fun deletePrincipleDate(principleDate: PrincipleDate)

    @Query ("DELETE FROM principles_dates")
    suspend fun deleteAllPrincipleDates()


    @Query ("SELECT * FROM principles")
    fun getAllPrinciples(): Flow<List<Principle>>

    @Query ("SELECT * FROM principles_dates")
    fun getAllPrinciplesDates(): Flow<List<PrincipleDate>>

    @Query ("SELECT * FROM principles_dates " +
            "WHERE DATE(date / 1000, 'unixepoch') = DATE(:date / 1000, 'unixepoch')")
    fun getPrinciplesDatesByDate(date: Long): Flow<List<PrincipleDate>>

    @Query ("""
        SELECT * 
        FROM principles_dates 
        WHERE DATE(date / 1000, 'unixepoch')
            BETWEEN 
            DATE(:dateStartInclusive / 1000, 'unixepoch') AND
            DATE(:dateEndInclusive / 1000, 'unixepoch') 
        """)
    fun getPrinciplesByDateRange(dateStartInclusive: Long, dateEndInclusive: Long): Flow<List<PrincipleDate>>

}