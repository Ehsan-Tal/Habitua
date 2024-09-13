package com.example.habitua.ui.habit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitua.HabitTopAppBar
import com.example.habitua.R
import com.example.habitua.ui.AppViewModelProvider
import com.example.habitua.ui.theme.HabituaTheme
import kotlinx.coroutines.launch
import com.example.habitua.ui.navigation.NavigationDestination
import com.example.habitua.ui.theme.PreviewHabituaTheme

object HabitEntryDestination : NavigationDestination {
    override val route = "habit_entry"
    override val title =  R.string.habit_entry_title
}

//TODO: reduce the renderer's reliance on the view model

@Composable
fun HabitEntryPostview(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HabitEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val coroutineScope = rememberCoroutineScope()

    HabitEntryScreen(
        name = viewModel.habitUiState.habitDetails.name,
        description = viewModel.habitUiState.habitDetails.description,
        isEntryValid = viewModel.habitUiState.isEntryValid,
        onNameChange = viewModel::updateName,
        onDescriptionChange = viewModel::updateDescription,
        onSaveClick = {
            coroutineScope.launch {
                viewModel.saveHabit()
                navigateBack()
            }
        },
        navigateBack = navigateBack

    )
}

@Composable
fun HabitEntryScreen(
    name: String,
    description: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    isEntryValid: Boolean,
    navigateBack: () -> Unit,
) {

    Scaffold(
        topBar = {
            HabitTopAppBar(
                navigateBack = navigateBack,
                title = stringResource(HabitEntryDestination.title),
            )
        }
    ) { innerPadding ->
        HabitEntryBody(
            name = name,
            description = description,
            onNameChange = onNameChange,
            onDescriptionChange = onDescriptionChange,
            onSaveClick = onSaveClick,
            isEntryValid = isEntryValid,
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun HabitEntryBody(
    name: String,
    onNameChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    isEntryValid: Boolean,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        HabitInputForm(
            name = name,
            description = description,
            onNameChange = onNameChange,
            onDescriptionChange = onDescriptionChange,
            onSaveClick = onSaveClick,
            isEntryValid = isEntryValid,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text((stringResource(R.string.habit_save_button)))
        }
    }
}

@Composable
fun HabitInputForm(
    name: String,
    onNameChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    isEntryValid: Boolean,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        // how are we going to have a field for selecting the images ?
        // we need some way for the img to be represented as valid buttons
        // that pass off their resId - how and how.
        OutlinedTextField(
            value = name,
            onValueChange = { onNameChange(it) },
            label = { Text(stringResource(R.string.habit_save_name)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = description,
            onValueChange = { onDescriptionChange(it) },
            label = { Text(stringResource(R.string.habit_save_description)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )

        //TODO: connect these two items with the viewModel.
        // they need the enum class - and a function to pass the value
        // it should only allow one button to be pressed
        // it should have values according to the enum
        // the selected one should be coloured with tertiary colours.

        Text(
            text = "Complexity",
            style = MaterialTheme.typography.bodyLarge
        )
        Row {
            ElevatedButton(
                onClick = { }
            ) {
                Text("Filled")
            }

            ElevatedButton(
                onClick = { }
            ) {
                Text("Filled")
            }

            ElevatedButton(
                onClick = { }
            ) {
                Text("Filled")
            }
        }


        Text(
            text = "Frequency",
            style = MaterialTheme.typography.bodyLarge
        )
        Row(

        ){

            ElevatedButton(
                onClick = { }
            ) {
                Text("Filled")
            }

            ElevatedButton(
                onClick = { }
            ) {
                Text("Filled")
            }

            ElevatedButton(
                onClick = { }
            ) {
                Text("Filled")
            }
        }


        //

        if (enabled) {
            Text(
                text = stringResource(R.string.habit_save_required),
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
            )
        }

    }
}

@Preview(
    showBackground = true,
    name = "Habit-Entry-Screen-Light-Mode",
    group = "HabitEntryScreens")
@Composable
private fun HabitEntryScreenPreviewLight() {
    PreviewHabituaTheme (darkTheme = false){
        HabitEntryScreen(
            name = "Funny",
            description = "Bunny",
            onNameChange = {},
            onDescriptionChange = {},
            onSaveClick = {},
            navigateBack = {},
            isEntryValid = true
        )
    }
}

@Preview(
    showBackground = true,
    name = "Habit-Entry-Screen-Dark-Mode",
    group = "HabitEntryScreens")
@Composable
private fun HabitEntryScreenPreviewDark() {
    PreviewHabituaTheme (darkTheme = true){
        HabitEntryScreen(
            name = "Funny",
            description = "Bunny",
            onNameChange = {},
            onDescriptionChange = {},
            onSaveClick = {},
            navigateBack = {},
            isEntryValid = true
        )
    }
}