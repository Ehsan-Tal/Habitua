package com.example.habitua


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.habitua.ui.theme.HabituaTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabituaTheme {
                HabitApp()
            }
        }
    }
}

/// this is kept empty - the navigation is in HabitApp (HabitScreen.kt)
// the individual pages are kept in .ui/