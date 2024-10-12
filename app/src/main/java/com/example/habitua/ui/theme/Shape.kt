package com.example.habitua.ui.theme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

val toothpasteShape = RoundedCornerShape(
    topStart = 24.dp,
    topEnd = 0.dp,
    bottomStart = 0.dp,
    bottomEnd = 24.dp
)