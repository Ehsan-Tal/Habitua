package com.example.habitua.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.dimensionResource
import com.example.habitua.R


@Composable
fun AppTitleBar(
    title: String,
){
//TODO: FIGMA THIS TO THE FIMGA

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = androidx.compose.ui.Modifier
            .padding(dimensionResource(id = R.dimen.padding_large))
    ){
        Text(
            text = title,
            style = MaterialTheme.typography.displayLarge,
        )

    }
}