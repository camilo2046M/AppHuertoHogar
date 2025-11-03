package com.example.apphuertohogar.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.apphuertohogar.R // Importa el R de TU app

// 1. Define la fuente "Playfair Display" desde tus archivos locales
val playfairDisplayFontFamily = FontFamily(
    Font(R.font.playfairdisplay_regular, FontWeight.Normal),
    Font(R.font.playfairdisplay_bold, FontWeight.Bold)
)

// 2. Define la fuente "Montserrat" desde tus archivos locales
val montserratFontFamily = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold)
)

// 3. RENOMBRAMOS 'Typography' a 'AppTypography' y usamos claves M3
val AppTypography = Typography(
    // Encabezados (usarán Playfair Display)
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

    // Títulos (Playfair Display)
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

    // Texto de cuerpo (usará Montserrat)
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

    // Texto de botones, etc. (usará Montserrat)
    labelLarge = TextStyle(
        fontFamily = montserratFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    )
)