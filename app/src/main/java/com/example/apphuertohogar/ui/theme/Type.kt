package com.example.apphuertohogar.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.apphuertohogar.R
val playfairDisplayFontFamily = FontFamily(
    Font(R.font.playfairdisplay_regular, FontWeight.Normal),
    Font(R.font.playfairdisplay_bold, FontWeight.Bold)
)

val montserratFontFamily = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold)
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = playfairDisplayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp
    ),
    displayMedium = TextStyle(
        fontFamily = playfairDisplayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp
    ),
    displaySmall = TextStyle(
        fontFamily = playfairDisplayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp
    ),

    titleLarge = TextStyle(
        fontFamily = playfairDisplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = playfairDisplayFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),

    bodyLarge = TextStyle(
        fontFamily = montserratFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = montserratFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = montserratFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),

    labelLarge = TextStyle(
        fontFamily = montserratFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    )
)