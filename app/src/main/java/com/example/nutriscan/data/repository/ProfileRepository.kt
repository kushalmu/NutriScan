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

    // Water Tracker logic
    fun getWaterCountForToday(): Int {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return prefs.getInt("water_$today", 0)
    }

    fun saveWaterCountForToday(count: Int) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        prefs.edit().putInt("water_$today", count).apply()
    }
}
