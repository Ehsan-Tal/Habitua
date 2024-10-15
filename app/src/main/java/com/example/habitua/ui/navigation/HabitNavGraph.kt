package com.example.habitua.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.habitua.ui.habit.HabitEditScreen
import com.example.habitua.ui.habit.HabitScreen
import com.example.habitua.ui.issues.IssueScreen
import com.example.habitua.ui.principles.PrincipleEditScreen
import com.example.habitua.ui.principles.PrincipleScreen
import com.example.habitua.ui.settings.SettingScreen
import com.example.habitua.ui.visual.VisualizationDestination
import com.example.habitua.ui.visual.VisualizationScreen

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

    NavHost(
        navController = navController,
        startDestination = PrincipleDestination.route, //HabitDestination.route,
        modifier = modifier
    ) {



        composable( route = HabitDestination.route ) {
            HabitScreen(
                currentScreenName = stringResource(id = HabitDestination.navTitle),
                navigateToHabit = { navController.navigate(HabitDestination.route) },
                navigateToPrinciple = { navController.navigate(PrincipleDestination.route) },
                navigateToIssue = { navController.navigate(IssueDestination.route) },
                navigateToYou = { navController.navigate(SettingDestination.route) },

                navigateToHabitEdit = { habitId ->
                    navController.navigate("${HabitEditDestination.route}/$habitId")
                },
            )
        }
        composable(route = PrincipleDestination.route) {
            PrincipleScreen(
                currentScreenName = stringResource(id = PrincipleDestination.navTitle),
                navigateToHabit = { navController.navigate(HabitDestination.route) },
                navigateToPrinciple = { navController.navigate(PrincipleDestination.route) },
                navigateToIssue = { navController.navigate(IssueDestination.route) },
                navigateToYou = { navController.navigate(SettingDestination.route) },

                navigateToPrincipleEdit = { id ->
                    navController.navigate("${PrincipleEditDestination.route}/$id")
                },
            )
        }
        composable(route = IssueDestination.route) {
            IssueScreen(
                currentScreenName = stringResource(id = IssueDestination.navTitle),
                navigateToHabit = { navController.navigate(HabitDestination.route) },
                navigateToPrinciple = { navController.navigate(PrincipleDestination.route) },
                navigateToIssue = { navController.navigate(IssueDestination.route) },
                navigateToYou = { navController.navigate(SettingDestination.route) },
            )
        }
        composable(route = SettingDestination.route) {
            SettingScreen(
                currentScreenName = stringResource(id = SettingDestination.navTitle),
                navigateToHabit = { navController.navigate(HabitDestination.route) },
                navigateToPrinciple = { navController.navigate(PrincipleDestination.route) },
                navigateToIssue = { navController.navigate(IssueDestination.route) },
                navigateToYou = { navController.navigate(SettingDestination.route) },
                navigateToVisualize = { navController.navigate(VisualizationDestination.route)},
            )
        }


        /**
         * The destination route contains the habit ID as a parameter
         * It's view models perform the fetch function
         *
         * The screen composable takes the navigate back and up as parameters
         */
        composable(
            route = HabitEditDestination.routeWithArgs,
            arguments = listOf(navArgument(HabitEditDestination.ID_ARG) { type = NavType.IntType })
        ) {
            HabitEditScreen(
                navigateBack = { navController.popBackStack() }
            )
        }


        composable(
            route = PrincipleEditDestination.routeWithArgs,
            arguments = listOf(navArgument(PrincipleEditDestination.ID_ARG) { type = NavType.IntType })
        ) {
            PrincipleEditScreen(
                currentScreenName = stringResource(id = SettingDestination.navTitle),
                navigateToHabit = { navController.navigate(HabitDestination.route) },
                navigateToPrinciple = { navController.navigate(PrincipleDestination.route) },
                navigateToIssue = { navController.navigate(IssueDestination.route) },
                navigateToYou = { navController.navigate(SettingDestination.route) },

        )}

        composable(route = VisualizationDestination.route) {
            VisualizationScreen(
                navigateBack = { navController.popBackStack() },
            )
        }
    }
}