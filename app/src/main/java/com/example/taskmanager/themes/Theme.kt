package com.example.taskmanager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Palette ──────────────────────────────────────────────────────────────────
val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)
val DarkGray = Color(0xFF1A1A1A)
val MidGray = Color(0xFF555555)
val LightGray = Color(0xFFE0E0E0)
val OffWhite = Color(0xFFF5F5F5)

// ── Light scheme: white bg, black fg ─────────────────────────────────────────
private val LightColors = lightColorScheme(
    primary = Black,
    onPrimary = White,
    primaryContainer = DarkGray,
    onPrimaryContainer = White,

    secondary = MidGray,
    onSecondary = White,
    secondaryContainer = LightGray,
    onSecondaryContainer = Black,

    tertiary = MidGray,
    onTertiary = White,

    background = White,
    onBackground = Black,

    surface = White,
    onSurface = Black,
    surfaceVariant = OffWhite,
    onSurfaceVariant = DarkGray,

    outline = MidGray,

    error = Color(0xFFB00020),
    onError = White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

// ── Dark scheme: black bg, white fg ──────────────────────────────────────────
private val DarkColors = darkColorScheme(
    primary = White,
    onPrimary = Black,
    primaryContainer = LightGray,
    onPrimaryContainer = Black,

    secondary = LightGray,
    onSecondary = Black,
    secondaryContainer = DarkGray,
    onSecondaryContainer = White,

    tertiary = LightGray,
    onTertiary = Black,

    background = Black,
    onBackground = White,

    surface = DarkGray,
    onSurface = White,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = LightGray,

    outline = MidGray,

    error = Color(0xFFCF6679),
    onError = Black,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
)

@Composable
fun TaskManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
