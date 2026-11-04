package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricLime,
    onPrimary = ObsidianBlack,
    primaryContainer = ElectricLimeContainer,
    onPrimaryContainer = ElectricLime,
    secondary = CyberCyan,
    onSecondary = ObsidianBlack,
    secondaryContainer = GraphiteElevated,
    onSecondaryContainer = CyberCyan,
    tertiary = IntelligenceViolet,
    onTertiary = ObsidianBlack,
    background = ObsidianBlack,
    onBackground = TextIvory,
    surface = DeepCharcoal,
    onSurface = TextIvory,
    surfaceVariant = GraphiteCard,
    onSurfaceVariant = TextMuted,
    outline = GraphiteBorder,
    outlineVariant = GraphiteElevated,
    error = StatusOutOfService,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = LightLimeAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCFCE7),
    onPrimaryContainer = LightLimeAccent,
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = Color(0xFF6366F1),
    onTertiary = Color.White,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextMuted,
    outline = LightBorder,
    outlineVariant = Color(0xFFCBD5E1),
    error = StatusOutOfService,
    onError = Color.White
)

@Composable
fun GymVerseTheme(
    darkTheme: Boolean = true, // Default to iconic Obsidian Black gym experience
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
