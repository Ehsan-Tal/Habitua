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
class HabitEntryViewModel(
    private val appRepository: AppRepository
): ViewModel() {

    var habitUiState by mutableStateOf(HabitUiState())
        private set

    fun updateUiState(habitDetails: HabitDetails) {
        habitUiState = HabitUiState(
            habitDetails = habitDetails,
            isEntryValid = validateInput(habitDetails)
        )
    }

    private fun validateInput(uiState: HabitDetails = habitUiState.habitDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && description.isNotBlank()
        }
    }

    suspend fun saveHabit(){
        if (validateInput()) {
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