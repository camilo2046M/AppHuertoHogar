package com.example.apphuertohogar.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 1. Define tu paleta de colores de marca para el modo claro
private val HuertoHogarLightColorScheme = lightColorScheme(
    primary = VerdeEsmeralda,           // Botones, TopAppBar, íconos activos
    onPrimary = BlancoSuave,            // Texto sobre botones primarios
    secondary = AmarilloMostaza,        // Botones de ofertas, badges
    onSecondary = GrisOscuro,           // Texto sobre botones secundarios
    tertiary = MarronClaro,             // Títulos, acentos
    onTertiary = BlancoSuave,           // Texto sobre acentos
    background = BlancoSuave,           // Fondo principal de la app
    onBackground = GrisOscuro,          // Texto principal sobre el fondo
    surface = BlancoSuave,              // Fondo de Cards, Menús
    onSurface = GrisOscuro,             // Texto sobre las Cards
    onSurfaceVariant = GrisMedio        // Texto secundario, descripciones
)

// (Opcional: puedes crear un HuertoHogarDarkColorScheme aquí si quieres)
private val HuertoHogarDarkColorScheme = darkColorScheme(
    primary = VerdeEsmeralda,
    onPrimary = BlancoSuave,
    secondary = AmarilloMostaza,
    onSecondary = GrisOscuro,
    tertiary = MarronClaro,
    onTertiary = BlancoSuave,
    background = Color(0xFF1C1C1E),     // Un fondo oscuro
    onBackground = BlancoSuave,
    surface = Color(0xFF2C2C2E),        // Cards oscuras
    onSurface = BlancoSuave,
    onSurfaceVariant = Color(0xFFAAAAAA) // Texto secundario gris claro
)


@Composable
fun AppHuertoHogarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> HuertoHogarDarkColorScheme
        else -> HuertoHogarLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()

            // --- CORRECCIÓN DEL BUG DE LA BARRA DE ESTADO ---
            // Le decimos que los íconos de la barra de estado sean oscuros
            // SI NO estamos en darkTheme.
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        // --- CORRECIÓN DE TIPOGRAFÍA ---
        // Usamos el val renombrado de Type.kt
        typography = AppTypography,
        content = content
    )
}