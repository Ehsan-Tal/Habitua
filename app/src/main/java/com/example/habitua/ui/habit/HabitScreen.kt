@file:OptIn(ExperimentalFoundationApi::class)
// apparently this allows the animation ?

package com.example.habitua.ui.habit

import android.net.Uri
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.habitua.R
import com.example.habitua.data.Habit
import com.example.habitua.ui.AppViewModelProvider
import com.example.habitua.ui.AppNavBar
import com.example.habitua.ui.navigation.NavigationDestination
import com.example.habitua.ui.theme.PreviewHabituaTheme
import kotlinx.coroutines.launch


object HabitDestination : NavigationDestination {
    override val route = "home"
    override val title = R.string.habit_title
    val navTitle = R.string.habit_nav_title
}

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
            navigateToPrinciple = navigateToPrinciple,
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
            navigateToPrinciple = navigateToPrinciple,
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
                HabitCard(
                    habit = habit,
                    onClickHabitCard = onClickHabitCard,
                    onMoreOptionsClick = { onMoreOptionsClick(habit.id) },
                    userReviewedToday = userReviewedToday,
                    onIconClick = onIconClick
                )
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
fun HabitCard (
    habit: Habit,
//    onHabitIconChanged: (Int) -> Unit,
    userReviewedToday: Boolean,
    modifier: Modifier = Modifier,
    onClickHabitCard: (Habit) -> Unit,
    onMoreOptionsClick: (Habit) -> Unit,

    onIconClick: () -> Unit
) {

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
            .border(
                if (userReviewedToday) 0.dp else if (habit.isActive) 3.dp else 1.dp,
                MaterialTheme.colorScheme.tertiary, MaterialTheme.shapes.medium
            )
            .alpha(if (userReviewedToday) 0.5f else 1f)
            .clickable {
                if (!userReviewedToday) {
                    onClickHabitCard(habit)
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
                //TODO: have something that connects the content description with the image, pls.
                    HabitIcon(
                        drawableRes = habit.imageResId,
                        contentDescription = "",
                        onIconClick = onIconClick,
                        modifier = Modifier
                            .weight(1f)
                        )
                    /*
                    HabitIcon(
                        imageURI = habit.imageURI,
                        iconMenuExpanded = iconMenuExpanded
                    ) */

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
    drawableRes: Int,
    contentDescription: String,
    onIconClick: () -> Unit,
    modifier: Modifier
){
    Image(
        painter = painterResource(id =  drawableRes),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .size(dimensionResource(R.dimen.image_size))
            .clip(RoundedCornerShape(100))
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.tertiary,
                shape = CircleShape
            )
            .shadow(elevation = 6.dp)
            .clickable { onIconClick() },
    )

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

