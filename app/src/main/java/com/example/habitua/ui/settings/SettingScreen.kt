package com.example.habitua.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.habitua.R
import com.example.habitua.ui.AppViewModelProvider
import com.example.habitua.ui.HabitNavBar
import com.example.habitua.ui.home.HabitDestination
import com.example.habitua.ui.navigation.NavigationDestination
import com.example.habitua.ui.theme.HabituaTheme
import com.example.habitua.ui.visual.VisualizationDestination

object SettingDestination: NavigationDestination {
    override val route = "settings"
    override val title = R.string.setting_title
    val navTitle = R.string.setting_nav_title
}

// we need a button to initiate the toggling
@Composable
fun SettingScreenWrapperCHANGESOON (
    currentScreenName: String,
    navController: NavHostController,
    //onShareButtonClicked: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = viewModel(factory = AppViewModelProvider.Factory),
    uiState: SettingUiState = viewModel.uiState.collectAsState().value,
) {

}

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
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_large)),
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
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant,
                            RectangleShape
                        )
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ){
                    val context = LocalContext.current
                    var showDialog by remember { mutableStateOf(false) }

                    val hasNotificationPermission by viewModel.hasNotificationPermission
                        .collectAsState()
                    val canCreateTestData by viewModel.canCreateTestData
                        .collectAsState()

                    val rowModifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_large))
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            MaterialTheme.shapes.small
                        )

                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.onPrimaryContainer)
                    ){

                    // Notification Permission
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        viewModel.checkNotificationPermission(context)

                        EnableNotificationDialog(
                            showDialog = showDialog,
                            onDismiss = {showDialog = false}
                        )

                        OutlinedCard(
                            colors = CardDefaults.outlinedCardColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            shape = MaterialTheme.shapes.small,
                            modifier = rowModifier
                                .clickable {
                                    if(!hasNotificationPermission) {
                                        showDialog = ! showDialog
                                    }
                                },
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(dimensionResource(id = R.dimen.padding_small))
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(dimensionResource(id = R.dimen.padding_large)),
                                    text = if (hasNotificationPermission)
                                        stringResource(R.string.notifications_enabled)
                                    else
                                        stringResource(R.string.notifications_disabled),
                                )
                            }
                        }
                    }

                    // DarkMode / LightMode
                    OutlinedCard(
                        colors = CardDefaults.outlinedCardColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = MaterialTheme.shapes.small,
                        modifier = rowModifier
                            .clickable { viewModel.selectThemeMode(!uiState.isDarkMode) }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(dimensionResource(id = R.dimen.padding_small))
                                .fillMaxWidth()
                        ){
                            Text(
                                text = if (uiState.isDarkMode)
                                    stringResource(id = R.string.preferences_dark_mode_on)
                                else stringResource(id = R.string.preferences_dark_mode_off),

                                style = MaterialTheme.typography.displaySmall,

                                modifier = Modifier
                                    .padding(dimensionResource(id = R.dimen.padding_large))
                            )
                            Icon(
                                imageVector = if (uiState.isDarkMode)
                                    Icons.Outlined.DarkMode
                                else Icons.Outlined.LightMode,

                                tint = MaterialTheme.colorScheme.tertiary,

                                contentDescription = if (uiState.isDarkMode)
                                    stringResource(id = R.string.preferences_dark_mode_on)
                                else stringResource(id = R.string.preferences_dark_mode_off),
                            )
                        }
                    }

                    // Share buttons
                    //TODO: Cancel share or keep it
                    OutlinedCard(
                        colors = CardDefaults.outlinedCardColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = MaterialTheme.shapes.small,
                        modifier = rowModifier
                            .clickable {

                            }
                        //onShareButtonClicked(newReceipt, habitViewModel.createHabitsString())
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(dimensionResource(id = R.dimen.padding_small))
                                .fillMaxWidth()
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(dimensionResource(id = R.dimen.padding_large)),
                                text = "Share"
                            )
                        }
                    }


                        // creating test data
                OutlinedCard(
                    colors = CardDefaults.outlinedCardColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = MaterialTheme.shapes.small,
                    modifier = rowModifier
                        .clickable {
                            if (canCreateTestData) {
                                viewModel.createTestData()
                            } else {
                                viewModel.deleteAllHabits()
                            }
                            // if(count)
                        },
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding_small))
                            .fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(dimensionResource(id = R.dimen.padding_large)),
                            text = if (canCreateTestData)
                                "Create Test Data"
                            else
                                "Delete Test Data",
                        )
                    }
                }

                    // download your data
                        OutlinedCard(
                            colors = CardDefaults.outlinedCardColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            shape = MaterialTheme.shapes.small,
                            modifier = rowModifier
                                .clickable {
                                    if (!canCreateTestData) {
                                        viewModel.downloadData()
                                    }
                                    // if(count)
                                },
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(dimensionResource(id = R.dimen.padding_small))
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(dimensionResource(id = R.dimen.padding_large)),
                                    text = if (canCreateTestData)
                                        "No Data to Download"
                                    else
                                        "Download your data",
                                )
                            }
                        }


                    }
                }
            }

            HabitNavBar(
                navigateToHabitDestination = { navController.navigate(HabitDestination.route)},
                navigateToVisualizeDestination = { navController.navigate(VisualizationDestination.route)},
                navigateToSettingDestination = { navController.navigate(SettingDestination.route)},
                currentScreenName = currentScreenName
            )
        }
    }
}

@Composable
fun EnableNotificationDialog(showDialog: Boolean, onDismiss: () -> Unit) {
    val context = LocalContext.current
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.dialog_reminder_notification_title)) },
            text = { Text(stringResource(R.string.dialog_reminder_notification_paragraph)) },
            confirmButton = {
                Button(onClick = {
                    // Navigate to app settings
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                    onDismiss() // Close the dialog
                }) {
                    Text(stringResource(id = R.string.dialog_affirmation))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(id = R.string.dialog_negation))
                }
            }
        )
    }
}


@Preview
@Composable
fun PreviewSettings(){
    HabituaTheme {
    }
}