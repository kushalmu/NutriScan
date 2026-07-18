package com.example.nutriscan.data.network

import android.graphics.Bitmap
import com.example.nutriscan.data.model.NutritionInfo
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AiScanner {
    suspend fun analyzeFoodImage(bitmap: Bitmap, apiKey: String): Result<NutritionInfo> {
        return withContext(Dispatchers.IO) {
            try {
                if (apiKey.isBlank()) {
                    return@withContext Result.failure(Exception("API Key is missing. Please add it in your Profile."))
                }

                val modelsToTry = listOf("gemini-1.5-flash", "gemini-1.5-flash-latest", "gemini-pro-vision")
                var responseText: String? = null
                var lastError: Exception? = null

                val prompt = """
                    Analyze this image of food. Identify what it is and estimate the nutritional information for a standard serving size.
                    Respond ONLY with a valid JSON object matching this exact structure, with no markdown formatting or backticks:
                    {
                        "foodName": "Name of the food",
                        "calories": 250.0,
                        "protein": 10.5,
                        "carbs": 30.0,
                        "fat": 5.0,
                        "fiber": 2.5
                    }
                """.trimIndent()

                val inputContent = content {
                    image(bitmap)
                    text(prompt)
                }

                for (modelName in modelsToTry) {
                    try {
                        val generativeModel = GenerativeModel(
                            modelName = modelName,
                            apiKey = apiKey
                        )
                        val response = generativeModel.generateContent(inputContent)
                        responseText = response.text
                        break // Success!
                    } catch (e: Exception) {
                        lastError = e
                    }
                }

                val jsonString = responseText?.trim()?.removePrefix("```json")?.removeSuffix("```")?.trim()
                    ?: throw lastError ?: Exception("Empty response from AI")

                val json = JSONObject(jsonString)
                val nutritionInfo = NutritionInfo(
                    foodName = json.getString("foodName"),
                    regionalName = "", // Could add translation later
                    portionGrams = 100f, // Estimated serving
                    calories = json.getDouble("calories").toFloat(),
                    proteinG = json.getDouble("protein").toFloat(),
                    carbsG = json.getDouble("carbs").toFloat(),
                    fatG = json.getDouble("fat").toFloat(),
                    fiberG = json.getDouble("fiber").toFloat(),
                    sugarG = 0f,
                    sodiumMg = 0f
                )

                Result.success(nutritionInfo)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
