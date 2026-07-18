package com.example.nutriscan.data.local

import androidx.room.TypeConverter
import com.example.nutriscan.data.model.NutritionInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromNutritionInfoList(value: List<NutritionInfo>?): String {
        return gson.toJson(value ?: emptyList<NutritionInfo>())
    }

    @TypeConverter
    fun toNutritionInfoList(value: String?): List<NutritionInfo> {
        if (value.isNullOrEmpty()) return emptyList()
        val listType = object : TypeToken<List<NutritionInfo>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
}
