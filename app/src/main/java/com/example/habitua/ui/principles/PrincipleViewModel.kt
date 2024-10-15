package com.example.habitua.ui.principles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitua.R
import com.example.habitua.data.AppRepository
import com.example.habitua.data.Principle
import com.example.habitua.data.PrincipleDetails
import com.example.habitua.data.toPrinciple
import com.example.habitua.ui.navigation.PrincipleEditDestination
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(600L),
            initialValue = PrincipleUiState()
        )
    // I am starting to understand what scope would define.
    // viewModel relates to the main thread, what we can do to reduce the load on it.

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
    private fun getCountOfPrinciplesOnDateBefore(){
        principleJob = viewModelScope.launch {
            appRepository.countPrinciplesDetailsByDateCreated(
                principleUiState.value.dateBefore.toEpochMilli()
            ).collect {
                _principleUiState.value = _principleUiState.value.copy(countOfPrinciplesOnDateBefore = it)
            }
        }
        //TODO: we need to stop this running when off the manners page
        //TODO: we need some way to "unsubscribe" from the flow
    }

    init {
        collectPrinciples()
        getCountOfPrinciplesOnDateBefore()
    }

    fun addPrinciple(navToPrincipleEdit: (Int) -> Unit) {

        //TODO: this should create a new principle and redirect the user to the principle edit screen
        val defaultPrinciple = Principle(
            name = "",
            description = "",
            dateCreated = principleUiState.value.dateToday.toEpochMilli()
        )
        var principleId = 0
        viewModelScope.launch {
            principleId = appRepository.insertPrinciple(defaultPrinciple).toInt()

            _principleUiState.update { currentState ->
                currentState.copy( currentPrincipleId = principleId)
            }
        }

        navToPrincipleEdit(principleId)
        //"${PrincipleEditDestination.route}/$principleId"
        // we pass it the principle Id since it will finish it over there.

    }
    suspend fun togglePrinciple(id: Int, date: Long){
        appRepository.updatePrincipleDate(date, id)
    }

    private fun collectPrincipleAfterUpdates(){
        collectPrinciples()
        getCountOfPrinciplesOnDateBefore()
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


    // editing the principle
    private fun validatePrincipleInput(): Boolean {
        return with(principleUiState.value.editablePrincipleDetails) {
            name.isNotBlank() && description.isNotBlank()
        }
    }

    //TODO: Make a dedicated principle Edit page
    // And have the addPrinciple go to it with the newly created principleId
    //

    //TODO: and the moreOptionsClick send the user to it
    // we can have moreOptionsClick be a () -> Unit after we attach the
    // principle.id to it

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
    ),// TODO: delete this



    var dateFormat: String = "EEEE, dd-MM",

    val dateToday: Instant = Instant.now(),
    val dateYesterday: Instant = dateToday.minus(1, ChronoUnit.DAYS),
    val dateWeekBeforeToday: Instant = dateToday.minus(7, ChronoUnit.DAYS),

    var dateBase: Instant = Instant.now(),
    var principleListToday: List<PrincipleDetails> = listOf(),

    var countOfPrinciplesOnDateBefore: Int = 1,

    var currentPrincipleId : Int = 0,
) {

    val dateBefore: Instant
        get() = dateBase.minus(1, ChronoUnit.DAYS)
    private val dateAfter: Instant
        get() = dateBase.plus(1, ChronoUnit.DAYS)

    val canCardClickBoolean: Boolean
        get() = dateBase.atZone(ZoneId.systemDefault()).toLocalDate()
                .isEqual(dateToday.atZone(ZoneId.systemDefault()).toLocalDate()) ||
                dateBase.atZone(ZoneId.systemDefault()).toLocalDate()
                .isEqual(dateYesterday.atZone(ZoneId.systemDefault()).toLocalDate())

    val isFirstActionButtonEnabled: Boolean
        get() = true

    val isSecondActionButtonEnabled: Boolean
        get() = !(dateBase.atZone(ZoneId.systemDefault()).toLocalDate()
            .isEqual(dateToday.atZone(ZoneId.systemDefault()).toLocalDate()))

    val isSerialBackwardButtonEnabled: Boolean
        get() = countOfPrinciplesOnDateBefore != 0

    val isSerialForwardButtonEnabled: Boolean
        get() = dateBase < dateToday
    //TODO: ideally these would mention Filter or Action bars


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
