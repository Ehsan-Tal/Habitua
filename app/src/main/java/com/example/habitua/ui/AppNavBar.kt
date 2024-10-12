package com.example.habitua.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.habitua.R
import com.example.habitua.ui.navigation.HabitDestination
import com.example.habitua.ui.navigation.IssueDestination
import com.example.habitua.ui.navigation.PrincipleDestination
import com.example.habitua.ui.navigation.SettingDestination
import com.example.habitua.ui.theme.PreviewHabituaTheme

@Composable
fun AppNavBar(
    navigateToHabit: () -> Unit ,
    navigateToPrinciple: () -> Unit ,
    navigateToIssue: () -> Unit,
    navigateToYou: () -> Unit,

    currentScreenName: String,
){
    Column ( modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally)
    {
        HorizontalDivider(
            thickness = dimensionResource(id = R.dimen.divider_thick_medium),
            color = MaterialTheme.colorScheme.outline
        )
        Row (
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(dimensionResource(R.dimen.padding_small))
        ) {
            AppNavButton(
                text = stringResource(id = HabitDestination.navTitle),
                currentScreenName = currentScreenName,
                onNextButtonClicked = navigateToHabit,
                icon = Icons.Filled.Schedule,
                contentDescription = stringResource(id = R.string.nav_bar_habit_content_description)
            )
            AppNavButton(
                text = stringResource(id = PrincipleDestination.navTitle),
                currentScreenName = currentScreenName,
                onNextButtonClicked = navigateToPrinciple,
                icon = Icons.Filled.Umbrella,
                contentDescription = stringResource(id = R.string.nav_bar_manner_content_description)
            )
            AppNavButton(
                text = stringResource(id = IssueDestination.navTitle),
                currentScreenName = currentScreenName,
                onNextButtonClicked = navigateToIssue,
                icon = Icons.Filled.Inbox,
                contentDescription = stringResource(id = R.string.nav_bar_issue_content_description)
            )
            AppNavButton(
                text = stringResource(id = SettingDestination.navTitle),
                currentScreenName = currentScreenName,
                onNextButtonClicked = navigateToYou,
                icon = Icons.Filled.Settings,
                contentDescription = stringResource(id = R.string.nav_bar_settings_content_description)
            )
        }
    }
}

@Composable
fun AppNavButton(
    text: String,
    icon: ImageVector,
    contentDescription: String,
    currentScreenName: String,
    onNextButtonClicked: () -> Unit
){
    TextButton(
        onClick = onNextButtonClicked,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (currentScreenName == text) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (currentScreenName == text) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        border = if (currentScreenName == text)
            BorderStroke(
                width = dimensionResource(id = R.dimen.border_stroke_small),
                color = MaterialTheme.colorScheme.outline
            ) else null,
        shape = MaterialTheme.shapes.medium,
    ) {
        //TODO: Button is partially hidden in layout because it is not contained within the bounds of its parent in a preview configuration.
        // Fix this issue by adjusting the size or position of Button
        // ellipses /

        Column( horizontalAlignment = Alignment.CenterHorizontally ){
            Icon( imageVector = icon, contentDescription = contentDescription )
            Text (
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}



@Preview
@Composable
fun PreviewAppNavBar() {
    PreviewHabituaTheme {
        AppNavBar(
            navigateToHabit = {},
            navigateToPrinciple = {},
            navigateToIssue = {},
            navigateToYou = {},
            currentScreenName = stringResource(id= HabitDestination.navTitle)
        )
    }
}