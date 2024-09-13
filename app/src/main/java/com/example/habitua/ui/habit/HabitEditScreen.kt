package com.example.habitua.ui.habit


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitua.HabitTopAppBar
import com.example.habitua.R
import com.example.habitua.data.Habit
import com.example.habitua.ui.AppViewModelProvider
import com.example.habitua.ui.navigation.NavigationDestination
import com.example.habitua.ui.theme.HabituaTheme
import com.example.habitua.ui.theme.PreviewHabituaTheme
import kotlinx.coroutines.launch

object HabitEditDestination: NavigationDestination{
    override val route = "habit_edit"
    override val title = R.string.habit_edit_title
    const val HABIT_ID_ARG = "habitId"
    val routeWithArgs = "$route/{$HABIT_ID_ARG}"
}


@Composable
fun HabitEditScreen(
    navigateBack: () -> Unit,
    viewModel: HabitEditViewModel = viewModel(factory = AppViewModelProvider.Factory)

){
    val coroutineScope = rememberCoroutineScope()

    HabitEditBody(
        habitUiState = viewModel.habitUiState,
        onHabitValueChange = viewModel::updateUiState,
        onSaveClick = {
            coroutineScope.launch {
                viewModel.updateHabit()
                navigateBack()
            }
        },
        onDelete = {
            coroutineScope.launch {
                viewModel.deleteHabit()
                navigateBack()
            }
        },
        navigateBack = navigateBack
    )
}

@Composable
fun HabitEditBody (
    habitUiState: HabitEditUiState,
    onHabitValueChange: (Habit) -> Unit,
    onSaveClick: () -> Unit,
    onDelete: () -> Unit,
    navigateBack: () -> Unit,
){
    Scaffold(
        topBar = {
                HabitTopAppBar(
                    title = stringResource(id = HabitEditDestination.title),
                    navigateBack = navigateBack,
                )
        },
    ){
        innerPadding ->
        HabitEditSkeleton(
            habitUiState = habitUiState,
            onHabitValueChange = onHabitValueChange,
            onSaveClick = onSaveClick,
            onDelete = onDelete,
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
fun HabitEditSkeleton (
    habitUiState: HabitEditUiState,
    onHabitValueChange: (Habit) -> Unit,
    onSaveClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
    ){
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

    Column (
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_large)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large))
    ) {
        HabitEditForm(
            habitValues = habitUiState.habit,
            onValueChange = onHabitValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = habitUiState.isValid,
            modifier = Modifier.fillMaxWidth()

        ) {
            Text(text = stringResource(id = R.string.habit_button_update))
        }
        Button (//onDelete
            onClick = { deleteConfirmationRequired = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.error,
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.habit_button_delete)
            )
        }
        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    onDelete()
                },
                onDeleteCancel = {
                    deleteConfirmationRequired = false
                },
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_large))
            )
        }
        
    }
}

@Composable
fun HabitEditForm(
    habitValues: Habit,
    modifier: Modifier = Modifier,
    onValueChange: (Habit) -> Unit = {},
){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large))
    ){
        // name
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(id = R.string.form_required_name)) },
            value = habitValues.name,
            onValueChange = { onValueChange(habitValues.copy(name = it)) },
            singleLine = true
        )

        // description
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(id = R.string.form_required_description)) },
            value = habitValues.description,
            onValueChange = { onValueChange(habitValues.copy(description = it)) },
            singleLine = true
        )

        // currentStreakOrigin != null
        if (habitValues.currentStreakOrigin != null) {
            Text(
                text = stringResource(id = R.string.habit_edit_prefix_date),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                text = habitValues.formattedOriginDate(),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.displaySmall
            )
            if (habitValues.streakLength() > 0) {
                Text(
                    text = stringResource(
                        R.string.streak_length_multiple_days_count,
                        habitValues.streakLength()
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.displaySmall

                )
            }
            else if (habitValues.streakLength() == 1) {
                Text(
                    text = stringResource(R.string.streak_length_single_day_count),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.displaySmall

                )
            } else {
                Text(
                    text = stringResource(R.string.streak_length_zero_day_count),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.displaySmall

                )
            }
        }
    }
}

// We need this
@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
){
    AlertDialog(
        onDismissRequest = {},
        title = {Text(stringResource(id = R.string.dialog_title))},
        text = {Text(stringResource(id = R.string.dialog_question_delete))},
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = stringResource(id = R.string.dialog_negation))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(text = stringResource(id = R.string.dialog_affirmation))
            }
        }
    )
}


@Preview(
    showBackground = true,
    name = "Habit-Edit-Screen-Light-Mode",
    group = "HabitEditScreens"
)
@Composable
fun HabitEditScreenPreviewLight(){
    PreviewHabituaTheme(darkTheme = false){
        HabitEditBody(
            habitUiState = HabitEditUiState(habit=Habit(name ="Race Walking", description = "Fear Not, I can catch him")),
            onHabitValueChange = {},
            onSaveClick = {},
            onDelete = {},
            navigateBack = {}
        )
    }
}

@Preview(
    showBackground = true,
    name = "Habit-Edit-Screen-Dark-Mode",
    group = "HabitEditScreens"
)
@Composable
fun HabitEditScreenPreviewDark(){
    PreviewHabituaTheme(darkTheme = true){
        HabitEditBody(
            habitUiState = HabitEditUiState(habit=Habit(name ="Race Walking", description = "Fear Not, I can catch him")),
            onHabitValueChange = {},
            onSaveClick = {},
            onDelete = {},
            navigateBack = {}
        )
    }
}