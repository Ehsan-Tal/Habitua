package com.example.habitua.ui.principles

import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.PanoramaVertical
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitua.R
import com.example.habitua.data.PrincipleDetails
import com.example.habitua.ui.AppNavBar
import com.example.habitua.ui.AppTitleBar
import com.example.habitua.ui.AppViewModelProvider
import com.example.habitua.ui.navigation.PrincipleDestination
import com.example.habitua.ui.theme.toothpasteShape
import kotlinx.coroutines.launch


//TODO - what items should be module
fun Modifier.innerShadow(
    shape: Shape,
    color: Color = Color.Black,
    blur: Dp = 4.dp,
    offsetY: Dp = 2.dp,
    offsetX: Dp = 2.dp,
    spread: Dp = 0.dp
) = this.drawWithContent {
    drawContent()
    drawIntoCanvas { canvas ->
        val shadowSize = Size(size.width + spread.toPx(), size.height + spread.toPx())
        val shadowOutline = shape.createOutline(shadowSize, layoutDirection, this)

        val paint = Paint()
        paint.color = color

        // save the current layer of the canvas
        canvas.saveLayer(size.toRect(), paint)

        // Drawing the shadow outline of the canvas
        canvas.drawOutline(shadowOutline, paint)

        // Configure the paint to act as an eraser to clip the shadow
        paint.asFrameworkPaint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            if (blur.toPx() > 0) {
                maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
            }
        }

        // set clipping color
        paint.color = Color.Black

        // Translate the canvas to the offset position
        canvas.translate(offsetX.toPx(), offsetY.toPx())

        // Draw the outline again to clip the shadow
        canvas.drawOutline(shadowOutline, paint)

        // Restore the canvas
        canvas.restore()
    }
}





