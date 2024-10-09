package com.example.habitua.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.habitua.R
import com.example.habitua.data.Habit
import com.example.habitua.data.PrincipleDetails
import com.example.habitua.ui.habit.HabitDestination
import com.example.habitua.ui.habit.HabitHomeBody
import com.example.habitua.ui.habit.HabitViewModel
import com.example.habitua.ui.habit.Painting
import com.example.habitua.ui.issues.IssueBody
import com.example.habitua.ui.issues.IssueDestination
import com.example.habitua.ui.principles.PrincipleBody
import com.example.habitua.ui.principles.PrincipleDestination
import com.example.habitua.ui.settings.SettingBody
import com.example.habitua.ui.settings.SettingDestination
import com.example.habitua.ui.theme.PreviewHabituaTheme
import com.example.habitua.ui.visual.VisualizationBody
import kotlin.random.Random

/**
 * This page should model every UI component of the app
 *
 * And it should modularize it - only one composable per page
 * Other ones add specific values to the parameters and must not repeat any code
 *
 * Oh, and quick aside, we say "screen" but we have to use "_body" since screen holds
 * view model and other things we can't access in a preview.
 *
 * Screens to be implemented
 * HabitScreen
 * PrincipleScreen
 * IssueScreen
 * YouScreen
 * DataScreen
 *
 * Modes - each line raises previews exponentially
 * Dark vs Light
 * Empty vs filled
 *
 * Dialogs
 * Habit edit
 * Principle Edit
 * Issue Edit
 *
 */

@Preview(
    showBackground = true,
    name = "default, light, empty - Habit screen",
    group = "habits"
)
@Composable
fun HabitScreenPreview(
    darkTheme: Boolean = false,

    previewHabitList: List<Habit> = listOf(),
    previewCountList: Map<HabitViewModel.DataSource, Int> = mapOf(),
    iconList: List<Painting> = listOf(),
    iconMenuExpanded: Boolean = false,
    reviewConfirmationRequired: Boolean = false
) {
    PreviewHabituaTheme (
        darkTheme = darkTheme
    ) {
        HabitHomeBody(
            currentScreenName = stringResource(id = HabitDestination.navTitle),
            navigateToHabitEdit = { },
            navigateToHabit = {},
            navigateToPrinciple = {  },
            navigateToIssue = {},
            navigateToYou = {},

            habitList = previewHabitList,
            countList = previewCountList,
            stringMap = mapOf(),
            iconList = iconList,

            onClickHabitCard = { habit: Habit -> },
            onIconClick = {},
            onIconDismiss = {},
            onSelectImage = { habit: Habit, img: Int -> },
            iconMenuExpanded = iconMenuExpanded,

            // review items
            reviewConfirmationRequired = reviewConfirmationRequired,
            onReviewAccept = {},
            onReviewDismiss = {},
            onReviewClick = {},
            userReviewedToday = false,

            dataSource = HabitViewModel.DataSource.TODO,
            onOptionSelected = {}
        )

    }
}
@Preview(
    showBackground = true,
    name = "default, light, empty - Principle screen",
    group = "principles"
)
@Composable
fun PrincipleScreenPreview(
    darkTheme: Boolean = false,

    principleListToday: List<PrincipleDetails> = listOf(),
    editablePrincipleDetails: PrincipleDetails = PrincipleDetails(
        principleId = 0,
        name = "Readiness",
        description = "useless",
        date = 0,
        dateCreated = 0,
        dateFirstActive = null,
        value = false
    ),
    isEqualToWeekBeforeToday: Boolean = false,
    isBeforeYesterday: Boolean = false,
    isEqualToToday: Boolean = true,
    expandEditMenu: Boolean = false
) {

    val backgroundDrawables = listOf(
        R.drawable.baseline_circle_24,
        R.drawable.baseline_elderly_24,
        R.drawable.baseline_hexagon_24,
        R.drawable.baseline_electric_bolt_24
    )

    val backgroundAccessorIndex = Random.nextInt(backgroundDrawables.size)

    PreviewHabituaTheme (
        darkTheme = darkTheme
    ) {
        PrincipleBody(
            // navigation
            currentScreenName = stringResource(id = PrincipleDestination.navTitle),
            navigateToHabit = {  },
            navigateToPrinciple = { },
            navigateToIssue = { },
            navigateToYou = { },

            // background patterns
            backgroundPatternList = backgroundDrawables,
            backgroundAccessorIndex = backgroundAccessorIndex,

            // dates
            dateBase = "Sunday, 8-10",

            onDateSwipe = {},
            updateToToday = { },
            updateToTomorrow = {  },
            updateToYesterday = {},

            isEqualToWeekBeforeToday = isEqualToWeekBeforeToday,
            isBeforeYesterday = isBeforeYesterday,
            isEqualToToday = isEqualToToday,

            // principles
            onClickPrinciple = { principleDetails: PrincipleDetails -> },
            onHoldPrinciple = { principleDetails: PrincipleDetails -> },
            principleListToday = principleListToday,
            canApplyChanges = false,

            // Action Bar items
            addPrinciple = {  },

            // edit dialog items
            expandEditMenu = expandEditMenu,
            editMenuPrincipleDetails = editablePrincipleDetails,
            onEditMenuDismiss = {},
            onEditMenuExpand = { principleDetails: PrincipleDetails -> },
            editMenuUpdatePrincipleInUiState = { principleDetail: PrincipleDetails -> },
            editMenuApplyChangesToPrinciple = {},
            editMenuDeletePrinciple = {},
        )
    }
}
@Preview(
    showBackground = true,
    name = "default, light, empty - issue screen",
    group = "issues"
)
@Composable
fun IssueScreenPreview(
    darkTheme: Boolean = false,
) {
    PreviewHabituaTheme {
        IssueBody(
            currentScreenName = stringResource(id = IssueDestination.navTitle),
            navigateToHabit = {  },
            navigateToPrinciple = { },
            navigateToIssue = { },
            navigateToYou = { },
        )
    }
}
@Preview(
    showBackground = true,
    name = "default, light, empty - you screen",
    group = "user"
)
@Composable
fun UserScreenPreview() {
    PreviewHabituaTheme {
        SettingBody(
            currentScreenName = stringResource(id = SettingDestination.navTitle),
            navigateToHabit = {  },
            navigateToPrinciple = { },
            navigateToIssue = { },
            navigateToYou = { },
        )
    }
}
@Preview(
    showBackground = true,
    name = "default, light, empty - data screen",
    group = "data"
)
@Composable
fun DataScreenPreview() {
    PreviewHabituaTheme {
        VisualizationBody()
    }
}




