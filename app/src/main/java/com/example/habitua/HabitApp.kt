package com.example.habitua

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.habitua.ui.navigation.HabitNavHost


@Composable
fun HabitApp(navController: NavHostController = rememberNavController()){
    HabitNavHost(navController = navController)
}

@Composable
fun HabitBottomAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {}
    // is there a scrollBehavior for bottom App bar ?
){
    BottomAppBar{
        if(canNavigateBack) {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.content_description_navigate_back)
                )
                Text(
                    title
                )
            }
        }
    }

}
// needs 3 buttons normally - in the entry view
// we can allow the back button



/*

// this needs a big retro-fitting
/* enum values define screens within the app - aosp - ehsan*/
// ideally this would be a string resource, but eyy, more work later
// focus on the complex stuff now

enum class AppScreen(title: String) {
    Start(title = "Your Habits"),
    Data(title = "Your Data"),
    Settings(title = "Your Settings")
}

private const val TAG = "AppScreen"

// the root application for my application
@Composable
fun HabitApp(
    habitViewModel: HomeViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
    ) {
    // Get current back stack entry - apparently necessary for collecting the current screen
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route ?: AppScreen.Start.name
    )

    // we do not want that ! ^

    Scaffold {
        innerPadding ->
        val uiState by habitViewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = AppScreen.Start.name,
            modifier = Modifier.padding(innerPadding)

        ) {
            composable(route = AppScreen.Start.name) {
            // this now requires the top level composable for each screen
                // and use this to pass in whatever the top level composable demands.
                HabitScreen (
                    habitViewModel = habitViewModel,
                    habitList = uiState.habitList,
                    currentScreenName = currentScreen.name,
                    navController = navController
                )
            }
            composable(route = AppScreen.Data.name) {
                VisualizationScreen (
                    habitViewModel = habitViewModel,
                    habitList = uiState.habitList,
                    currentScreenName = currentScreen.name,
                    navController = navController,
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = AppScreen.Settings.name) {
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
*/