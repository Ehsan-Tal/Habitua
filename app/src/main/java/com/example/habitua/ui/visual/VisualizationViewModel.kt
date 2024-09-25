package com.example.habitua.ui.visual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitua.R
import com.example.habitua.data.AppRepository
import com.example.habitua.data.Habit
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private const val TAG = "VisualizationViewModel"

/**
 * ViewModel containing the app data to use the visualization composable.
 *
 * @param appRepository the data source this ViewModel will fetch results from.
 */
class VisualizationViewModel(
    private val appRepository: AppRepository
): ViewModel() {
/*
    /**
     * The UI state for the visualization composable.
     * Performs operations on data as its collected
     */
    val habitVizUiState: StateFlow<HabitVizUiState> =
        appRepository.getAllHabitsStream()
            .map { habIt ->
                if (habIt.isEmpty()) {
                    HabitVizUiState(
                        acquiredHabitList = habIt,
                        totalSize = 0,
                        portionAcquired = 0,
                        portionInStreaks = 0,
                        perCentAcquired = 0f,
                        perCentInStreak =  0f,
                    )
                } else {
                    HabitVizUiState(
                        acquiredHabitList = habIt.filter { it.hasBeenAcquired },
                        totalSize = habIt.size,
                        portionAcquired = habIt.count{ it.hasBeenAcquired },
                        portionInStreaks = habIt.count{ it.currentStreakOrigin != null && !it.hasBeenAcquired },
                        perCentAcquired = (habIt.count {it.hasBeenAcquired
                        }.toFloat() / habIt.size ) * 100f,
                        perCentInStreak =  (habIt.count {it.currentStreakOrigin != null && !it.hasBeenAcquired
                        }.toFloat() / habIt.size ) * 100f,
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HabitVizUiState()
            )

    /**
     * the companion object that defines the timeout for collecting data
     */
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    /**
     * Prepares the data set that the pie uses
     *
     * @param rStringOn
     * @param percent
     * @param rStringOff
     *
     * @return Map<Int, Float>
     */
    private fun preparePieDataSet(rStringOn: Int, percent: Float, rStringOff: Int): Map<Int, Float>{
        val data = mapOf(
            rStringOn to
                    percent,
            rStringOff to
                    (100f - percent)
        )
        return data
    }

    /**
     * Prepares the data set that the pie uses for streaks
     *
     * @return Map<Int, Float>
     */
    fun preparePieDataSetForStreakComparisons(): Map<Int, Float>{
        return preparePieDataSet(
            rStringOn = R.string.visualization_pie_chart_Streak_on,
            percent = habitVizUiState.value.perCentInStreak,
            rStringOff = R.string.visualization_pie_chart_Streak_off
        )
    }

    /**
     * Prepares the data set that the pie uses for the acquired habits
     *
     * @return Map<Int, Float>
     */
    fun preparePieDataSetForAcquiredHabits(): Map<Int, Float>{
        return preparePieDataSet(
            rStringOn = R.string.visualization_pie_chart_Acquired_on,
            percent = habitVizUiState.value.perCentAcquired,
            rStringOff = R.string.visualization_pie_chart_Acquired_off
        )
    }
    
 */

}

/**
 * Represents the state for the visualization composable.
 *
 * @property acquiredHabitList
 * @property totalSize
 * @property portionAcquired
 * @property portionInStreaks
 * @property perCentAcquired
 * @property perCentInStreak
 */
data class HabitVizUiState(
    val acquiredHabitList: List<Habit> = listOf(),
    val totalSize: Int = 0,
    val portionAcquired: Int = 0,
    val portionInStreaks: Int = 0,
    val perCentAcquired: Float = 0f,
    val perCentInStreak: Float = 0f,
)
