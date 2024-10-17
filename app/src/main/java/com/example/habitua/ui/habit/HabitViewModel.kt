package com.example.habitua.ui.habit

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.example.habitua.data.AppRepository
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import com.example.habitua.R
import com.example.habitua.data.Habit
import com.example.habitua.data.HabitDetails
import com.example.habitua.ui.backgroundDrawables
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.random.Random

private const val TAG = "HabitViewModel"


class HabitViewModel(
    private val appRepository: AppRepository,
): ViewModel() {

    enum class DataSource {
        TODO, AT_RISK, NOT_STREAKING, NOT_ACQUIRED, ACQUIRED, ALL
    }

    var backgroundAccessorIndex = Random.nextInt(backgroundDrawables.size)

    private val _habitUiState = MutableStateFlow(HabitUiState())
    val habitUiState: StateFlow<HabitUiState> = _habitUiState.asStateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(600L),
            initialValue = HabitUiState()
        )

    private var habitsJob: Job? = null

//    private fun updateDataSource(datasource: DataSource) {

    private fun collectHabits() {
        habitsJob?.cancel()

            /*
            when (datasource) {
                DataSource.TODO -> appRepository.getAllHabitsTODOStream(
                    dateTodayMilli,
                    dateYesterdayMilli
                )
                DataSource.AT_RISK -> appRepository.getAllHabitsAtRiskStream(dateYesterdayMilli)
                DataSource.NOT_STREAKING -> appRepository.getAllHabitsNotStreakingStream()
                DataSource.NOT_ACQUIRED -> appRepository.getAllHabitsNotAcquiredStream()
                DataSource.ACQUIRED -> appRepository.getAllHabitsAcquiredStream()
                DataSource.ALL -> appRepository.getAllHabitsStream()
            */
        habitsJob = viewModelScope.launch {
            appRepository.getHabitsAndHabitDates(
                habitUiState.value.dateBase.toEpochMilli()
            ).collect {
                _habitUiState.value = _habitUiState.value.copy(habitList = it)
            }
            _habitUiState.value.currentlyLoading = false
        }
    }

    private fun getCountOfHabitsOnDateBefore(){
        habitsJob = viewModelScope.launch {
            appRepository.countHabitsDetailsByDateCreated(
                habitUiState.value.dateBefore.toEpochMilli()
            ).collect {
                _habitUiState.value = _habitUiState.value.copy(countOfHabitsOnDateBefore = it)
            }
        }
        //TODO: we need to stop this running when off the manners page
        //TODO: we need some way to "unsubscribe" from the flow
    }

    init {
        collectHabits()
        getCountOfHabitsOnDateBefore()
    }

    fun addHabit(navToHabitEdit: (Int) -> Unit) {

        //TODO: this should create a new habit and redirect the user to the habit edit screen
        val defaultHabit = Habit(
            name = "",
            description = "",
            dateCreated = habitUiState.value.dateToday.toEpochMilli()
        )
        var habitId = 0
        viewModelScope.launch {
            habitId = appRepository.insertHabit(defaultHabit).toInt()


            _habitUiState.update { currentState ->
                currentState.copy( currentHabitId = habitId)
            } //TODO: what does this do ?
        }

        navToHabitEdit(habitId)
        //"${HabitEditDestination.route}/$habitId"
        // we pass it the habit Id since it will finish it over there.

    }

    // how does habtsJob fill up the homeuiState ?
    private fun updateCountList() {
        /*
        viewModelScope.launch {
            // only this one works.
            appRepository.countAllHabitsStream().collect { count ->
                _habitUiState.value = _habitUiState.value.copy(
                    countList = _habitUiState.value.countList + (DataSource.ALL to count)
                )}
        }

        // I'm assuming these do not work because of some black magic.
        // UPDATE
        // Each one requires its on "Scope"
        // YES, deleting and creating does modify the numbers.

        viewModelScope.launch {
            appRepository.countAllHabitsAcquiredStream().collect { count ->
                _habitUiState.value = _habitUiState.value.copy(
                    countList = _habitUiState.value.countList + (DataSource.ACQUIRED to count)
                )}
        }
        viewModelScope.launch {
            appRepository.countAllHabitsNotAcquiredStream().collect { count ->
                _habitUiState.value = _habitUiState.value.copy(
                    countList = _habitUiState.value.countList + (DataSource.NOT_ACQUIRED to count)
                )}
        }
        viewModelScope.launch {
            appRepository.countAllHabitsNotStreakingStream().collect { count ->
                _habitUiState.value = _habitUiState.value.copy(
                    countList = _habitUiState.value.countList + (DataSource.NOT_STREAKING to count)
                )}
        }
        viewModelScope.launch {
            appRepository.countAllHabitsTODOStream(dateTodayMilli, dateYesterdayMilli).collect { count ->
            _habitUiState.value = _habitUiState.value.copy(
                countList = _habitUiState.value.countList + (DataSource.TODO to count)
            )}
        }
        viewModelScope.launch {
            appRepository.countAllHabitsAtRiskStream(dateYesterdayMilli).collect{ count ->
                _habitUiState.value = _habitUiState.value.copy(
                    countList = _habitUiState.value.countList + (DataSource.AT_RISK to count)
                )}
        }
       }
 */
    }

