package com.example.habitua.ui.issues

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitua.R
import com.example.habitua.ui.AppNavBar
import com.example.habitua.ui.AppTitleBar
import com.example.habitua.ui.AppViewModelProvider

import com.example.habitua.ui.navigation.NavigationDestination

object IssueDestination: NavigationDestination {
    override val route = "issue"
    override val title = R.string.issue_title
    val navTitle = R.string.issue_nav_title

}
//TODO: maybe move these to the navigation Destination screen ?

@Composable
fun IssueScreen(
    viewModel: IssueViewModel = viewModel(factory = AppViewModelProvider.Factory),
    currentScreenName: String,
    navigateToHabit: () -> Unit,
    navigateToPrinciple: () -> Unit,
    navigateToIssue: () -> Unit,
    navigateToYou: () -> Unit,
) {
    IssueBody(
        currentScreenName = currentScreenName,
        navigateToHabit = navigateToHabit,
        navigateToPrinciple = navigateToPrinciple,
        navigateToIssue = navigateToIssue,
        navigateToYou = navigateToYou
    )
}

@Composable
fun IssueBody (
    currentScreenName: String,
    navigateToHabit: () -> Unit,
    navigateToPrinciple: () -> Unit,
    navigateToIssue: () -> Unit,
    navigateToYou: () -> Unit,

) {
    AppTitleBar(
        title = stringResource(id = IssueDestination.title)
    )

    AppNavBar(
        currentScreenName = currentScreenName,
        navigateToHabit = navigateToHabit,
        navigateToPrinciple = navigateToPrinciple,
        navigateToIssue = navigateToIssue,
        navigateToYou = navigateToYou
    )

}
