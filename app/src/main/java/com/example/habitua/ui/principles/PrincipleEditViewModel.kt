package com.example.habitua.ui.principles

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitua.R
import com.example.habitua.data.AppRepository
import com.example.habitua.data.Principle
import com.example.habitua.ui.backgroundDrawables
import com.example.habitua.ui.navigation.PrincipleEditDestination
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.random.Random

/*
sealed interface PrincipleEditUiState {
    object Error : PrincipleEditUiState
    object Loading : PrincipleEditUiState
    data class Success(val principle: Principle) : PrincipleEditUiState
}

 */
// we can then ascribe the the data class here.

class PrincipleEditViewModel (
    savedStateHandle: SavedStateHandle,
    private val appRepository: AppRepository
): ViewModel(){

    val principleId: Int =
        checkNotNull(savedStateHandle[PrincipleEditDestination.ID_ARG])

    var uiState by mutableStateOf(PrincipleEditUiState())
        private set
    //TODO: how do we use sealed interfaces ? find out next quarter.


    init {
        viewModelScope.launch {
            uiState = appRepository.getPrincipleStream(principleId)
                .filterNotNull()
                .first()
                .toPrincipleEditUiState(true)
        } //because we have the translate function, we can get the principle and do so
    }

    var backgroundAccessorIndex = Random.nextInt(backgroundDrawables.size)


    fun updateUiState(principle: Principle){
        uiState = PrincipleEditUiState(
            principle = principle,
            isEntryValid = validateInput(),
            hasThereBeenChanges = true
        )
    }
    suspend fun deletePrinciple() {
        appRepository.deletePrinciple(uiState.principle)
    }
    suspend fun savePrinciple() {
        if (validateInput()){
            appRepository.updatePrinciple(uiState.principle)
        }
    }

    private fun validateInput(): Boolean {
        return with(uiState.principle) {
            name.isNotBlank() && description.isNotBlank()
        }
    }
}

data class PrincipleEditUiState(
    val principle: Principle = Principle(0, 0, null, "", ""),
    val isEntryValid: Boolean = false,
    val hasThereBeenChanges: Boolean = false,
){}

fun Principle.toPrincipleEditUiState(isEntryValid: Boolean = false): PrincipleEditUiState = PrincipleEditUiState(
    principle = this,
    isEntryValid = isEntryValid
)
fun Principle.formatDateCreatedString(): String {
    return DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")
        .withZone(ZoneId.systemDefault())
        .format(Instant.ofEpochMilli(this.dateCreated))
}//TODO: change these to the app entity place
fun Principle.formatFirstActiveDateString(): String {
    return DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")
        .withZone(ZoneId.systemDefault())
        .format(this.dateFirstActive?.let { Instant.ofEpochMilli(it) })
}//TODO: change these to the app entity place

fun Principle.formatDaysSinceDateCreatedString(): String{
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

