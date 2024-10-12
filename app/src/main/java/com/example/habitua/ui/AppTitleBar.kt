package com.example.habitua.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.habitua.R
import com.example.habitua.ui.theme.PreviewHabituaTheme

@Composable
fun AppTitleBar( title: String ){
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = title,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        HorizontalDivider(
            thickness = dimensionResource(id = R.dimen.divider_thick_medium),
            color = MaterialTheme.colorScheme.outline
        )
    }
}


@Preview
@Composable
fun PreviewAppTitleBar(){
    PreviewHabituaTheme {
        AppTitleBar(title = "Habitua")
    }
}
