package com.example.foodexpiryreminder.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.foodexpiryreminder.MainActivity
import com.example.foodexpiryreminder.R

class NotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "food_expiry_channel"
        private const val CHANNEL_NAME = "Food Expiry Reminders"
        private const val NOTIFICATION_ID_OFFSET = 10000
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            channel.description = "Notifications for expired food items"
            channel.enableVibration(true)
            channel.setShowBadge(true)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showExpiryNotification(foodId: Int, foodName: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("FOOD_ID", foodId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            foodId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("食品已过期")
            .setContentText("$foodName 已过期，请及时处理")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)

        notificationManager.notify(NOTIFICATION_ID_OFFSET + foodId, builder.build())
    }

    fun cancelNotification(foodId: Int) {
        notificationManager.cancel(NOTIFICATION_ID_OFFSET + foodId)
    }
}
