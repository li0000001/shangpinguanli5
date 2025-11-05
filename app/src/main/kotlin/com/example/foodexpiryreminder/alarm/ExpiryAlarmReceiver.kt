package com.example.foodexpiryreminder.alarm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.foodexpiryreminder.notification.NotificationHelper

class ExpiryAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.example.foodexpiryreminder.EXPIRY_ALARM") {
            val foodId = intent.getIntExtra("FOOD_ID", -1)
            val foodName = intent.getStringExtra("FOOD_NAME") ?: "Food"

            val notificationHelper = NotificationHelper(context)
            notificationHelper.showExpiryNotification(foodId, foodName)
        }
    }
}
