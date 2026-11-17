package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Core Brand Palette: Violet -> Pink -> Orange
val NexusViolet = Color(0xFF7C3AED)
val NexusVioletLight = Color(0xFFA78BFA)
val NexusPink = Color(0xFFEC4899)
val NexusPinkLight = Color(0xFFF472B6)
val NexusOrange = Color(0xFFF97316)
val NexusOrangeLight = Color(0xFFFB923C)

val NexusOnlineGreen = Color(0xFF22C55E)
val NexusVerifiedBlue = Color(0xFF8B5CF6)

// Light Surface & Text Tokens
val LightBackground = Color(0xFFFAF8FF)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF3EEF9)
val LightSurfaceHigh = Color(0xFFEBE3F5)
val LightOutline = Color(0xFFDCD3EB)
val LightOnSurface = Color(0xFF161124)
val LightOnSurfaceVariant = Color(0xFF5D5373)

// Dark Surface & Text Tokens
val DarkBackground = Color(0xFF0C0914)
val DarkSurface = Color(0xFF141021)
val DarkSurfaceVariant = Color(0xFF1D172E)
val DarkSurfaceHigh = Color(0xFF28203F)
val DarkOutline = Color(0xFF342A4F)
val DarkOnSurface = Color(0xFFF5F2FF)
val DarkOnSurfaceVariant = Color(0xFFB3A9CC)

val NexusBrandColors = listOf(NexusViolet, NexusPink, NexusOrange)

val NexusBrandGradient: Brush = Brush.linearGradient(colors = NexusBrandColors)

val NexusHorizontalGradient: Brush = Brush.horizontalGradient(colors = NexusBrandColors)

val NexusCardGradients: List<List<Color>> = listOf(
    listOf(Color(0xFF7C3AED), Color(0xFFEC4899)),
    listOf(Color(0xFFEC4899), Color(0xFFF97316)),
    listOf(Color(0xFF4F46E5), Color(0xFFA855F7)),
    listOf(Color(0xFF9333EA), Color(0xFFF97316))
)

val AvatarPalette: List<List<Color>> = listOf(
    listOf(Color(0xFF7C3AED), Color(0xFFEC4899)),
    listOf(Color(0xFFEC4899), Color(0xFFF97316)),
    listOf(Color(0xFF6366F1), Color(0xFFEC4899)),
    listOf(Color(0xFF8B5CF6), Color(0xFFF59E0B)),
    listOf(Color(0xFFD946EF), Color(0xFFF97316)),
    listOf(Color(0xFF4C1D95), Color(0xFFDB2777))
)
