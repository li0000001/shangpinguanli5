package com.example.foodexpiryreminder.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val expiryTime: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val syncedToCalendar: Boolean = false
)
