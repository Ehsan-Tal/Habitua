package com.example.habitua

import com.example.habitua.data.AppRepository
import com.example.habitua.data.Habit
import com.example.habitua.ui.visual.VisualizationViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

class VisualizationViewModelTest {

    private lateinit var viewModel: VisualizationViewModel
    private lateinit var mockAppRepository: AppRepository

    @Before
    fun setup() {
        mockAppRepository = mock()
        viewModel = VisualizationViewModel(mockAppRepository)
    }

    @Test
    fun `habitVizUiState empty results in zero`() = runTest {
        `when`(mockAppRepository.getAllHabitsStream())
            .thenReturn(flowOf(emptyList()))

        val uiState = viewModel.habitVizUiState.value

        assertEquals(emptyList<Habit>(), uiState.acquiredHabitList)
        assertEquals(0, uiState.totalSize)
        assertEquals(0, uiState.portionAcquired)
        assertEquals(0, uiState.portionInStreaks)
        assertEquals(0f, uiState.perCentAcquired)
        assertEquals(0f, uiState.perCentInStreak)
    }

    @Test
    fun `habitVizUiState proper results`() = runTest {
        val testHabits = listOf(
            Habit(
                id = 1,
                name = "Habit 1",
                description = "1st desc",
                hasBeenAcquired = true,
                currentStreakOrigin = "2023-01-01"),
            Habit(
                id = 2,
                name = "Habit 2",
                description = "2nd desc",
                hasBeenAcquired = false,
                currentStreakOrigin = "2024-01-01"),
            Habit(
                id = 3,
                name = "Habit 3",
                description = "3rd desc",
                hasBeenAcquired = false,
                currentStreakOrigin = null),
            Habit(
                id = 2,
                name = "Habit 4",
                description = "4th desc",
                hasBeenAcquired = false,
                currentStreakOrigin = "2024-02-01"),
            Habit(
                id = 3,
                name = "Habit 5",
                description = "5th desc",
                hasBeenAcquired = false,
                currentStreakOrigin = "2024-03-01")
        )

        `when`(mockAppRepository.getAllHabitsStream())
            .thenReturn(flowOf(testHabits))

        val uiState = viewModel.habitVizUiState.first()

        /**
        assertEquals(testHabits, uiState.habitList)
        assertEquals(5, uiState.totalSize)
        assertEquals(1, uiState.portionAcquired)
        assertEquals(3, uiState.portionInStreaks)

        // 0.1f is the delta value to account for rounding errors
        assertEquals(20f, uiState.perCentAcquired, 0.01f)
        assertEquals(60f, uiState.perCentInStreak, 0.01f)
         */
        //TODO: Fix this test, needs to change mockAppRepo and have it alter the HabitVizUiState
    }

}