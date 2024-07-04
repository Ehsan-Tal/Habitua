package com.example.habitua.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Update
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the Habit class. The HabitDao.
 * Controls CRUD operations on the habits table.
 */
@Dao
interface HabitDao {

    /**
     * Inserts a [Habit] object into the database.
     * @param habit [Habit] object to be inserted.
     */
    @Insert
    suspend fun insert(habit: Habit)

    /**
     * Deletes a [Habit] object from the database.
     * @param habit [Habit] object to be deleted.
     */
    @Delete
    suspend fun delete(habit: Habit)

    /**
     * Updates a [Habit] object in the database.
     * @param habit [Habit] object to be updated.
     */
    @Update
    suspend fun update(habit: Habit) // <- update habits, reference ?

    /**
     * Returns a Flow of all the habits in the database.
     * @param hasBeenAcquired
     * @return a Flow of a List of all Habit satisfying the specified [hasBeenAcquired] status.
     */
    @Query("SELECT * from habits WHERE hasBeenAcquired = :hasBeenAcquired")
    fun getAllHabitsByAcquired(hasBeenAcquired: Boolean): Flow<List<Habit>>

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

    //Flow as return returns notifications whenever data source changes.
    //Meaning you only need to explicitly get it once (with Room).
    // also, since suspend ensures it runs on a different thread
    // we do not need suspend as Room performs this in co-routine scope already -
    // due to the Flow return.

}