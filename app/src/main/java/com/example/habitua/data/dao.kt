package com.example.habitua.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update

import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface HabitDao {

    @Insert
    suspend fun insert(habit: Habit)

    @Delete
    suspend fun delete(habit: Habit)

    @Update
    suspend fun updateHabit(habit: Habit)

    @Query("SELECT * FROM Habit WHERE id = :id")
    fun getHabitById(id: Int): Flow<Habit>

    @Query("SELECT * FROM Habit WHERE isActive = true")
    fun getAllActiveHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM Habit WHERE isAcquired = true")
    fun getAllAcquiredHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM Habit WHERE isAcquired = false")
    fun getCurrentHabits(): Flow<List<Habit>>

}

@Dao
interface StreakLogsDao {
    @Insert
    suspend fun insert(streakLogs: StreakLogs)

    @Query("UPDATE StreakLogs SET endDate = :endDate WHERE habitId = :habitId AND startDate = :startDate")
    suspend fun updateEndDate(habitId: Int, startDate: Long, endDate: Long)

    @Query("SELECT startDate FROM StreakLogs WHERE habitId = :habitId AND endDate IS NULL")
    fun getStreakStartDateByHabitId(habitId: Int): Long

    @Query("SELECT * FROM StreakLogs WHERE habitId = :habitId AND endDate IS NOT NULL")
    fun getStreakWithEndDatesByHabitId(habitId: Int): Flow<List<StreakLogs>>

    @Query("SELECT EXISTS(SELECT 1 FROM StreakLogs WHERE habitId = :habitId AND endDate IS NULL)")
    fun isStreakActive(habitId: Int): Boolean

}

@Dao
interface ConditionLogsDao {
    @Insert
    suspend fun insert(conditionLogs: ConditionLogs)

    @Query("Select * FROM  ConditionLogs WHERE habitId = :habitId")
    fun getAllConditionLogs(habitId: Int): Flow<List<ConditionLogs>>
}
