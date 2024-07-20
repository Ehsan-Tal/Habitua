package com.example.habitua.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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

/**
 * Main navigation graph for the app.
 *
 * @param navController The navigation controller for the app.
 * @param modifier The modifier to apply to the navigation graph.
 */
@Composable
fun HabitNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    /**
     * This composable is the root of the navigation graph.
     *
     * sets the StartDestination of the application
     */
    NavHost(
        navController = navController,
        startDestination = SettingDestination.route, //HabitDestination.route,
        modifier = modifier
    ) {

        /**
         * The setting screen
         *
         * Takes the name of the current screen as a parameter
         * Takes the name of navigation controller as a parameter
         */
        composable(route = SettingDestination.route) {
            SettingScreen(
                currentScreenName = stringResource(id = SettingDestination.navTitle),
                navController = navController
            )
        }
    }
}