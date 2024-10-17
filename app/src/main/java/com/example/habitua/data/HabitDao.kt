package com.example.habitua.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Update
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * The Data Access Object for the Habit class. The HabitDao.
 * Controls CRUD operations on the habits table.
 */
@Dao
interface HabitDao {

    @Insert
    suspend fun insert(habit: Habit): Long
    @Delete
    suspend fun delete(habit: Habit)
    @Update
    suspend fun update(habit: Habit)


    @Query ("SELECT * from habits")
    fun getAllHabits(): Flow<List<Habit>>
    @Query ("SELECT COUNT(habitId) from habits")
    fun countAllHabits(): Flow<Int>
    @Query("SELECT * from habits WHERE habitId = :habitId")
    fun getHabit(habitId: Int): Flow<Habit>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHabitDate(habitDate: HabitDate)
    @Delete
    suspend fun deleteHabitDate(habitDate: HabitDate)
    @Query("""
        UPDATE 
            habits_dates 
        SET 
            value = CASE WHEN value = 0 THEN 1 ELSE 0 END 
        WHERE 
            habitId = :habitId
            AND DATE(date / 1000, 'unixepoch') = DATE(:date / 1000, 'unixepoch') 
    """)
    suspend fun updateHabitDate(date: Long, habitId: Int)


    @Query("""
        SELECT
        *
        FROM
        habits_dates
    """)
    fun getAllHabitDates(): Flow<List<HabitDate>>
    @Query("""
        SELECT
        *
        FROM
        habits_dates
        WHERE
            DATE(habits_dates.date / 1000, 'unixepoch') = DATE(:date / 1000, 'unixepoch')
    """)
    fun getHabitsDatesByDate(date: Long): Flow<List<HabitDate>>
    @Query("""
        SELECT 
            *
        FROM
            habits
        WHERE
            DATE(habits.dateCreated / 1000, 'unixepoch') < DATE(:date/ 1000, 'unixepoch')""")
    fun getHabitsByDateCreated(date: Long): Flow<List<Habit>>
    @Query("""
        SELECT
            COUNT(habits.habitId)
        FROM 
            habits
        WHERE
        DATE(habits.dateCreated / 1000, 'unixepoch') <= DATE(:date/ 1000, 'unixepoch')""")
    fun countHabitsDetailsByDateCreated(date: Long): Flow<Int>


    @Query("""
        SELECT
            habits.habitId,
            habits.imageResId,
            habits.dateCreated,
            habits.name,
            habits.description,
            habits.complexity,
            habits.frequency,
            habits.isActive,
            habits.currentStreakOrigin,
            habits.nextReviewedDate,
            habits.dateAcquired,
            habits.daysUntilAcquisition,
            COALESCE(habits_dates.date, :date) AS date, 
            habits_dates.value
            FROM
            habits_dates
        INNER JOIN
            habits
        ON
            habits.habitId = habits_dates.habitId
        WHERE
            DATE(habits_dates.date / 1000, 'unixepoch') = DATE(:date / 1000, 'unixepoch')
        """)
    fun getHabitsDetails(date: Long): Flow<List<HabitDetails>>
//TODO: less just... do this manually

    @Query("""
        DELETE FROM habits_dates WHERE habitId = :id
    """)
    fun deleteHabitDateById(id: Int)
    @Query("""
        DELETE FROM habits WHERE habitId = :id
    """)
    fun deleteHabitById(id: Int)


    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits(): Void
    @Insert
    suspend fun createTestHabits(habitList: List<Habit>): Void


    // Review Logic
    @Query("""
         UPDATE habits 		
         SET isActive = 0,
            currentStreakOrigin = :date,
            nextReviewedDate = :date,
            daysUntilAcquisition = :medianAcquisitionDays
         WHERE 
            dateAcquired IS NULL AND
            currentStreakOrigin IS NULL AND 
            isActive = 1 
         """)
    fun startStreaks(date: Long, medianAcquisitionDays: Long)
    @Query( """
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
        """ )
    fun streakSatisfied(dateToday: Long, dateYesterday: Long)
    @Query( """
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
    	""" )
    fun breakStreaks(date: Long)
    @Query( """
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
        """ )
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
    suspend fun insertPrinciple(principle: Principle): Long
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
    @Query("""SELECT 
            *
        FROM
            principles
        WHERE
            DATE(principles.dateCreated / 1000, 'unixepoch') < DATE(:date/ 1000, 'unixepoch')""")
    fun getPrinciplesDetailsByDateCreated(date: Long): Flow<List<Principle>>
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


    @Query("SELECT * FROM principles WHERE principleId = :id")
    fun getPrinciple(id: Int): Flow<Principle>
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


    @Query("""
        DELETE FROM principles_dates WHERE principleId = :principleId
    """)
    fun deletePrincipleDateById(principleId: Int)
    @Query("""
        DELETE FROM principles WHERE principleId = :principleId
    """)
    fun deletePrincipleById(principleId: Int)

}