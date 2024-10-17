package com.example.habitua.ui.habit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitua.data.AppRepository
import com.example.habitua.data.Habit
import com.example.habitua.ui.backgroundDrawables
import com.example.habitua.ui.navigation.HabitEditDestination
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class HabitEditViewModel (
    savedStateHandle: SavedStateHandle,
    private val appRepository: AppRepository
): ViewModel(){

    var backgroundAccessorIndex =
        Random.nextInt(backgroundDrawables.size)
    val habitId: Int =
        checkNotNull(savedStateHandle[HabitEditDestination.ID_ARG])
    var uiState by mutableStateOf(HabitEditUiState())
        private set

    init {
        viewModelScope.launch {
            uiState = appRepository.getHabitStream(habitId)
                .filterNotNull()
                .first()
                .toHabitEditUiState(true)
        } //because we have the translate function, we can get the habit and do so
    }

    fun updateUiState(habit: Habit){
        uiState = HabitEditUiState(
            habit = habit,
            isEntryValid = validateInput(),
            hasThereBeenChanges = true
        )
    }
    suspend fun deleteHabit() {
        appRepository.deleteHabit(uiState.habit)
    }
    suspend fun saveHabit() {
        if (validateInput()){
            appRepository.updateHabit(uiState.habit)
        }
    }
    private fun validateInput(): Boolean {
        return with(uiState.habit) {
            name.isNotBlank() && description.isNotBlank()
        }
    }
}

data class HabitEditUiState(
    val habit: Habit = Habit(0,name = "", description = ""),
    val isEntryValid: Boolean = false,
    val hasThereBeenChanges: Boolean = false,
){}
fun Habit.toHabitEditUiState(isEntryValid: Boolean = false): HabitEditUiState = HabitEditUiState(
    habit = this,
    isEntryValid = isEntryValid
)
fun Habit.formatDateCreatedString(): String {
    return DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")
        .withZone(ZoneId.systemDefault())
        .format(Instant.ofEpochMilli(this.dateCreated))
}//TODO: change these to the app entity place
fun Habit.formatCurrentStreakOriginString(): String {
    return DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")
        .withZone(ZoneId.systemDefault())
        .format(this.currentStreakOrigin?.let { Instant.ofEpochMilli(it) })
}//TODO: change these to the app entity place
fun Habit.formatDaysSinceDateCreatedString(): String{
    val today = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate()
    val dateCreatedInstantObject = Instant.ofEpochMilli(this.dateCreated).atZone(ZoneId.systemDefault()).toLocalDate()

    // this is going to be a pain to translate
    val period = Period.between(dateCreatedInstantObject, today)
    val totalMonths = period.years * 12 + period.months
    val normalizedYears = totalMonths / 12
    val remainingMonths = totalMonths % 12

    val yearsString = if (normalizedYears > 0) "$normalizedYears year${if (normalizedYears > 1) "s" else ""}" else ""
    val monthsString = if (remainingMonths > 0) "$remainingMonths month${if (remainingMonths > 1) "s" else ""}" else ""
    val daysString = if (period.days > 0) "${period.days} day${if (period.days > 1) "s" else ""}" else ""

    val daySince = listOf(yearsString, monthsString, daysString)
        .filter { it.isNotEmpty() }
        .let { filteredList ->
            if (filteredList.size > 1) {
                filteredList.dropLast(2) + filteredList.takeLast(2).joinToString(", and ")
            } else {
                filteredList
            }
        }
        .joinToString(", ")
        .ifEmpty { "Born today, believe it or not." }

    return daySince
}