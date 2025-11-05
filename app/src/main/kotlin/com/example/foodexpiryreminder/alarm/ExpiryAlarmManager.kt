package com.example.foodexpiryreminder.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.foodexpiryreminder.data.FoodItem

class ExpiryAlarmManager(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleExpiryAlarm(foodItem: FoodItem) {
        val intent = Intent(context, ExpiryAlarmReceiver::class.java).apply {
            action = "com.example.foodexpiryreminder.EXPIRY_ALARM"
            putExtra("FOOD_ID", foodItem.id)
            putExtra("FOOD_NAME", foodItem.name)
            putExtra("EXPIRY_TIME", foodItem.expiryTime)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            foodItem.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        foodItem.expiryTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        foodItem.expiryTime,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    foodItem.expiryTime,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                foodItem.expiryTime,
                pendingIntent
            )
        }
    }

    fun cancelExpiryAlarm(foodId: Int) {
        val intent = Intent(context, ExpiryAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            foodId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
