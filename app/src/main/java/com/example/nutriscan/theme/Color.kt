package com.example.nutriscan.theme

import androidx.compose.ui.graphics.Color

// === NutriScan Claude-Inspired Color Palette ===
// Soft, minimal, organic colors

// Backgrounds & Surfaces
val SoftBeige = Color(0xFFF9F8F6)        // Main app background
val SurfaceWhite = Color(0xFFFFFFFF)     // Clean white for cards
val SurfaceVariant = Color(0xFFF0EBE1)   // Slightly darker beige for contrast

// Primary & Accents (Warm & Organic)
val TerracottaPrimary = Color(0xFFD97757) // Main action color
val TerracottaLight = Color(0xFFE89A80)
val OliveGreen = Color(0xFF8B9B7B)        // Success / Health indicator
val DeepCharcoal = Color(0xFF2C2C2A)      // For main text (softer than pure black)
val MutedText = Color(0xFF7A7975)         // Secondary text

// Dark Theme Variants
val DarkBackground = Color(0xFF1E1E1C)
val DarkSurface = Color(0xFF282826)
val DarkText = Color(0xFFEBEAE7)
val DarkTerracotta = Color(0xFFC76545)

// Macro colors (muted and soft)
val MacroProtein = Color(0xFF8B9B7B)      // Olive
val MacroCarbs = Color(0xFFDCA774)        // Warm sand
val MacroFat = Color(0xFFB58484)          // Soft mauve
val MacroFiber = Color(0xFF90A3AD)        // Slate blue
val WaterCyan = Color(0xFF7BA6B6)         // Muted cyan

// Legacy color aliases for other screens to compile with the new theme
val NutriGreen = TerracottaPrimary
val NutriGreenDark = DeepCharcoal
val NutriGreenLight = TerracottaLight
val NutriGreenSurface = SurfaceVariant
val NutriOrange = TerracottaPrimary
val SuccessGreen = OliveGreen
val ErrorRed = Color(0xFFD95757)
val CarbsAmber = MacroCarbs
val ProteinBlue = MacroProtein
val FatPurple = MacroFat
val FiberGreen = MacroFiber
