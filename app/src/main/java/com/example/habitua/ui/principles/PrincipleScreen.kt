package com.example.habitua.ui.principles

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.withSave
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitua.R
import com.example.habitua.data.PrincipleDetails
import com.example.habitua.ui.AppTitleBar
import com.example.habitua.ui.AppViewModelProvider
import com.example.habitua.ui.HabitNavBar
import com.example.habitua.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch
import kotlin.random.Random


object PrincipleDestination: NavigationDestination {
    override val route = "principle"
    override val title = R.string.principle_title
    val navTitle = R.string.principle_nav_title
}


@Composable
fun PrincipleScreen(
    viewModel: PrincipleViewModel = viewModel(factory = AppViewModelProvider.Factory),

    currentScreenName: String,
    navigateToHabit: () -> Unit,
    navigateToPrinciple: () -> Unit,
    navigateToVisualize: () -> Unit,
    navigateToSetting: () -> Unit,

    ){
    val principleUiState by viewModel.principleUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var expandEditMenu by remember { mutableStateOf(false) }

    PrincipleBody(
        // navigation
        currentScreenName = currentScreenName,
        navigateToHabit = navigateToHabit,
        navigateToPrinciple = navigateToPrinciple,
        navigateToVisualize = navigateToVisualize,
        navigateToSetting = navigateToSetting,

        // background patterns
        backgroundPatternList = viewModel.backgroundDrawables,
        backgroundAccessorIndex = viewModel.backgroundAccessorIndex,

        // dates
        dateBase = principleUiState.dateBaseString,

        onDateSwipe = {},
        updateToToday = { viewModel.updateToToday() },
        updateToTomorrow = { viewModel.updateToTomorrow() },
        updateToYesterday = { viewModel.updateToYesterday() },

        isEqualToWeekBeforeToday = principleUiState.isBaseEqualWeekBefore(),
        isBeforeYesterday = principleUiState.isBaseBeforeYesterday(),
        isEqualToToday = principleUiState.isBaseEqualToday(),

        // principles
        onClickPrinciple = { principleDetails: PrincipleDetails ->
            coroutineScope.launch {
                viewModel.togglePrinciple(principleDetails)
            }
        },
        onHoldPrinciple = { principleDetails: PrincipleDetails ->
            Log.d("t", "$principleDetails held")
            //TODO: change sorting order
        },
        principleListToday = principleUiState.principleListToday,

        // Action Bar items
        addPrinciple = { viewModel.addPrinciple() },

        // edit dialog items
        expandEditMenu = expandEditMenu,
        editMenuPrincipleDetails = principleUiState.editablePrincipleDetails,
        onEditMenuDismiss = { expandEditMenu = false },
        onEditMenuExpand = { principleDetails: PrincipleDetails ->
            viewModel.setEditablePrincipleDetails(principleDetails)
            expandEditMenu = true
       },
        editMenuUpdatePrincipleInUiState = { principleDetail: PrincipleDetails ->
            viewModel.editMenuUpdatePrincipleInUiState(principleDetail)
        },
        editMenuApplyChangesToPrinciple = { viewModel.editMenuApplyChangesToPrinciple() },
        editMenuDeletePrinciple = {
            expandEditMenu = false
            viewModel.editMenuDeletePrinciple()
        },
        // delete confirmation
        //TODO: figure out how if we can have two dialogs
        // or how we could have a delete button - e.g.,
        // a button that asks, do you want to delete this, and then it's
        // set to readonly and a new one appears to the right of it
        // and that one actually deletes it.
    )
}


