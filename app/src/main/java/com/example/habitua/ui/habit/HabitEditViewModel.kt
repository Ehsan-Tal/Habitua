package com.example.habitua.ui.habit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitua.data.AppRepository
import com.example.habitua.data.Habit
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * The View Model for the state of the Habit Editing Screen
 *
 * @param savedStateHandle - a [SavedStateHandle] responsible for persisting data between
 * process death between destinations (e.g., the ID argument the habit edit destination)
 * @param appRepository - allows access to the app database of habits
 */
class HabitEditViewModel (
    savedStateHandle: SavedStateHandle,
    private val appRepository: AppRepository
): ViewModel(){

    val habitId: Int =
        checkNotNull(savedStateHandle[HabitEditDestination.HABIT_ID_ARG])

    var habitUiState by mutableStateOf(HabitEditUiState())
        private set

    /**
     * collects the specific habit from the DB by habitId
     * filters out Null, collects the first from the flow, and converts it into the UiState
     */
    init {
        viewModelScope.launch {
            habitUiState = appRepository.getHabitStream(habitId)
                .filterNotNull()
                .first()
                .toHabitEditUiState(true)
        }
    }

    /**
     * function to save changes into the db
     */
    suspend fun updateHabit(){
        if (validateInput()) {
            appRepository.updateHabit(habitUiState.habit)
        }
    }

    /**
     * function to delete changes into the db
     */
    suspend fun deleteHabit(){
        appRepository.deleteHabit(habitUiState.habit)
    }

    /**
     * function to update the State not the DB
     */
    fun updateUiState(habit: Habit) {
        habitUiState =
            HabitEditUiState(habit = habit, isValid = validateInput())
    }

    /**
     * Validation to ensure the current habit in state does not have blank names or description
     */
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
