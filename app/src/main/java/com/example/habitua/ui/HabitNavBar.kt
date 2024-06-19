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
import androidx.navigation.NavHostController
import com.example.habitua.HabitScreen
import com.example.habitua.R


@Composable
fun HabitNavBar(
    navController: NavHostController,
    currentScreenName: String,
){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_small))
    ) {
        HabitNavButton(
            text = HabitScreen.Start.name,
            currentScreenName = currentScreenName,
            onNextButtonClicked = {
                navController.navigate(HabitScreen.Start.name)
            })
        HabitNavButton(
            text = HabitScreen.Data.name,
            currentScreenName = currentScreenName,
            onNextButtonClicked = {
                navController.navigate(HabitScreen.Data.name)
            })
        HabitNavButton(
            text = HabitScreen.Settings.name,
            currentScreenName = currentScreenName,
            onNextButtonClicked = {
                navController.navigate(HabitScreen.Settings.name)
            })
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