@Composable
fun PrincipleBody(
    // navigation
    currentScreenName: String,
    navigateToHabit: () -> Unit,
    navigateToPrinciple: () -> Unit,
    navigateToVisualize: () -> Unit,
    navigateToSetting: () -> Unit,

    // background drawables
    backgroundPatternList: List<Int>,
    backgroundAccessorIndex: Int,

    // edit menus
    expandEditMenu: Boolean,
    editMenuPrincipleDetails: PrincipleDetails,
    onEditMenuDismiss: () -> Unit,
    onEditMenuExpand: (PrincipleDetails) -> Unit,
    editMenuUpdatePrincipleInUiState: (PrincipleDetails) -> Unit,
    editMenuApplyChangesToPrinciple: () -> Unit,
    editMenuDeletePrinciple: () -> Unit,

    // dates
    dateBase: String,
    onDateSwipe: (Long) -> Unit,

    updateToToday: () -> Unit,
    updateToTomorrow: () -> Unit,
    updateToYesterday: () -> Unit,

    isEqualToWeekBeforeToday: Boolean,
    isBeforeYesterday: Boolean,
    isEqualToToday: Boolean,

    // principles
    principleListToday: List<PrincipleDetails>,
    onClickPrinciple: (PrincipleDetails) -> Unit,
    onHoldPrinciple: (PrincipleDetails) -> Unit,

    // action bar
    addPrinciple: () -> Unit
){
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /**
             * How can we un-fragment the title coloring such that the title bar only needs
             * the current Screen name
             * Title and Nav bar should not have padding beyond the scaffold
             */
            val paddedModifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_large))
                .border(1.dp, MaterialTheme.colorScheme.tertiary, RectangleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)

            var editMenuDeleteConfirmation by remember { mutableStateOf(false) }


            Column( modifier = Modifier.weight(1f) ){
                // title bar
                AppTitleBar( title = stringResource(id = PrincipleDestination.title) )
            }
            Column( modifier = Modifier.weight(8f)) {
                Column (modifier = Modifier.weight(1f)) {
                    // principle filter list
                    PrincipleFilterBar(
                        modifier = paddedModifier,

                        dateBase = dateBase,
                        isEqualToToday = isEqualToToday,
                        isEqualToWeekBeforeToday = isEqualToWeekBeforeToday,

                        onDateSwipe = onDateSwipe,
                        updateToTomorrow = updateToTomorrow,
                        updateToYesterday = updateToYesterday
                    )
                }
                Column( modifier = Modifier.weight(4f) ){
                    // principle list bar
                    PrincipleListBar(
                        modifier = paddedModifier,

                        // background drawables
                        backgroundPatternList = backgroundPatternList,
                        backgroundAccessorIndex = backgroundAccessorIndex,

                        onClickPrinciple = onClickPrinciple,
                        onHoldPrinciple = onHoldPrinciple,
                        principleListToday = principleListToday,

                        isBeforeYesterday = isBeforeYesterday,

                        // edit menus
                        expandEditMenu = expandEditMenu,
                        onEditMenuDismiss = onEditMenuDismiss,
                        onEditMenuExpand = onEditMenuExpand,
                        editMenuPrincipleDetails = editMenuPrincipleDetails,
                        editMenuUpdatePrincipleInUiState = editMenuUpdatePrincipleInUiState,
                        editMenuApplyChangesToPrinciple = editMenuApplyChangesToPrinciple,
                        editMenuDeletePrinciple = editMenuDeletePrinciple,
                        editMenuDeleteConfirmation = editMenuDeleteConfirmation,
                        editMenuDeleteToExpand = { editMenuDeleteConfirmation = true}

                    )
                }
                Column ( modifier = Modifier.weight(1f) ) {
                    // action bar
                    PrincipleActionBar(
                        modifier = paddedModifier,

                        addPrinciple = addPrinciple,
                        setOfferRebase = !isEqualToToday,
                        rebaseToToday = updateToToday
                    )
                }

            }
            Column ( modifier = Modifier.weight(1f) ) {
                // navigation bar
                HabitNavBar(
                    navigateToHabit = navigateToHabit,
                    navigateToPrinciple = navigateToPrinciple,
                    navigateToVisualize = navigateToVisualize,
                    navigateToSetting = navigateToSetting,
                    currentScreenName = currentScreenName
                )
            }
        }
    }
}


