package com.example.nutriscan.data

import com.example.nutriscan.data.model.DailySummary
import com.example.nutriscan.data.model.MealEntry
import com.example.nutriscan.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// === Data Repository ===
// Central place to manage all app data.
// Currently uses in-memory storage. Will be replaced with Firebase/Room in later phases.

interface DataRepository {
    val userProfile: Flow<UserProfile>
    val todayMeals: Flow<List<MealEntry>>
    val dailySummary: Flow<DailySummary>

    suspend fun saveProfile(profile: UserProfile)
    suspend fun addMeal(meal: MealEntry)
}

class DefaultDataRepository : DataRepository {
    private val _userProfile = MutableStateFlow(UserProfile())
    override val userProfile: Flow<UserProfile> = _userProfile.asStateFlow()

    private val _todayMeals = MutableStateFlow<List<MealEntry>>(emptyList())
    override val todayMeals: Flow<List<MealEntry>> = _todayMeals.asStateFlow()

    private val _dailySummary = MutableStateFlow(DailySummary())
    override val dailySummary: Flow<DailySummary> = _dailySummary.asStateFlow()

    override suspend fun saveProfile(profile: UserProfile) {
        _userProfile.value = profile
    }

    override suspend fun addMeal(meal: MealEntry) {
        val currentMeals = _todayMeals.value.toMutableList()
        currentMeals.add(meal)
        _todayMeals.value = currentMeals
        _dailySummary.value = _dailySummary.value.copy(meals = currentMeals)
    }
}
