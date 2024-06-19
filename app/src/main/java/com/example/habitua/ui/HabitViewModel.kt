package com.example.habitua.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.habitua.data.Habit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.habitua.data.DataSource
import kotlinx.coroutines.flow.update
import java.util.concurrent.TimeUnit


private const val TAG = "HabitViewModel"

class HabitViewModel: ViewModel() {

    // we pass in the data source over here because we didn't put in a default value over there
    // ideally this is moved to the refreshHabits page
    private val _uiState = MutableStateFlow(HabitUIState())
    val uiState: StateFlow<HabitUIState> = _uiState.asStateFlow()

    init {
        refreshHabits()
        reviewHabits()
    }

    fun refreshHabits(){
        // done on the pull down

        // ideally this would be a collection of different DBs
        // remember UI state can be completely different
        // okay ! This works, I think.
        _uiState.update { currentState ->
            currentState.copy(
                habitList = DataSource().loadAffirmations(),
            )
        }
    }


    // private checks

    private fun checkIfHabitIsAcquired(habit: Habit): Boolean {
        // needs to compare the two dates and check if they can occur in the day
        // We can assume that it would have a streak
        val isAcquired: Boolean = TimeUnit.MILLISECONDS.toDays(
            (System.currentTimeMillis() - habit.currentStreakOrigin!!)
        ) > 66
        // 66 days, needs to be edited

        return isAcquired
    }


    fun createHabit(){
        val newHabit = Habit(
            id = (_uiState.value.habitList.size + 1),
            name = "Generic habit",
            description = "Generic description"
        )
        //TODO: creating new ids

        _uiState.update { currentState ->
            currentState.copy(
                habitList = currentState.habitList + newHabit
            )
        }

    }

    // demands you update the habit
    private fun updateHabit(habit: Habit) {
        _uiState.update { currentState -> currentState.copy(
                habitList = currentState.habitList.map { unchangedHabit ->
                    if (unchangedHabit.id == habit.id) habit else unchangedHabit
                }
        )}
    }

    fun deleteHabit(habit: Habit){
        // deletes the habit from the habitList
        _uiState.update { currentState ->
            currentState.copy(
                habitList = currentState.habitList.filter { it ->
                    it.id != habit.id
                },
            )
        }
    }

    private fun markHabitAcquired(habit: Habit) {
        updateHabit(habit.copy(isAcquired = !habit.isAcquired))
    }

    fun updateHabitName(habit: Habit, newName: String){
        updateHabit(habit.copy(name = newName))
    }

    fun updateHabitDescription(habit: Habit, newDescription: String){
        updateHabit(habit.copy(description = newDescription))
    }

    fun updateHabitIcon(habit: Habit, pictureResId: Int){
        updateHabit(habit.copy(imageResourceId = pictureResId))
    }

    fun createHabitsString(): String{
        return _uiState.value.habitList.joinToString(
            separator = ", ",
            postfix = "" // so, we need to avoid any default trailing comma
        ) {
            if (it == _uiState.value.habitList.last()) {
                "and ${it.name}"
            } else {
                it.name
            }
        }
    }


    private fun reviewHabits() {
        // for every habit in parameter, beep
        //reviewHabit()
        _uiState.value.habitList.map { habit -> reviewHabit(habit) }
    }

    // ideally this should run also when an item is toggled
    private fun reviewHabit(habit: Habit) {
        val habitHasAStreak: Boolean = habit.currentStreakOrigin != null

        // run whenever the app is first run.


        if (habit.isActive && !habit.hasMissableOpportunity) {
                updateHabit(habit.copy(hasMissableOpportunity = true))
        }

        if( habitHasAStreak && checkIfHabitIsAcquired(habit)) {
            markHabitAcquired(habit)

        } else {
            if (!habit.hasMissableOpportunity) {
                updateHabit(habit.copy(hasMissableOpportunity = false))

            } else {
                breakHabitStreak(habit)
            }
        }
    }

    private fun breakHabitStreak(habit: Habit) {
        // should reset all the booleans to their default values
        // except for missed opp - that should only regen if it had been active
        // reset streak, currentStreakOrigin
        updateHabit(habit.copy(currentStreakOrigin = null))
        resetHabit(habit)

    }

    private fun resetHabit(habit: Habit) {
        updateHabit(habit.copy(hasMissableOpportunity = true))
    }


    // UI related items
    fun toggleHabitExpansion(habit: Habit){
        updateHabit(habit.copy(isExpanded = !habit.isExpanded))
    }

    fun toggleHabitActive(habit: Habit) {
        updateHabit(habit.copy(isActive = !habit.isActive))
        if ( habit.isActive ) {
             if (checkIfHabitIsAcquired(habit)) {
                markHabitAcquired(habit)
            }
        }
    }


    // change icons, text, etc.
}

