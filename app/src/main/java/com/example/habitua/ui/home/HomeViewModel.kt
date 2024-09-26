package com.example.habitua.ui.home

import android.icu.util.Calendar
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import com.example.habitua.data.AppRepository
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import com.example.habitua.data.Habit
import com.example.habitua.data.UserPreferencesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

private const val TAG = "HomeViewModel"

/**
 * View Model to provide data to the Home screen.
 *
 * @param appRepository the repository of habits
 */
class HomeViewModel(
    private val appRepository: AppRepository,
): ViewModel() {

    /**
     * The operations to get today's and yesterday's dates.
     */
    private val dateTodayInstant = Instant.now()
    private val dateYesterdayInstant = dateTodayInstant.minus(1, ChronoUnit.DAYS)

    val dateTodayMilli = dateTodayInstant.toEpochMilli()
    val dateYesterdayMilli = dateYesterdayInstant.toEpochMilli()

/*
    val dateToday: String= DateTimeFormatter
        .ofPattern("yyyy-MM-dd")
        .withZone(ZoneId.systemDefault())
        .format(dateTodayInstant)

    val dateYesterday: String = DateTimeFormatter
        .ofPattern("yyyy-MM-dd")
        .withZone(ZoneId.systemDefault())
        .format(dateYesterdayInstant)

 */

    // oh hello !!!
    //TODO: Use these for the filters !
    //TODO: Default is getAllHabitsTODOStream
    // Remember to view the counts of these lists and send that to the screen.

    /*
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    init { viewModelScope.launch {
        appRepository.getAllHabitsTODOStream(dateTodayMilli, dateYesterdayMilli).collect { habits ->
            _homeUiState.value = HomeUiState(habits)
        }
    }}
*/


    //TODO: Change the review button to gray out if no nextRequiredDate habits = today/tomorrow

    // we set a private and public var - the mutable stateflow connects with the repo
    // the stateflow converts the mutable and provides it to the screen
    // the repository is then used to collect value into the mutable

    enum class DataSource {
        TODO, AT_RISK, ACQUIRED, NOT_ACQUIRED, STREAKING, NOT_STREAKING
    }


    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private var habitsJob: Job? = null


    private fun updateDataSource(datasource: DataSource) {
        habitsJob?.cancel()
        habitsJob = viewModelScope.launch {
            when (datasource) {
                DataSource.TODO -> appRepository.getAllHabitsTODOStream(
                    dateTodayMilli, dateYesterdayMilli
                )
                DataSource.AT_RISK -> appRepository.getAllHabitsAtRiskStream(
                    dateYesterdayMilli
                )
                DataSource.ACQUIRED -> appRepository.getAllHabitsAcquiredStream()
                DataSource.NOT_ACQUIRED -> appRepository.getAllHabitsNotAcquiredStream()
                DataSource.STREAKING -> appRepository.getAllHabitsStreakingStream()
                DataSource.NOT_STREAKING -> appRepository.getAllHabitsNotStreakingStream()
            }.collect { habits ->
                _homeUiState.value = HomeUiState(habits)
            }
        }
    }

    //TODO - we need a count function

    fun setDataSource(dataSource: DataSource) {
        updateDataSource(dataSource)
    }
    init {
        updateDataSource(DataSource.TODO)
    }

    /**
     * Toggle the active state of the habit.
     */
    suspend fun toggleHabitActive(habit: Habit) {
        appRepository.updateHabit(habit.copy(isActive = !habit.isActive))
    }

    /**
     * Update the habit in the repository.
     */
    suspend fun updateHabit(habit: Habit) {
        appRepository.updateHabit(habit)
    }

    suspend fun reviewHabits() {
        appRepository.reviewHabits(dateTodayMilli, dateYesterdayMilli)
    }

    suspend fun checkIfHabitIsAcquired() {

        //TODO Change the spirit of this function into a user Requesterer
        // this will be a new issue altogether
        /*
        if today == habit.daysUntilAcquired
        then
            habit.dateAcquired = today
            // UPSTREAM APP REPO FUNCTION, TO ADD ALL OTHER OPS LIKE NULLING OUT FIELDS

         */
        TODO("not implemented yet or soon")
    }
}


data class HomeUiState(
    val habitList: List<Habit> = listOf(),
)