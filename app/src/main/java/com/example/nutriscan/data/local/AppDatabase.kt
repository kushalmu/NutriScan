package com.example.nutriscan.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [MealEntity::class, FoodItemEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun mealDao(): MealDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nutriscan_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
            super.onCreate(db)
            // Pre-populate some offline food data on first run
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = database.mealDao()
                    dao.insertFoodItems(FoodData.foods)
                }
            }
        }
    }
}
