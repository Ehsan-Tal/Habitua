package com.example.habitua.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.habitua.data.AppRepository
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import com.example.habitua.data.Habit
import com.example.habitua.data.UserPreferencesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

private const val TAG = "HomeViewModel"

/**
 * View Model to provide data to the Home screen.
 *
 * @param appRepository the repository of habits
 * @param userPreferencesRepository the repository of user preferences
 */
class HomeViewModel(
    private val appRepository: AppRepository,
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

    /**
     * The date of today in yyyy-MM-dd format.
     */
    val dateToday: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    /**
     * The UiState of the Home screen.
     *
     * combines the app repository and the user preferences repository
     * The app repository returns a list of habits that haven't been acquired
     * The user preferences repository returns the last reviewed date
     *
     * @property reviewedToday does not allow a review if the pref repo returns today
     *
     */
    val homeUiState: StateFlow<HomeUiState> =
        combine(
            appRepository.getAllHabitsByAcquiredStream(false),
            userPreferencesRepository.lastReviewedFlow
        ) { habits, lastReviewedDate ->
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

    /**
     * Goes through each each habit in the UiState and passes them to reviewHabit
     *
     * it then saves the dateToday in the userPreferencesRepository
     */
    suspend fun reviewHabits() {
        homeUiState.value.habitList.map {
                habit -> reviewHabit(habit)
        }
        userPreferencesRepository.saveDateReviewed(dateToday)

    }

    /**
     * Reviews the state of a habit and updates it based on its current properties.
     *
     * This function handles various scenarios related to habit tracking, such as:
     * - Refilling the missed opportunity if the habit is active.
     * - Starting a streak if the habit is active and doesn't have one.
     * - Marking the habit as acquired if it meets the acquisition criteria.
     * - Removing a missed opportunity if available.
     * - Breaking the habit streak if necessary.
     * - Deactivating the habit.
     *
     * @param habit The habit to review and update.
     */
    suspend fun reviewHabit(habit: Habit) {
        val habitHasAStreak: Boolean = habit.currentStreakOrigin != null

        var updatedHabit: Habit = habit

        // Only proceed if the habit has not been acquired yet
        if (!habit.hasBeenAcquired) {
            //Handle active habits
            if (habit.isActive) {
                // Refill missed opportunity if not already available
                if (!habit.hasMissedOpportunity) {
                    updatedHabit = updatedHabit.copy(hasMissedOpportunity = true)
                }

                // Start a streak if the habit doesn't have one
                if (!habitHasAStreak) {
                    updatedHabit = updatedHabit.copy(currentStreakOrigin = dateToday)
                }

            }

            // Check if the habit should be marked as acquired
            if (habitHasAStreak && checkIfHabitIsAcquired(habit)) {
                updatedHabit = updatedHabit.copy(hasBeenAcquired = true)

            } else {
                // Handle inactive habits or those that don't meet acquisition criteria
                if (habit.hasMissedOpportunity) {
                    // Remove a missed opportunity
                    updatedHabit = updatedHabit.copy(hasMissedOpportunity = false)
                } else {
                    // Break the habit streak
                    breakHabitStreak(habit)
                }
            }

            // Deactivate the habit
            updatedHabit = updatedHabit.copy(isActive = false)
        }

        updateHabit(updatedHabit)
    }

    /**
     * breaks the streak by updating to a default habit
     */
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

    /**
     * Checks if the habit is acquired
     *
     * Compares the difference between the date today and the date the habit will be acquired
     * That will-be date will be taken from daysTillAcquired added to the currentStreakOrigin
     *
     * If greater or equal to 0, the habit is acquired
     */
    fun checkIfHabitIsAcquired(habit: Habit): Boolean {
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

/**
 * UI state for the Home screen
 *
 * @property habitList all habits as a list
 * @property userReviewedToday stops multiple reviews a day
 */
data class HomeUiState(
    val habitList: List<Habit> = listOf(),
    var userReviewedToday: Boolean = false
)