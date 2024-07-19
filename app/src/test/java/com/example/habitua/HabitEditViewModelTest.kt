package com.example.habitua

import android.util.Log
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
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class HabitEditViewModelTest {
    private lateinit var viewModel: HabitEditViewModel
    private lateinit var mockSavedStateHandle: SavedStateHandle
    private lateinit var mockAppRepository: AppRepository
    val habitId = 9

    @Before
    fun setup(){
        mockSavedStateHandle = mock()
        mockAppRepository = mock()

        viewModel = HabitEditViewModel(mockSavedStateHandle, mockAppRepository)
    }

}