/**
 * setVisible - true when dateBase is today, so only show it when it isn't
 */
@Composable
fun PrincipleFilterBar(
    modifier: Modifier = Modifier,

    dateBase: String,

    isEqualToToday: Boolean,
    isEqualToWeekBeforeToday: Boolean,

    onDateSwipe: (Long) -> Unit,
    updateToTomorrow: () -> Unit,
    updateToYesterday: () -> Unit,
){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
        ){
            IconButton(
                enabled = !isEqualToWeekBeforeToday,
                onClick = updateToYesterday,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardDoubleArrowLeft,
                    contentDescription = "Yesterday"
                )
            }
            Text(
                modifier = Modifier.weight(2f),
                textAlign = TextAlign.Center,
                text = dateBase,
                style = MaterialTheme.typography.displayMedium
            )
            IconButton(
                enabled = !isEqualToToday,
                onClick = updateToTomorrow,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardDoubleArrowRight,
                    contentDescription = "Tomorrow"
                )
            }
        }
    }
}

/**
 * onHold is where we can edit the name of the principle
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PrincipleListBar(
    modifier: Modifier = Modifier,

    // background drawables
    backgroundPatternList: List<Int>,
    backgroundAccessorIndex: Int,

    isBeforeYesterday: Boolean,

    principleListToday: List<PrincipleDetails>,

    onClickPrinciple: (PrincipleDetails) -> Unit,
    onHoldPrinciple: (PrincipleDetails) -> Unit,

    // edit menus
    expandEditMenu: Boolean,
    onEditMenuDismiss: () -> Unit,
    onEditMenuExpand: (PrincipleDetails) -> Unit,
    editMenuPrincipleDetails: PrincipleDetails,
    editMenuUpdatePrincipleInUiState: (PrincipleDetails) -> Unit,
    editMenuApplyChangesToPrinciple: () -> Unit,
    editMenuDeletePrinciple: () -> Unit,
    editMenuDeleteConfirmation: Boolean,
    editMenuDeleteToExpand: () -> Unit,
){
    val patternPainter = painterResource(id = backgroundPatternList[backgroundAccessorIndex])

    //TODO: Maybe lower the opacity
    //TODO: increase the amount of vectors
    //TODO: reduce the size - increase the padding
    //TODO: color them differently according to Material theme
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .drawBehind {
            val patternWidth = patternPainter.intrinsicSize.width
            val patternHeight = patternPainter.intrinsicSize.height

            val repetitionsX = (size.width / patternWidth).toInt() + 1
            val repetitionsY = (size.height / patternHeight).toInt() + 1

            for (i in 0..repetitionsX) {
                for (j in 0..repetitionsY) {
                    translate(i * patternWidth, j * patternHeight) {
                        // Draw the vector drawable
                        with(patternPainter) {
                            draw(size = Size(patternWidth, patternHeight))
                        }
                    }
                }
            }
        },
    ){

        PrincipleDetailEditMenuDialog(
            expandEditMenu = expandEditMenu,
            onEditMenuDismiss = onEditMenuDismiss,
            editMenuPrincipleDetails = editMenuPrincipleDetails,

            editMenuUpdatePrincipleInUiState = editMenuUpdatePrincipleInUiState,
            editMenuApplyChangesToPrinciple = editMenuApplyChangesToPrinciple,
            editMenuDeletePrinciple = editMenuDeletePrinciple,

            editMenuDeleteConfirmation = editMenuDeleteConfirmation,
            editMenuDeleteToExpand = editMenuDeleteToExpand,

            isBeforeYesterday = isBeforeYesterday,
        )

        if (principleListToday.isEmpty()){
            Text(
                text = stringResource(id = R.string.principle_list_empty),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
        else {
            LazyColumn(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_large))
            ){
                items(principleListToday, key = { principleDetail -> principleDetail.principleId }) { principleDetail ->

                    Row(
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding_medium))
                            .animateItemPlacement(tween(200))
                            //onHoldPrinciple should be here to allow sort changes
                    ) {
                        PrincipleDetailsCard(
                            principleDetail = principleDetail,

                            onEditMenuExpand = { onEditMenuExpand(principleDetail) },
                            onClickPrinciple = { value ->
                                onClickPrinciple(principleDetail.copy(value = value))},

                            isBeforeYesterday = isBeforeYesterday,
                        )
                    }
                }
            }
        }
    }
}


/**
 * setOfferRebase - Base date !isEqual to Today, if true, show a button that offers rebasing to today.
 */
