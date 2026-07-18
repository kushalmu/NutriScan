package com.example.nutriscan.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nutriscan.data.model.NutritionInfo

@Entity(tableName = "food_items")
data class FoodItemEntity(
    @PrimaryKey val foodName: String, // We'll use the ML Kit label as the key (e.g. "Apple")
    val regionalName: String,
    val calories: Float,
    val proteinG: Float,
    val carbsG: Float,
    val fatG: Float,
    val fiberG: Float
) {
    fun toNutritionInfo() = NutritionInfo(
        foodName = foodName,
        regionalName = regionalName,
        portionGrams = 100f,
        calories = calories,
        proteinG = proteinG,
        carbsG = carbsG,
        fatG = fatG,
        fiberG = fiberG
    )
}
