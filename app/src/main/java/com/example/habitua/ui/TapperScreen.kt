package com.example.habitua.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.habitua.HabitApp
import com.example.habitua.R
import com.example.habitua.data.Habit



@Composable
fun TapperScreen(
    habitViewModel: HabitViewModel,
    currentScreenName: String,
    habitList: List<Habit>,
    navController: NavHostController
){
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { habitViewModel.createHabit() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    tint = MaterialTheme.colorScheme.tertiary,
                    contentDescription = "Add a habit"
                )
            }
        }
        // floater button
    )
    { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ("Your $currentScreenName"), // should be a str resource,
                    style = MaterialTheme.typography.displayLarge,
                )
            }
            Column (
                modifier = Modifier
                    .weight(8f)
                    .padding(dimensionResource(R.dimen.padding_small))
            ){
                TapperColumn(
                    habitList = habitList,
                    habitViewModel = habitViewModel,
                )
            }
            HabitNavBar(
                navController = navController,
                currentScreenName = currentScreenName
            )
        }
    }
}


@Composable
fun TapperColumn(
    habitList: List<Habit>,
    habitViewModel: HabitViewModel,
    modifier: Modifier = Modifier,
){

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .fillMaxSize()
    ) {
        LazyColumn (
            modifier = modifier
                .padding(
                    start = dimensionResource(R.dimen.padding_medium),
                    end = dimensionResource(R.dimen.padding_medium),
                    top = dimensionResource(R.dimen.padding_small),
                    bottom = dimensionResource(R.dimen.padding_small)
                )
        ) {
            items(habitList) {habit ->
                TapperCard(
                    habit = habit,
                    onToggleActive = {
                        habitViewModel.toggleHabitActive(habit)
                    },
                    onToggleExpansion = {
                        habitViewModel.toggleHabitExpansion(habit)
                    },
                    onHabitNameChanged = {
                        habitViewModel.updateHabitName(habit,it)
                    },
                    onHabitDescriptionChanged = {
                        habitViewModel.updateHabitDescription(habit, it)
                    },
                    onHabitIconChanged = {
                        habitViewModel.updateHabitIcon(habit, it)
                        // Now, I have written an int, but we'd need to
                        // change this once we understand how to change the img properly.
                        // or remove this :)
                    },
                    onDeleteHabit = {
                        habitViewModel.deleteHabit(habit)
                    },
                    modifier = modifier
                )
            }
        }
    }
}




@Composable
fun TapperCard(
    habit: Habit,
    onToggleActive: () -> Unit,
    onToggleExpansion: () -> Unit,
    onHabitNameChanged: (String) -> Unit,
    onHabitDescriptionChanged: (String) -> Unit,
    onHabitIconChanged: (Int) -> Unit,
    onDeleteHabit: () -> Unit,
    modifier: Modifier = Modifier
) {

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
            .padding(bottom = 0.dp),

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
                TextButton(
                    onClick = onToggleActive,
                ) {
                    Box(
                        //TODO:  change this to an img button !

                        modifier = Modifier
                            .padding(dimensionResource(R.dimen.padding_small))
                            .weight(1f)
                    ) {
                        TapperIcon(habit.imageResourceId)
                    }
                    Column(
                        modifier = Modifier
                            .padding(dimensionResource(R.dimen.padding_medium))
                            .weight(6f),
                    ) {
                        Text(
                            text = habit.name,
                            style = MaterialTheme.typography.displayMedium,
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
                    TapperCardButton(
                        modifier = Modifier.weight(1f),
                        habit = habit,
                        onHabitExpansion = onToggleExpansion
                    )
                }
            }
            Row (
            )
            {
                if (habit.isExpanded) {
                    // here is where we can use the onValue Change items
                    // Image Button, Two Text fields,
                    Column(
                        modifier = Modifier
                            .weight(5f)

                    ) {
                        OutlinedTextField(
                            value = habit.name,
                            onValueChange = onHabitNameChanged,
                            label = {Text("Name")},
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = habit.description,
                            onValueChange = onHabitDescriptionChanged,
                            label = {Text("Description")},
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }

                    //TextField(value = , onValueChange = )
                    IconButton(
                        modifier = Modifier.weight(1f),
                        onClick = { onDeleteHabit() }
                        // alert dialog should fire !
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            tint = MaterialTheme.colorScheme.error,
                            contentDescription = "Delete Habit"
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun TapperIcon(
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

@Composable
private fun TapperCardButton(
    habit: Habit,
    onHabitExpansion: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onHabitExpansion,
        modifier = modifier
    ) {
        Icon (
            imageVector = if (habit.isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = stringResource(R.string.expand_button_content_description),
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}
