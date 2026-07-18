package com.example.nutriscan.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkTerracotta,
    onPrimary = DarkText,
    primaryContainer = DarkSurface,
    onPrimaryContainer = DarkText,
    secondary = OliveGreen,
    onSecondary = DarkText,
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText,
    surfaceVariant = DarkBackground,
    onSurfaceVariant = MutedText,
)

private val LightColorScheme = lightColorScheme(
    primary = TerracottaPrimary,
    onPrimary = SurfaceWhite,
    primaryContainer = SurfaceVariant,
    onPrimaryContainer = DeepCharcoal,
    secondary = OliveGreen,
    onSecondary = SurfaceWhite,
    background = SurfaceWhite, // Changed from SoftBeige to pure white
    onBackground = DeepCharcoal,
    surface = SurfaceWhite,
    onSurface = DeepCharcoal,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = MutedText,
)

@Composable
fun NutriScanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    // Force Light Theme unconditionally for a cleaner aesthetic
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
