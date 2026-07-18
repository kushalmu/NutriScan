package com.example.nutriscan.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nutriscan.data.model.MealEntry
import com.example.nutriscan.data.model.MealType

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val dateString: String,
    val mealType: String,
    val itemsJson: String,
    val isAiAnalyzed: Boolean
) {
    fun toMealEntry(nutritionItems: List<com.example.nutriscan.data.model.NutritionInfo>): MealEntry {
        return MealEntry(
            id = id,
            timestamp = timestamp,
            mealType = MealType.valueOf(mealType),
            items = nutritionItems,
            photoUri = null,
            isAiAnalyzed = isAiAnalyzed
        )
    }
}
