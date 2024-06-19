package com.example.habitua.data

import com.example.habitua.R
import com.example.habitua.data.Habit
import java.util.Date

class DataSource {
    fun loadAffirmations(): List<Habit>{
        return listOf<Habit>(
            Habit(
                1,
                R.drawable.tal_derpy,
                "Pet Cat",
                "7 times at least",
                System.currentTimeMillis(),
            ),
            Habit(
                2,
                R.drawable.tal_derpy,
                "Water Cat",
                "Refill his water bowl before breakfast",
                System.currentTimeMillis(),
            ),
        )
    }
}