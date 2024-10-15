package com.example.habitua.ui.principles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitua.R
import com.example.habitua.data.Principle
import com.example.habitua.ui.AppNavBar
import com.example.habitua.ui.AppTitleBar
import com.example.habitua.ui.AppViewModelProvider
import com.example.habitua.ui.navigation.PrincipleDestination
import com.example.habitua.ui.navigation.PrincipleEditDestination
import com.example.habitua.ui.theme.PreviewHabituaTheme
import com.example.habitua.ui.theme.toothpasteShape
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.random.Random


@Composable
fun PrincipleEditScreen(
    viewModel: PrincipleEditViewModel = viewModel(factory = AppViewModelProvider.Factory),

    currentScreenName: String,
    navigateToHabit: () -> Unit,
    navigateToPrinciple: () -> Unit,
    navigateToIssue: () -> Unit,
    navigateToYou: () -> Unit,
) {
    //TODO: what.
        // what you need to do now is work the view model
    // and then pass in all the relevant parameterized functions
    // And then give yourself a sanity check of it working
    // and hope testing it would be easy to do.

    /*
            habitUiState = viewModel.habitUiState,
        onHabitValueChange = viewModel::updateUiState,
        onSaveClick = {
            coroutineScope.launch {
                viewModel.updateHabit()
                navigateBack()
            }
        },
        onDelete = {
            coroutineScope.launch {
                viewModel.deleteHabit()
                navigateBack()
            }
        },


    when (uiState) {
        PrincipleEditUiState.Error -> {}
        PrincipleEditUiState.Loading -> {}
        PrincipleEditUiState.Success -> {}
    }
     */

    val coroutineScope = rememberCoroutineScope()

    val uiState = viewModel.uiState

    PrincipleEditBody(
        // app title bar stuff
        appTitle = stringResource(id = PrincipleEditDestination.title),

        backgroundPatternList = viewModel.backgroundDrawables,
        backgroundAccessorIndex = viewModel.backgroundAccessorIndex,

        principle = uiState.principle,

        onValueEdit = viewModel::updateUiState,
        //TODO: what does :: mean ? in this case
        onDelete = {
            coroutineScope.launch {
                viewModel.deletePrinciple()
                navigateToPrinciple()
            }
        },
        onSave = {
            coroutineScope.launch {
                viewModel.savePrinciple()
            }
        },
        hasThereBeenChanges = uiState.hasThereBeenChanges,

        //navigation
        currentScreenName = currentScreenName,
        navigateToHabit = navigateToHabit,
        navigateToPrinciple = navigateToPrinciple,
        navigateToIssue = navigateToIssue,
        navigateToYou = navigateToYou,
    )
}

/**
 *
 * secondActionButtonName: String = "" // if no given name, do not generate a second button
 */
@Composable
fun PrincipleEditBody (

    onValueEdit: (Principle) -> Unit,
    onDelete: () -> Unit,
    onSave: () -> Unit,
    hasThereBeenChanges: Boolean,

    // app title bar stuff
    appTitle: String,

    // navigation necessities
    currentScreenName: String,
    navigateToHabit: () -> Unit,
    navigateToPrinciple: () -> Unit,
    navigateToIssue: () -> Unit,
    navigateToYou: () -> Unit,

    backgroundAccessorIndex: Int,
    backgroundPatternList: List<Int>,

    principle: Principle,
) {

    val patternPainter = painterResource(id = backgroundPatternList[backgroundAccessorIndex])
    val color = MaterialTheme.colorScheme.tertiary
    val modifierForBars = Modifier
        .fillMaxSize()
        .padding( horizontal = dimensionResource(id = R.dimen.padding_small) )
        .padding( bottom= dimensionResource(id = R.dimen.padding_small) )
        .background(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.small
        )
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
                                    alpha = 0.15f,
                                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                                        color
                                    )
                                )
                            }
                        }
                    }
                }
            }
        } //this changed the little bits alpha to be far lower for easier entry

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
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium)),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                .padding(vertical = dimensionResource(id = R.dimen.padding_large))

        ) {
            PrincipleEditBar(
                principle = principle,
                modifier = modifierForBars,
                onSave = onSave,
                onValueEdit = onValueEdit,
                onDelete = onDelete,
                hasThereBeenChanges = hasThereBeenChanges
            )
        }
    }
}

