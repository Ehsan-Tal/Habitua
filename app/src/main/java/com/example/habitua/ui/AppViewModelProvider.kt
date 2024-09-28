package com.example.habitua.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.habitua.HabitApplication
import com.example.habitua.ui.habit.HabitEditViewModel
import com.example.habitua.ui.home.HomeViewModel
import com.example.habitua.ui.habit.HabitEntryViewModel
import com.example.habitua.ui.settings.SettingViewModel
import com.example.habitua.ui.visual.VisualizationViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // a whole group of initializers for the other view models

        // initializer for home
        initializer {
            HomeViewModel(
                habitApplication().container.appRepository
            )
        }

        // initializer for habit Entry
        // -update used h instead of H, so now it works
        initializer {
            HabitEntryViewModel(habitApplication().container.appRepository)
        }

        // initializer for habit edit
        initializer {
            HabitEditViewModel(
                this.createSavedStateHandle(),
                habitApplication().container.appRepository
            )
        }


        // initializer for ui.visual
        /*
        initializer {
            VisualizationViewModel(
                habitApplication().container.appRepository
            )
        }
         */

        // initializer for ui.settings
        initializer {
            SettingViewModel(
                habitApplication().container.userPreferencesRepository,
                habitApplication().container.appRepository
            )
        }

    }
}

// This makes Application colored in - would this effect the item ?
/**
 * Extension function to queries for [Application] object and returns an instance of
 * [HabitApplication].
 */
fun CreationExtras.habitApplication(): HabitApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as HabitApplication)