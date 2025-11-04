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

private val HuertoHogarLightColorScheme = lightColorScheme(
    primary = VerdeEsmeralda,
    onPrimary = BlancoSuave,
    secondary = AmarilloMostaza,
    onSecondary = GrisOscuro,
    tertiary = MarronClaro,
    onTertiary = BlancoSuave,
    background = BlancoSuave,
    onBackground = GrisOscuro,
    surface = BlancoSuave,
    onSurface = GrisOscuro,
    onSurfaceVariant = GrisMedio
)

private val HuertoHogarDarkColorScheme = darkColorScheme(
    primary = VerdeEsmeralda,
    onPrimary = BlancoSuave,
    secondary = AmarilloMostaza,
    onSecondary = GrisOscuro,
    tertiary = MarronClaro,
    onTertiary = BlancoSuave,
    background = Color(0xFF1C1C1E),
    onBackground = BlancoSuave,
    surface = Color(0xFF2C2C2E),
    onSurface = BlancoSuave,
    onSurfaceVariant = Color(0xFFAAAAAA)
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


            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,

        typography = AppTypography,
        content = content
    )
}