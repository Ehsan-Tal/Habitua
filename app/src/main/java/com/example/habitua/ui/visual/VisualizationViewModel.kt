package com.example.habitua.ui.visual

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitua.R
import com.example.habitua.data.AppRepository
import com.example.habitua.data.Habit
import com.example.habitua.ui.home.HomeUiState
import com.example.habitua.ui.home.HomeViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


private const val TAG = "VisualizationViewModel"

class VisualizationViewModel(
    private val appRepository: AppRepository
): ViewModel() {


    //TODO: ideally these should be cached or otherwise use smart operations to
    // avoid using count
    val habitVizUiState: StateFlow<HabitVizUiState> =
        appRepository.getAllHabitsStream()
            .map { habIt ->
                HabitVizUiState(
                habitList = habIt,
                totalSize = habIt.size,
                portionAcquired = habIt.count{ it.hasBeenAcquired },
                portionInStreaks = habIt.count{ it.currentStreakOrigin != null && !it.hasBeenAcquired },
                perCentAcquired = (habIt.count {it.hasBeenAcquired
                }.toFloat() / habIt.size ) * 100f,
                perCentInStreak =  (habIt.count {it.currentStreakOrigin != null && !it.hasBeenAcquired
                }.toFloat() / habIt.size ) * 100f,
            )}
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HabitVizUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private fun preparePieDataSet(rStringOn: Int, percent: Float, rStringOff: Int): Map<Int, Float>{
        val data = mapOf(
            rStringOn to
                    percent,
            rStringOff to
                    (100f - percent)
        )
        return data
    }

    fun preparePieDataSetForStreakComparisons(): Map<Int, Float>{
        return preparePieDataSet(
            rStringOn = R.string.visualization_pie_chart_Streak_on,
            percent = habitVizUiState.value.perCentInStreak,
            rStringOff = R.string.visualization_pie_chart_Streak_off
        )
    }

    fun preparePieDataSetForAcquiredHabits(): Map<Int, Float>{
        return preparePieDataSet(
            rStringOn = R.string.visualization_pie_chart_Acquired_on,
            percent = habitVizUiState.value.perCentAcquired,
            rStringOff = R.string.visualization_pie_chart_Acquired_off
        )
    }

}

data class HabitVizUiState(
    val habitList: List<Habit> = listOf(),
    val totalSize: Int = 0,
    val portionAcquired: Int = 0,
    val portionInStreaks: Int = 0,
    val perCentAcquired: Float = 0f,
    val perCentInStreak: Float = 0f,
)

data class PieChartData(
    val totalSize: Int = 0,

)