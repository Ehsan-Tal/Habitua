package com.example.habitua.ui.home

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.example.habitua.data.AppRepository
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import coil.compose.rememberAsyncImagePainter
import com.example.habitua.R
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

    enum class DataSource {
        TODO, AT_RISK, NOT_STREAKING, NOT_ACQUIRED, ACQUIRED, ALL
    }

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private var habitsJob: Job? = null

    private fun updateDataSource(datasource: DataSource) {
        habitsJob?.cancel()

        habitsJob = viewModelScope.launch {
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
            }.collect{ habits ->
                _homeUiState.value = _homeUiState.value.copy(habitList = habits)
            }
        }
    }

    // how does habtsJob fill up the homeuiState ?
    private fun updateCountList() {
        viewModelScope.launch {
            // only this one works.
            appRepository.countAllHabitsStream().collect { count ->
                _homeUiState.value = _homeUiState.value.copy(
                    countList = _homeUiState.value.countList + (DataSource.ALL to count)
                )}
        }

        // I'm assuming these do not work because of some black magic.
        // UPDATE
        // Each one requires its on "Scope"
        // YES, deleting and creating does modify the numbers.

        viewModelScope.launch {
            appRepository.countAllHabitsAcquiredStream().collect { count ->
                _homeUiState.value = _homeUiState.value.copy(
                    countList = _homeUiState.value.countList + (DataSource.ACQUIRED to count)
                )}
        }
        viewModelScope.launch {
            appRepository.countAllHabitsNotAcquiredStream().collect { count ->
                _homeUiState.value = _homeUiState.value.copy(
                    countList = _homeUiState.value.countList + (DataSource.NOT_ACQUIRED to count)
                )}
        }
        viewModelScope.launch {
            appRepository.countAllHabitsNotStreakingStream().collect { count ->
                _homeUiState.value = _homeUiState.value.copy(
                    countList = _homeUiState.value.countList + (DataSource.NOT_STREAKING to count)
                )}
        }
        viewModelScope.launch {
            appRepository.countAllHabitsTODOStream(dateTodayMilli, dateYesterdayMilli).collect { count ->
            _homeUiState.value = _homeUiState.value.copy(
                countList = _homeUiState.value.countList + (DataSource.TODO to count)
            )}
        }
        viewModelScope.launch {
            appRepository.countAllHabitsAtRiskStream(dateYesterdayMilli).collect{ count ->
                _homeUiState.value = _homeUiState.value.copy(
                    countList = _homeUiState.value.countList + (DataSource.AT_RISK to count)
                )}
        }
}

    fun setDataSource(dataSource: DataSource) {
        Log.d(TAG, "setDataSource: $dataSource")
        updateDataSource(dataSource)
        updateCountList()
    }
    init {
        setDataSource(DataSource.TODO)
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

    suspend fun onSelectImage(habit: Habit, img: Int) {
        updateHabit(habit = habit.copy(imageResId = img))
    }
}


data class HomeUiState(
    var habitList: List<Habit> = listOf(),
    var countList: Map<HomeViewModel.DataSource, Int> = emptyMap(),
    var nameMaps: Map<HomeViewModel.DataSource, String> = mapOf(
        HomeViewModel.DataSource.ALL to "All",
        HomeViewModel.DataSource.TODO to "TODO",
        HomeViewModel.DataSource.AT_RISK to "Streak at Risk",
        HomeViewModel.DataSource.ACQUIRED to "Acquired",
        HomeViewModel.DataSource.NOT_ACQUIRED to "Not Acquired",
        HomeViewModel.DataSource.NOT_STREAKING to "Not in a streak"
    ),
    var iconList: List<Painting> = listOf(
        Painting(R.drawable.tal_derpy, "Tal the cat having a derp face"),
        Painting(R.drawable.ic_launcher_foreground, "Tal the cat having a derp face"),
        Painting(R.drawable.tal_derpy, "Tal the cat having a derp face"),
        Painting(R.drawable.tal_derpy, "Tal the cat having a derp face"),
    )
)

data class Painting(
    @DrawableRes val imageUri : Int = R.drawable.tal_derpy,
    val contentDescription: String = ""
)