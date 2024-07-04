package com.example.habitua

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.habitua.data.Habit
import com.example.habitua.data.HabitDao
import com.example.habitua.data.HabitDatabase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HabitDaoTest {

    private lateinit var database: HabitDatabase
    private lateinit var habitDao: HabitDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            HabitDatabase::class.java
        ).allowMainThreadQueries() // Allow queries on main thread for testing
            .build()
        habitDao = database.habitDao()
    }

    @Test
    fun insertHabit_andGetAllHabits() = runBlocking {
        val habit = Habit(1, R.drawable.tal_derpy, "Drink Water", "Stay hydrated", true, false, false)
        habitDao.insert(habit)

        val allHabits = habitDao.getAllHabits().first()
        assertEquals(1, allHabits.size)
        assertEquals(habit, allHabits[0])
    }

    // inserts, then deletes - then compares the size of the list which should be 0
    @Test
    fun deleteHabit_andGetAllHabits() = runBlocking {
        val habit = Habit(1, R.drawable.tal_derpy, "Drink Water", "Stay hydrated", true, false, false)
        habitDao.insert(habit)
        habitDao.delete(habit)

        val allHabits = habitDao.getAllHabits().first()
        assertEquals(0, allHabits.size)
    }

    @Test
    fun updateHabit_andGetAllHabits() = runBlocking {
        val namedUpdate = "AI Water Mark Removal"
        val habit = Habit(1, R.drawable.tal_derpy, "Drink Water", "Stay hydrated", true, false, false)

        habitDao.insert(habit)
        habitDao.update(habit.copy(name = namedUpdate))

        val allHabits = habitDao.getAllHabits().first()
        assertEquals(namedUpdate, allHabits[0].name)
    }

    @Test
    fun getHabit_andGetAllHabits() = runBlocking {
        val id = 1
        val habit = Habit(1, R.drawable.tal_derpy, "Drink Water", "Stay hydrated", true, false, false)
        habitDao.insert(habit)

        val allHabits = habitDao.getHabit(id).first()
        assertEquals(habit, allHabits)
    }
}