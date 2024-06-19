package com.example.habitua

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.habitua.data.Habit
import com.example.habitua.ui.HabitViewModel
import com.example.habitua.ui.SettingScreen
import com.example.habitua.ui.TapperScreen
import com.example.habitua.ui.VisualizationScreen




/* enum values define screens within the app - aosp - ehsan*/
// ideally this would be a string resource, but eyy, more work later
// focus on the complex stuff now
enum class HabitScreen(title: String) {
    Start(title = "Your Habits"),
    Data(title = "Your Data"),
    Settings(title = "Your Settings")
}

private const val TAG = "HabitScreen"

// the root application for my application
@Composable
fun HabitApp(
    habitViewModel: HabitViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
    ) {
    // Get current back stack entry - apparently necessary for collecting the current screen
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = HabitScreen.valueOf(
        backStackEntry?.destination?.route ?: HabitScreen.Start.name
    )

    // we do not want that ! ^

    Scaffold {
        innerPadding ->
        val uiState by habitViewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = HabitScreen.Start.name,
            modifier = Modifier.padding(innerPadding)

        ) {
            composable(route = HabitScreen.Start.name) {
            // this now requires the top level composable for each screen
                // and use this to pass in whatever the top level composable demands.
                TapperScreen (
                    habitViewModel = habitViewModel,
                    habitList = uiState.habitList,
                    currentScreenName = currentScreen.name,
                    navController = navController
                )
            }
            composable(route = HabitScreen.Data.name) {
                VisualizationScreen (
                    habitViewModel = habitViewModel,
                    habitList = uiState.habitList,
                    currentScreenName = currentScreen.name,
                    navController = navController,
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = HabitScreen.Settings.name) {
                val context = LocalContext.current
                SettingScreen (
                    habitViewModel = habitViewModel,
                    currentScreenName = currentScreen.name,
                    navController = navController,
                    // so to keep the share habit private, we can pass in the things
                    onShareButtonClicked = {
                        subject: String, habits: String ->
                        shareHabitTracking(context, subject = subject, habits = habits)
                                           },
                    // will learn more about what to do here when the preferences module goes along
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}


// I think it'd be fun to share habits, there should be a button to take all the habits and share.
private fun shareHabitTracking(context: Context, subject: String, habits: String) {
    // createHabitsString() <- requires this

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        // we need to make a string of it
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, habits)
    }
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.new_habit_receipt)
        )
    )
}
