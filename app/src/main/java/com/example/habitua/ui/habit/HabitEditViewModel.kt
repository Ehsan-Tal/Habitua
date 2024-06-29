package com.example.habitua.ui.habit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitua.data.AppRepository
import com.example.habitua.data.Habit
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HabitEditViewModel (
    savedStateHandle: SavedStateHandle,
    private val appRepository: AppRepository
): ViewModel(){

    private val habitId: Int =
        checkNotNull(savedStateHandle[HabitEditDestination.HABIT_ID_ARG])

    var habitUiState by mutableStateOf(HabitEditUiState())
        private set
    init {
        viewModelScope.launch {
            habitUiState = appRepository.getHabitStream(habitId)
                .filterNotNull()
                .first()
                .toHabitEditUiState(true)
        }
    }

    suspend fun updateHabit(){
        if (validateInput()) {
            appRepository.updateHabit(habitUiState.habit)
        }
    }

    suspend fun deleteHabit(){
        appRepository.deleteHabit(habitUiState.habit)
    }

    fun updateUiState(habit: Habit) {
        habitUiState =
            HabitEditUiState(habit = habit, isValid = validateInput())
    }

    private fun validateInput(): Boolean {
        return with(habitUiState.habit) {
            name.isNotBlank() && description.isNotBlank()
        }
    }
}

// habit details be in the entry

data class HabitEditUiState(
    val habit: Habit = Habit(name = "", description = ""),
    val isValid: Boolean = false
)

fun Habit.toHabitEditUiState(isValid: Boolean = false): HabitEditUiState = HabitEditUiState(
    habit = this,
    isValid = isValid
)
