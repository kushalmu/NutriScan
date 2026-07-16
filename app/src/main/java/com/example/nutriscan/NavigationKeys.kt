package com.example.nutriscan

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// === Navigation Keys ===
// Each object represents a screen in the app.
// The bottom nav bar uses these to switch between tabs.

@Serializable data object Main : NavKey           // Not used directly — redirects to Dashboard
@Serializable data object Dashboard : NavKey       // 🏠 Home — daily calorie summary
@Serializable data object Scanner : NavKey         // 📸 Scan — camera food scanning
@Serializable data object DietPlan : NavKey        // 🍽️ Plan — diet plan suggestions
@Serializable data object Profile : NavKey         // 👤 Profile — user health profile