@Composable
fun PrincipleActionBar(
    modifier: Modifier = Modifier,

    setOfferRebase: Boolean,
    rebaseToToday: () -> Unit,

    addPrinciple: () -> Unit,
){
    Row(
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.padding_medium))
    ) {

        // Create button portion
        ElevatedButton(
            onClick = addPrinciple,
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
                contentDescription = stringResource(R.string.content_description_FAB_principle_create)
            )
            Text(
                text = stringResource(id = R.string.principle_button_create),
                style = MaterialTheme.typography.displaySmall
            )
        }


        //rebase to today button
        ElevatedButton(
            enabled = setOfferRebase,
            onClick = rebaseToToday,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondary
            ),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary),
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_small)),
        ) {
            Icon(
                imageVector = Icons.Filled.CalendarToday,
                tint = MaterialTheme.colorScheme.tertiary,
                contentDescription = stringResource(R.string.content_description_rebaseToToday)
            )
            Text(
                text = stringResource(id = R.string.rebaseToToday),
                style = MaterialTheme.typography.displaySmall
            )
        }
    }
}


/**
 * setReadOnly - checks if base date == day before yesterday.
 */
@Composable
fun PrincipleDetailsCard(
    principleDetail: PrincipleDetails,

    onEditMenuExpand: () -> Unit,
    onClickPrinciple: (Boolean) -> Unit,

    isBeforeYesterday: Boolean
){
    // should be the coloured portion
    // onMoreClick allows you to change the name of it

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
    ){
        OutlinedCard(
            modifier = Modifier
                .weight(2f)
                .padding(dimensionResource(id = R.dimen.padding_small))
                .fillMaxSize()
                .clickable { onEditMenuExpand() }
        ){
            Text(
                text = principleDetail.name,
                style = MaterialTheme.typography.displaySmall
            )
 //           Text(principleDetail.description)
        }

        Checkbox(
            modifier = Modifier
                .weight(1f)
                .padding(dimensionResource(id = R.dimen.padding_small))
                .fillMaxSize(),
            checked = principleDetail.value,
            onCheckedChange = onClickPrinciple,
            enabled = !isBeforeYesterday
        )

    }
    //TODO: we need to change the checkbox to have different visuals
    //TODO:
    //principleDetail.description should be a hover or equivalent.
}

@Composable
fun PrincipleDetailEditMenuDialog(
    expandEditMenu: Boolean,
    onEditMenuDismiss: () -> Unit,

    isBeforeYesterday: Boolean,
    editMenuPrincipleDetails: PrincipleDetails,

    editMenuUpdatePrincipleInUiState: (PrincipleDetails) -> Unit,
    editMenuApplyChangesToPrinciple: () -> Unit,
    editMenuDeletePrinciple: () -> Unit,

    editMenuDeleteConfirmation: Boolean,
    editMenuDeleteToExpand: () -> Unit,
){
    if (expandEditMenu) {
        Dialog(
            onDismissRequest = onEditMenuDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            PrincipleDetailEditMenuDialogContent(
                editMenuPrincipleDetails = editMenuPrincipleDetails,
                editMenuUpdatePrincipleInUiState = editMenuUpdatePrincipleInUiState,
                editMenuApplyChangesToPrinciple = editMenuApplyChangesToPrinciple,
                editMenuDeletePrinciple = editMenuDeletePrinciple,
                editMenuDeleteConfirmation = editMenuDeleteConfirmation,
                editMenuDeleteToExpand = editMenuDeleteToExpand,

                isBeforeYesterday = isBeforeYesterday,
            )
        }
    }
}

