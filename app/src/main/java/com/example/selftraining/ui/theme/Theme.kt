package com.example.selftraining.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkGreen = Color(0xFF1B5E20)
private val MediumGreen = Color(0xFF2E7D32)
private val LightGreen = Color(0xFF4CAF50)
private val AccentGreen = Color(0xFF81C784)
private val BackgroundDark = Color(0xFF121212)
private val SurfaceDark = Color(0xFF1E1E1E)
private val OnPrimary = Color(0xFFFFFFFF)
private val OnBackground = Color(0xFFE0E0E0)

private val WorkoutColorScheme = darkColorScheme(
    primary = LightGreen,
    onPrimary = OnPrimary,
    primaryContainer = MediumGreen,
    onPrimaryContainer = OnPrimary,
    secondary = AccentGreen,
    onSecondary = BackgroundDark,
    background = BackgroundDark,
    onBackground = OnBackground,
    surface = SurfaceDark,
    onSurface = OnBackground,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFBDBDBD)
)

@Composable
fun SelfTrainingTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WorkoutColorScheme,
        content = content
    )
}
