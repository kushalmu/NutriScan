package com.example.nutriscan.data.model

// === User Profile ===
// Stores the user's health information for personalized recommendations

data class UserProfile(
    val name: String = "",
    val age: Int = 0,
    val gender: Gender = Gender.MALE,
    val heightCm: Float = 0f,
    val weightKg: Float = 0f,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val healthGoal: HealthGoal = HealthGoal.MAINTAIN,
    val healthConditions: List<String> = emptyList(),
    val dietaryPreference: DietaryPreference = DietaryPreference.NON_VEG,
    val city: String = "Mysore",
)

enum class Gender(val displayName: String) {
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other"),
}

enum class ActivityLevel(val displayName: String, val multiplier: Float) {
    SEDENTARY("Sedentary (little or no exercise)", 1.2f),
    LIGHT("Lightly Active (1-3 days/week)", 1.375f),
    MODERATE("Moderately Active (3-5 days/week)", 1.55f),
    ACTIVE("Very Active (6-7 days/week)", 1.725f),
    EXTRA_ACTIVE("Extra Active (athlete level)", 1.9f),
}

enum class HealthGoal(val displayName: String) {
    LOSE("Lose Weight"),
    MAINTAIN("Maintain Weight"),
    GAIN("Gain Weight / Muscle"),
}

enum class DietaryPreference(val displayName: String) {
    VEG("Vegetarian"),
    NON_VEG("Non-Vegetarian"),
    VEGAN("Vegan"),
    EGGETARIAN("Eggetarian"),
}

// === Nutrition Info ===
// Nutritional data for a single food item

data class NutritionInfo(
    val foodName: String = "",
    val regionalName: String = "",         // Kannada / local name
    val portionGrams: Float = 0f,
    val calories: Float = 0f,             // kcal
    val proteinG: Float = 0f,
    val carbsG: Float = 0f,
    val fatG: Float = 0f,
    val fiberG: Float = 0f,
    val sugarG: Float = 0f,
    val sodiumMg: Float = 0f,
)

// === Meal Entry ===
// A logged meal (can contain multiple food items)

data class MealEntry(
    val id: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val mealType: MealType = MealType.LUNCH,
    val items: List<NutritionInfo> = emptyList(),
    val photoUri: String? = null,          // Path to the food photo
    val isAiAnalyzed: Boolean = false,     // Was this scanned by AI?
) {
    // Total nutrition for the entire meal
    val totalCalories: Float get() = items.sumOf { it.calories.toDouble() }.toFloat()
    val totalProtein: Float get() = items.sumOf { it.proteinG.toDouble() }.toFloat()
    val totalCarbs: Float get() = items.sumOf { it.carbsG.toDouble() }.toFloat()
    val totalFat: Float get() = items.sumOf { it.fatG.toDouble() }.toFloat()
    val totalFiber: Float get() = items.sumOf { it.fiberG.toDouble() }.toFloat()
}

enum class MealType(val displayName: String, val emoji: String) {
    BREAKFAST("Breakfast", "🌅"),
    MORNING_SNACK("Morning Snack", "🍎"),
    LUNCH("Lunch", "☀️"),
    EVENING_SNACK("Evening Snack", "🫖"),
    DINNER("Dinner", "🌙"),
}

// === Daily Summary ===
// Aggregated nutrition for an entire day

data class DailySummary(
    val date: String = "",                 // "2026-07-16"
    val meals: List<MealEntry> = emptyList(),
    val waterGlasses: Int = 0,
    val targetCalories: Float = 2000f,
) {
    val totalCalories: Float get() = meals.sumOf { it.totalCalories.toDouble() }.toFloat()
    val totalProtein: Float get() = meals.sumOf { it.totalProtein.toDouble() }.toFloat()
    val totalCarbs: Float get() = meals.sumOf { it.totalCarbs.toDouble() }.toFloat()
    val totalFat: Float get() = meals.sumOf { it.totalFat.toDouble() }.toFloat()
    val totalFiber: Float get() = meals.sumOf { it.totalFiber.toDouble() }.toFloat()
    val calorieProgress: Float get() = if (targetCalories > 0) (totalCalories / targetCalories).coerceIn(0f, 1.5f) else 0f
}
