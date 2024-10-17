@file:OptIn(ExperimentalFoundationApi::class)
// apparently this allows the animation ?

package com.example.habitua.ui.habit

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.PanoramaVertical
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitua.R
import com.example.habitua.data.HabitDetails
import com.example.habitua.ui.AppViewModelProvider
import com.example.habitua.ui.AppNavBar
import com.example.habitua.ui.AppTitleBar
import com.example.habitua.ui.backgroundDrawables
import com.example.habitua.ui.issues.innerShadow
import com.example.habitua.ui.navigation.HabitDestination
import kotlinx.coroutines.launch
import com.example.habitua.ui.principles.ActionBar
import com.example.habitua.ui.principles.TrackingCard


@Composable
fun HabitScreen(
    viewModel: HabitViewModel = viewModel(factory = AppViewModelProvider.Factory),

    currentScreenName: String,
    navigateToHabitEdit: (Int) -> Unit,
    navigateToHabit: () -> Unit,
    navigateToPrinciple: () -> Unit,
    navigateToIssue: () -> Unit,
    navigateToYou: () -> Unit,
) {
    val habitUiState by viewModel.habitUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var reviewConfirmationRequired by rememberSaveable { mutableStateOf(false) }
    var dataSource by remember { mutableStateOf(HabitViewModel.DataSource.TODO) }
    var expandedIconMenu by remember { mutableStateOf(false) }

    HabitBody(
        // app title bar stuff
        appTitle = stringResource(id = HabitDestination.title),


        // FILTER BAR
        serialCategoryString = habitUiState.dateBaseString,
        serialBackwardButtonLambda = { viewModel.updateToYesterday() },
        serialForwardButtonLambda = { viewModel.updateToTomorrow() },
        serialBackwardButtonContentDescription = stringResource(R.string.serial_backward_button_content_description),
        serialForwardButtonContentDescription = stringResource(R.string.serial_forward_button_content_description_for_dates),
        isSerialBackwardButtonEnabled = habitUiState.isSerialBackwardButtonEnabled,
        isSerialForwardButtonEnabled = habitUiState.isSerialForwardButtonEnabled,
        //TODO: change the serial button booleans to not need a !
        //TODO: add filter parameters
        //TODO: add string resources
        //TODO: add a serial category swipe function

        isFilterDropdownEnabled = false,


        // LIST BAR
        // habits
        backgroundPatternList = backgroundDrawables,
        backgroundAccessorIndex = viewModel.backgroundAccessorIndex,

        listOfCards = habitUiState.habitList,
        isListOfCardsEmpty = false,
        getCanCardClickBoolean = habitUiState.canCardClickBoolean,

        // background patterns
        onClickHabit = { date: Long, id: Int ->
            coroutineScope.launch {
                viewModel.toggleHabit(id, date)
            }
        },
        onHoldHabit = { habitDetails: HabitDetails ->
            Log.d("t", "$habitDetails held")
            //TODO: change sorting order
        },
        navigateToHabitEdit = navigateToHabitEdit,

        emptyCardTitle = "No habits to show",
        emptyCardDescription = "how can I get this from the view Model",

        // Action Bar items
        firstActionButtonName = stringResource(id = R.string.action_bar_create_button_general),
        firstActionButtonIcon = Icons.Outlined.AddCircleOutline,
        firstActionButtonIconContentDescription = stringResource(id = R.string.action_bar_create_issue_content_description),
        firstActionButtonLambda = { viewModel.addHabit(navigateToHabitEdit) },
        isFirstButtonEnabled = habitUiState.isFirstActionButtonEnabled,

        secondActionButtonName = stringResource(id = R.string.action_bar_today_button),
        secondActionButtonIcon = Icons.Outlined.Today,
        secondActionButtonIconContentDescription = stringResource(id = R.string.action_bar_today_button_content_description),
        secondActionButtonLambda = { viewModel.updateToToday() },
        isSecondButtonEnabled = habitUiState.isSecondActionButtonEnabled,

        //navigation
        currentScreenName = currentScreenName,
        navigateToHabit = navigateToHabit,
        navigateToPrinciple = navigateToPrinciple,
        navigateToIssue = navigateToIssue,
        navigateToYou = navigateToYou,
        currentlyLoading = habitUiState.currentlyLoading,
    )
}

