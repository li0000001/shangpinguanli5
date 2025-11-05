package com.example.foodexpiryreminder

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodexpiryreminder.alarm.ExpiryAlarmManager
import com.example.foodexpiryreminder.calendar.CalendarSyncHelper
import com.example.foodexpiryreminder.data.AppDatabase
import com.example.foodexpiryreminder.data.FoodItem
import com.example.foodexpiryreminder.data.FoodRepository
import com.example.foodexpiryreminder.databinding.ActivityMainBinding
import com.example.foodexpiryreminder.ui.FoodAdapter
import com.example.foodexpiryreminder.ui.FoodViewModel
import com.example.foodexpiryreminder.ui.FoodViewModelFactory
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: FoodViewModel
    private lateinit var adapter: FoodAdapter
    private lateinit var alarmManager: ExpiryAlarmManager
    private var selectedDate: Long = 0

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            showPermissionDeniedDialog("通知权限被拒绝，应用无法发送过期提醒。请在设置中启用通知权限。")
        }
    }

    private val requestCalendarPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            showPermissionDeniedDialog("日历权限被拒绝，应用无法自动同步到日历。请在设置中启用日历权限。")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDatabase()
        setupViewModel()
        setupRecyclerView()
        setupListeners()
        requestNotificationPermission()
        requestCalendarPermission()

        alarmManager = ExpiryAlarmManager(this)

        lifecycleScope.launch {
            viewModel.allFoodItems.collect { foodItems ->
                adapter.submitList(foodItems)
            }
        }
    }

    private fun setupDatabase() {
        val database = AppDatabase.getDatabase(this)
        val calendarSyncHelper = CalendarSyncHelper(this)
        val repository = FoodRepository(database.foodItemDao(), calendarSyncHelper)
        val factory = FoodViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[FoodViewModel::class.java]
    }

    private fun setupViewModel() {
        // Already set up in setupDatabase()
    }

    private fun setupRecyclerView() {
        adapter = FoodAdapter(
            onItemDelete = { foodItem ->
                deleteFood(foodItem)
            },
            onItemSyncCalendar = { foodItem ->
                syncToCalendar(foodItem)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
            addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))
        }
    }

    private fun setupListeners() {
        binding.btnAdd.setOnClickListener {
            showAddFoodDialog()
        }
    }

    private fun showAddFoodDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_food, null)
        val editTextFoodName = dialogView.findViewById<android.widget.EditText>(R.id.editTextFoodName)
        val btnSelectDate = dialogView.findViewById<android.widget.Button>(R.id.btnSelectDate)
        val tvSelectedDateTime = dialogView.findViewById<android.widget.TextView>(R.id.tvSelectedDateTime)

        selectedDate = 0
        tvSelectedDateTime.text = "选择过期时间"

        btnSelectDate.setOnClickListener {
            showDateTimePicker(tvSelectedDateTime)
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("添加食品")
            .setPositiveButton("保存") { _, _ ->
                val foodName = editTextFoodName.text.toString().trim()
                if (foodName.isNotEmpty() && selectedDate > 0) {
                    val newFoodItem = FoodItem(
                        name = foodName,
                        expiryTime = selectedDate
                    )

                    lifecycleScope.launch {
                        val database = AppDatabase.getDatabase(this@MainActivity)
                        val calendarSyncHelper = CalendarSyncHelper(this@MainActivity)
                        val insertedId = database
                            .foodItemDao()
                            .insertFoodItem(newFoodItem)
                        val updatedFoodItem = newFoodItem.copy(id = insertedId.toInt())
                        
                        val syncSuccess = calendarSyncHelper.syncFoodItemToCalendar(updatedFoodItem)
                        if (syncSuccess) {
                            database.foodItemDao().updateFoodItem(updatedFoodItem.copy(syncedToCalendar = true))
                        }
                        
                        alarmManager.scheduleExpiryAlarm(updatedFoodItem)
                    }
                } else {
                    showValidationError()
                }
            }
            .setNegativeButton("取消", null)
            .create()

        dialog.show()
    }

    private fun showDateTimePicker(tvSelectedDateTime: android.widget.TextView) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                calendar.set(year, month, dayOfMonth)
                TimePickerDialog(
                    this,
                    { _: TimePicker, hourOfDay: Int, minute: Int ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        calendar.set(Calendar.SECOND, 0)
                        selectedDate = calendar.timeInMillis

                        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        tvSelectedDateTime.text = sdf.format(Date(selectedDate))
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun deleteFood(foodItem: FoodItem) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("删除食品")
            .setMessage("确定要删除 ${foodItem.name} 吗？")
            .setPositiveButton("删除") { _, _ ->
                viewModel.deleteFoodItem(foodItem)
                alarmManager.cancelExpiryAlarm(foodItem.id)
            }
            .setNegativeButton("取消", null)
            .create()
        dialog.show()
    }

    private fun syncToCalendar(foodItem: FoodItem) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = foodItem.expiryTime
        }

        val startMillis = calendar.timeInMillis
        val endMillis = calendar.timeInMillis + (60 * 60 * 1000)

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, "${foodItem.name} 过期提醒")
            putExtra(CalendarContract.Events.DESCRIPTION, "${foodItem.name} 已过期")
            putExtra(CalendarContract.Events.DTSTART, startMillis)
            putExtra(CalendarContract.Events.DTEND, endMillis)
            putExtra(CalendarContract.Events.HAS_ALARM, 1)
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            android.widget.Toast.makeText(
                this,
                "无法打开系统日历应用",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun requestCalendarPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestCalendarPermissionLauncher.launch(Manifest.permission.WRITE_CALENDAR)
        }
    }

    private fun showPermissionDeniedDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("权限被拒绝")
            .setMessage(message)
            .setPositiveButton("确定", null)
            .create()
            .show()
    }

    private fun showValidationError() {
        AlertDialog.Builder(this)
            .setTitle("输入错误")
            .setMessage("请填写食品名称并选择过期时间")
            .setPositiveButton("确定", null)
            .create()
            .show()
    }
}