// Habit Screen Modifiers

@Preview(
    showBackground = true,
    name = "dark, empty - Habit screen",
    group = "habits"
)
@Composable
fun HabitScreenPreviewVariantDarkEmpty() {
    HabitScreenPreview(
        darkTheme = true
    )
}
@Preview(
    showBackground = true,
    name = "light, not empty - Habit screen",
    group = "habits"
)
@Composable
fun HabitScreenPreviewVariantLightNotEmpty(){
    HabitScreenPreview(
        darkTheme = false,
        previewHabitList = test_data_HabitList

    )
}
@Preview(
    showBackground = true,
    name = "dark, not empty - Habit screen",
    group = "habits"
)
@Composable
fun HabitScreenPreviewVariantDarkNotEmpty(){
    HabitScreenPreview(
        darkTheme = true,
        previewHabitList = test_data_HabitList,
    )
}
@Preview(
    showBackground = true,
    name = "light, icon menu - Habit screen",
    group = "habits"
)
@Composable
fun HabitScreenPreviewVariantLightIcon() {
    HabitScreenPreview(
        darkTheme = false,
        iconMenuExpanded = true,
    )
}
@Preview(
    showBackground = true,
    name = "dark, icon menu - Habit screen",
    group = "habits"
)
@Composable
fun HabitScreenPreviewVariantDarkIcon() {
    HabitScreenPreview(
        darkTheme = true,
        iconMenuExpanded = true,
        iconList = test_data_iconList
    )
}



// Principle Screen Modifiers

@Preview(
    showBackground = true,
    name = "dark, empty - Principle screen",
    group = "principles"
)
@Composable
fun PrincipleScreenPreviewVariantDarkEmpty() {
    PrincipleScreenPreview(
        darkTheme = true
    )
}
@Preview(
    showBackground = true,
    name = "light, not empty - Principle screen",
    group = "principles"
)
@Composable
fun PrincipleScreenPreviewVariantLightNotEmpty(){
    PrincipleScreenPreview(
        darkTheme = false,
        principleListToday = test_data_principleListToday
    )
}
@Preview(
    showBackground = true,
    name = "dark, not empty - Principle screen",
    group = "principles"
)
@Composable
fun PrincipleScreenPreviewVariantDarkNotEmpty(){
    PrincipleScreenPreview(
        darkTheme = true,
        principleListToday = test_data_principleListToday
    )
}
@Preview(
    showBackground = true,
    name = "light, edit dialog - Principle screen",
    group = "principles"
)
@Composable
fun PrincipleScreenPreviewVariantLightEditDialogOpen(){
    PrincipleScreenPreview(
        darkTheme = false,
        principleListToday = test_data_principleListToday,
        editablePrincipleDetails = PrincipleDetails(
            principleId = 0,
            name = "Readiness",
            description = "useless",
            date = 0,
            dateCreated = todayMinus24days_inMilli,
            value = true,
            dateFirstActive = todayMinus1day_inMilli
        ),
        expandEditMenu = true
    )
}
@Preview(
    showBackground = true,
    name = "dark, edit dialog - Principle screen",
    group = "principles"
)
@Composable
fun PrincipleScreenPreviewVariantDarkEditDialogOpen(){
    PrincipleScreenPreview(
        darkTheme = true,
        principleListToday = test_data_principleListToday,
        editablePrincipleDetails = PrincipleDetails(
            principleId = 0,
            name = "Readiness",
            description = "yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes ",
            date = 0,
            dateCreated = todayMinus66days_inMilli,
            value = true,
            dateFirstActive = todayMinus24days_inMilli
        ),
        expandEditMenu = true

    )
}




// Settings screen preview modifier
@Preview(
    showBackground = true,
    name = "dark - you screen",
    group = "user"
)
@Composable
fun UserScreenPreviewDark() {
    PreviewHabituaTheme (
        darkTheme = true
    ) {
        SettingBody(
            currentScreenName = stringResource(id = SettingDestination.navTitle),
            navigateToHabit = {  },
            navigateToPrinciple = { },
            navigateToIssue = { },
            navigateToYou = { },
        )

    }
}

// data screen preview modifiers
@Preview(
    showBackground = true,
    name = "dark, empty - data screen",
    group = "data"
)
@Composable
fun DataScreenPreviewDark() {
    PreviewHabituaTheme (
        darkTheme = true
    ) {
        VisualizationBody(
        )
    }
}