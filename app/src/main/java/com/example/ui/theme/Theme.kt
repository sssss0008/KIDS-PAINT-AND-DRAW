package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = KidsOrange,
    onPrimary = Color.White,
    primaryContainer = KidsYellow,
    onPrimaryContainer = KidsDark,
    secondary = KidsBlue,
    onSecondary = Color.White,
    secondaryContainer = KidsBlueLight,
    onSecondaryContainer = KidsDark,
    tertiary = KidsPurple,
    onTertiary = Color.White,
    tertiaryContainer = KidsPurpleLight,
    onTertiaryContainer = KidsDark,
    background = KidsCream,
    onBackground = KidsDark,
    surface = Color.White,
    onSurface = KidsDark,
    surfaceVariant = KidsSoftYellow,
    onSurfaceVariant = KidsDark,
    outline = KidsCardBorder
)

private val DarkColorScheme = lightColorScheme(
    // Keep kids app bright, cheerful, and inviting even if system dark mode is active
    primary = KidsOrange,
    onPrimary = Color.White,
    primaryContainer = KidsYellow,
    onPrimaryContainer = KidsDark,
    secondary = KidsBlue,
    onSecondary = Color.White,
    secondaryContainer = KidsBlueLight,
    onSecondaryContainer = KidsDark,
    tertiary = KidsPurple,
    onTertiary = Color.White,
    background = KidsCream,
    onBackground = KidsDark,
    surface = Color.White,
    onSurface = KidsDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep kids colors consistent and vibrant
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