/**
 *
 * secondActionButtonName: String = "" // if no given name, do not generate a second button
 */
@Composable
fun HabitBody (
    // app title bar stuff
    appTitle: String,

    // filter bar stuff
    // serial category
    serialCategoryString: String,
    serialBackwardButtonLambda: () -> Unit,
    serialForwardButtonLambda: () -> Unit,
    serialBackwardButtonContentDescription: String,
    serialForwardButtonContentDescription: String,
    isSerialBackwardButtonEnabled: Boolean,
    isSerialForwardButtonEnabled: Boolean,

    // Dropdowns
    isFilterDropdownEnabled: Boolean,


    // LIST BAR STUFF
    // background drawables for the list bar
    backgroundPatternList: List<Int>,
    backgroundAccessorIndex: Int,

    // edit menus - TODO: what here ?

    // habits
    listOfCards: List<HabitDetails>,
    isListOfCardsEmpty: Boolean,
    getCanCardClickBoolean: Boolean,

    onClickHabit: (Long, Int) -> Unit,
    onHoldHabit: (HabitDetails) -> Unit,
    navigateToHabitEdit: (Int) -> Unit,

    emptyCardTitle: String,
    emptyCardDescription: String,


    // ACTION BAR stuff
    firstActionButtonName: String = "", // if no given name, do not generate a second button
    firstActionButtonIcon: ImageVector = Icons.Filled.AddCircleOutline,
    firstActionButtonIconContentDescription: String = "",
    firstActionButtonLambda: () -> Unit = {},
    isFirstButtonEnabled: Boolean = true,

    secondActionButtonName: String = "", // if no given name, do not generate a second button
    secondActionButtonIcon: ImageVector = Icons.Outlined.PanoramaVertical,
    secondActionButtonIconContentDescription: String = "",
    secondActionButtonLambda: () -> Unit = {},
    isSecondButtonEnabled: Boolean = true,

    // navigation necessities
    currentScreenName: String,
    navigateToHabit: () -> Unit,
    navigateToPrinciple: () -> Unit,
    navigateToIssue: () -> Unit,
    navigateToYou: () -> Unit,
    currentlyLoading: Boolean,
) {

    val modifierForBars = Modifier
        .fillMaxWidth()
        .padding( horizontal = dimensionResource(id = R.dimen.padding_small) )
        .padding( bottom= dimensionResource(id = R.dimen.padding_small) )
        .background(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.small
        )
        .innerShadow(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.outline.copy(0.68f),
            offsetY = (4).dp, offsetX = (0).dp
        )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            AppTitleBar( title = appTitle )
        },
        bottomBar = {
            AppNavBar(
                currentScreenName = currentScreenName,
                navigateToHabit = navigateToHabit,
                navigateToPrinciple = navigateToPrinciple,
                navigateToIssue = navigateToIssue,
                navigateToYou = navigateToYou
            )
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(top = dimensionResource(id = R.dimen.padding_medium))
                .padding(bottom = dimensionResource(id = R.dimen.padding_small))
        ){

            HabitFilterBar(
                modifier = modifierForBars,

                serialCategoryString = serialCategoryString,
                serialBackwardButtonLambda = serialBackwardButtonLambda,
                serialForwardButtonLambda = serialForwardButtonLambda,
                serialBackwardButtonContentDescription = serialBackwardButtonContentDescription,
                serialForwardButtonContentDescription = serialForwardButtonContentDescription,
                isSerialForwardButtonEnabled = isSerialForwardButtonEnabled,
                isSerialBackwardButtonEnabled = isSerialBackwardButtonEnabled,
                onSerialDragLeft = {},
                onSerialDragRight = {},

                isFilterDropdownEnabled = isFilterDropdownEnabled,
            )

            HabitListBar(
                // modifiers
                backgroundPatternList = backgroundPatternList,

                // background drawables
                backgroundAccessorIndex = backgroundAccessorIndex,
                listOfCards = listOfCards,

                // habits
                getCanCardClickBoolean = getCanCardClickBoolean,
                isListOfCardsEmpty = isListOfCardsEmpty,
                onClickHabit = onClickHabit,

                onHoldHabit = onHoldHabit,
                navigateToHabitEdit = navigateToHabitEdit,
                modifier = modifierForBars.weight(1f),

                emptyCardTitle = emptyCardTitle,
                emptyCardDescription = emptyCardDescription,
                currentlyLoading = currentlyLoading
            )

            ActionBar(
                modifier = modifierForBars,

                firstActionButtonName = firstActionButtonName,
                firstActionButtonIcon = firstActionButtonIcon,
                firstActionButtonIconContentDescription = firstActionButtonIconContentDescription,
                firstActionButtonLambda = firstActionButtonLambda,
                isFirstButtonEnabled = isFirstButtonEnabled,

                secondActionButtonName = secondActionButtonName,
                secondActionButtonIcon = secondActionButtonIcon,
                secondActionButtonIconContentDescription = secondActionButtonIconContentDescription,
                secondActionButtonLambda = secondActionButtonLambda,
                isSecondButtonEnabled = isSecondButtonEnabled,
            )


        }
    }
}




