package com.example.habitua.ui.issues

import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Desk
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitua.R
import com.example.habitua.ui.AppNavBar
import com.example.habitua.ui.AppTitleBar
import com.example.habitua.ui.AppViewModelProvider
import com.example.habitua.ui.navigation.IssueDestination
import com.example.habitua.ui.theme.PreviewHabituaTheme
import com.example.habitua.ui.theme.toothpasteShape
import kotlin.random.Random


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
fun IssueScreen(
    viewModel: IssueViewModel = viewModel(factory = AppViewModelProvider.Factory),
    currentScreenName: String,
    navigateToHabit: () -> Unit,
    navigateToPrinciple: () -> Unit,
    navigateToIssue: () -> Unit,
    navigateToYou: () -> Unit,
) {

    val backgroundDrawables = listOf(
        R.drawable.baseline_circle_24,
        R.drawable.baseline_elderly_24,
        R.drawable.baseline_hexagon_24,
        R.drawable.baseline_electric_bolt_24
    )

    var backgroundAccessorIndex = Random.nextInt(backgroundDrawables.size)


    IssueBody(
        // app title bar stuff
        appTitle = stringResource(id = IssueDestination.title),

        //background drawables
        backgroundPatternList = backgroundDrawables,
        backgroundAccessorIndex = backgroundAccessorIndex,

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
fun IssueBody (
    // app title bar stuff
    appTitle: String,

    // background drawables for the list bar
    backgroundPatternList: List<Int>,
    backgroundAccessorIndex: Int,

    // filter bar stuff
    //TODO: parameterize a lot of variables
    //TODO: parameterize the filter dropdownable
    //TODO: Introduce a new parameter for a "counter" for, e.g., issues' points
    // List Bar stuff

    // LIST BAR STUFF


    // ACTION BAR stuff
    firstActionButtonName: String = "", // if no given name, do not generate a second button
    firstActionButtonIcon: Int = R.drawable.tal_derpy,
    firstActionButtonIconContentDescription: String = "",
    firstActionButtonLambda: () -> Unit = {},
    isFirstButtonEnabled: Boolean = true,

    secondActionButtonName: String = "", // if no given name, do not generate a second button
    secondActionButtonIcon: Int = R.drawable.tal_derpy,
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
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
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

            IssueFilterBar(
                modifier = modifierForBars
            )
            IssueListBar(
                // background drawables
                backgroundPatternList = backgroundPatternList,
                backgroundAccessorIndex = backgroundAccessorIndex,

                modifier = modifierForBars.weight(1f)
            )
            IssueActionBar(
                modifier = modifierForBars
            )
        }
    }
}
@Composable
fun IssueFilterBar(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(horizontal = dimensionResource(id = R.dimen.padding_xmedium))
            .padding(vertical = dimensionResource(id = R.dimen.padding_small))
    ){
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

        //TODO: max text = 11 characters + 2 integer

        //TODO: change the "dropdown" label to a weight(1f)
        // I want the date part to take up as much space as it needs to
        // the label can take the rest

        //TODO: variablize the icon buttons (x2) and date name and label name
        // and also the dropdowns

        //TODO: The buttons need to have a smaller size

        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            IconButton(
                enabled = true,
                onClick = {},
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardDoubleArrowLeft,
                    contentDescription = "Yesterday"
                )
            }
            Text(
                textAlign = TextAlign.Center,
                text = "Saturday, 8 Dec",
                style = MaterialTheme.typography.displayMedium
            )
            IconButton(
                enabled = true,
                onClick = {},
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardDoubleArrowRight,
                    contentDescription = "Tomorrow"
                )
            }
        }
    }
}
@Composable
fun IssueListBar(
    // background drawables
    backgroundPatternList: List<Int>,
    backgroundAccessorIndex: Int,

    modifier: Modifier = Modifier
){

    val patternPainter = painterResource(id = backgroundPatternList[backgroundAccessorIndex])
    val color = MaterialTheme.colorScheme.tertiary

    val listBarColumnModifier = modifier
    .padding(vertical = dimensionResource(id = R.dimen.padding_large))
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
                                    alpha = 0.35f,
                                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(color)
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
        LazyVerticalGrid(
            columns = GridCells.Adaptive(400.dp),
        ) {
            //TODO: Parameterize the listicle contents.
            //TODO: dimension resource the maxmimum card width

            items(2){ _ ->
                HabitCard()
            }
            items(2){ _ ->
                PrincipleCard()
            }
        }
    }
}
@Composable
fun IssueActionBar( modifier: Modifier = Modifier ){
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
    ){
        ActionBarButton(
            buttonGeneralText = stringResource(R.string.action_bar_create_button_general),
            iconContentDescription = stringResource(R.string.action_bar_create_issue_content_description),
            icon = Icons.Default.AddCircleOutline,
            actionBarButtonOnClick = {}
        )
        ActionBarButton(
            buttonGeneralText = stringResource(R.string.action_bar_review_button_general),
            iconContentDescription = stringResource(R.string.action_bar_review_button_content_description),
            icon = Icons.Default.Desk,
            actionBarButtonOnClick = {}
        )
    }
}

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

    // icon stuff
    cardIconResource: Int,
    cardIconContentDescription: String,
    cardIconLambda: () -> Unit,

    // card options
    enableIcon: Boolean = true,
    enableDescription: Boolean = false,
) {

    val containerColor = MaterialTheme.colorScheme.secondaryContainer
    val contentColor = MaterialTheme.colorScheme.onSecondaryContainer

    val cardElevation = CardDefaults.cardElevation(
        defaultElevation = 4.dp,
        pressedElevation = 2.dp
    )
    val cardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    )
    val cardModifier = Modifier
        .padding(horizontal = dimensionResource(id = R.dimen.padding_small))
        .padding(vertical = dimensionResource(id = R.dimen.padding_medium))
        .innerShadow(
            shape = toothpasteShape,
            color = MaterialTheme.colorScheme.surfaceTint.copy(0.65f),
            blur = 1.dp,
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
                modifier =  Modifier.padding(dimensionResource(id = R.dimen.padding_small)).weight(1f))

            // More options
            CardMoreOptionsIcon(cardIconLambda)
        }
    }
}

