package com.example.habitua.ui.settings

import android.provider.Contacts.SettingsColumns
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
import androidx.compose.material3.OutlinedCard
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
    override val title = R.string.setting_title
    val navTitle = R.string.setting_nav_title
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row (
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = SettingDestination.title),
                    style = MaterialTheme.typography.displayLarge,
                )
            }
            Column (
                modifier = Modifier
                    .weight(8f)
                    .padding(dimensionResource(id = R.dimen.padding_small))
            ){
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                {
                    val rowModifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
                        .padding(vertical = dimensionResource(id = R.dimen.padding_medium))

                    Row(
                        modifier = rowModifier
                    ) {
                        Text(
                            text = "Enable Notifications"
                        )
                    }

                    Row(
                        modifier = rowModifier
                    ) {
                        ToggleButton(
                            titleString = stringResource(id = R.string.preferences_dark_mode_title),
                            onString = stringResource(id = R.string.preferences_dark_mode_on),
                            offString = stringResource(id = R.string.preferences_dark_mode_off),
                            isOn = uiState.isDarkMode,
                            onToggle = viewModel::selectThemeMode,
                            onIcon = Icons.Outlined.DarkMode,
                            offIcon = Icons.Outlined.LightMode,
                        )
                    }

                    Row(
                        modifier = rowModifier
                    ) {
                        Text(
                            text = "Share"
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

                    Row{
                        Text(
                            text = "About Habitua",
                            style = MaterialTheme.typography.displayMedium
                        )

                }
                    //TODO: Cut a lot of fru fru and keep design decisions standard
                    Text(
                        text = """
                 These concepts came from my reading of 'How are habits formed' by Lally, P. et al. (2009). Inaccuracies and assumptions from my design decisions are my responsibility.

                 Acquirement times: Very dependent on its factors, particularly the activity's step complexity. 66 days is an average, though acquisition can come a lot sooner or a lot later. The more chances the mind has to move the self to another action, the more chances it will do so, etc.

                 Missed Opportunities: Missing a day does not break your streak - why ? Well, a single day does not translate into SIGNIFICANTLY longer acquirement times.
                             """.trimIndent()
                    )
                }
            }
            HabitNavBar(
                navController = navController,
                currentScreenName = currentScreenName
            )
        }
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
            text = if (isOn) onString else offString,
            style = MaterialTheme.typography.displaySmall
        )
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
    }
}