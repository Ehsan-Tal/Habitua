package com.example.habitua.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.habitua.R
import com.example.habitua.data.Habit
import com.example.habitua.data.PrincipleDetails
import com.example.habitua.ui.habit.HabitViewModel
import com.example.habitua.ui.habit.Painting
import com.example.habitua.ui.navigation.HabitDestination
import com.example.habitua.ui.navigation.IssueDestination
import com.example.habitua.ui.navigation.PrincipleDestination
import com.example.habitua.ui.navigation.SettingDestination
import com.example.habitua.ui.principles.PrincipleBody
import com.example.habitua.ui.settings.SettingBody
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
        /*
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

         */

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


    val backgroundAccessorIndex = Random.nextInt(backgroundDrawables.size)

    PreviewHabituaTheme (
        darkTheme = darkTheme
    ) {
        PrincipleBody(
            // navigation
            currentScreenName = stringResource(id = PrincipleDestination.navTitle),
            navigateToHabit = { },
            navigateToPrinciple = { },
            navigateToIssue = { },
            navigateToYou = { },

            // background patterns
            backgroundPatternList = backgroundDrawables,
            backgroundAccessorIndex = backgroundAccessorIndex,
            listOfCards = principleListToday,
            isListOfCardsEmpty = principleListToday.isEmpty(),
            getCanCardClickBoolean = true,

            onClickPrinciple = { _, _ -> },
            onHoldPrinciple = { principleDetails: PrincipleDetails -> },
            appTitle = stringResource(id = PrincipleDestination.title),
            serialCategoryString = "Sunday, 8-10",
            serialBackwardButtonLambda = {},
            serialForwardButtonLambda = {},
            serialBackwardButtonContentDescription = "",
            serialForwardButtonContentDescription = "",
            isSerialBackwardButtonEnabled = true,
            isSerialForwardButtonEnabled = false,
            isFilterDropdownEnabled = false,
            emptyCardTitle = "No Principles",
            emptyCardDescription = "No desc in preview",
            firstActionButtonName = "Create",
            firstActionButtonIcon = Icons.Outlined.AddCircleOutline,
            firstActionButtonIconContentDescription = "",
            firstActionButtonLambda = {},
            isFirstButtonEnabled = true,
            secondActionButtonName = "",
            secondActionButtonIcon = Icons.Outlined.AddCircleOutline,
            secondActionButtonIconContentDescription = "",
            secondActionButtonLambda = {},
            isSecondButtonEnabled = false,
            navigateToPrincipleEdit = {},
            currentlyLoading = false,
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


    val backgroundAccessorIndex = Random.nextInt(backgroundDrawables.size)

    PreviewHabituaTheme {
        /*
        IssueBody(
            currentScreenName = stringResource(id = IssueDestination.navTitle),
            navigateToHabit = {  },
            navigateToPrinciple = { },
            navigateToIssue = { },
            navigateToYou = { },
            appTitle = stringResource(id = IssueDestination.title),
            backgroundPatternList = backgroundDrawables,
            backgroundAccessorIndex = backgroundAccessorIndex
        )

         */
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



@Preview(
    showBackground = true,
    name = "background pattern",
    group = "background"
)
@Composable
fun BackgroundPattern() {

    var backgroundAccessorIndex = Random.nextInt(backgroundDrawables.size)

    val patternPainter = painterResource(id = backgroundDrawables[backgroundAccessorIndex])
    val color = MaterialTheme.colorScheme.tertiary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {

                val patternWidth = patternPainter.intrinsicSize.width
                val patternHeight = patternPainter.intrinsicSize.height

                clipRect (
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = size.height - patternHeight / 2
                ){
                    val repetitionsX = (size.width / patternWidth).toInt() + 1
                    val repetitionsY = (size.height / patternHeight).toInt() + 1

                    for (i in 0..repetitionsX) {
                        for (j in 0..repetitionsY) {
                            val translateX = if (j % 2 == 0) i * patternWidth * 3 - patternWidth / 2 else i * patternWidth * 3 + patternWidth

                            translate(translateX - patternWidth, j * patternHeight * 2 + patternHeight ) {
                                with(patternPainter) {
                                    draw(
                                        size = Size(patternWidth, patternHeight),
                                        alpha = 0.5f,
                                        colorFilter = ColorFilter.tint(color)

                                    )
                                }
                            }

                        }
                    }
                }
            }
        ,
        contentAlignment = Alignment.Center
    ) {
        // This Box will be behind other content
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