/**
 * category is often a date, but can be other serial properties as well
 */
@Composable
fun HabitFilterBar(
    // serial category
    serialCategoryString: String,
    serialBackwardButtonLambda: () -> Unit,
    serialForwardButtonLambda: () -> Unit,
    serialBackwardButtonContentDescription: String,
    serialForwardButtonContentDescription: String,
    isSerialBackwardButtonEnabled: Boolean,
    isSerialForwardButtonEnabled: Boolean,

    onSerialDragLeft: () -> Unit,
    onSerialDragRight: () -> Unit,

    // Dropdowns
    isFilterDropdownEnabled: Boolean,
    modifier: Modifier = Modifier,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(horizontal = dimensionResource(id = R.dimen.padding_xmedium))
            .padding(vertical = dimensionResource(id = R.dimen.padding_small))
    ){
        // Dropdowns
        if (isFilterDropdownEnabled) {
            Row(
                Modifier
                    .border(
                        BorderStroke(
                            dimensionResource(id = R.dimen.border_stroke_small),
                            MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(dimensionResource(id = R.dimen.padding_medium))
                    .weight(1f)
            ){ Text(text = "Acquired 99", style = MaterialTheme.typography.labelMedium) }
        } //TODO: what heck, how do ????????
        //TODO: make this a dropdown
        //TODO: max text = 11 characters + 2 integer


        // Serial categories
        Row ( verticalAlignment = Alignment.CenterVertically ){
            IconButton(
                enabled = isSerialBackwardButtonEnabled,
                onClick = serialBackwardButtonLambda,
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardDoubleArrowLeft,
                    contentDescription = serialBackwardButtonContentDescription
                )
            }
            Text(
                textAlign = TextAlign.Center,
                text = serialCategoryString,
                style = MaterialTheme.typography.displayMedium
            )
            IconButton(
                enabled = isSerialForwardButtonEnabled,
                onClick = serialForwardButtonLambda,
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardDoubleArrowRight,
                    contentDescription = serialForwardButtonContentDescription
                )
            }
        }
    }
}


