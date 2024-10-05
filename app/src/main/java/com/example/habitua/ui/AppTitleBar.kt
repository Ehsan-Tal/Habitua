package com.example.habitua.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun AppTitleBar(
    title: String,
){
    Text(
        text = title,
        style = MaterialTheme.typography.displayLarge,
    )
}