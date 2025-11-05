package com.example.foodexpiryreminder.calendar

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import com.example.foodexpiryreminder.data.FoodItem

class CalendarSyncHelper(private val context: Context) {

    fun syncFoodItemToCalendar(foodItem: FoodItem): Boolean {
        return try {
            if (!hasCalendarPermission()) {
                return false
            }

            val calendarId = getDefaultCalendarId() ?: return false
            val startMillis = foodItem.expiryTime
            val endMillis = foodItem.expiryTime + (60 * 60 * 1000)

            val values = ContentValues().apply {
                put(CalendarContract.Events.CALENDAR_ID, calendarId)
                put(CalendarContract.Events.TITLE, "${foodItem.name} 过期提醒")
                put(CalendarContract.Events.DESCRIPTION, "${foodItem.name} 已过期")
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.EVENT_TIMEZONE, java.util.TimeZone.getDefault().id)
                put(CalendarContract.Events.HAS_ALARM, 1)
            }

            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            uri != null
        } catch (e: Exception) {
            false
        }
    }

    private fun hasCalendarPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getDefaultCalendarId(): Long? {
        return try {
            val projection = arrayOf(CalendarContract.Calendars._ID)
            val uri = CalendarContract.Calendars.CONTENT_URI
            val selection = "(${CalendarContract.Calendars.OWNER_ACCOUNT} IS NOT NULL) AND (${CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL} >= 700)"
            val cursor = context.contentResolver.query(uri, projection, selection, null, null)

            cursor?.use {
                if (it.moveToFirst()) {
                    val calendarIdColumn = it.getColumnIndex(CalendarContract.Calendars._ID)
                    return it.getLong(calendarIdColumn)
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }
}