@Composable
fun HabitListBar(
    // background drawables
    backgroundPatternList: List<Int>,
    backgroundAccessorIndex: Int,

    //TODO: remember, we need to generic these functions as much as possible
    // we do this so that it's not as nasty to change
    // because we are not smart enough to get it right the first time :)

    // Habits
    listOfCards: List<HabitDetails>,
    getCanCardClickBoolean: Boolean,
    isListOfCardsEmpty: Boolean,

    onClickHabit: (Long, Int) -> Unit,
    onHoldHabit: (HabitDetails) -> Unit,
    navigateToHabitEdit: (Int) -> Unit,

    modifier: Modifier = Modifier,

    emptyCardTitle: String,
    emptyCardDescription: String,
    currentlyLoading: Boolean,
){

    val patternPainter = painterResource(id = backgroundPatternList[backgroundAccessorIndex])
    val color = MaterialTheme.colorScheme.tertiary

    val listBarColumnModifier = modifier
        .padding(vertical = dimensionResource(id = R.dimen.padding_large))
        .drawBehind {

            val patternWidth = patternPainter.intrinsicSize.width
            val patternHeight = patternPainter.intrinsicSize.height

            clipRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height - patternHeight / 2
            ) {
                val repetitionsX = (size.width / patternWidth).toInt() + 1
                val repetitionsY = (size.height / patternHeight).toInt() + 1

                for (i in 0..repetitionsX) {
                    for (j in 0..repetitionsY) {
                        val translateX =
                            if (j % 2 == 0) i * patternWidth * 3 - patternWidth / 2 else i * patternWidth * 3 + patternWidth

                        translate(
                            translateX - patternWidth,
                            j * patternHeight * 2 + patternHeight
                        ) {
                            with(patternPainter) {
                                draw(
                                    size = Size(patternWidth, patternHeight),
                                    alpha = 0.35f,
                                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                                        color
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = listBarColumnModifier
    ){
        if (currentlyLoading) {
            // insert a loading spinners
            CircularProgressIndicator(
                modifier = Modifier.size(dimensionResource(id = R.dimen.loading_spinner_size)),
                color = MaterialTheme.colorScheme.tertiary,
                trackColor = MaterialTheme.colorScheme.surface
            )
        }
        else if (isListOfCardsEmpty) {
            TrackingCard(
                // card icons
                cardIconResource = 0,
                cardIconContentDescription = "",
                cardIconLambda = {},

                // card stuff
                cardTitle = emptyCardTitle,
                cardDescription = emptyCardDescription,
                cardValue = false,
                onCardClick = { },
                getCanCardClickBoolean = true,

                // more options
                onMoreOptionsClick = {},

                //  option booleans
                enableDescription = true,
                enableMoreOptions = false,
                enableIcon = false,
            )

        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(400.dp),
            ) {
                //TODO: I do not care to incorporate generics right now
                //my mind weak.
                items(listOfCards, key = {habit -> habit.habitId} ) { habit ->
                    // it doubles...
                    // this is empty, why is this empty.

                    Row( modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)) ) {
                        //TODO: parameterize these where possible !
                        // much of these cannot be ascertained since they are generated with
                        // habit, an object of items

                        //though, this is not the case with other items.

                        //TODO: we could just use Tracking card instead an entirely new card
                        HabitCard(
                            habit = habit,

                            cardIconLambda = { /*TODO*/ },

                            //TODO: needs either date base
                            onClickHabit = {onClickHabit(habit.date, habit.habitId)}, // active/inactive
                            getCanCardClickBoolean = getCanCardClickBoolean, // disable active/inactive

                            onMoreOptionsClick = {
                                navigateToHabitEdit(habit.habitId)
                            }, // more options
//                            onMoreOptionsClick = { onEditMenuExpand(habit) }, // more options
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun HabitCard(
    habit: HabitDetails,

    onMoreOptionsClick: () -> Unit,

    onClickHabit: () -> Unit,
    getCanCardClickBoolean: Boolean,

    cardIconLambda: () -> Unit,
) {
    TrackingCard(
        // card icons
        cardIconResource = habit.imageResId,
        cardIconContentDescription = "", // this should come here, the habit
        cardIconLambda = cardIconLambda, // this should travel from the body
        //TODO: get a table for the card icons.

        // card stuff
        cardTitle = habit.name,
        cardDescription = habit.description,
        cardValue = habit.value,
        onCardClick = onClickHabit,
        getCanCardClickBoolean = getCanCardClickBoolean,

        // more options
        onMoreOptionsClick = onMoreOptionsClick,

        //  option booleans
        enableDescription = true,
        enableMoreOptions = true,
    )
}


/*
    HabitHomeBody(
        currentScreenName = currentScreenName,
        navigateToHabitEdit = navigateToHabitEdit,
        navigateToHabit = navigateToHabit,
        navigateToPrinciple = navigateToPrinciple,
        navigateToIssue = navigateToIssue,
        navigateToYou = navigateToYou,

        habitList = habitUiState.habitList,
        countList = habitUiState.countList,
        stringMap = habitUiState.nameMaps,
        iconList = habitUiState.iconList,

        onClickHabitCard = { habit: Habit ->
            coroutineScope.launch{ viewModel.toggleHabitActive(habit) }
       },
        onIconClick = {
            expandedIconMenu = true
        },
        onIconDismiss = {
            expandedIconMenu = false
        },
        onSelectImage = { habit:Habit, img: Int ->
            coroutineScope.launch {
                viewModel.onSelectImage(habit, img)
            }
        },
        iconMenuExpanded = expandedIconMenu,

        // review items
        reviewConfirmationRequired = reviewConfirmationRequired,
        onReviewAccept = {
            coroutineScope.launch {
                reviewConfirmationRequired = false
                viewModel.reviewHabits()
            }
        },
        onReviewDismiss = {
            reviewConfirmationRequired = false
        },
        onReviewClick = {
            reviewConfirmationRequired = true
        },
        userReviewedToday = false,

        dataSource = dataSource,
        onOptionSelected = { newDataSource ->
            dataSource = newDataSource
            viewModel.setDataSource(newDataSource)
        }
    )
}


@Composable
fun HabitHomeBody(
    currentScreenName: String,
    navigateToHabitEdit: (Int) -> Unit,
    navigateToHabit: () -> Unit,
    navigateToPrinciple: () -> Unit,
    navigateToIssue: () -> Unit,
    navigateToYou: () -> Unit,

    habitList: List<Habit>,
    onClickHabitCard: (Habit) -> Unit,

    iconList: List<Painting>,
    onSelectImage: (Habit, Int) -> Unit,
    onIconClick: () -> Unit,
    onIconDismiss: () -> Unit,
    iconMenuExpanded: Boolean,

    reviewConfirmationRequired: Boolean,
    onReviewClick: () -> Unit,
    onReviewAccept: () -> Unit,
    onReviewDismiss: () -> Unit,
    userReviewedToday: Boolean,

    dataSource: HabitViewModel.DataSource,
    onOptionSelected: (HabitViewModel.DataSource) -> Unit,
    countList: Map<HabitViewModel.DataSource, Int>,
    stringMap: Map<HabitViewModel.DataSource, String>,
){

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    )
    { innerPadding ->
        HabitHomeSkeleton(
            habitList = habitList,
            countList = countList,
            nameMaps = stringMap,

            iconList = iconList,
            onSelectImage = onSelectImage,
            onIconClick = onIconClick,
            onIconDismiss = onIconDismiss,
            iconMenuExpanded = iconMenuExpanded,

            currentScreenName = currentScreenName,
            navigateToHabit = navigateToHabit,
            navigateToHabit = navigateToHabit,
            navigateToIssue = navigateToIssue,
            navigateToYou = navigateToYou,

            contentPadding = innerPadding,
            onCreateButtonClick = {},
            onMoreOptionsClick = navigateToHabitEdit,
            onClickHabitCard = onClickHabitCard,

            dataSource = dataSource,
            onOptionSelected = onOptionSelected,

            //review button related material
            reviewConfirmationRequired = reviewConfirmationRequired,
            onReviewClick = onReviewClick,
            onReviewAccept = onReviewAccept,
            onReviewDismiss = onReviewDismiss,
            userReviewedToday = userReviewedToday,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitHomeSkeleton(
    habitList: List<Habit>,
    onClickHabitCard: (Habit) -> Unit,

    reviewConfirmationRequired: Boolean,
    userReviewedToday: Boolean,

    currentScreenName: String,
    navigateToHabit: () -> Unit,
    navigateToPrinciple: () -> Unit,
    navigateToIssue: () -> Unit,
    navigateToYou: () -> Unit,

    contentPadding: PaddingValues,

    onCreateButtonClick: () -> Unit,
    onMoreOptionsClick: (Int) -> Unit,
    onReviewClick: () -> Unit,
    onReviewAccept: () -> Unit,
    onReviewDismiss: () -> Unit,

    dataSource: HabitViewModel.DataSource,
    onOptionSelected: (HabitViewModel.DataSource) -> Unit,

    countList: Map<HabitViewModel.DataSource, Int>,
    nameMaps: Map<HabitViewModel.DataSource, String>,

    iconList: List<Painting>,
    onSelectImage: (Habit, Int) -> Unit,
    iconMenuExpanded: Boolean,
    onIconClick: () -> Unit,
    onIconDismiss: () -> Unit,
){
    var expanded by remember { mutableStateOf(false) }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_large))
        ) {
            Text(
                text = stringResource(id = HabitDestination.title),
                style = MaterialTheme.typography.displayLarge,
            )
        }
        Column (
            modifier = Modifier
                .weight(8f)
                .padding(dimensionResource(R.dimen.padding_small))
        ){

            // START OF DROPDOWN MENU
            var dataSourceOptionText by remember { mutableStateOf(HabitViewModel.DataSource.TODO.name) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = dataSource.name,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)},
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    HabitViewModel.DataSource.entries.forEach { dataSourceOption ->
                        DropdownMenuItem(
                            text = {
                                Row {
                                Text(text = countList[dataSourceOption].toString())
                                Text(text = nameMaps[dataSourceOption].toString())
                                   }
                                },
                            onClick = {
                                dataSourceOptionText = dataSourceOption.name
                                onOptionSelected(dataSourceOption)
                                expanded = false
                            }
                        )
                    }
                }
            }
            // END OF DROPDOWN MENU


            HabitColumn(
                habitList = habitList,
                onClickHabitCard = onClickHabitCard,
                onMoreOptionsClick = onMoreOptionsClick,

                userReviewedToday = userReviewedToday,

                iconList = iconList,
                onSelectImage = onSelectImage,
                iconMenuExpanded = iconMenuExpanded,
                onIconClick = onIconClick,
                onIconDismiss = onIconDismiss
                )
        }


        // buttons for adding and reviewing
        Row (
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_medium))
        ) {


            ElevatedButton(
                onClick = onCreateButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary
                ),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary),
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small)),
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    tint = MaterialTheme.colorScheme.tertiary,
                    contentDescription = stringResource(R.string.content_description_FAB_habit_create)
                )
                Text(text = stringResource(id = R.string.habit_button_create), style = MaterialTheme.typography.displaySmall)
            }



            // Review button portion
            ElevatedButton(
                onClick = onReviewClick,
                enabled = !userReviewedToday && habitList.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary),
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
            ) {
                Icon(imageVector = Icons.Outlined.Newspaper,
                    contentDescription = stringResource(id = R.string.habit_button_review))
                Text(text = stringResource(id = R.string.habit_button_review), style = MaterialTheme.typography.displaySmall)
            }

            if (reviewConfirmationRequired) {
                AlertDialog(
                    onDismissRequest = {},
                    title = {Text(stringResource(id = R.string.review_dialog_title))},
                    text = {Text(stringResource(id = R.string.review_dialog_text))},
                    dismissButton = {
                        TextButton(onClick = onReviewDismiss) {
                            Text(text = stringResource(id = R.string.review_dialog_negation))
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = onReviewAccept) {
                            Text(text = stringResource(id = R.string.review_dialog_affirmation))
                        }
                    }
                )
            }
        }

        AppNavBar(
            currentScreenName = currentScreenName,
            navigateToHabit = navigateToHabit,
            navigateToHabit = navigateToHabit,
            navigateToIssue = navigateToIssue,
            navigateToYou = navigateToYou
        )
    }
}


@Composable
private fun HabitColumn(
    habitList: List<Habit>,
    userReviewedToday: Boolean,

    onMoreOptionsClick: (Int) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onClickHabitCard: (Habit) -> Unit,

    iconList: List<Painting>,
    onSelectImage: (Habit, Int) -> Unit,
    iconMenuExpanded: Boolean,
    onIconClick: () -> Unit,
    onIconDismiss: () -> Unit
    ){
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .fillMaxSize()
    ) {
        if (habitList.isEmpty()) {
            Text(
                text = stringResource(R.string.habit_body_no_habits),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_large))
            )
        } else {
            HabitList(
                habitList = habitList,
                contentPadding = contentPadding,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_large)),
                onClickHabitCard = onClickHabitCard,
                onMoreOptionsClick = onMoreOptionsClick,
                userReviewedToday = userReviewedToday,

                iconList = iconList,
                onSelectImage = onSelectImage,
                iconMenuExpanded = iconMenuExpanded,
                onIconClick = onIconClick,
                onIconDismiss = onIconDismiss
            )
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HabitList(
    habitList: List<Habit>,
    contentPadding: PaddingValues,
    onMoreOptionsClick: (Int) -> Unit,
    userReviewedToday: Boolean,
    onClickHabitCard: (Habit) -> Unit,

    iconList: List<Painting>,
    onSelectImage: (Habit, Int) -> Unit,
    iconMenuExpanded: Boolean,
    onIconClick: () -> Unit,
    onIconDismiss: () -> Unit,

    modifier: Modifier = Modifier
) {

    LazyColumn (
        modifier = modifier,
        contentPadding = contentPadding
    ) {

        items(habitList, key = { habit -> habit.id }) { habit ->
            Row (
                modifier = Modifier
                    .animateItemPlacement(
                        tween(200)
                    )
            ){
                ImageMenu(
                    onSelectImage = onSelectImage,
                    listOfImages = iconList,
                    habit = habit,
                    iconMenuExpanded = iconMenuExpanded,
                    onIconDismiss = onIconDismiss
                )
            }
        }
    }
}

@Composable
fun ImageMenu(
    onSelectImage: (Habit, Int) -> Unit,
    listOfImages: List<Painting>,
    habit: Habit,
    iconMenuExpanded: Boolean,
    onIconDismiss: () -> Unit,
) {
    if (iconMenuExpanded) {
        Dialog( onDismissRequest = {onIconDismiss()}) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.image_menu_dialog_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_large))
                )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
            ) {
                //This loads slower, is coil worth it for my purposes ?
                items(listOfImages) { painting ->
                    val imgUri = with(androidx.compose.ui.platform.LocalContext.current) {
                        Uri.parse("android.resource://$packageName/${painting.imageUri}")
                    }

                    val painter = rememberAsyncImagePainter(
                        model = imgUri,
                    )

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
                            .padding(dimensionResource(R.dimen.padding_small))
                            .clickable { onSelectImage(habit, painting.imageUri) }
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = painting.contentDescription,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(dimensionResource(R.dimen.padding_small))
                                .size(dimensionResource(R.dimen.image_size))
                                .clip(RoundedCornerShape(100))
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    shape = CircleShape
                                )
                                .shadow(elevation = 6.dp)
                        )
                    }
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    name = "image-news",
    group = "components"
)
@Composable
fun ImageGallery(){
    PreviewHabituaTheme(darkTheme = false){
        ImageMenu(
            onSelectImage = {habit, i -> } ,
            listOfImages = listOf(
                Painting(R.drawable.ic_launcher_foreground, "Content Description"),

                Painting(R.drawable.ic_launcher_background, "Content Description"),

                Painting(R.drawable.ic_launcher_foreground, "Content Description"),
                Painting(R.drawable.ic_launcher_background, "Content Description"),

                Painting(R.drawable.ic_launcher_background, "Content Description"),

                Painting(R.drawable.ic_launcher_foreground, "Content Description")
            ),
            Habit(name = "name", description = ""),
            iconMenuExpanded = false,
            onIconDismiss = {},
        )
    }
}

 */