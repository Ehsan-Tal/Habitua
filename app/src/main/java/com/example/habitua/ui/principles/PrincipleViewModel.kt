package com.example.habitua.ui.principles

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitua.R
import com.example.habitua.data.AppRepository
import com.example.habitua.data.Principle
import com.example.habitua.data.PrincipleDetails
import com.example.habitua.data.toPrinciple
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.random.Random

private val TAG = "PrincipleViewModel"

class PrincipleViewModel(
   private val appRepository: AppRepository
): ViewModel() {

    private val _principleUiState = MutableStateFlow(PrincipleUiState())
    val principleUiState: StateFlow<PrincipleUiState> = _principleUiState.asStateFlow()

    val backgroundDrawables = listOf(
        R.drawable.baseline_circle_24,
        R.drawable.baseline_elderly_24,
        R.drawable.baseline_hexagon_24,
        R.drawable.baseline_electric_bolt_24
    )

    var backgroundAccessorIndex = Random.nextInt(backgroundDrawables.size)

    // create a function that collects all principles of today and yesterday and tomorrow.
    private var principleJob: Job? = null

    private fun collectPrinciples() {
        principleJob?.cancel()

        backgroundAccessorIndex = Random.nextInt(backgroundDrawables.size)

        principleJob = viewModelScope.launch {
            appRepository.getPrinciplesAndPrincipleDates(
                principleUiState.value.dateBase.toEpochMilli()
            ).collect {
                _principleUiState.value = _principleUiState.value.copy(principleListToday = it)
            }
        }
    }

    private fun collectPrinciplesWithoutCreating(){
        principleJob?.cancel()

        backgroundAccessorIndex = Random.nextInt(backgroundDrawables.size)

        principleJob = viewModelScope.launch {
            appRepository.getPrinciplesAndPrincipleDatesWithoutCreating(
                principleUiState.value.dateBase.toEpochMilli()
            ).collect {
                _principleUiState.value = _principleUiState.value.copy(principleListToday = it)
            }
        }
    }

    init {

        collectPrinciples()
    }

/*
    fun updateDate(direction: SwipeDirection) {
        when (direction) {
            SwipeDirection.Start ->
            SwipeDirection.End ->
        }

    }
 */
    fun addPrinciple() {

        //TODO: Add a date created to Principles
        // also, a "group" with the default being "Manual"
        val defaultPrinciple = Principle(
            name = "Added principle",
            description = "Added on ${_principleUiState.value.convertInstantToString(principleUiState.value.dateToday)}}",
        )

        viewModelScope.launch {
            appRepository.insertPrinciple(defaultPrinciple)
        }
        collectPrinciples()
    }

    private fun collectPrincipleAfterUpdates(){
        if (_principleUiState.value.isBaseEqualToday()) {
            collectPrinciples()
        } else {
            collectPrinciplesWithoutCreating()
        }
    }
    fun updateToToday(){
        _principleUiState.value.rebaseToToday()
        collectPrincipleAfterUpdates()
    }
    fun updateToTomorrow(){
        _principleUiState.value.addDay()
        collectPrincipleAfterUpdates()
    }
    fun updateToYesterday(){
        _principleUiState.value.removeDay()
        collectPrincipleAfterUpdates()
    }

    suspend fun togglePrinciple(principleDetails: PrincipleDetails) {
        appRepository.updatePrincipleDate(
            principleDetails.date, principleDetails.principleId, principleDetails.value
        )
    }


    // editing the principle
    private fun validatePrincipleInput(): Boolean {
        return with(principleUiState.value.editablePrincipleDetails) {
            name.isNotBlank() && description.isNotBlank()
        }
    }

    //TODO: THIS HAS MAJOR SETBACKS TO EFFICIENT CHANGE MANAGEMENT
    // but I"m sleepy, so wee.
    // and then again, so is it with most edit forms.
    // I feel like we can get away with just modifying it with PrincipleDetails
    fun setEditablePrincipleDetails(principleDetail: PrincipleDetails){
        Log.d(TAG, "Editable principle details set to $principleDetail")
        _principleUiState.value.editablePrincipleDetails = principleDetail
    }
    fun editMenuUpdatePrincipleInUiState(principleDetail: PrincipleDetails){
        _principleUiState.value.editablePrincipleDetails = principleDetail
    }
    /**
     * sent a copy of the updated principle details, which would used to update the principle.
     * Or takes a
     */
    fun editMenuApplyChangesToPrinciple(){
        if (validatePrincipleInput()){
            val principle = principleUiState.value.editablePrincipleDetails.toPrinciple()
            viewModelScope.launch {
                appRepository.updatePrinciple(principle)
            }
        }
    }
    fun editMenuDeletePrinciple(){
        val principle = principleUiState.value.editablePrincipleDetails.toPrinciple()
        viewModelScope.launch {
            appRepository.deletePrinciple(principle)
        }
    }

}

