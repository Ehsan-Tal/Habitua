package com.example.habitua.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.habitua.ui.AppViewModelProvider
import com.example.habitua.ui.habit.HabitEditDestination
import com.example.habitua.ui.habit.HabitEditScreen
import com.example.habitua.ui.habit.HabitEntryDestination
import com.example.habitua.ui.habit.HabitEntryPostview
import com.example.habitua.ui.habit.HabitEntryScreen
import com.example.habitua.ui.home.HabitDestination
import com.example.habitua.ui.home.HabitScreen
import com.example.habitua.ui.settings.SettingDestination
import com.example.habitua.ui.settings.SettingScreen
import com.example.habitua.ui.visual.VisualizationDestination

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
        startDestination = HabitDestination.route, //HabitDestination.route,
        modifier = modifier
    ) {

        /**
         * The home screen
         * The screen takes in its name, nav controller, navigate to habit entry, and navigate to habit edit
         */
        composable( route = HabitDestination.route ) {
            HabitScreen(
                currentScreenName = stringResource(id = HabitDestination.navTitle),

                navigateToHabitEntry = {
                    navController.navigate(HabitEntryDestination.route)
                                      },
                navigateToHabitEdit = {
                    navController.navigate("${HabitEditDestination.route}/${it}")
                },
                navController = navController,
                )
        }

        /**
         * The habit entry screen
         * The screen composable takes the navigate back and up as parameters
         */
        composable( route = HabitEntryDestination.route ) {
            HabitEntryPostview(
                navigateBack = { navController.popBackStack() }
            )
        }

        /**
         * The habit edit screen
         *
         * The destination route contains the habit ID as a parameter
         * It's view models perform the fetch function
         *
         * The screen composable takes the navigate back and up as parameters
         */
        composable(
            route = HabitEditDestination.routeWithArgs,
            arguments = listOf(navArgument(HabitEditDestination.HABIT_ID_ARG) {
                type = NavType.IntType
            })
        ) {
            HabitEditScreen(
                navigateBack = { navController.popBackStack() }
            )
        }

        /**
         * The data visualization screen
         *
         * Takes the name of the current screen as a parameter
         * Takes the name of navigation controller as a parameter
         */
        /*
        composable(route = VisualizationDestination.route) {
            VisualizationScreen(
                currentScreenName = stringResource(id = VisualizationDestination.navTitle),
                navController = navController
            )
        }

         */

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