

import com.example.habitua.data.Habit
import com.example.habitua.data.HabitDao
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class HabitDaoTest {

    @Mock
    private lateinit var habitDao: HabitDao

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)}

    @Test
    fun testInsertHabit() {
        val habit = Habit(id =1, name="Test Habit", description="Description", hasBeenAcquired= false)
        runBlocking {
            habitDao.insert(habit)
            verify(habitDao).insert(habit)
        }
    }

    @Test
    fun testDeleteHabit() {
        val habit = Habit(id =1, name="Test Habit", description="Description", hasBeenAcquired= false)
        runBlocking {
            habitDao.delete(habit)
            verify(habitDao).delete(habit)
        }
    }

    @Test
    fun testUpdateHabit() {
        val habit = Habit(id =1, name="Test Habit", description="Description", hasBeenAcquired= false)
        runBlocking {
            habitDao.update(habit)
            verify(habitDao).update(habit)
        }
    }

    @Test
    fun testGetAllHabitsByAcquired() {
        val testHabits = listOf(
            Habit(id = 1, name = "Habit 1", description = "Description 1", hasBeenAcquired = false),
            Habit(id = 2, name = "Habit 2", description = "Description 2", hasBeenAcquired = false)
        )
        `when`(habitDao.getAllHabitsByAcquired(false)).thenReturn(flowOf(testHabits))

        runBlocking {
            val result = habitDao.getAllHabitsByAcquired(false)
            assertEquals(testHabits, result)
        }
    }

    // Similar tests for getAllHabits() and getHabit()
}