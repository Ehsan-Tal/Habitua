package com.example.habitua.ui.habit

import com.example.habitua.data.AppRepository
import androidx.lifecycle.ViewModel
import com.example.habitua.data.Habit
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * View Model to keep a reference to the app repository and an up-to-date list of all habits.
 *
 * Manages the UI state for habit creation
 *
 * @param appRepository the data source this ViewModel will fetch habits from.
 */
private const val TAG = "HabitEntryViewModel"
class HabitEntryViewModel(
    private val appRepository: AppRepository
): ViewModel() {

    /**
     * Stream of habits
     */
    var habitUiState by mutableStateOf(HabitUiState())
        private set

    /**
     * Updates the UI state with the provided [HabitDetails] and validates input
     *
     * @param habitDetails the new habit's details
     */
    fun updateUiState(habitDetails: HabitDetails) {
        habitUiState = HabitUiState(
            habitDetails = habitDetails,
            isEntryValid = validateInput(habitDetails)
        )
    }

    /**
     * Validates the user input - ensures name and description aren't blank
     */
    private fun validateInput(uiState: HabitDetails = habitUiState.habitDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && description.isNotBlank()
        }
    }

    /**
     * Inserts the new habit into the database
     */
    suspend fun saveHabit(){
        if (validateInput()) {
            appRepository.insertHabit((habitUiState.habitDetails.toHabit()))
        }
    }

}


// this is for the UI State, not for any specific data point
/**
 * Represents the UI state for an entry form.
 */
data class HabitUiState(
    val habitDetails: HabitDetails = HabitDetails(),
    val isEntryValid: Boolean = false
)

/**
 * Represents the user input for creating a new habit.
 */
data class HabitDetails(
    val name: String = "",
    val description: String = "",

)

fun HabitDetails.toHabit(): Habit = Habit(
    name = name,
    description = description,
)
/*
fun Habit.toHabitEntryUiState(isEntryValid: Boolean = false): HabitUiState = HabitUiState(
    habitDetails = this.toHabitDetails(),
    isEntryValid = isEntryValid
)

fun Habit.toHabitDetails(): HabitDetails = HabitDetails(
    name = name,
    description = description,
)
*/

/**
 * Formats the date for display
 */
fun Habit.formattedOriginDate(): String {

    var formattedDate: String

    if (currentStreakOrigin != null) {
        val date = Date(currentStreakOrigin!!)
        val format = SimpleDateFormat(
            "dd MMMM yyyy", Locale.getDefault()
        )
        formattedDate = format.format(date)
    } else {
        formattedDate = ""
    }

    return formattedDate
}