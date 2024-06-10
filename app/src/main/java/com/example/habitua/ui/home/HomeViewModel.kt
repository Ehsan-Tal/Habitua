package com.example.habitua.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.habitua.data.ConditionLogs
import com.example.habitua.data.Habit
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import com.example.habitua.data.HabitDao
import com.example.habitua.data.StreakLogsDao
import com.example.habitua.data.ConditionLogsDao
import com.example.habitua.data.StreakLogs
import kotlinx.coroutines.launch
/*
@Inject constructor(
    private val habitDao: HabitDao,
    private val streakLogsDao: StreakLogsDao,
    private val conditionLogsDao: ConditionLogsDao
)
* */

class HomeViewModel : ViewModel() {

    private val _habitList = MutableLiveData<List<Habit>>()

    val habitList: LiveData<List<Habit>> = _habitList
    /*
    init {
        viewModelScope.launch {
            habitDao.getCurrentHabits()
                .asLiveData()
                .observeForever{
                    _habitList.value = it
                }

        }
    }

    suspend fun onHabitToggled(habit: Habit, isActive: Boolean) {
        val updatedHabit = habit.copy(isActive = isActive)
        habitDao.updateHabit(updatedHabit)

        // streak insertion
        if (isActive){
            if (!streakLogsDao.isStreakActive(habit.id)) {
                val startDate = System.currentTimeMillis()
                streakLogsDao.insert(StreakLogs(habitId = habit.id, startDate = startDate) )
                updatedHabit.currentStreakOrigin = startDate
                habitDao.updateHabit(updatedHabit)
            }

            // Acquired Logic
            if (habit.currentStreakOrigin != null) {
                if ((System.currentTimeMillis() - habit.currentStreakOrigin!!) / (1000 * 60 * 60 * 24) >= 66) {
                    // ideally it should ask at separate ranges
                    // ask user if they have acquired it
                    // assume yes
                    habitDao.updateHabit(habit.copy(isAcquired = true))
                    Log.d("HomeViewModel", "Habit ${habit.name} acquired at ${getCurrentDateTime()}")

                }
            }
        }
    }

    fun handleHabitDelete(habit: Habit){
        viewModelScope.launch {
            habitDao.delete(habit)
        }
    }

    fun updateHabitName(habit: Habit, newName: String) {
        viewModelScope.launch {
            habitDao.updateHabit(habit.copy(name = newName))

        }
    }

    fun updateHabitCondition(habit: Habit, newCondition: String) {
        viewModelScope.launch {
            habitDao.updateHabit(habit.copy(condition = newCondition ))
            conditionLogsDao.insert(ConditionLogs(habitId = habit.id, condition = newCondition, startDate = System.currentTimeMillis()))
        }
    }

    fun handleHabitCreate(habit: Habit){
        viewModelScope.launch {
            habitDao.insert(habit)
        }
    }

    suspend fun handleStreakBreakingLogic() {
        habitDao.getCurrentHabits()
            .collect { habits ->
                habits.forEach { habit ->
                    if (habit.isActive) {
                        if (habit.hasMissedOpportunity) {
                            habitDao.updateHabit(habit.copy(hasMissedOpportunity = false))
                        }
                    } else {
                        if (!habit.hasMissedOpportunity) {
                            habitDao.updateHabit(habit.copy(hasMissedOpportunity = true))
                        } else {
                            // handle the streak break
                            val brokenStartDate = habit.currentStreakOrigin

                            habitDao.updateHabit(
                                habit.copy(
                                    hasMissedOpportunity = false,
                                    currentStreakOrigin = null
                                )
                            )

                            brokenStartDate?.let {
                                streakLogsDao.updateEndDate(habit.id, brokenStartDate, System.currentTimeMillis())
                            }
                        }
                    }
                }
            }
    }
    */

    private fun getCurrentDateTime(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return current.format(formatter)

    }

    fun logHabitChange(habit: Habit) {
        Log.d("HomeViewModel", "Habit ${habit.name} changed at ${getCurrentDateTime()}")
    }
}
