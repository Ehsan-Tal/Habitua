package com.example.habitua.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Update
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    // (onConflict = OnConflictStrategy.IGNORE)
    @Insert
    suspend fun insert(habit: Habit)

    @Delete
    suspend fun delete(habit: Habit)

    @Update
    suspend fun update(habit: Habit) // <- update habits, reference ?

    @Query("SELECT * from habits WHERE hasBeenAcquired = :hasBeenAcquired")
    fun getAllHabitsByAcquired(hasBeenAcquired: Boolean): Flow<List<Habit>>

    @Query ("SELECT * from habits")
    fun getAllHabits(): Flow<List<Habit>>

    @Query("SELECT * from habits WHERE id = :habitId")
    fun getHabit(habitId: Int): Flow<Habit>

    //Flow as return returns notifications whenever data source changes.
    //Meaning you only need to explicitly get it once (with Room).
    // also, since suspend ensures it runs on a different thread
    // we do not need suspend as Room performs this in co-routine scope already -
    // due to the Flow return.

}