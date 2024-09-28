package com.example.habitua.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.habitua.data.AppRepository
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import com.example.habitua.data.Habit
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit

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
        ALL, TODO, AT_RISK, ACQUIRED, NOT_ACQUIRED, STREAKING, NOT_STREAKING
    }

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private var habitList: List<Habit> = listOf()
    private var countList: Map<DataSource, Int> = emptyMap()


    // DELETE LATER -
    // There was an issue with this function because it did not modify the values of the mutable stateflow
    //  and I think all it did was construct a new HomeUiState() item or maybe it privately failed
    // either way I fixed it by turning vals into vars, etc.

    private fun updateDataSource(datasource: DataSource) {

        viewModelScope.launch {
            when (datasource) {
                DataSource.ALL -> appRepository.getAllHabitsStream()
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
                _homeUiState.value.habitList = habits
            }

            _homeUiState.value.countList = mapOf(
                DataSource.ALL to appRepository.countAllHabitsStream(),
                DataSource.ACQUIRED to appRepository.countAllHabitsAcquiredStream(),
                DataSource.STREAKING to appRepository.countAllHabitsStreakingStream(),
                DataSource.NOT_ACQUIRED to appRepository.countAllHabitsNotAcquiredStream(),
                DataSource.NOT_STREAKING to appRepository.countAllHabitsNotStreakingStream(),
                DataSource.TODO to appRepository.countAllHabitsTODOStream(dateTodayMilli, dateYesterdayMilli),
                DataSource.AT_RISK to appRepository.countAllHabitsAtRiskStream(dateYesterdayMilli)
            )
            Log.d(TAG, "countList: $countList")
            Log.d(TAG, "habitList: $habitList")

        }
        Log.d(TAG, "countList: $countList")
        Log.d(TAG, "habitList: $habitList")

    }

    fun setDataSource(dataSource: DataSource) {
        Log.d(TAG, "setDataSource: $dataSource")
        updateDataSource(dataSource)
    }
    init {
        Log.d(TAG, "init: ")
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
    var habitList: List<Habit> = listOf(),
    var countList: Map<HomeViewModel.DataSource, Int> = emptyMap()
)