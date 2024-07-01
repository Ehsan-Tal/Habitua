package com.example.habitua.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.habitua.ui.settings.SettingDestination
import com.example.habitua.ui.settings.SettingScreen
import com.example.habitua.ui.visual.VisualizationDestination
import com.example.habitua.ui.visual.VisualizationScreen
import com.example.habitua.ui.habit.HabitEditDestination
import com.example.habitua.ui.habit.HabitEditScreen
import com.example.habitua.ui.habit.HabitEntryDestination
import com.example.habitua.ui.habit.HabitEntryScreen
import com.example.habitua.ui.home.HabitDestination
import com.example.habitua.ui.home.HabitScreen

// what tis a nav graph ?
// ah, it makes the graph for navigation
@Composable
fun HabitNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HabitDestination.route,
        modifier = modifier
    ) {
        // Habit Home
        composable(route = HabitDestination.route) {
            HabitScreen(
                currentScreenName = HabitDestination.title,

                navigateToHabitEntry = {
                    navController.navigate(HabitEntryDestination.route)
                                      },
                navigateToHabitEdit = {
                    navController.navigate("${HabitEditDestination.route}/${it}")
                },
                navController = navController
                )
        }
        // Habit Entry
        composable( route = HabitEntryDestination.route ) {
            HabitEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        // Habit Edit
        //TODO needs an id based thing !
        composable(
            route = HabitEditDestination.routeWithArgs,
            arguments = listOf(navArgument(HabitEditDestination.HABIT_ID_ARG) {
                type = NavType.IntType
            })
        ) {
            HabitEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        // Data Visualization
        composable(route = VisualizationDestination.route) {
            VisualizationScreen(
                currentScreenName = VisualizationDestination.title,
                navController = navController
            )
        }

        // Setting
        composable(route = SettingDestination.route) {
            SettingScreen(currentScreenName = SettingDestination.title, navController = navController)
        }


    }
}