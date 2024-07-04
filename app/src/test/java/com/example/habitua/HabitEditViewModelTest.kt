package com.example.habitua

import androidx.lifecycle.SavedStateHandle
import com.example.habitua.data.AppRepository
import com.example.habitua.data.Habit
import com.example.habitua.ui.habit.HabitEditDestination
import com.example.habitua.ui.habit.HabitEditViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class HabitEditViewModelTest {
    private lateinit var viewModel: HabitEditViewModel
    private lateinit var mockSavedStateHandle: SavedStateHandle
    private lateinit var mockAppRepository: AppRepository

    @Before
    fun setup(){
        mockSavedStateHandle = mock()
        mockAppRepository = mock()

        viewModel = HabitEditViewModel(mockSavedStateHandle, mockAppRepository)
    }

    // how would we test for habitId ?
    @Test
    fun `habitId is retrieved from SavedStateHandle`() = runTest {
        val testHabitId = 9

        // This is interesting
        whenever(mockSavedStateHandle
            .get<Int>(HabitEditDestination.HABIT_ID_ARG)
        )
            .thenReturn(testHabitId)

        val retrievedHabitId = viewModel.habitId

        assertEquals(testHabitId, retrievedHabitId)
    }

    // How can we test for the init ?
    @Test
    fun `init block fetches and updates habitUiState`() = runTest {
        val habitId = 1
        val testHabit = Habit(id = habitId, name = "Test Habit", description = "Sloppy Mega Hz")
        whenever(mockAppRepository.getHabitStream(habitId)).thenReturn(flowOf(testHabit))

        // Initialize the ViewModel (triggering the init block)
        viewModel = HabitEditViewModel(mockSavedStateHandle, mockAppRepository)

        assertEquals(viewModel.habitUiState.habit,testHabit)
    }

    // testing delete habits would need a `when` as it calls the app Repo
    @Test
    fun `deleteHabit calls repository to delete habit`() = runTest {

        viewModel.updateUiState(
            habit = Habit(id = 33, name = "Test Habit", description = "Chris Chan")
        )

        viewModel.deleteHabit()

        // this tries to verify that the app repo function is called
        // when we call viewModel delete Habit - apparently
        verify(mockAppRepository)
            .deleteHabit(viewModel.habitUiState.habit)
    }

    // check the updateUiState validity


}