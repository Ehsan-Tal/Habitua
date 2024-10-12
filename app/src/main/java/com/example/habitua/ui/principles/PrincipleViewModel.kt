package com.example.habitua.ui.principles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

private const val  TAG = "PrincipleViewModel"

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
    //TODO: change record traversal - we must create date records by each items origin date
    // say, if Happiness had a set origin of 8-10 and today is 8-12
    // then records containing Happiness must be created for each date until that date.
    // *space*
    // d
    // either we create records in the DB that may or may not be read only
    // , OR
    // we we create in the DAO, i.e., we send back things with their assumed values
    // the
    // the second option is superior for data redundancy - they can't change it, so why bother
    // On the other hand, if we are to allow them to modify ANY value from any date - pref ?
    // Then we could use the first
    // but, that's alot of work
    // So...
    // just create them in the DAO for any whose origin is before the given :date

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

    init { collectPrinciples() }

    fun addPrinciple() {

        //TODO: Add a date created to Principles
        // also, a "group" with the default being "Manual"
        val defaultPrinciple = Principle(
            name = "Added principle",
            description = "Added on ${_principleUiState.value.convertInstantToString(principleUiState.value.dateToday)}}",
            dateCreated = principleUiState.value.dateToday.toEpochMilli()
        )

        viewModelScope.launch {
            appRepository.insertPrinciple(defaultPrinciple)
        }
        collectPrinciples()
    }

    private fun collectPrincipleAfterUpdates(){
        if (_principleUiState.value.canCardClickBoolean) {
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

        if (principleDetails.value && principleDetails.dateFirstActive == null){
            appRepository.updatePrincipleOrigin(
                principleDetails.date, principleDetails.principleId
            )
        }
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
        date = 0,
        dateCreated = 0,
        dateFirstActive = null,
        value = false,
    ),

    var dateFormat: String = "EEEE, dd-MM",

    val dateToday: Instant = Instant.now(),
    val dateYesterday: Instant = dateToday.minus(1, ChronoUnit.DAYS),
    val dateWeekBeforeToday: Instant = dateToday.minus(7, ChronoUnit.DAYS),

    var dateBase: Instant = Instant.now(),
    var principleListToday: List<PrincipleDetails> = listOf(),
    val isPrincipleListTodayEmpty: Boolean = principleListToday.isEmpty(),

    val isFirstActionButtonEnabled: Boolean = true,
    val isSecondActionButtonEnabled: Boolean = true,
    // second action button should be NOT today
) {
    val canApplyChanges: Boolean = false
    //TODO: make this true if changes occurred

    private val dateBefore: Instant
        get() = dateBase.minus(1, ChronoUnit.DAYS)
    private val dateAfter: Instant
        get() = dateBase.plus(1, ChronoUnit.DAYS)

    val canCardClickBoolean: Boolean
        get() = dateBase.atZone(ZoneId.systemDefault()).toLocalDate()
                .isEqual(dateToday.atZone(ZoneId.systemDefault()).toLocalDate()) ||
                dateBase.atZone(ZoneId.systemDefault()).toLocalDate()
                .isEqual(dateYesterday.atZone(ZoneId.systemDefault()).toLocalDate())

    val isSerialBackwardButtonEnabled: Boolean
        get() = false

    val isSerialForwardButtonEnabled: Boolean
        get() = true
    //TODO: use the same stuff that the view model uses to disable traversal


    val dateBaseString: String
        get() = DateTimeFormatter.ofPattern(dateFormat)
            .withZone(ZoneId.systemDefault())
            .format(dateBase)

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
    fun isBaseNotEqualYesterday(): Boolean {
        return !(dateBase.atZone(ZoneId.systemDefault()).toLocalDate()
            .isEqual(dateYesterday.atZone(ZoneId.systemDefault()).toLocalDate()))
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
