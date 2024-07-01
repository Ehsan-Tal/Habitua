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


private const val TAG = "HabitEntryViewModel"

/**
 * The View Model for the Habit Entry screen
 *
 * @param appRepository is the repo relevant to Room and the Database
 */
class HabitEntryViewModel(
    private val appRepository: AppRepository
): ViewModel() {

    var habitUiState by mutableStateOf(HabitUiState())
        private set

    /**
     * Updates state based on entry details
     *
     * this function also calls validateInput and passes in habitDetails to ensure it
     *
     * @param habitDetails - the details gathered from entry
     */
    fun updateUiState(habitDetails: HabitDetails) {
        habitUiState = HabitUiState(
            habitDetails = habitDetails,
            isEntryValid = validateInput(habitDetails)
        )
    }

    /**
     * Validates input based on given entry details
     *
     * this function ensures name and description are not blank
     * //TODO: add length-based validation - we'd need to share that with HabitEdit.
     *
     * @param habitDetails - details gathered from entry
     * @return true if it passes validation, false otherwise
     */
    private fun validateInput(habitDetails: HabitDetails): Boolean {
        return with(habitDetails) {
            name.isNotBlank() && description.isNotBlank()
        }
    }

    /**
     * Saves Habit to the DB using the appRepository
     *
     * Translates to Habit the habitDetails of habitUiState -
     * only after that value has passed validation
     *
     */
    suspend fun saveHabit(){
        if (validateInput(habitUiState.habitDetails)) {
            appRepository.insertHabit((habitUiState.habitDetails.toHabit()))
        }
    }

}


// this is for the UI State, not for any specific data point
data class HabitUiState(
    val habitDetails: HabitDetails = HabitDetails(),
    val isEntryValid: Boolean = false
)

// also store counts of various categories of habits
// maybe this does not need to have as many fields because this is the
// entry field for habit cards
//TODO: How do we save the habits ?
data class HabitDetails(
    val name: String = "",
    val description: String = "",

)
/*

    @DrawableRes val imageResId: Int = R.drawable.tal_derpy,
    val name: String = "",
    val description: String = "",
    val isActive: Boolean = false,
    val hasMissedOpportunity: Boolean = true,
    val hasBeenAcquired: Boolean = false,
    val currentStreakOrigin: Long? = null,
* */
// now how do we avoid lossy details ?
// by using other items of course, right now, we can afford to lose them.

// now to convert them to their convertables

fun HabitDetails.toHabit(): Habit = Habit(
    name = name,
    description = description,
)

// fun Habit.formattedDate(): String {return }

// I'm not sure what their default values would - how would they be invalid ?
// now, would we need to convert items into things ? probably the date


fun Habit.toHabitEntryUiState(isEntryValid: Boolean = false): HabitUiState = HabitUiState(
    habitDetails = this.toHabitDetails(),
    isEntryValid = isEntryValid
)

fun Habit.toHabitDetails(): HabitDetails = HabitDetails(
    name = name,
    description = description,
)

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