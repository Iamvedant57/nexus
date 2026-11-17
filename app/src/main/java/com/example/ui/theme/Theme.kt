package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

private val NexusDarkColorScheme = darkColorScheme(
    primary = NexusVioletLight,
    onPrimary = Color(0xFF1F094E),
    primaryContainer = Color(0xFF4C1D95),
    onPrimaryContainer = Color(0xFFEDE9FE),
    secondary = NexusPinkLight,
    onSecondary = Color(0xFF4D0628),
    secondaryContainer = Color(0xFF831843),
    onSecondaryContainer = Color(0xFFFCE7F3),
    tertiary = NexusOrangeLight,
    onTertiary = Color(0xFF431407),
    tertiaryContainer = Color(0xFF7C2D12),
    onTertiaryContainer = Color(0xFFFFEDD5),
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceContainerLowest = Color(0xFF090610),
    surfaceContainerLow = DarkSurface,
    surfaceContainer = DarkSurfaceVariant,
    surfaceContainerHigh = DarkSurfaceHigh,
    surfaceContainerHighest = Color(0xFF332952),
    outline = DarkOutline,
    outlineVariant = Color(0xFF28203F)
)

private val NexusLightColorScheme = lightColorScheme(
    primary = NexusViolet,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = Color(0xFF2E1065),
    secondary = NexusPink,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFCE7F3),
    onSecondaryContainer = Color(0xFF500724),
    tertiary = NexusOrange,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFEDD5),
    onTertiaryContainer = Color(0xFF431407),
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFFAF6FF),
    surfaceContainer = LightSurfaceVariant,
    surfaceContainerHigh = LightSurfaceHigh,
    surfaceContainerHighest = Color(0xFFE3D9F2),
    outline = LightOutline,
    outlineVariant = Color(0xFFEBE4F5)
)

val NexusShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun NexusTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val useDarkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = if (useDarkTheme) NexusDarkColorScheme else NexusLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NexusTypography,
        shapes = NexusShapes,
        content = content
    )
}