/**
 * Today save's today's date
 *
 * dayBeforeYesterday informs the read-only portion
 *
 * weekBeforeToday informs the scroll only portion
 *
 */
data class PrincipleUiState(
    var editablePrincipleDetails: PrincipleDetails = PrincipleDetails(
        principleId = 0,
        name = "",
        description = "",
        date = Instant.now().toEpochMilli(),
        value = false,
    ),

    var dateFormat: String = "dd-MM",

    val dateToday: Instant = Instant.now(),
    val dateYesterday: Instant = dateToday.minus(1, ChronoUnit.DAYS),
    val dateWeekBeforeToday: Instant = dateToday.minus(7, ChronoUnit.DAYS),

    var dateBase: Instant = Instant.now(),
    var principleListToday: List<PrincipleDetails> = listOf(),
) {
    private val dateBefore: Instant
        get() = dateBase.minus(1, ChronoUnit.DAYS)
    private val dateAfter: Instant
        get() = dateBase.plus(1, ChronoUnit.DAYS)

    val dateBaseString: String
        get() = DateTimeFormatter.ofPattern(dateFormat)
            .withZone(ZoneId.systemDefault())
            .format(dateBase)
    val dateBeforeString: String
        get() = DateTimeFormatter.ofPattern(dateFormat)
            .withZone(ZoneId.systemDefault())
            .format(dateBefore)
    val dateAfterString: String
        get() = DateTimeFormatter.ofPattern(dateFormat)
            .withZone(ZoneId.systemDefault())
            .format(dateAfter)

    fun convertInstantToString(instant: Instant): String {
        return DateTimeFormatter.ofPattern(dateFormat)
            .withZone(ZoneId.systemDefault())
            .format(instant)
    }

    fun addDay() {
        if (isBefore(dateBase, dateToday)){
            dateBase = dateBase.plus(1, ChronoUnit.DAYS)
        }
    }
    fun removeDay(){
        if (isAfter(dateBase, dateWeekBeforeToday)) {
            dateBase = dateBase.minus(1, ChronoUnit.DAYS)
        }
    }
    fun rebaseToToday(){
        dateBase = dateToday
    }

    private fun isBefore(first: Instant, second: Instant): Boolean {
        return first.atZone(ZoneId.systemDefault()).toLocalDate()
            .isBefore(second.atZone(ZoneId.systemDefault()).toLocalDate())
    }
    private fun isAfter(first: Instant, second: Instant): Boolean {
        return first.atZone(ZoneId.systemDefault()).toLocalDate()
            .isAfter(second.atZone(ZoneId.systemDefault()).toLocalDate())
    }

    fun isBaseEqualToday(): Boolean {
        return dateBase.atZone(ZoneId.systemDefault()).toLocalDate()
        .isEqual(dateToday.atZone(ZoneId.systemDefault()).toLocalDate())
    }
    fun isBaseEqualYesterday(): Boolean {
        return dateBase.atZone(ZoneId.systemDefault()).toLocalDate()
            .isEqual(dateYesterday.atZone(ZoneId.systemDefault()).toLocalDate())
    }
    fun isBaseBeforeYesterday(): Boolean {
        return dateBase.atZone(ZoneId.systemDefault()).toLocalDate()
            .isBefore(dateYesterday.atZone(ZoneId.systemDefault()).toLocalDate())
    }
    fun isBaseEqualWeekBefore(): Boolean {
        return dateBase.atZone(ZoneId.systemDefault()).toLocalDate()
            .isEqual(dateWeekBeforeToday.atZone(ZoneId.systemDefault()).toLocalDate())
    }
}