@Composable

fun PrincipleEditBar(
    principle: Principle,

    onValueEdit: (Principle) -> Unit,
    onDelete: () -> Unit,
    onSave: () -> Unit,
    hasThereBeenChanges: Boolean,


    modifier: Modifier = Modifier,
){
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(true) }

    val buttonModifier = Modifier
        .fillMaxWidth()
        .padding(dimensionResource(id = R.dimen.padding_small))
        .padding(horizontal = dimensionResource(id = R.dimen.padding_small))

    val fieldModifier = Modifier
        .fillMaxWidth()
        .padding(dimensionResource(id=R.dimen.padding_large))

    Column(
        modifier = modifier
    ){
        // name
        OutlinedTextField(
            modifier = fieldModifier,
            label = { Text(text = stringResource(id = R.string.form_required_name), style = MaterialTheme.typography.displayMedium) },
            value = principle.name,
            onValueChange = { onValueEdit(principle.copy(name = it)) },
            singleLine = true
        )
        //onValueChange(habitValues.copy(name = it))
        // description

        OutlinedTextField(
            modifier = fieldModifier,
            label = { Text(text = stringResource(id = R.string.form_required_description), style = MaterialTheme.typography.displayMedium) },
            value = principle.description,
            onValueChange = { onValueEdit(principle.copy(description = it)) },
            singleLine = false,
            minLines = 4,
            maxLines = 4
        )


        //TODO: add an icon

        // first active origin (though I'm not sure how would we trigger it).
        if (principle.dateFirstActive != null) {
            Row(
                modifier = fieldModifier
            ){
                Text(
                    text = "Date First Active: ",
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.weight(1f)
                )
                Spacer(
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Text(principle.formatFirstActiveDateString())
            }
        }

        //  save row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id=R.dimen.padding_medium))
        ){
            // add button
            Button(
                onClick = onSave,
                modifier = buttonModifier,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                enabled = hasThereBeenChanges
            ){
                Text(
                    text = "Save Changes",
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Icon(
                    imageVector = Icons.Outlined.AddCircleOutline,
                    contentDescription = "Save Changes"
                )

            }
        }
        // end of row


        HorizontalDivider()

        // read only attributes
        // date Created

        Row(
            modifier = fieldModifier
        ){
            Text(
                text = "Date Created: ",
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.weight(1f)
            )
            Spacer(
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Text(principle.formatDateCreatedString())
        }

        Row(
            modifier = fieldModifier
        ){
            Text(
                text = "That was ${principle.formatDaysSinceDateCreatedString()}",
            )//TODO: find a font that isn't bolded
        }

        Spacer(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
        )

        // delete button (starts confirmation)
        Button(
            onClick = { deleteConfirmationRequired = false },
            modifier = buttonModifier,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ){
            Text(
                text = "Delete This",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Icon(
                imageVector = Icons.Outlined.DeleteOutline,
                contentDescription = "Delete This (needs confirmation)"
            )
        }


        if (!deleteConfirmationRequired) {
            // delete button (after confirmation)
            Button(
                onClick = onDelete,
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Text(
                    text = "Yes, delete this",
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = "Confirm deletion"
                )
            }
        }
    }
}


@Preview
@Composable
fun PrincipleEditBodyPreview(){

    val backgroundDrawables = listOf(
        R.drawable.baseline_circle_24,
        R.drawable.baseline_elderly_24,
        R.drawable.baseline_hexagon_24,
        R.drawable.baseline_electric_bolt_24
    )

    var backgroundAccessorIndex = Random.nextInt(backgroundDrawables.size)


    PreviewHabituaTheme {
        PrincipleEditBody(
            // app title bar stuff
            appTitle = stringResource(id = PrincipleEditDestination.title),

            backgroundPatternList = backgroundDrawables,
            backgroundAccessorIndex = backgroundAccessorIndex,
            principle = Principle(
                0,
                0,
                null,
                "replace me",
                "Oh, there needs to be an icon resource too",
            ),
            hasThereBeenChanges = false,

            //navigation
            currentScreenName = "",
            navigateToHabit = {},
            navigateToPrinciple = {},
            navigateToIssue = {},
            navigateToYou = {},
            onValueEdit = {},
            onDelete = {},
            onSave = {},
        )
    }
}