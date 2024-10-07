package com.example.habitua.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun AppTitleBar(
    title: String,
){
    Text(
        text = title,
        style = MaterialTheme.typography.displayLarge,
    )
}