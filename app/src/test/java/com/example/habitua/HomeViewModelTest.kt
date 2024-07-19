package com.example.habitua

import android.util.Log
import com.example.habitua.data.AppRepository
import com.example.habitua.data.Habit
import com.example.habitua.data.UserPreferencesRepository
import com.example.habitua.ui.home.HomeViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {


    @Mock
    private lateinit var mockAppRepository: AppRepository

    @Mock
    private lateinit var mockUserPreferencesRepository: UserPreferencesRepository

    private lateinit var viewModel: HomeViewModel

    private val dateToday: String = SimpleDateFormat("yyyy-MM-dd").format(Date())

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = HomeViewModel(mockAppRepository, mockUserPreferencesRepository)
    }
    // HomeViewModel collects from the repository

    // there are a lot of private functions that we can't directly test
    // well, we can test their after effects by keeping track of data
    // and ensuring that, e.g., a habit of X streak days must be marked acquired
/*
    @Test
    fun `homeUiState emits habit list and reviewedToday true when habits exist and reviewed today`() = runTest {
        val testHabits = listOf(
            Habit(id = 1, name = "Habit 1", description = "Desc 1"),
            Habit(id = 2, name = "Habit 2", description = "Desc 2")
        )
        `when`(mockAppRepository.getAllHabitsByAcquiredStream(false)).thenReturn(flowOf(testHabits))
        `when`(mockUserPreferencesRepository.lastReviewedFlow).thenReturn(flowOf(dateToday))

        val uiState = viewModel.homeUiState.first()

        assertEqual(testHabits, uiState.habitList)
        assertEquals(true, uiState.reviewedToday)
    }

    @Test
    fun `homeUiState emits habit list and reviewedToday false when habits exist and reviewed is old`() = runTest {
        val testHabits = listOf(
            Habit(id = 1, name = "Habit 1", description = "Desc 1"),
            Habit(id = 2, name = "Habit 2", description = "Desc 2")
        )
        `when`(mockAppRepository.getAllHabitsByAcquiredStream(false)).thenReturn(flowOf(testHabits))
        `when`(mockUserPreferencesRepository.lastReviewedFlow).thenReturn(flowOf("2023-01-01"))

        val uiState = viewModel.homeUiState.first()

        assertEquals(testHabits, uiState.habitList)
        assertEquals(true, uiState.reviewedToday)
    }

 */

    @Test
    fun `reviewHabit refills missed opportunity for active habit without streak`() = runTest {
        val habit = Habit(
            id= 1,
            name = "Test Habit",
            description = "Test Description",
            isActive = true,
            hasMissedOpportunity = false,
            currentStreakOrigin = null
        )

        viewModel.reviewHabit(habit)

        //These verify that these functions are called with the correct arguments
        verify(mockAppRepository).updateHabit(
            habit.copy(
                hasMissedOpportunity = true,
            )
        )

        verify(mockAppRepository).updateHabit(
            habit.copy(
                currentStreakOrigin = dateToday
            )
        )
    }


    @Test
    fun `reviewHabit marks habit as acquired when conditions are met`() = runTest {
        val toAcquireHabit = Habit(
            id = 1,
            name = "Test Habit",
            description = "Test Description",
            isActive = true,
            hasMissedOpportunity = true,
            currentStreakOrigin = "2023-12-01" // Simulate an existing streak
        )

        viewModel.reviewHabit(toAcquireHabit)

        //These verify that these functions are called with the correct arguments
        verify(mockAppRepository).updateHabit(
            toAcquireHabit.copy(
                hasBeenAcquired = true,
            )
        )

    }

    @Test
    fun `reviewHabit does not mark habit as acquired when conditions are not met`() = runTest {
        val daysTillAcquired = 65L

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val habitShortDayNotYetAcquiredDate = LocalDate.parse(dateToday, formatter)
            .minusDays(daysTillAcquired)
            .format(formatter)

        val notAcquiredHabit = Habit(
            id = 1,
            name = "Test Habit",
            description = "Test Description",
            isActive = true,
            hasMissedOpportunity = true,
            currentStreakOrigin = habitShortDayNotYetAcquiredDate
        )

        viewModel.reviewHabit(notAcquiredHabit)

        //These verify that these functions are called with the correct arguments
        verify(mockAppRepository).updateHabit(
            notAcquiredHabit.copy(
                isActive = false,
                hasBeenAcquired = false,
            )
        )
    }

    @Test
    fun `reviewHabit not active breaks streak when conditions are met`() = runTest {
        val breakStreakHabit = Habit(
            id = 1,
            name = "Test Habit",
            description = "Test Description",
            isActive = false,
            hasMissedOpportunity = false,
            currentStreakOrigin = dateToday
        )

        viewModel.reviewHabit(breakStreakHabit)

        //These verify that these functions are called with the correct arguments
        verify(mockAppRepository).updateHabit(
            breakStreakHabit.copy(
                hasMissedOpportunity = true,
                currentStreakOrigin = null
            )
        )
    }

    @Test
    fun `reviewHabit not active spends missed opportunity`() = runTest {
        val spendMissingOpportunityHabit = Habit(
            id = 1,
            name = "Test Habit",
            description = "Test Description",
            isActive = false,
            hasMissedOpportunity = true,
            currentStreakOrigin = dateToday
        )

        viewModel.reviewHabit(spendMissingOpportunityHabit)

        //These verify that these functions are called with the correct arguments
        verify(mockAppRepository).updateHabit(
            spendMissingOpportunityHabit.copy(
                hasMissedOpportunity = false,
            )
        )
    }

    @Test
    fun `toggleHabitActive toggles isActive to !isActive`() = runTest {
        val falseHabit = Habit(id = 1, name = "False ja", description = "Inactive", isActive = false)
        val trueHabit = Habit(id = 1, name = "True ja", description = "Active", isActive = true)

        viewModel.toggleHabitActive(trueHabit)
        verify(mockAppRepository).updateHabit(trueHabit.copy(isActive = false))

        viewModel.toggleHabitActive(falseHabit)
        verify(mockAppRepository).updateHabit(falseHabit.copy(isActive = true))
    }


    @Test
    fun `checkIfHabitIsAcquired returns true when time is enough`() = runTest {
        val longStreakHabit = Habit(
            id = 1,
            name = "Test Habit",
            description = "Test Description",
            currentStreakOrigin = "2023-12-01",
        )
        val daysTillAcquired = 66L

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val habitShortDayNotYetAcquiredDate = LocalDate.parse(dateToday, formatter)
            .minusDays(daysTillAcquired - 2L)

        val shortStreakHabit = Habit(
            id = 1,
            name = "Test Habit",
            description = "Test Description",
            currentStreakOrigin = habitShortDayNotYetAcquiredDate.format(formatter),
        )

        assertEquals(viewModel.checkIfHabitIsAcquired(longStreakHabit), true)
        assertEquals(viewModel.checkIfHabitIsAcquired(shortStreakHabit), false)

    }

}