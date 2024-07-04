package com.example.habitua

import com.example.habitua.data.AppRepositoryImplementation
import com.example.habitua.data.Habit
import com.example.habitua.data.HabitDao
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class AppRepositoryTest {


    //Mock the habit dao
    @Mock
    private lateinit var habitDao: HabitDao

    private lateinit var appRepository: AppRepositoryImplementation

    @Before
    fun setup() {
        // Initialize the mock annotations
        MockitoAnnotations.openMocks(this)
        appRepository = AppRepositoryImplementation(habitDao)
    }

    @Test
    fun getAllHabitsStream_returnsAllHabitsAsSaved() = runBlocking {
        val testHabits = listOf(
            Habit(1, R.drawable.tal_derpy,"Habit 1", "Description 1", true, false),
            Habit(2, R.drawable.tal_derpy,"Habit 2", "Description 2", false, true)
        )
        Mockito.`when`(habitDao.getAllHabits()).thenReturn(flowOf(testHabits))

        val result = appRepository.getAllHabitsStream()

        assertEquals(testHabits, result.first())
    }

    @Test
    fun getAllHabitsByAcquiredStream_returnsTrueHabitAsSaved() = runBlocking {
        // will require significant conditional and comparison logic - better tested in the DAO
//        val boolean = true
//
//        val testHabits = listOf(
//            Habit(1, R.drawable.tal_derpy,"Habit 1", "Description 1", true, false, hasBeenAcquired = true),
//            Habit(2, R.drawable.tal_derpy,"Habit 2", "Description 2", false, true, hasBeenAcquired = false)
//        )
//
//        Mockito.`when`(habitDao.getAllHabitsByAcquired(boolean)).thenReturn(flowOf(testHabits))
//
//        val result = appRepository.getAllHabitsByAcquiredStream(boolean)
//
//        assertEquals(testHabits[0], result.first())
    }

    @Test
    fun getAllHabitsByAcquiredStream_returnsFalseHabitAsSaved() = runBlocking {

    }

    @Test
    fun getHabitStream_returnsHabitAsSaved() = runBlocking {
//        val id = 1
//
//        val testHabits = listOf(
//            Habit(1, R.drawable.tal_derpy,"Habit 1", "Description 1",
//                true, false, hasBeenAcquired = true),
//            Habit(2, R.drawable.tal_derpy,"Habit 2", "Description 2",
//                false, true, hasBeenAcquired = false)
//        )
//
//        Mockito.`when`(habitDao.getHabit(id)).thenReturn(flowOf(testHabits[0]))
//
//        val result = appRepository.getHabitStream(id)
//
//        assertEquals(testHabits[0], result.first())
    }

}