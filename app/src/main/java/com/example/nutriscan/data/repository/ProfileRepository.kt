package com.example.nutriscan.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.nutriscan.data.model.ActivityLevel
import com.example.nutriscan.data.model.DietaryPreference
import com.example.nutriscan.data.model.Gender
import com.example.nutriscan.data.model.HealthGoal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("NutriScanProfile", Context.MODE_PRIVATE)
    
    // Profile Data
    fun getName(): String = prefs.getString("name", "") ?: ""
    fun saveName(name: String) = prefs.edit().putString("name", name).apply()

    fun getAge(): String = prefs.getString("age", "") ?: ""
    fun saveAge(age: String) = prefs.edit().putString("age", age).apply()

    fun getHeight(): String = prefs.getString("height", "") ?: ""
    fun saveHeight(height: String) = prefs.edit().putString("height", height).apply()

    fun getWeight(): String = prefs.getString("weight", "") ?: ""
    fun saveWeight(weight: String) = prefs.edit().putString("weight", weight).apply()

    fun getGender(): Gender {
        val name = prefs.getString("gender", Gender.MALE.name) ?: Gender.MALE.name
        return try { Gender.valueOf(name) } catch (e: Exception) { Gender.MALE }
    }
    fun saveGender(gender: Gender) = prefs.edit().putString("gender", gender.name).apply()

    fun getActivityLevel(): ActivityLevel {
        val name = prefs.getString("activity", ActivityLevel.MODERATE.name) ?: ActivityLevel.MODERATE.name
        return try { ActivityLevel.valueOf(name) } catch (e: Exception) { ActivityLevel.MODERATE }
    }
    fun saveActivityLevel(activity: ActivityLevel) = prefs.edit().putString("activity", activity.name).apply()

    fun getHealthGoal(): HealthGoal {
        val name = prefs.getString("goal", HealthGoal.MAINTAIN.name) ?: HealthGoal.MAINTAIN.name
        return try { HealthGoal.valueOf(name) } catch (e: Exception) { HealthGoal.MAINTAIN }
    }
    fun saveHealthGoal(goal: HealthGoal) = prefs.edit().putString("goal", goal.name).apply()

    fun getDietaryPreference(): DietaryPreference {
        val name = prefs.getString("diet", DietaryPreference.NON_VEG.name) ?: DietaryPreference.NON_VEG.name
        return try { DietaryPreference.valueOf(name) } catch (e: Exception) { DietaryPreference.NON_VEG }
    }
    fun saveDietaryPreference(diet: DietaryPreference) = prefs.edit().putString("diet", diet.name).apply()

    fun getCity(): String = prefs.getString("city", "Mysore") ?: "Mysore"
    fun saveCity(city: String) = prefs.edit().putString("city", city).apply()

    fun getWaterCountForToday(): Int {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return prefs.getInt("water_$today", 0)
    }

    fun saveWaterCountForToday(count: Int) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        prefs.edit().putInt("water_$today", count).apply()
    }

    // New Personalization Settings
    fun hasCompletedOnboarding(): Boolean = prefs.getBoolean("has_completed_onboarding", false)
    fun setHasCompletedOnboarding(completed: Boolean) = prefs.edit().putBoolean("has_completed_onboarding", completed).apply()

    fun isCalorieTargetManual(): Boolean = prefs.getBoolean("is_calorie_target_manual", false)
    fun setCalorieTargetManual(isManual: Boolean) = prefs.edit().putBoolean("is_calorie_target_manual", isManual).apply()

    fun getManualCalorieTarget(): Float = prefs.getFloat("manual_calorie_target", 2000f)
    fun setManualCalorieTarget(calories: Float) = prefs.edit().putFloat("manual_calorie_target", calories).apply()

    fun getDailyWaterGoal(): Int = prefs.getInt("daily_water_goal", 8)
    fun setDailyWaterGoal(goal: Int) = prefs.edit().putInt("daily_water_goal", goal).apply()

    fun getTargetCalories(): Float {
        if (isCalorieTargetManual()) {
            return getManualCalorieTarget()
        }
        val weight = getWeight().toFloatOrNull() ?: 70f
        val height = getHeight().toFloatOrNull() ?: 170f
        val age = getAge().toIntOrNull() ?: 30
        var bmr = (10 * weight) + (6.25f * height) - (5 * age)
        bmr += if (getGender() == Gender.MALE) 5f else -161f
        val multiplier = when (getActivityLevel()) {
            ActivityLevel.SEDENTARY -> 1.2f
            ActivityLevel.LIGHT -> 1.375f
            ActivityLevel.MODERATE -> 1.55f
            ActivityLevel.ACTIVE -> 1.725f
            ActivityLevel.EXTRA_ACTIVE -> 1.9f
        }
        var tdee = (bmr * multiplier).toInt()
        tdee += when (getHealthGoal()) {
            HealthGoal.LOSE -> -500
            HealthGoal.GAIN -> 500
            HealthGoal.MAINTAIN -> 0
        }
        return tdee.toFloat()
    }

    fun saveDietPlanReplacement(date: String, index: Int, foodName: String, calories: Int, description: String) {
        prefs.edit()
            .putString("meal_replace_${date}_${index}_name", foodName)
            .putInt("meal_replace_${date}_${index}_cals", calories)
            .putString("meal_replace_${date}_${index}_desc", description)
            .apply()
    }

    fun getDietPlanReplacement(date: String, index: Int): Triple<String, Int, String>? {
        val name = prefs.getString("meal_replace_${date}_${index}_name", null)
        val cals = prefs.getInt("meal_replace_${date}_${index}_cals", -1)
        val desc = prefs.getString("meal_replace_${date}_${index}_desc", "") ?: ""
        if (name != null && cals != -1) {
            return Triple(name, cals, desc)
        }
        return null
    }
}