@Composable
fun CardCenterDescriptors(
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
private fun CardMoreOptionsIcon(cardIconLambda: () -> Unit) {
    //TODO: parameterize the lambda the icon button
    // more options
    IconButton(onClick = cardIconLambda) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = stringResource(R.string.expand_button_content_description)
        )
    }
}

@Composable
fun HabitCard() {
    TrackingCard(
        enableDescription = true,
        cardTitle = "Toothpaste",
        cardDescription = "yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes",
        cardIconResource = R.drawable.tal_derpy,
        cardIconContentDescription = "",
        cardIconLambda = {}
    )
}
@Composable
fun PrincipleCard() {
    TrackingCard(
        enableDescription = false,
        cardTitle = "Toothpaste",
        cardDescription = "yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes yes",
        cardIconResource = R.drawable.tal_derpy,
        cardIconContentDescription = "",
        cardIconLambda = {}
    )
}
@Composable
fun CardIcon(
    drawableRes: Int,
    contentDescription: String,
    onIconClick: () -> Unit,
    contentColor: Color,
    modifier: Modifier = Modifier
){

    // parameterize the shape of the card icon
    // parameterize the color of the card icon

    // collect the shape

    Image(
        painter = painterResource(id =  drawableRes),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .size(dimensionResource(R.dimen.image_size))
            .clip(toothpasteShape)
            .innerShadow(
                shape = toothpasteShape,
                color = contentColor.copy(0.8f),
                offsetY = (2).dp,
                offsetX = (2).dp,
            )
            .innerShadow(
                shape = toothpasteShape,
                color = contentColor.copy(0.8f),
                offsetY = (-1).dp,
                offsetX = (-1).dp,
                spread = (-1).dp
            )
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

    actionBarButtonOnClick: () -> Unit
) {
    Button(
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



@Preview
@Composable
fun idd(){

    val backgroundDrawables = listOf(
        R.drawable.baseline_circle_24,
        R.drawable.baseline_elderly_24,
        R.drawable.baseline_hexagon_24,
        R.drawable.baseline_electric_bolt_24
    )

    var backgroundAccessorIndex = Random.nextInt(backgroundDrawables.size)

    PreviewHabituaTheme {
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
    }
}