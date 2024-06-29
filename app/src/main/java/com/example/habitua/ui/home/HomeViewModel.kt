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
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

private const val TAG = "HomeViewModel"

// this needs a significant amount of work - everything does :(.
// remember that this is not where we want to allow adding
// only toggling whether or not an item is active
// and that is an individual item.
// I can probably add HabitDetails here instead.
class HomeViewModel(private val appRepository: AppRepository): ViewModel() {

    val homeUiState: StateFlow<HomeUiState> =
        appRepository.getAllHabitsByAcquiredStream(false).map { HomeUiState(it) }
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

    private suspend fun markHabitAcquired(habit: Habit){
        updateHabit(habit.copy(hasBeenAcquired = true))
    }

    suspend fun reviewHabits() {
        //TODO: we need to attach this to the button
        homeUiState.value.habitList.map {
            habit -> reviewHabit(habit)
        }
    }

    // ideally this should run also when an item is toggled
    private suspend fun reviewHabit(habit: Habit) {
        val habitHasAStreak: Boolean = habit.currentStreakOrigin != null

        // run whenever the user presses the dialog-ed-button

        // only runs the rest of the logic if the habit has not been acquired
        if (!habit.hasBeenAcquired) {
            //TODO: this needs to ensure that it always reset

            // refills the missedOpportunity
            if (habit.isActive) {
                if (!habit.hasMissedOpportunity) {
                    updateHabit(habit.copy(
                        hasMissedOpportunity = true)
                    )
                }

                // if active, if no streak - create streak
                if (!habitHasAStreak) {
                    //TODO: create streak, enable !
                    //appRepository.createStreak(habit.id, System.currentTimeMillis())
                 }
            }

            //TODO: How do we start a streak ?

            // check if the habit has a streak and if warrants being acquired
            if( habitHasAStreak && checkIfHabitIsAcquired(habit)) {
                markHabitAcquired(habit)

            } else {
                // if not active, but can afford a missed opportunity
                if (habit.hasMissedOpportunity) {
                    updateHabit(habit.copy(hasMissedOpportunity = false))

                // breaks HabitStreak
                } else {
                    breakHabitStreak(habit)
                }
            }

            updateHabit(habit.copy(
                isActive = false,
            ))
        }
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
        val isAcquired: Boolean = TimeUnit.MILLISECONDS.toDays(
            (System.currentTimeMillis() - habit.currentStreakOrigin!!)
        ) > 66
        // 66 days, needs to be edited

        return isAcquired
    }
}
data class HomeUiState(
    val habitList: List<Habit> = listOf(),
)
