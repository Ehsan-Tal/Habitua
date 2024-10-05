package com.example.habitua.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.habitua.R
import com.example.habitua.ui.home.HabitDestination
import com.example.habitua.ui.principles.PrincipleDestination
import com.example.habitua.ui.settings.SettingDestination
import com.example.habitua.ui.visual.VisualizationDestination


@Composable
fun HabitNavBar(
    navigateToHabit: () -> Unit,
    navigateToPrinciple: () -> Unit,
    navigateToVisualize: () -> Unit,
    navigateToSetting: () -> Unit,
    currentScreenName: String,
){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_small))
    ) {
        HabitNavButton(
            text = stringResource(id = HabitDestination.navTitle),
            currentScreenName = currentScreenName,
            onNextButtonClicked = navigateToHabit
        )
        HabitNavButton(
            text = stringResource(id = PrincipleDestination.navTitle),
            currentScreenName = currentScreenName,
            onNextButtonClicked = navigateToPrinciple
        )
        HabitNavButton(
            text = stringResource(id = VisualizationDestination.navTitle),
            currentScreenName = currentScreenName,
            onNextButtonClicked = navigateToVisualize
        )
        HabitNavButton(
            text = stringResource(id = SettingDestination.navTitle),
            currentScreenName = currentScreenName,
            onNextButtonClicked = navigateToSetting
        )
    }
}

@Composable
fun HabitNavButton(
    text: String,
    currentScreenName: String,
    onNextButtonClicked: () -> Unit
){
    TextButton(
        onClick = onNextButtonClicked
    ) {
        Text (
            text = text,
            style = if (currentScreenName == text) MaterialTheme.typography.displayMedium else MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small))
        )
    }

}
