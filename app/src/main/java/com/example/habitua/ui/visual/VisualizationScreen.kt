package com.example.habitua.ui.visual

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.habitua.R
import com.example.habitua.ui.AppViewModelProvider
import com.example.habitua.ui.HabitNavBar
import com.example.habitua.ui.navigation.NavigationDestination
import com.example.habitua.ui.theme.HabituaTheme
import com.example.habitua.ui.theme.LocalCustomColorsPalette

object VisualizationDestination: NavigationDestination {
    override val route = "visualization"
    override val title = R.string.visualization_title
    val navTitle = R.string.visualization_nav_title
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row (
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_large)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.visualization_title),
                    style = MaterialTheme.typography.displayLarge,
                )
            }

            Column (
                modifier = Modifier
                    .weight(8f)
                    .padding(dimensionResource(id = R.dimen.padding_small))
            ){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RectangleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {

                    VisualizationData(
                        viewModel = viewModel,
                        habitVizUiState = habitVizUiState
                    )
                }

            }
            HabitNavBar(
                navController = navController,
                currentScreenName = currentScreenName
            )
        }

    }
}

@Composable
fun VisualizationData(
    viewModel: VisualizationViewModel,
    habitVizUiState: HabitVizUiState
){
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_small))
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ){
            PieChart(
                data = viewModel.preparePieDataSetForStreakComparisons(),
                colors = listOf(MaterialTheme.colorScheme.tertiary, Color.LightGray),
                title = stringResource(id = R.string.visualization_pie_chart_title_Streak_Comparison),
                modifier = Modifier
                    .weight(1f)
            )
            PieChart(
                data = viewModel.preparePieDataSetForAcquiredHabits(),
                colors = listOf(LocalCustomColorsPalette.current.acquired, Color.LightGray),
                title = stringResource(id = R.string.visualization_pie_chart_title_Acquired_Habits),
                modifier = Modifier
                    .weight(1f)
            )

        }
        DataTable(
            listOf(
                listOf(
                    stringResource(id = R.string.visualization_header_InStreaks),
                    habitVizUiState.portionInStreaks.toString(),
                ),
                listOf(
                    stringResource(id = R.string.visualization_header_Acquired),
                    habitVizUiState.portionAcquired.toString(),
                ),
                listOf(
                    stringResource(id = R.string.visualization_header_Total),
                    habitVizUiState.totalSize.toString()
                ),
            )
        )

        // Acquired Habits
        if (habitVizUiState.acquiredHabitList.isNotEmpty()) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.padding_large))
            ){
                Text(
                    text="Acquired Habits",
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                )
                LazyColumn {
                    items(items = viewModel.habitVizUiState.value.acquiredHabitList) {
                        OutlinedCard(
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = LocalCustomColorsPalette.current.acquired
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimensionResource(R.dimen.padding_small))
                                .padding(bottom = 0.dp)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.tertiary,
                                    MaterialTheme.shapes.medium
                                )
                        ){
                            Column(
                                modifier = Modifier
                                    .padding(dimensionResource(id = R.dimen.padding_medium))
                            ){
                                Text(
                                    text=it.name,
                                    style = MaterialTheme.typography.displaySmall,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text=it.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
                                )
                                Text(
                                    text="Practicing since: ${it.currentStreakOrigin!!}",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
                                )
                                /*
                                Text(
                                    text="Acquired at: ${it.currentAcquiredOrigin!!}",
                                    color = LocalCustomColorsPalette.current.onAcquired
                                )
                                 */


                            }
                                                  }
                    }
                }
            }
        }
    }
}


@Composable
fun DataTable(
    tableData: List<List<String>>
){
    LazyColumn {
        items(items = tableData) { item ->
            Column (
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_medium))
                    .border(1.dp, MaterialTheme.colorScheme.outline)

            ) {
                Row {
                    item.forEach { cell ->
                        Text(
                            text = cell,
                            modifier = Modifier
                                .padding(dimensionResource(id = R.dimen.padding_medium))
                                .weight(1f),
                            textAlign = TextAlign.Left
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun PieChart(
    data: Map<Int, Float>,
    colors: List<Color>,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
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
            )
        }
    }
}