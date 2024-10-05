package com.example.habitua.ui.principles

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitua.R
import com.example.habitua.data.PrincipleDetails
import com.example.habitua.ui.AppTitleBar
import com.example.habitua.ui.AppViewModelProvider
import com.example.habitua.ui.HabitNavBar
import com.example.habitua.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch


object PrincipleDestination: NavigationDestination {
    override val route = "principle"
    override val title = R.string.principle_title
    val navTitle = R.string.principle_nav_title
}


@Composable
fun PrincipleScreen(
    viewModel: PrincipleViewModel = viewModel(factory = AppViewModelProvider.Factory),

    currentScreenName: String,
    navigateToPrincipleEntry: () -> Unit,
    navigateToHabit: () -> Unit,
    navigateToPrinciple: () -> Unit,
    navigateToVisualize: () -> Unit,
    navigateToSetting: () -> Unit,

    ){
    val principleUiState by viewModel.principleUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    PrincipleBody(
        // navigation
        currentScreenName = currentScreenName,
        navigateToHabit = navigateToHabit,
        navigateToPrinciple = navigateToPrinciple,
        navigateToVisualize = navigateToVisualize,
        navigateToSetting = navigateToSetting,
        navigateToPrincipleEntry = navigateToPrincipleEntry,

        // dates
        dateBefore = principleUiState.dateBeforeString,
        dateBase = principleUiState.dateBaseString,
        dateAfter = principleUiState.dateAfterString,
        
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
        onMoreOptionsClickPrinciple = {},

    )
}


@Composable
fun PrincipleBody(
    // navigation
    currentScreenName: String,
    navigateToPrincipleEntry: () -> Unit,
    navigateToHabit: () -> Unit,
    navigateToPrinciple: () -> Unit,
    navigateToVisualize: () -> Unit,
    navigateToSetting: () -> Unit,

    // dates
    dateBefore: String,
    dateBase: String,
    dateAfter: String,
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
    onMoreOptionsClickPrinciple: (PrincipleDetails) -> Unit,
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
                .padding(dimensionResource(id = R.dimen.padding_large))
                .border(1.dp, MaterialTheme.colorScheme.tertiary, RectangleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)

            // title bar
            AppTitleBar(
                title = stringResource(id = PrincipleDestination.title)
            )

            // principle filter list
            PrincipleFilterBar(
                modifier = paddedModifier,

                dateBase = dateBase,
                dateBefore = dateBefore,
                dateAfter = dateAfter,
                isEqualToToday = isEqualToToday,
                isEqualToWeekBeforeToday = isEqualToWeekBeforeToday,

                onDateSwipe = onDateSwipe,
                updateToTomorrow = updateToTomorrow,
                updateToYesterday = updateToYesterday
            )

            // principle list bar
            PrincipleListBar(
                modifier = paddedModifier,

                onClickPrinciple = onClickPrinciple,
                onHoldPrinciple = onHoldPrinciple,
                onMoreOptionsClickPrinciple = onMoreOptionsClickPrinciple,
                principleListToday = principleListToday,

                isBeforeYesterday = isBeforeYesterday,
            )

            // action bar
            PrincipleActionBar(
                navigateToPrincipleEntry = navigateToPrincipleEntry,
                setOfferRebase = !isEqualToToday,
                rebaseToToday = updateToToday
            )

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


/**
 * setVisible - true when dateBase is today, so only show it when it isn't
 */
@Composable
fun PrincipleFilterBar(
    modifier: Modifier = Modifier,

    dateBase: String,
    dateBefore: String,
    dateAfter: String,

    isEqualToToday: Boolean,
    isEqualToWeekBeforeToday: Boolean,

    onDateSwipe: (Long) -> Unit,
    updateToTomorrow: () -> Unit,
    updateToYesterday: () -> Unit,
){
    Column(
        modifier = modifier
    ){
        Row(){
            if (!isEqualToWeekBeforeToday) {
                Text(text = dateBefore)
            }
            Text(text = dateBase)

            // we also want to perhaps expand it as far as possible

            // either this, or we color it differently
            if (!isEqualToToday){
                Text(text = dateAfter)
            }
        }
        Row(){

            Button(
                enabled = !isEqualToWeekBeforeToday,
                onClick = updateToYesterday,
            ) {
                Text("Change to Yesterday")
            }
            Button(
                enabled = !isEqualToToday,
                onClick = updateToTomorrow,
            ) {
                Text("Change to Tomorrow")
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

    onMoreOptionsClickPrinciple: (PrincipleDetails) -> Unit,
    onClickPrinciple: (PrincipleDetails) -> Unit,
    onHoldPrinciple: (PrincipleDetails) -> Unit,

    isBeforeYesterday: Boolean,

    principleListToday: List<PrincipleDetails>,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ){
        if (principleListToday.isEmpty()){
            Text(
                text = stringResource(id = R.string.principle_list_empty),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_medium))
            )
        } else {
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
                            onMoreClick = onMoreOptionsClickPrinciple,
                            onClickPrinciple = onClickPrinciple,
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
    navigateToPrincipleEntry: () -> Unit,

    setOfferRebase: Boolean,
    rebaseToToday: () -> Unit,
){
    Row(
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.padding_medium))
    ) {

        // Create button portion
        ElevatedButton(
            onClick = navigateToPrincipleEntry,
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
        if (setOfferRebase){
            TextButton(
                onClick = rebaseToToday,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
            ) {
                Text(text = "To Today")
            }
        }
    }
}


/**
 * setReadOnly - checks if base date == day before yesterday.
 */
@Composable
fun PrincipleDetailsCard(
    principleDetail: PrincipleDetails,

    onMoreClick: (PrincipleDetails) -> Unit,
    onClickPrinciple: (PrincipleDetails) -> Unit,

    isBeforeYesterday: Boolean
){
    // should be the coloured portion
    // onMoreClick allows you to change the name of it

    OutlinedCard(

    ){
        Text(principleDetail.name)
        Text(principleDetail.description)
        Text(principleDetail.date.toString())
        Text(isBeforeYesterday.toString())
    }
    //principleDetail.description should be a hover or equivalent.
}
