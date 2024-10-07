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
import com.example.habitua.ui.habit.HabitDestination
import com.example.habitua.ui.issues.IssueDestination
import com.example.habitua.ui.principles.PrincipleDestination
import com.example.habitua.ui.settings.SettingDestination


@Composable
fun AppNavBar(
    navigateToHabit: () -> Unit,
    navigateToPrinciple: () -> Unit,
    navigateToIssue: () -> Unit,
    navigateToYou: () -> Unit,
    currentScreenName: String,
){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
    ) {
        AppNavButton(
            text = stringResource(id = HabitDestination.navTitle),
            currentScreenName = currentScreenName,
            onNextButtonClicked = navigateToHabit
        )
        AppNavButton(
            text = stringResource(id = PrincipleDestination.navTitle),
            currentScreenName = currentScreenName,
            onNextButtonClicked = navigateToPrinciple
        )
        AppNavButton(
            text = stringResource(id = IssueDestination.navTitle),
            currentScreenName = currentScreenName,
            onNextButtonClicked = navigateToIssue
        )
        AppNavButton(
            text = stringResource(id = SettingDestination.navTitle),
            currentScreenName = currentScreenName,
            onNextButtonClicked = navigateToYou
        )
    }
}

@Composable
fun AppNavButton(
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
