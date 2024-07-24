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

object HabitEntryDestination : NavigationDestination {
    override val route = "habit_entry"
    override val title =  R.string.habit_entry_title
}

@Composable
fun HabitEntryScreen(
    navigateBack: () -> Unit,
    viewModel: HabitEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            HabitTopAppBar(
                navigateBack = navigateBack,
                title = stringResource(HabitEntryDestination.title),
            )
        }
    ) { innerPadding ->
        HabitEntryBody(
            habitUiState = viewModel.habitUiState,
            onHabitValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveHabit()
                    navigateBack()
                }
            },
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
    habitUiState: HabitUiState,
    onHabitValueChange: (HabitDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        HabitInputForm(
            habitDetails = habitUiState.habitDetails,
            onValueChange = onHabitValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = habitUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text((stringResource(R.string.habit_save_button)))
        }
    }
}

@Composable
fun HabitInputForm(
    habitDetails: HabitDetails,
    modifier: Modifier = Modifier,
    onValueChange: (HabitDetails) -> Unit = {},
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
            value = habitDetails.name,
            onValueChange = { onValueChange(habitDetails.copy(name = it)) },
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
            value = habitDetails.description,
            onValueChange = { onValueChange(habitDetails.copy(description = it)) },
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
        if (enabled) {
            Text(
                text = stringResource(R.string.habit_save_required),
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
            )
        }

        // OPTIONAL
        // These two should ideally be a part of calculating daysTillAcquired
        Column {
            Text(
                text = "Habit Type - one-step, simple, complex"
            )
            Text(
                text = "Habit Chrono Type - daily, bi-daily, weekly"
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HabitEntryScreenPreview() {
    HabituaTheme {
        HabitEntryBody(habitUiState = HabitUiState(
            HabitDetails(
                name = "Brush Cat", description = "1-over and continue for loose hairs"
            )
        ), onHabitValueChange = {}, onSaveClick = {})
    }
}