package com.example.foodexpiryreminder.data

import kotlinx.coroutines.flow.Flow

class FoodRepository(private val foodItemDao: FoodItemDao) {

    val allFoodItems: Flow<List<FoodItem>> = foodItemDao.getAllFoodItems()

    suspend fun insertFoodItem(foodItem: FoodItem): Long {
        return foodItemDao.insertFoodItem(foodItem)
    }

    suspend fun updateFoodItem(foodItem: FoodItem) {
        foodItemDao.updateFoodItem(foodItem)
    }

    suspend fun deleteFoodItem(foodItem: FoodItem) {
        foodItemDao.deleteFoodItem(foodItem)
    }

    suspend fun deleteFoodItemById(id: Int) {
        foodItemDao.deleteFoodItemById(id)
    }

    suspend fun getFoodItemById(id: Int): FoodItem? {
        return foodItemDao.getFoodItemById(id)
    }
}
