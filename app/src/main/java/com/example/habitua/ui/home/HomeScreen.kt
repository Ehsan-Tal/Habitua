@file:OptIn(ExperimentalFoundationApi::class)

package com.example.habitua.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.habitua.R
import com.example.habitua.data.Habit
import com.example.habitua.ui.AppViewModelProvider
import com.example.habitua.ui.HabitNavBar
import com.example.habitua.ui.navigation.NavigationDestination
import com.example.habitua.ui.theme.LocalCustomColorsPalette
import kotlinx.coroutines.launch
import kotlin.random.Random


object HabitDestination : NavigationDestination {
    override val route = "home"
    override val title = R.string.habit_title
    val navTitle = R.string.habit_nav_title
}

// try not to retrofit your arch here
//

// we need a way
// view Model functions
// a way to get to the habit and habit details
// time to study !s

//    val homeUiState by viewModel.homeUiState.collectAsState()
@Composable
fun HabitScreen(
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    currentScreenName: String,
    navigateToHabitEntry: () -> Unit,
    navigateToHabitEdit: (Int) -> Unit,
    navController: NavHostController
){
    val homeUiState by viewModel.homeUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var reviewConfirmationRequired by rememberSaveable { mutableStateOf(false) }

    //viewModel.dateToday
    // we need this somewhere

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    )
    { innerPadding ->
        HabitBody(
            habitList = homeUiState.habitList,
            currentScreenName = currentScreenName,
            contentPadding = innerPadding,
            navController = navController,
            viewModel = viewModel,
            onCreateButtonClick = navigateToHabitEntry,
            onMoreOptionsClick = navigateToHabitEdit,

            //review button related material
            reviewConfirmationRequired = reviewConfirmationRequired,
            onReviewClick = {
                reviewConfirmationRequired = true
            },
            onReviewAccept = {
                coroutineScope.launch {
                    reviewConfirmationRequired = false
                    viewModel.reviewHabits()
                }
            },
            onReviewDismiss = {
                reviewConfirmationRequired = false
            },
            userReviewedToday = homeUiState.userReviewedToday,
        )
    }
}


@Composable
private fun HabitBody(
    habitList: List<Habit>,

    reviewConfirmationRequired: Boolean,
    userReviewedToday: Boolean,

    currentScreenName: String,
    contentPadding: PaddingValues,
    navController: NavHostController,
    viewModel: HomeViewModel,
    onCreateButtonClick: () -> Unit,
    onMoreOptionsClick: (Int) -> Unit,
    onReviewClick: () -> Unit,
    onReviewAccept: () -> Unit,
    onReviewDismiss: () -> Unit,
){
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
            HabitColumn(
                habitList = habitList,
                viewModel = viewModel,
                onMoreOptionsClick = onMoreOptionsClick,

                userReviewedToday = userReviewedToday,

                )
        }

        // habit buttons
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

        HabitNavBar(
            navController = navController,
            currentScreenName = currentScreenName
        )
    }
}


@Composable
private fun HabitColumn(
    habitList: List<Habit>,
    userReviewedToday: Boolean,
    viewModel: HomeViewModel,
    onMoreOptionsClick: (Int) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),

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
                modifier = Modifier.padding(contentPadding)
            )
        } else {
            HabitList(
                habitList = habitList,
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_medium)),
                viewModel = viewModel,
                onMoreOptionsClick = onMoreOptionsClick,
                userReviewedToday = userReviewedToday,
            )
        }

    }
}

@Composable
private fun HabitList(
    habitList: List<Habit>,
    contentPadding: PaddingValues,
    viewModel: HomeViewModel,
    onMoreOptionsClick: (Int) -> Unit,
    userReviewedToday: Boolean,

    modifier: Modifier = Modifier,
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
                HabitCard(
                    habit = habit,
                    viewModel = viewModel,
                    onMoreOptionsClick = { onMoreOptionsClick(habit.id) },
                    userReviewedToday = userReviewedToday,
                )
            }
        }
    }
}

@Composable
fun HabitCard (
    habit: Habit,
//    onHabitIconChanged: (Int) -> Unit,
    userReviewedToday: Boolean,
    modifier: Modifier = Modifier,
    onMoreOptionsClick: (Habit) -> Unit,
    viewModel: HomeViewModel
) {

    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    val foregroundColor by animateColorAsState(
        targetValue =
        if (habit.isActive){ MaterialTheme.colorScheme.primary }
        else MaterialTheme.colorScheme.secondary,
        label = "Foreground color change")

    val backgroundColor by animateColorAsState(
        targetValue =
            if (habit.isActive){ MaterialTheme.colorScheme.primaryContainer }
            else MaterialTheme.colorScheme.secondaryContainer,
        label = "Background color change"
    )

    // Animating card.

    // Trigger animation when either color changes
    LaunchedEffect(key1 = habit.isActive) {
        val randomDelay = 0.2f

        scale.animateTo(
            targetValue = 0.9f,
            animationSpec = tween( (randomDelay * 1000).toInt(), easing = LinearEasing)
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(easing = LinearEasing)
        )
    }

    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = backgroundColor,
            contentColor = foregroundColor
        ),
        elevation = CardDefaults.cardElevation(
            4.dp
        ),
        modifier = Modifier
            .scale(scale.value)
            .padding(dimensionResource(R.dimen.padding_small))
            .padding(bottom = 0.dp)
            .border(1.dp, MaterialTheme.colorScheme.tertiary, MaterialTheme.shapes.medium)
            .alpha(if (userReviewedToday) 0.8f else 1f)
            .clickable {
                if (!userReviewedToday) {
                    coroutineScope.launch {
                        viewModel.toggleHabitActive(habit)
                    }
                }
            },
    ) {

        Column (
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                    // not really sure what this did to improve the animation
                    // perhaps text fields are a bit laggy
                )
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = {},
                    //TODO:  change this to an img button !

                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_small))
                        .weight(1f)
                ) {
                    HabitIcon(habit.imageResId)
                }
                Column(
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_medium))
                        .weight(6f),
                ) {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.displaySmall,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = habit.description,
                        style = MaterialTheme.typography.bodySmall,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(
                    onClick = { onMoreOptionsClick(habit) },
                    modifier = modifier
                        .weight(1f)
                ) {
                    Icon (
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = stringResource(R.string.expand_button_content_description),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun HabitIcon(
    @DrawableRes habitIcon: Int
){

    /*
    val painter = remember(habitIcon) {
        try {
            painterResource(id = habitIcon)
        } catch(e: Exception) {
            Log.w("HabitIcon", "Invalid imageResId: $habitIcon", e)
            painterResource(id = R.drawable.tal_derpy)
        }
    }
    */
    Image(
        modifier = Modifier
            .size(dimensionResource(R.dimen.image_size))
            .clip(RoundedCornerShape(100))
            .border(width = 2.dp, color = MaterialTheme.colorScheme.tertiary, shape = CircleShape)
            .shadow(elevation = 6.dp),

        contentScale = ContentScale.Crop,
        contentDescription = null,
        //painter = painterResource(habitIcon),
        painter = painterResource(id = R.drawable.tal_derpy),

        )
}
