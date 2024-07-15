package com.example.habitua.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
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
import kotlinx.coroutines.launch


object HabitDestination : NavigationDestination {
    override val route = "home"
    override val title = "Habits" //R.string.app_name
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

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToHabitEntry,
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    tint = MaterialTheme.colorScheme.tertiary,
                    contentDescription = stringResource(R.string.content_description_FAB_habit_create)
                )
            }
         }
        // floater button
    )
    { innerPadding ->
        HabitBody(
            habitList = homeUiState.habitList,
            currentScreenName = currentScreenName,
            contentPadding = innerPadding,
            navController = navController,
            viewModel = viewModel,
            onMoreOptionsClick = navigateToHabitEdit,
            //,
        )
    }
}


@Composable
private fun HabitBody(
    habitList: List<Habit>,
    currentScreenName: String,
    contentPadding: PaddingValues,
    navController: NavHostController,
    viewModel: HomeViewModel,
    onMoreOptionsClick: (Int) -> Unit
){
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = (stringResource(R.string.screen_your) + " " + currentScreenName), // should be a str resource,
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
            )
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
    contentPadding: PaddingValues = PaddingValues(0.dp),
    viewModel: HomeViewModel,
    onMoreOptionsClick: (Int) -> Unit
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
                onMoreOptionsClick = onMoreOptionsClick
            )

        }
    }
}

@Composable
private fun HabitList(
     habitList: List<Habit>,
     contentPadding: PaddingValues,
     modifier: Modifier = Modifier,
     viewModel: HomeViewModel,
     onMoreOptionsClick: (Int) -> Unit
) {
    LazyColumn (
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(habitList) {habit ->

            HabitCard(
                habit = habit,
                viewModel = viewModel,
                onMoreOptionsClick = { onMoreOptionsClick(it.id) },
            )
        }
    }
}

@Composable
fun HabitCard (
    habit: Habit,
//    onHabitIconChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onMoreOptionsClick: (Habit) -> Unit,
    viewModel: HomeViewModel
) {
    val coroutineScope = rememberCoroutineScope()

    val foregroundColor by animateColorAsState(
        targetValue = if (habit.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "Foreground color change")

    val backgroundColor by animateColorAsState(
        targetValue = if (habit.isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        label = "Background color change"
    )

    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(
            containerColor = backgroundColor,
            contentColor = foregroundColor
        ),
        elevation = CardDefaults.cardElevation(
            4.dp
        ),
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .padding(bottom = 0.dp)
            .clickable {
                coroutineScope.launch {
                    viewModel.toggleHabitActive(habit)
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
                Box(
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
    Image(
        modifier = Modifier
            .size(dimensionResource(R.dimen.image_size))
            .clip(RoundedCornerShape(100))
            .border(width = 2.dp, color = MaterialTheme.colorScheme.tertiary, shape = CircleShape)
            .shadow(elevation = 6.dp),
        contentScale = ContentScale.Crop,
        contentDescription = null,
        painter = painterResource(habitIcon),

        )
}

