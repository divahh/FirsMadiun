package com.example.firsmadiun.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DamkarColorScheme = lightColorScheme(
    primary = DamkarBlue,
    onPrimary = White,
    primaryContainer = DamkarBlueLight,
    onPrimaryContainer = White,
    secondary = DamkarAccent,
    onSecondary = White,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    outline = InputBorder,
    error = EmergencyRed
)

@Composable
fun DamkarTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DamkarColorScheme,
        typography = Typography,
        content = content
    )
}