package com.example.habitua.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.habitua.R
import com.example.habitua.ui.AppViewModelProvider
import com.example.habitua.ui.HabitNavBar
import com.example.habitua.ui.navigation.NavigationDestination
import com.example.habitua.ui.theme.HabituaTheme

object SettingDestination: NavigationDestination {
    override val route = "settings"
    override val title = "Settings"
}

// we need a button to initiate the toggling

@Composable
fun SettingScreen (
    currentScreenName: String,
    navController: NavHostController,
    //onShareButtonClicked: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = viewModel(factory = AppViewModelProvider.Factory),
    uiState: SettingUiState = viewModel.uiState.collectAsState().value,
) {
    val newReceipt = stringResource(R.string.new_habit_receipt)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        // floater button
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row (
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = (stringResource(id = R.string.screen_your) + currentScreenName), // should be a str resource,
                    style = MaterialTheme.typography.displayLarge,
                )
            }
            Column (
                modifier = Modifier
                    .weight(8f)
                    .padding(innerPadding)
            ){
                SettingColumn(
                    uiState = uiState,
                    viewModel = viewModel,
                )
                /*

                // I'm keeping this here, the onClick must exist elsewhere
                Button(
                    onClick = {
                        onShareButtonClicked(newReceipt, habitViewModel.createHabitsString())
                    }
                ){

                }*/
            }
            HabitNavBar(
                navController = navController,
                currentScreenName = currentScreenName
            )
        }
    }
}

@Composable
fun SettingColumn(
    uiState: SettingUiState,
    viewModel: SettingViewModel,
) {

    Column (
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.padding_small))
            .fillMaxSize()
            .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ){

        //dark mode to light mode
        ToggleButton(
            titleString = stringResource(id = R.string.preferences_dark_mode_title),
            onString = stringResource(id = R.string.preferences_dark_mode_on),
            offString = stringResource(id = R.string.preferences_dark_mode_off),
            isOn = uiState.isDarkMode,
            onToggle = viewModel::selectThemeMode,
            onIcon = Icons.Outlined.DarkMode,
            offIcon = Icons.Outlined.LightMode,
        )

        LocaleDropdown()
        // need a locale list
        // expanded state
        // selected locale is a mutable state
        // a dropdown menu
        // we then need for each each of the supported locale lists
        // import locale Manager and do it there.

        //w we need some reference to current Loacle
    }
}

@Composable
fun LocaleDropdown(){

//    val supportedLocales = listOf("en") // edit this to add more locales- alongside everything else
//
//    var selectedLocale by remember {
//        mutableStateOf(currentLocale.ifEmpty{ "Not Set" })
//    }

    //I'm going to work on the locales later
    //There seems to be a way even if it's a bit clunky

        Column (
            modifier = Modifier
                .fillMaxWidth()
        ){

        }
}

@Composable
fun ToggleButton(
    titleString: String,
    onString: String,
    offString: String,
    isOn: Boolean,
    onToggle: (Boolean) -> Unit,
    onIcon: ImageVector,
    offIcon: ImageVector,
){

    Column {
        Text(
            text = titleString,
            style = MaterialTheme.typography.displaySmall
        )
        //TODO: find some other way to incorporate a toggle Button
        IconToggleButton(
            checked = isOn,
            onCheckedChange = onToggle
        ) {
            Icon(
                imageVector = if (isOn) onIcon else offIcon,
                contentDescription = if (isOn) onString else offString
            )
        }

    }
}

@Preview
@Composable
fun PreviewSettings(){
    HabituaTheme {
        ToggleButton(
            titleString = stringResource(id = R.string.preferences_dark_mode_title),
            onString = stringResource(id = R.string.preferences_dark_mode_on),
            offString = stringResource(id = R.string.preferences_dark_mode_off),
            isOn = true,
            onToggle = {},
            onIcon = Icons.Outlined.DarkMode,
            offIcon = Icons.Outlined.LightMode,
        )
    }
}