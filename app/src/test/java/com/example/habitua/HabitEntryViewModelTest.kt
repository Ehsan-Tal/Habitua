package com.example.habitua

import com.example.habitua.data.AppRepository
import com.example.habitua.ui.habit.HabitDetails
import com.example.habitua.ui.habit.HabitEntryViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class HabitEntryViewModelTest {

    private lateinit var viewModel: HabitEntryViewModel
    private lateinit var mockAppRepository: AppRepository

    @Before
    fun setup() {
        mockAppRepository = mock() // we need mockito
        viewModel = HabitEntryViewModel(mockAppRepository)
    }
    // we mock the app repo so we can test the view model


    @Test
    fun `updateUiState updates state correctly`() {
        val habitDetails = HabitDetails(
            name = "The habit name",
            description = "The habit description"
        )
        // this also checks whether the input is valid
        assertEquals(false, viewModel.habitUiState.isEntryValid)

         viewModel.updateUiState(habitDetails)

        assertEquals(habitDetails, viewModel.habitUiState.habitDetails)
        assertEquals(true, viewModel.habitUiState.isEntryValid)
    }

    // as this test showcases, we can simulate scope and lifecycles
    // and further more, we can simulate the pass of time !
    @Test
    fun `saveHabit saves habit`() =
        runTest {
            val habitDetails = HabitDetails(
                name = "The habit name",
                description = "The habit description"
            )
            viewModel.updateUiState(habitDetails)
            viewModel.saveHabit()

            verify(mockAppRepository).insertHabit(any())

        }

}