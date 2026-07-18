package com.example.nutriscan.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMeal(meal: MealEntity)

    @Delete
    fun deleteMeal(meal: MealEntity)

    @Query("SELECT * FROM meals WHERE dateString = :dateString ORDER BY timestamp DESC")
    fun getMealsForDate(dateString: String): Flow<List<MealEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFoodItems(items: List<FoodItemEntity>)

    @Query("SELECT * FROM food_items WHERE foodName LIKE '%' || :query || '%' LIMIT 1")
    fun searchFoodItem(query: String): FoodItemEntity?
}
