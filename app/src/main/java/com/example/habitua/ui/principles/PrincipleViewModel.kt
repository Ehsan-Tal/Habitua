package com.example.habitua.ui.principles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitua.data.AppRepository
import com.example.habitua.data.PrincipleDetails
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val TAG = "PrincipleViewModel"

class PrincipleViewModel(
   private val appRepository: AppRepository
): ViewModel() {

    private val _principleUiState = MutableStateFlow(PrincipleUiState())
    val principleUiState: StateFlow<PrincipleUiState> = _principleUiState.asStateFlow()

    // create a function that collects all principles of today and yesterday and tomorrow.
    private var principleJob: Job? = null

    private fun collectPrinciples() {
        principleJob?.cancel()

        principleJob = viewModelScope.launch {
            appRepository.getPrinciplesAndPrincipleDates(principleUiState.value.dateBase.toEpochMilli()).collect { principleDetails ->
                _principleUiState.value = _principleUiState.value.copy(principleListToday = principleDetails)
            }
        }
    }

    private fun collectPrinciplesWithoutCreating(){
        principleJob?.cancel()

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
        // we ought to change both th
        appRepository.updatePrincipleDate(
            principleDetails.date, principleDetails.principleId, !principleDetails.value
        )
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
        // just remember that these change the dates, but we still need to update the flow
        // from the other place.
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
    fun isBaseBeforeYesterday(): Boolean {
        return dateBase.atZone(ZoneId.systemDefault()).toLocalDate()
            .isBefore(dateYesterday.atZone(ZoneId.systemDefault()).toLocalDate())
    }
    fun isBaseEqualWeekBefore(): Boolean {
        return dateBase.atZone(ZoneId.systemDefault()).toLocalDate()
            .isEqual(dateWeekBeforeToday.atZone(ZoneId.systemDefault()).toLocalDate())
    }
}
