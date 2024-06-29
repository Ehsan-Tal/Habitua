package com.example.habitua

import androidx.lifecycle.SavedStateHandle
import com.example.habitua.data.AppRepository
import com.example.habitua.ui.habit.HabitEditViewModel
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

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

    // how would we test for deletion, updating ?
    // how would we test for habitId ?
    // how do we test anything over here ?

}