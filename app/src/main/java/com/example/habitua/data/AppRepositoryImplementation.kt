package com.example.habitua.data

import kotlinx.coroutines.flow.Flow

/**
 * Implementation of [AppRepository] interface - provides habit data to the app
 *
 * Interacts with [HabitDao] to perform & control CRUD operations on data.
 */
class AppRepositoryImplementation(
    private val habitDao: HabitDao
    ):  AppRepository {

    /**
     * Returns a Flow of all the habits in the database by whether or not they have been acquired.
     *
     * @param hasBeenAcquired
     * @return a Flow emitting a List of [Habit] objects that match the
     * specified [hasBeenAcquired] status.
     */
    override fun getAllHabitsByAcquiredStream(hasBeenAcquired: Boolean): Flow<List<Habit>> =
        habitDao.getAllHabitsByAcquired(hasBeenAcquired)

    /**
     * Returns a Flow of all the habits in the database.
     *
     * @return a Flow emitting a List of [Habit] objects.
     */
    override fun getAllHabitsStream(): Flow<List<Habit>> = habitDao.getAllHabits()

    /**
     * Returns a Flow of a single [Habit] object with the specified [habitId].
     *
     * @param habitId
     * @return a Flow emitting a [Habit] object with the specified [habitId].
     */
    override fun getHabitStream(habitId: Int): Flow<Habit?> =
        habitDao.getHabit(habitId)

    /**
     * Inserts a [Habit] object into the database.
     *
     * @param habit [Habit] object to be inserted.
     */
    override suspend fun insertHabit(habit: Habit) = habitDao.insert(habit)

    /**
     * Deletes a [Habit] object from the database.
     *
     * @param habit [Habit] object to be deleted.
     */
    override suspend fun deleteHabit(habit: Habit) = habitDao.delete(habit)

    /**
     * Updates a [Habit] object in the database.
     *
     * @param habit [Habit] object to be updated.x
     *
     */
    override suspend fun updateHabit(habit: Habit) = habitDao.update(habit)

}