/*
    fun setDataSource(dataSource: DataSource) {
        updateDataSource(dataSource)
        updateCountList()
    }
    init {
        setDataSource(DataSource.TODO)
    }

 */

    suspend fun updateHabit(habit: Habit) {
        appRepository.updateHabit(habit)
    }

    suspend fun toggleHabit(id: Int, date: Long){
        appRepository.updateHabitDate(date, id)
    }

    private fun collectHabitAfterUpdates(){
        collectHabits()
        getCountOfHabitsOnDateBefore()
    }
    fun updateToToday(){
        _habitUiState.value.rebaseToToday()
        collectHabitAfterUpdates()
    }
    fun updateToTomorrow(){
        _habitUiState.value.addDay()
        collectHabitAfterUpdates()
    }
    fun updateToYesterday(){
        _habitUiState.value.removeDay()
        collectHabitAfterUpdates()
    }


    suspend fun onSelectImage(habit: Habit, img: Int) {
        updateHabit(habit = habit.copy(imageResId = img))
    }
}


data class HabitUiState(
    var habitList: List<HabitDetails> = listOf(),
    var countList: Map<HabitViewModel.DataSource, Int> = emptyMap(),
    var nameMaps: Map<HabitViewModel.DataSource, String> = mapOf(
        HabitViewModel.DataSource.ALL to "All",
        HabitViewModel.DataSource.TODO to "TODO",
        HabitViewModel.DataSource.AT_RISK to "Streak at Risk",
        HabitViewModel.DataSource.ACQUIRED to "Acquired",
        HabitViewModel.DataSource.NOT_ACQUIRED to "Not Acquired",
        HabitViewModel.DataSource.NOT_STREAKING to "Not in a streak"
    ),
    var iconList: List<Painting> = listOf(
        Painting(R.drawable.tal_derpy, "Tal the cat having a derp face"),
        Painting(R.drawable.ic_launcher_foreground, "Tal the cat having a derp face"),
        Painting(R.drawable.tal_derpy, "Tal the cat having a derp face"),
        Painting(R.drawable.tal_derpy, "Tal the cat having a derp face"),
    ),

    var dateFormat: String = "EEEE, dd-MM",

    val dateToday: Instant = Instant.now(),
    val dateYesterday: Instant = dateToday.minus(1, ChronoUnit.DAYS),
    val dateWeekBeforeToday: Instant = dateToday.minus(7, ChronoUnit.DAYS),

    var dateBase: Instant = Instant.now(),

    var countOfHabitsOnDateBefore: Int = 1,

    var currentHabitId : Int = 0,

    var currentlyLoading: Boolean = true,
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
        get() = countOfHabitsOnDateBefore != 0

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

data class Painting(
    @DrawableRes val imageUri : Int = R.drawable.tal_derpy,
    val contentDescription: String = ""
)