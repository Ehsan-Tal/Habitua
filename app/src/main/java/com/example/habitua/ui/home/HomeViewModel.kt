package com.example.habitua.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.habitua.data.AppRepository
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.example.habitua.data.Habit
import com.example.habitua.data.UserPreferencesRepository
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

private const val TAG = "HomeViewModel"

class HomeViewModel(
    private val appRepository: AppRepository,
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

    /*
    val homeUiState: StateFlow<HomeUiState> =
        appRepository.getAllHabitsByAcquiredStream(false).map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )
    */

    //TODO: make a new branch and TDD the new repo - after you TDD "containerizing" it

    val dateToday: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val homeUiState: StateFlow<HomeUiState> =
        combine(
            appRepository.getAllHabitsByAcquiredStream(false),
            userPreferencesRepository.lastReviewedFlow
        ) {
                habits, lastReviewedDate ->

            val reviewedToday = (dateToday == lastReviewedDate)

            HomeUiState(habits, reviewedToday)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    suspend fun toggleHabitActive(habit: Habit) {
        updateHabit(habit.copy(isActive = !habit.isActive))
    }

    private suspend fun updateHabit(habit: Habit) {
        appRepository.updateHabit(habit)
    }

    //TODO: some way to habits are being reviewed -
    // e.g., make them do a jig and delay changes by 0.5 seconds
    suspend fun reviewHabits() {
        homeUiState.value.habitList.map {
                habit -> reviewHabit(habit)
        }
        userPreferencesRepository.saveDateReviewed(dateToday)

    }

    //TODO: create streak, enable history !

    // ideally this should run also when an item is toggled
    private suspend fun reviewHabit(habit: Habit) {
        val habitHasAStreak: Boolean = habit.currentStreakOrigin != null

        // run whenever the user presses the dialog-ed-button

        // only runs the rest of the logic if the habit has not been acquired
        if (!habit.hasBeenAcquired) {

            // refills the missedOpportunity
            if (habit.isActive) {
                if (!habit.hasMissedOpportunity) {
                    updateHabit(habit.copy(hasMissedOpportunity = true))
                }
                // if active, if no streak - create streak
                if (!habitHasAStreak) {
                    updateHabit(habit.copy(currentStreakOrigin = dateToday))
                }
            }

            // check if the habit has a streak and if warrants being acquired
            if( habitHasAStreak && checkIfHabitIsAcquired(habit)) {
                updateHabit(habit.copy(hasBeenAcquired = true))

            } else {
                // if not active, but can afford a missed opportunity
                if (habit.hasMissedOpportunity) {
                    updateHabit(habit.copy(hasMissedOpportunity = false))

                    // breaks HabitStreak
                } else {
                    breakHabitStreak(habit)
                }
            }

            updateHabit(habit.copy(isActive = false,))
        }
    }

    private suspend fun checkIfUserReviewed(){

    }

    private suspend fun breakHabitStreak(habit: Habit) {
        // should reset all the booleans to their default values
        // except for missed opp - that should only regen if it had been active
        // reset streak, currentStreakOrigin
        //appRepository.endStreak(habit.id)

        updateHabit(habit.copy(
            isActive = false,
            hasBeenAcquired = false,
            currentStreakOrigin = null,
            hasMissedOpportunity = true)
        )
    }

    private fun checkIfHabitIsAcquired(habit: Habit): Boolean {
        // needs to compare the two dates and check if they can occur in the day
        // We can assume that it would have a streak

        //TODO: en-variable missed opportunities as they can afford much looser and
        // dynamic opportunities

        //TODO: en-variable 66 days as not all habits occupy this form
        //TODO: and type that into an ENUM of Integers

        // val daysTillAcquired = habit.daysTillAcquired
        val daysTillAcquired = 66L

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val habitStreakOriginDate = LocalDate.parse(habit.currentStreakOrigin, formatter)
        val habitFutureDayAcquired = habitStreakOriginDate.plusDays(daysTillAcquired)
            .format(formatter)

        // needs to check if "acquired day is today"
        val isAcquired: Boolean = dateToday.compareTo(habitFutureDayAcquired) >= 0

        return isAcquired
    }
}
data class HomeUiState(
    val habitList: List<Habit> = listOf(),
    var userReviewedToday: Boolean = false
)