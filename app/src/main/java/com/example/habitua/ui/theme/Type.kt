package com.example.habitua.ui.theme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.habitua.R

val UbuntuMono = FontFamily(
    Font(R.font.ubuntumono_regular),
    Font(R.font.ubuntumono_bold, FontWeight.Bold)
)

val SingleDay = FontFamily(
    Font(R.font.singleday_regular)
)

// Default Material 3 typography values
val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = SingleDay,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp
    ),
    displayMedium = TextStyle(
        fontFamily = UbuntuMono,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    displaySmall = TextStyle(
        fontFamily = SingleDay,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = UbuntuMono,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = UbuntuMono,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    bodySmall = TextStyle(
        fontFamily = UbuntuMono,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
)
)


