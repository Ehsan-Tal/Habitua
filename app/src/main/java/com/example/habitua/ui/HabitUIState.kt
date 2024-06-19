package com.example.habitua.ui

import com.example.habitua.data.Habit

// this is for the UI State, not for any specific data point
data class HabitUIState(
    val habitList: List<Habit> = listOf(),
    val currentHabitCount: Int = 0,
)

// also store counts of various categories of habits
