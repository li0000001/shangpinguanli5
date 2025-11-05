package com.example.foodexpiryreminder.data

import com.example.foodexpiryreminder.calendar.CalendarSyncHelper
import kotlinx.coroutines.flow.Flow

class FoodRepository(
    private val foodItemDao: FoodItemDao,
    private val calendarSyncHelper: CalendarSyncHelper? = null
) {

    val allFoodItems: Flow<List<FoodItem>> = foodItemDao.getAllFoodItems()

    suspend fun insertFoodItem(foodItem: FoodItem): Long {
        return foodItemDao.insertFoodItem(foodItem)
    }

    suspend fun insertFoodItemWithCalendarSync(foodItem: FoodItem): Long {
        val insertedId = foodItemDao.insertFoodItem(foodItem)
        if (calendarSyncHelper != null) {
            val updatedFoodItem = foodItem.copy(id = insertedId.toInt())
            val syncSuccess = calendarSyncHelper.syncFoodItemToCalendar(updatedFoodItem)
            if (syncSuccess) {
                foodItemDao.updateFoodItem(updatedFoodItem.copy(syncedToCalendar = true))
            }
        }
        return insertedId
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
