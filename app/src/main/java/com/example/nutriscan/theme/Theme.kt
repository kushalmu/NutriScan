package com.example.nutriscan.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// NutriScan Dark Color Scheme — sleek, modern dark mode
private val DarkColorScheme = darkColorScheme(
    primary = NutriGreenLight,
    onPrimary = Color.Black,
    primaryContainer = NutriGreenDark,
    onPrimaryContainer = NutriGreenSurface,
    secondary = NutriOrangeLight,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF5A3300),
    onSecondaryContainer = NutriOrangeLight,
    tertiary = WaterCyan,
    onTertiary = Color.Black,
    background = DarkSurface,
    onBackground = Color(0xFFE6E6E6),
    surface = DarkSurface,
    onSurface = Color(0xFFE6E6E6),
    surfaceVariant = DarkCard,
    onSurfaceVariant = Color(0xFFCACACA),
    error = ErrorRed,
    onError = Color.Black,
)

// NutriScan Light Color Scheme — fresh, clean, health-focused
private val LightColorScheme = lightColorScheme(
    primary = NutriGreen,
    onPrimary = Color.White,
    primaryContainer = NutriGreenSurface,
    onPrimaryContainer = NutriGreenDark,
    secondary = NutriOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0B2),
    onSecondaryContainer = Color(0xFF5A3300),
    tertiary = WaterCyan,
    onTertiary = Color.White,
    background = NeutralWhite,
    onBackground = NeutralDark,
    surface = Color.White,
    onSurface = NeutralDark,
    surfaceVariant = NeutralLight,
    onSurfaceVariant = NeutralMedium,
    error = ErrorRed,
    onError = Color.White,
)

@Composable
fun NutriScanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to use our custom health-themed colors
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
