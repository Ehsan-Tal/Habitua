package com.example.habitua.ui.visual

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.habitua.R
import com.example.habitua.data.Habit
import com.example.habitua.ui.AppViewModelProvider
import com.example.habitua.ui.HabitNavBar
import com.example.habitua.ui.home.HomeViewModel
import com.example.habitua.ui.navigation.NavigationDestination
import com.example.habitua.ui.theme.HabituaTheme
import com.example.habitua.ui.theme.outlineDark
import kotlin.math.cos
import kotlin.math.sin

object VisualizationDestination: NavigationDestination {
    override val route = "visualization"
    override val title = "Data"
}
@Composable
fun VisualizationScreen (
    currentScreenName: String,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: VisualizationViewModel = viewModel(factory = AppViewModelProvider.Factory),
){
    val habitVizUiState by viewModel.habitVizUiState.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        // floater button
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row (
                modifier = Modifier,
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
                    .padding(innerPadding)
                    .fillMaxSize()
            ){
            VisualizationColumn(
                viewModel = viewModel,
                habitVizUiState = habitVizUiState
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
fun VisualizationColumn(
    viewModel: VisualizationViewModel,
    habitVizUiState: HabitVizUiState
){
    Column(
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.padding_small))
            .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        PieChart(
            data = viewModel.preparePieDataSetForStreakComparisons(),
            colors = listOf(Color.Magenta, Color.LightGray),
            title = stringResource(id = R.string.visualization_pie_chart_title_Streak_Comparison),
            portion = habitVizUiState.portionInStreaks,
            totalSize = habitVizUiState.totalSize,
        )
        PieChart(
            data = viewModel.preparePieDataSetForAcquiredHabits(),
            colors = listOf(Color.Yellow, Color.LightGray),
            title = stringResource(id = R.string.visualization_pie_chart_title_Acquired_Habits),
            portion = habitVizUiState.portionAcquired,
            totalSize = habitVizUiState.totalSize,
        )

        HeaderCount(
            header = stringResource(id = R.string.visualization_header_InStreaks),
            count = habitVizUiState.portionInStreaks.toString()
        )

        HeaderCount(
            header = stringResource(id = R.string.visualization_header_Acquired),
            count = habitVizUiState.portionAcquired.toString()
        )
        HeaderCount(
            header = stringResource(id = R.string.visualization_header_Total),
            count = habitVizUiState.totalSize.toString()
        )
    }
}

@Composable
fun HeaderCount(
    header: String,
    count: String
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_medium))
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_small))

        ) {
            Text(
                text = header,
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                text = count,
                style = MaterialTheme.typography.bodyMedium
            )
        }

    }
}


@Composable
fun PieChart(
    data: Map<Int, Float>,
    colors: List<Color>,
    title: String,
    portion: Int,
    totalSize: Int,
    modifier: Modifier = Modifier
) {
    // this should work to produce a pie chart
    // each iteration updates the start angle
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
        )
        Canvas(
            modifier = Modifier
                .size(100.dp)
        ) {
            var startAngle = 0f
            data.values.forEachIndexed {
                   index, value ->
                       val sweepAngle = value / data.values.sum() * 360f
                       drawArc(
                           color = colors[index % colors.size],
                           startAngle = startAngle,
                           sweepAngle = sweepAngle,
                           useCenter = true,
                           style = Fill
                       )
                       drawArc(
                           color = Color.Black,
                           startAngle = startAngle,
                           sweepAngle = sweepAngle,
                           useCenter = true,
                           style = Stroke(width = 2f),
                       )
                       startAngle += sweepAngle
                   }
            }

        // outlined box
        // color + label

        Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))) {
            data.keys.forEachIndexed { index, label ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(colors[index % colors.size])
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = label),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVisualization(){
    HabituaTheme {
        Column {


        PieChart(
            data = mapOf(
                R.string.visualization_pie_chart_Acquired_on to
                        40f,
                R.string.visualization_pie_chart_Acquired_off to
                        60f,
            ),
            colors = listOf(
                Color.Yellow,
                Color.LightGray
            ),
            title = "Preview",
            portion = 40,
            totalSize = 100,
        )
        HeaderCount(header = "Preview", count = "40")
        }
    }
}