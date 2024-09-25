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

    val dateToday: String= DateTimeFormatter
        .ofPattern("yyyy-MM-dd")
        .withZone(ZoneId.systemDefault())
        .format(dateTodayInstant)

    val dateYesterday: String = DateTimeFormatter
        .ofPattern("yyyy-MM-dd")
        .withZone(ZoneId.systemDefault())
        .format(dateYesterdayInstant)

    // oh hello !!!
    //TODO: Use these for the filters !
    //TODO: Default is getAllHabitsTODOStream
    // Remember to view the counts of these lists and send that to the screen.
    /*

    fun getAllHabitsAcquiredStream(): Flow<List<Habit>>
    fun getAllHabitsNotAcquiredStream(): Flow<List<Habit>>

    fun getAllHabitsStreakingStream(): Flow<List<Habit>>
    fun getAllHabitsNotStreakingStream(): Flow<List<Habit>>

    fun getAllHabitsTODOStream(dateToday: String, dateYesterday: String): Flow<List<Habit>>
    fun getAllHabitsAtRiskStream(dateYesterday: String): Flow<List<Habit>>


     */
    //TODO: Change the review button to gray out if no nextRequiredDate habits = today/tomorrow

    /*
        val homeUiState: StateFlow<HomeUiState> =
        combine(
            appRepository.getAllHabitsByAcquiredStream(false),
            userPreferencesRepository.lastReviewedFlow
        ) { habits, lastReviewedDate ->

            HomeUiState(habits, )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )
    * */

    // we set a private and public var - the mutable stateflow connects with the repo
    // the stateflow converts the mutable and provides it to the screen
    // the repository is then used to collect value into the mutable

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    init { viewModelScope.launch {
        appRepository.getAllHabitsTODOStream(dateTodayMilli, dateYesterdayMilli).collect { habits ->
            _homeUiState.value = HomeUiState(habits)
        }
    }}


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