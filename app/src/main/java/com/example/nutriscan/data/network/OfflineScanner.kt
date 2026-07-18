package com.example.nutriscan.data.network

import android.content.Context
import android.graphics.Bitmap
import com.example.nutriscan.data.local.AppDatabase
import com.example.nutriscan.data.model.NutritionInfo
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class OfflineScanner(private val context: Context) {
    private val labeler by lazy { ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS) }
    private val db = AppDatabase.getDatabase(context)

    suspend fun analyzeFoodImage(bitmap: Bitmap): Result<NutritionInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val image = InputImage.fromBitmap(bitmap, 0)
                val labels = labeler.process(image).await()
                
                if (labels.isEmpty()) {
                    return@withContext Result.failure(Exception("Food not recognized. Try again with better lighting."))
                }

                val genericLabels = setOf("food", "dish", "cuisine", "ingredient", "recipe", "meal", "plate", "tableware")
                
                // Check labels against our offline DB
                for (label in labels) {
                    val labelText = label.text.trim().lowercase()
                    if (labelText in genericLabels) continue
                    
                    val foodItem = db.mealDao().searchFoodItem(labelText)
                    
                    if (foodItem != null) {
                        return@withContext Result.success(foodItem.toNutritionInfo())
                    }
                }

                Result.failure(Exception("Food not recognized. Try again with better lighting."))
            } catch (e: Exception) {
                Result.failure(Exception("Food not recognized. Try again with better lighting."))
            }
        }
    }
}