@Composable
fun PrincipleDetailEditMenuDialogContent(
    editMenuPrincipleDetails: PrincipleDetails,
    isBeforeYesterday: Boolean,
    editMenuUpdatePrincipleInUiState: (PrincipleDetails) -> Unit,
    editMenuApplyChangesToPrinciple: () -> Unit,
    editMenuDeletePrinciple: () -> Unit,

    editMenuDeleteConfirmation: Boolean,
    editMenuDeleteToExpand: () -> Unit,
){
    Column {
        // title
        Text(
            text = "Editing principle"
        )

        Button(
            onClick = editMenuApplyChangesToPrinciple
        ) {
            Text(
                text = "Apply changes"
            )
        }
        //TODO: set this read only if there were no changes made

        // name
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(id = R.string.form_required_name)) },
            value = editMenuPrincipleDetails.name,
            onValueChange = {
                editMenuUpdatePrincipleInUiState(editMenuPrincipleDetails.copy(name = it))
            },
            singleLine = true
        )

        // description
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(id = R.string.form_required_description)) },
            value = editMenuPrincipleDetails.description,
            onValueChange = {
                editMenuUpdatePrincipleInUiState(editMenuPrincipleDetails.copy(description = it))
            },
            singleLine = true
        )

        // value
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Checked",
                style = MaterialTheme.typography.displaySmall
            )

            OutlinedButton(
                onClick = {
                    editMenuUpdatePrincipleInUiState(editMenuPrincipleDetails.copy(value =
                    !editMenuPrincipleDetails.value ))
                },
            ) {
                if (editMenuPrincipleDetails.value) {
                    Text(
                        text = "Yes"
                    )
                } else {
                    Text(
                        text = "No"
                    )
                }
            }

        }


        // group
        /*
        Text(
            text = "${editMenuPrincipleDetails.group}"
        )
        //TODO: add group as a property and allow some editing maybe
         */

        // date created
        /*
        Text(
            text = longToStringFormat_ddMMYYYY(editMenuPrincipleDetails.dateCreated)
        )
        //TODO: Make a function and pass it down for the dating
        //TODO: make a property for that
         */


        // delete expand and actual delete
        Button(
            onClick = editMenuDeleteToExpand
        ){
            Text(
                text = "Delete principle"
            )
        }
        if ( editMenuDeleteConfirmation ) {
            Button(
                onClick = editMenuDeletePrinciple
            ){
                Text(
                    text = "Confirm deletion"
                )
            }
        }

    }
}


@Preview(
    showBackground = true,
)
@Composable
fun BackgroundPattern() {

    val backgroundDrawables = listOf(
        R.drawable.baseline_circle_24,
        R.drawable.baseline_elderly_24,
        R.drawable.baseline_hexagon_24,
        R.drawable.baseline_electric_bolt_24
    )

    var backgroundAccessorIndex = Random.nextInt(backgroundDrawables.size)

    val patternPainter = painterResource(id = backgroundDrawables[backgroundAccessorIndex])

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .drawBehind {
                val patternWidth = patternPainter.intrinsicSize.width
                val patternHeight = patternPainter.intrinsicSize.height

                val repetitionsX = (size.width / patternWidth).toInt() + 1
                val repetitionsY = (size.height / patternHeight).toInt() + 1

                for (i in 0..repetitionsX) {
                    for (j in 0..repetitionsY) {
                        translate(i * patternWidth, j * patternHeight) {
                            // Draw the vector drawable
                            with(patternPainter) {
                                draw(size = Size(patternWidth, patternHeight))
                            }
                        }
                    }
                }
            }
            .blur(6.dp)
        ,
        contentAlignment = Alignment.Center
    ) {
        // This Box will be behind other content
    }
}