@Composable
fun PrincipleScreen(
    viewModel: PrincipleViewModel = viewModel(factory = AppViewModelProvider.Factory),

    currentScreenName: String,
    navigateToHabit: () -> Unit,
    navigateToPrinciple: () -> Unit,
    navigateToIssue: () -> Unit,
    navigateToYou: () -> Unit,
) {
    val principleUiState by viewModel.principleUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    PrincipleBody(
        // app title bar stuff
        appTitle = stringResource(id = PrincipleDestination.title),


        // FILTER BAR
        serialCategoryString = principleUiState.dateBaseString,
        serialBackwardButtonLambda = { viewModel.updateToYesterday() },
        serialForwardButtonLambda = { viewModel.updateToTomorrow() },
        serialBackwardButtonContentDescription = stringResource(R.string.serial_backward_button_content_description),
        serialForwardButtonContentDescription = stringResource(R.string.serial_forward_button_content_description_for_dates),
        isSerialBackwardButtonEnabled = principleUiState.isSerialBackwardButtonEnabled,
        isSerialForwardButtonEnabled = principleUiState.isSerialForwardButtonEnabled,
        //TODO: change the serial button booleans to not need a !
        //TODO: add filter parameters
        //TODO: add string resources
        //TODO: add a serial category swipe function

        isFilterDropdownEnabled = false,


        // LIST BAR
        // principles
        backgroundPatternList = viewModel.backgroundDrawables,
        backgroundAccessorIndex = viewModel.backgroundAccessorIndex,

        listOfCards = principleUiState.principleListToday,
        isListOfCardsEmpty = false,
        getCanCardClickBoolean = principleUiState.canCardClickBoolean,

        // background patterns
        onClickPrinciple = { date: Long, id: Int ->
            coroutineScope.launch {
                viewModel.togglePrinciple(id, date)
            }
        },
        onHoldPrinciple = { principleDetails: PrincipleDetails ->
            Log.d("t", "$principleDetails held")
            //TODO: change sorting order
        },

        emptyCardTitle = "No principles to show",
        emptyCardDescription = "how can I get this from the view Model",

        // Action Bar items
        firstActionButtonName = stringResource(id = R.string.action_bar_create_button_general),
        firstActionButtonIcon = Icons.Outlined.AddCircleOutline,
        firstActionButtonIconContentDescription = stringResource(id = R.string.action_bar_create_issue_content_description),
        firstActionButtonLambda = { viewModel.addPrinciple() },
        isFirstButtonEnabled = principleUiState.isFirstActionButtonEnabled,

        secondActionButtonName = stringResource(id = R.string.action_bar_today_button),
        secondActionButtonIcon = Icons.Outlined.Today,
        secondActionButtonIconContentDescription = stringResource(id = R.string.action_bar_today_button_content_description),
        secondActionButtonLambda = { viewModel.updateToToday() },
        isSecondButtonEnabled = principleUiState.isSecondActionButtonEnabled,

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
fun PrincipleBody (
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

    // principles
    listOfCards: List<PrincipleDetails>,
    isListOfCardsEmpty: Boolean,
    getCanCardClickBoolean: Boolean,

    onClickPrinciple: (Long, Int) -> Unit,
    onHoldPrinciple: (PrincipleDetails) -> Unit,

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
) {

    val modifierForBars = Modifier
        .fillMaxWidth()
        .padding(
            horizontal = dimensionResource(id = R.dimen.padding_large),
            vertical = dimensionResource(id = R.dimen.padding_small)
        )
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
                .padding(vertical = dimensionResource(id = R.dimen.padding_medium))
        ){

            PrincipleFilterBar(
                modifier = modifierForBars,

                serialCategoryString = serialCategoryString,
                serialBackwardButtonLambda = serialBackwardButtonLambda,
                serialForwardButtonLambda = serialForwardButtonLambda,
                serialBackwardButtonContentDescription = serialBackwardButtonContentDescription,
                serialForwardButtonContentDescription = serialForwardButtonContentDescription,
                isSerialForwardButtonEnabled = isSerialForwardButtonEnabled,
                isSerialBackwardButtonEnabled = isSerialBackwardButtonEnabled,

                isFilterDropdownEnabled = isFilterDropdownEnabled,
            )

            PrincipleListBar(
                // modifiers
                modifier = modifierForBars.weight(1f),

                // background drawables
                backgroundPatternList = backgroundPatternList,
                backgroundAccessorIndex = backgroundAccessorIndex,

                // principles
                listOfCards = listOfCards,
                getCanCardClickBoolean = getCanCardClickBoolean,
                isListOfCardsEmpty = isListOfCardsEmpty,

                onClickPrinciple = onClickPrinciple,
                onHoldPrinciple = onHoldPrinciple,

                emptyCardTitle = emptyCardTitle,
                emptyCardDescription = emptyCardDescription
            )

            PrincipleActionBar(
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
fun PrincipleFilterBar(
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
fun PrincipleListBar(
    // background drawables
    backgroundPatternList: List<Int>,
    backgroundAccessorIndex: Int,

    //TODO: remember, we need to generic these functions as much as possible
    // we do this so that it's not as nasty to change
    // because we are not smart enough to get it right the first time :)

    // Principles
    listOfCards: List<PrincipleDetails>,
    getCanCardClickBoolean: Boolean,
    isListOfCardsEmpty: Boolean,

    onClickPrinciple: (Long, Int) -> Unit,
    onHoldPrinciple: (PrincipleDetails) -> Unit,

    modifier: Modifier = Modifier,

    emptyCardTitle: String,
    emptyCardDescription: String,
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
        if (isListOfCardsEmpty) {
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
                items(listOfCards, key = {principle -> principle.principleId} ) { principle ->
                    // it doubles...
                    // this is empty, why is this empty.

                    Row( modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)) ) {
                        //TODO: parameterize these where possible !
                        // much of these cannot be ascertained since they are generated with
                        // principle, an object of items

                        //though, this is not the case with other items.

                        //TODO: we could just use Tracking card instead an entirely new card
                        PrincipleCard(
                            principle = principle,

                            cardIconLambda = { /*TODO*/ },

                            onClickPrinciple = {onClickPrinciple(principle.date, principle.principleId)}, // active/inactive
                            getCanCardClickBoolean = getCanCardClickBoolean, // disable active/inactive

                            onMoreOptionsClick = {  }, // more options
//                            onMoreOptionsClick = { onEditMenuExpand(principle) }, // more options
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun PrincipleCard(
    principle: PrincipleDetails,

    onMoreOptionsClick: () -> Unit,

    onClickPrinciple: () -> Unit,
    getCanCardClickBoolean: Boolean,

    cardIconLambda: () -> Unit,
) {
    TrackingCard(
        // card icons
        cardIconResource = R.drawable.tal_derpy, // this should come here, the principle
        cardIconContentDescription = "", // this should come here, the principle
        cardIconLambda = cardIconLambda, // this should travel from the body
        //TODO: get a table for the card icons.

        // card stuff
        cardTitle = principle.name,
        cardDescription = principle.description,
        cardValue = principle.value,
        onCardClick = onClickPrinciple,
        getCanCardClickBoolean = getCanCardClickBoolean,

        // more options
        onMoreOptionsClick = onMoreOptionsClick,

        //  option booleans
        enableDescription = false,
        enableMoreOptions = true,
    )
}



@Composable
fun PrincipleActionBar(
    modifier: Modifier = Modifier,
    firstActionButtonName: String,
    firstActionButtonIcon: ImageVector,
    firstActionButtonIconContentDescription: String,
    firstActionButtonLambda: () -> Unit,
    isFirstButtonEnabled: Boolean,

    secondActionButtonName: String, // if no given name, do not generate a second button
    secondActionButtonIcon: ImageVector,
    secondActionButtonIconContentDescription: String,
    secondActionButtonLambda: () -> Unit,
    isSecondButtonEnabled: Boolean,
    ){

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
    ){
        if (firstActionButtonName != "") {
            ActionBarButton(
                buttonGeneralText = firstActionButtonName,
                iconContentDescription = firstActionButtonIconContentDescription,
                icon = firstActionButtonIcon,
                actionBarButtonOnClick = firstActionButtonLambda,
                isActionBarButtonEnabled = isFirstButtonEnabled
            )
        }

        if (secondActionButtonName != "") {
            ActionBarButton(
                buttonGeneralText = secondActionButtonName,
                iconContentDescription = secondActionButtonIconContentDescription,
                icon = secondActionButtonIcon,
                actionBarButtonOnClick = secondActionButtonLambda,
                isActionBarButtonEnabled = isSecondButtonEnabled
            )
        }
    }
}


//TODO Move TrackingCard() to its own App_Bar thing ?
/**
 * we've separated a lot of stuff into component parts to reduce the visual quagmire
 * that prevents modification by being so nasty.
 *
 * Now we can hopefully create custom items for other tracking cards easier
 *
 * For example, I want there to be a checkbox for issue cards, but that shouldn't
 * be present for other items
 *
 * I could use an "enableX" option provide for that - modifying the CenterDescriptors
 * or adding a whole additional row for the checkbox that acts as a lazy list
 * or even a lazy vertical grid
 */
@Composable
fun TrackingCard(
    // card descriptor stuff
    cardTitle: String,
    cardDescription: String,
    cardValue: Boolean,
    onCardClick: () -> Unit,
    getCanCardClickBoolean: Boolean,

    // icon stuff
    cardIconResource: Int,
    cardIconContentDescription: String,
    cardIconLambda: () -> Unit,

    // more options stuff
    onMoreOptionsClick: () -> Unit,

    // card options
    enableIcon: Boolean = true,
    enableDescription: Boolean = false,
    enableMoreOptions: Boolean = true,
) {
    var localCardValue by remember { mutableStateOf(cardValue) }

    val contentColor by animateColorAsState(
        targetValue = if (localCardValue){ MaterialTheme.colorScheme.onPrimaryContainer }
        else MaterialTheme.colorScheme.onSecondaryContainer,
        label = "Foreground color change"
    )
    val containerColor by animateColorAsState(
        targetValue = if (localCardValue){ MaterialTheme.colorScheme.primaryContainer }
        else MaterialTheme.colorScheme.secondaryContainer,
        label = "Background color change"
    )
    val cardElevation = CardDefaults.cardElevation(
        defaultElevation = 4.dp, pressedElevation = 2.dp
    )
    val cardColors = CardDefaults.cardColors(
        containerColor = containerColor, contentColor = contentColor
    )
    val cardModifier = Modifier
        .padding(horizontal = dimensionResource(id = R.dimen.padding_small))
        .padding(vertical = dimensionResource(id = R.dimen.padding_medium))
        .clickable(enabled = getCanCardClickBoolean, onClick = onCardClick)
        .clickable(enabled = getCanCardClickBoolean, onClick = {localCardValue = !localCardValue})
        .innerShadow(
            shape = toothpasteShape,
            color = MaterialTheme.colorScheme.surfaceTint.copy(0.65f),
            blur = 1.dp,
        )
        .border(
            if (cardValue) 3.dp else 1.dp,
            MaterialTheme.colorScheme.outlineVariant,
            toothpasteShape
        )
        .alpha(
            if (getCanCardClickBoolean) 1f else 0.5f
        )


    OutlinedCard(
        modifier = cardModifier,
        elevation = cardElevation,
        colors = cardColors,
        shape = toothpasteShape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row ( verticalAlignment = Alignment.CenterVertically ){

            // Iconic
            if (enableIcon) {
                CardIcon(
                    drawableRes = cardIconResource,
                    contentDescription = cardIconContentDescription,
                    onIconClick = cardIconLambda,
                    contentColor = contentColor
                )
            }

            // Body names
            CardCenterDescriptors(
                cardTitle = cardTitle,
                enableDescription = enableDescription,
                cardDescription = cardDescription,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .weight(1f)
            )

            // More options
            if (enableMoreOptions){
                CardMoreOptionsIcon(onMoreOptionsClick)
            }
        }
    }
}
@Composable
private fun CardCenterDescriptors(
    cardTitle: String,
    enableDescription: Boolean,
    cardDescription: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = cardTitle,
            style = MaterialTheme.typography.labelLarge,
            softWrap = false,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        if (enableDescription) {
            Text(
                text = cardDescription,
                style = MaterialTheme.typography.bodyMedium,
                softWrap = true,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}
@Composable
private fun CardMoreOptionsIcon(onMoreOptionsClick: () -> Unit) {
    IconButton(onClick = onMoreOptionsClick) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = stringResource(R.string.expand_button_content_description)
        )
    }
}
@Composable
fun CardIcon(
    drawableRes: Int,
    contentDescription: String,
    onIconClick: () -> Unit,
    contentColor: Color,
    modifier: Modifier = Modifier
){

    Image(
        painter = painterResource(id =  drawableRes),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .size(dimensionResource(R.dimen.image_size))
            .clip(toothpasteShape)
            .border(
                BorderStroke(
                    width = 1.dp,
                    color = contentColor,
                ),
                shape = toothpasteShape
            )
            .clickable { onIconClick() },
    )
}


@Composable
fun ActionBarButton(
    buttonGeneralText: String,

    icon: ImageVector,
    iconContentDescription: String,

    actionBarButtonOnClick: () -> Unit,
    isActionBarButtonEnabled: Boolean
) {
    Button(
        enabled = isActionBarButtonEnabled,
        onClick = actionBarButtonOnClick,
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.padding_small)),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary,
        ),
        shape = MaterialTheme.shapes.small,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = dimensionResource(id = R.dimen.elevation_small),
            pressedElevation = dimensionResource(id = R.dimen.elevation_medium)
        )
    ) {
        Text(
            text = buttonGeneralText,
            style = MaterialTheme.typography.labelLarge,
        )
        Spacer(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
        )
        Icon(
            imageVector = icon,
            contentDescription = iconContentDescription
        )
    }
}



