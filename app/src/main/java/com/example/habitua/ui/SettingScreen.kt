package com.example.habitua.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.habitua.R
import com.example.habitua.data.Habit

@Composable
fun SettingScreen (
    habitViewModel: HabitViewModel,
    currentScreenName: String,
    navController: NavHostController,
    onShareButtonClicked: (String, String) -> Unit,
    modifier: Modifier = Modifier
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
                    text = ("Your $currentScreenName"), // should be a str resource,
                    style = MaterialTheme.typography.displayLarge,
                )
            }
            Column (
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .weight(8f)
                    .padding(innerPadding)
            ){
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
fun SettingColumn() {

}