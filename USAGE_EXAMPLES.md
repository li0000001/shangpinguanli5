# 使用示例

## 场景 1: 日常家庭使用

### 场景描述
王女士买了牛奶和酸奶，需要在到期前食用，并希望在过期时收到提醒。

### 操作步骤

1. **添加牛奶**
   - 打开应用，点击"添加食品"
   - 输入名称: "牛奶"
   - 点击"选择过期时间"
   - 日期选择: 2024年1月15日
   - 时间选择: 10:00 (早上 10 点)
   - 点击"保存"

2. **添加酸奶**
   - 重复上述步骤
   - 输入名称: "酸奶"
   - 过期时间: 2024年1月20日 10:00

3. **预期结果**
   - 列表显示两项食品
   - 牛奶：还剩 3 天
   - 酸奶：还剩 8 天
   - 到期时间到达，手机会收到通知

### 对应代码位置
- **添加逻辑**: `MainActivity.kt` - `showAddFoodDialog()`
- **列表显示**: `FoodAdapter.kt` - `bind()`
- **提醒触发**: `ExpiryAlarmReceiver.kt` - `onReceive()`

---

## 场景 2: 工作食堂库存管理

### 场景描述
食堂管理员需要追踪多种食材的保质期，避免使用过期食材。

### 操作步骤

批量添加食材：

```
食材列表：
1. 面粉 - 2024/02/15
2. 食用油 - 2024/03/01
3. 生菜 - 2024/01/08
4. 番茄酱 - 2024/04/10
5. 冷冻鸡翅 - 2024/02/28
```

1. 逐个添加每种食材
2. 查看列表，按过期时间排序（自动排序）
3. 接收即将过期的通知
4. 点击"同步到日历"将关键日期同步到工作日历

### 对应代码位置
- **排序查询**: `FoodItemDao.kt` - `getAllFoodItems()` (ORDER BY expiryTime ASC)
- **日历同步**: `MainActivity.kt` - `syncToCalendar()`

---

## 场景 3: 长期存储物品追踪

### 场景描述
用户购买了一些长期保存物品（如罐头、饮料等），需要长期追踪。

### 操作步骤

1. 添加罐头食品
   - 名称: "番茄罐头"
   - 过期时间: 2026年6月15日（2年后）

2. 查看应用
   - 应用首页显示所有物品
   - 可随时查看剩余时间

3. 收到提醒
   - 到期前一段时间收到系统通知
   - 点击通知打开应用确认

### 对应代码位置
- **数据持久化**: `AppDatabase.kt` - Room 数据库
- **长期存储**: `FoodItem.kt` - 数据实体
- **流式更新**: `FoodViewModel.kt` - `allFoodItems` StateFlow

---

## 场景 4: 国产手机上的可靠提醒

### 场景描述
用户在小米手机上使用应用，需要确保即使关闭屏幕也能收到提醒。

### 系统配置要求

1. **通知权限设置**
   - 设置 > 应用 > 食品保质期提醒 > 通知
   - 启用所有通知权限

2. **电池优化设置**
   - 设置 > 电池 > 电池优化
   - 搜索应用并选择"不优化"

3. **权限设置**
   - 设置 > 应用权限 > 日程提醒
   - 启用"显示在锁屏上"

### 代码实现

```kotlin
// AlarmManager 使用
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    if (alarmManager.canScheduleExactAlarms()) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            foodItem.expiryTime,
            pendingIntent
        )
    }
}

// NotificationChannel 配置
channel.enableVibration(true)
channel.setShowBadge(true)
```

对应代码位置:
- **精确闹钟**: `ExpiryAlarmManager.kt` - `scheduleExpiryAlarm()`
- **通知配置**: `NotificationHelper.kt` - `createNotificationChannel()`

---

## 场景 5: 删除已过期食品

### 场景描述
用户已经消费或丢弃了某些食品，需要从列表中删除。

### 操作步骤

1. 在列表中找到要删除的食品
2. 点击"删除"按钮
3. 确认删除对话框
4. 食品被移除，相关闹钟被取消

### 预期结果
- 食品从列表中移除
- 后续不会收到该食品的提醒
- 如果该食品有日历事件，需要手动删除

### 对应代码位置
- **删除逻辑**: `MainActivity.kt` - `deleteFood()`
- **取消闹钟**: `ExpiryAlarmManager.kt` - `cancelExpiryAlarm()`
- **ViewModel 处理**: `FoodViewModel.kt` - `deleteFoodItem()`

---

## 场景 6: 同步到系统日历

### 场景描述
用户希望将食品过期提醒同步到手机的系统日历，以便与其他日历事件统一管理。

### 操作步骤

1. 在食品列表中，点击"同步到日历"按钮
2. 系统日历应用打开
3. 事件创建界面显示：
   - 标题: "食品名 过期提醒"
   - 描述: "食品名 已过期"
   - 时间: 与食品过期时间相同
4. 用户可添加额外细节和提醒设置
5. 点击"保存"完成同步

### 代码实现

```kotlin
val intent = Intent(Intent.ACTION_INSERT).apply {
    data = CalendarContract.Events.CONTENT_URI
    putExtra(CalendarContract.Events.TITLE, "${foodItem.name} 过期提醒")
    putExtra(CalendarContract.Events.DTSTART, startMillis)
    putExtra(CalendarContract.Events.DTEND, endMillis)
    putExtra(CalendarContract.Events.HAS_ALARM, 1)
}
startActivity(intent)
```

对应代码位置:
- **日历集成**: `MainActivity.kt` - `syncToCalendar()`

---

## 场景 7: 应用后台被杀后的恢复

### 场景描述
用户的应用被系统杀死，但设置的食品过期提醒仍然需要正常工作。

### 工作原理

1. **AlarmManager 持久性**
   - AlarmManager 中的闹钟在应用被杀死后仍会保留
   - 系统会在适当时间触发闹钟

2. **广播接收器配置**
   ```xml
   <receiver
       android:name=".alarm.ExpiryAlarmReceiver"
       android:exported="true"
       android:permission="android.permission.RECEIVE_BOOT_COMPLETED">
       <intent-filter>
           <action android:name="android.intent.action.BOOT_COMPLETED" />
           <action android:name="com.example.foodexpiryreminder.EXPIRY_ALARM" />
       </intent-filter>
   </receiver>
   ```

3. **触发流程**
   - 应用被杀死
   - 到期时间到达
   - 系统触发闹钟
   - ExpiryAlarmReceiver 接收广播
   - 发送通知

对应代码位置:
- **广播接收**: `ExpiryAlarmReceiver.kt`
- **清单配置**: `AndroidManifest.xml` - receiver 标签
- **闹钟设置**: `ExpiryAlarmManager.kt` - `scheduleExpiryAlarm()`

---

## 场景 8: 权限申请流程

### 场景描述
App 首次启动时，自动申请必要权限。

### 权限申请顺序

1. **通知权限** (Android 13+)
   ```kotlin
   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
       requestNotificationPermissionLauncher.launch(
           Manifest.permission.POST_NOTIFICATIONS
       )
   }
   ```

2. **用户响应**
   - 允许: 应用可以发送通知
   - 拒绝: 显示提示对话框

3. **其他权限**
   - SCHEDULE_EXACT_ALARM: 系统自动授予
   - READ/WRITE_CALENDAR: 用户同步到日历时会提示

对应代码位置:
- **权限申请**: `MainActivity.kt` - `requestNotificationPermission()`
- **权限处理**: `MainActivity.kt` - `requestNotificationPermissionLauncher`

---

## 故障排查场景

### 场景 A: 通知无法显示

**检查步骤**:
1. 打开系统设置 > 应用 > 食品保质期提醒 > 通知
2. 检查是否已启用
3. 检查通知类别是否被屏蔽
4. 重启应用

### 场景 B: 闹钟不工作

**检查步骤**:
1. 确认设备时间正确
2. 检查是否有 VPN 干扰
3. 检查 Android 版本 (需要 API 26+)
4. 查看应用日志:
   ```bash
   adb logcat | grep FoodExpiryReminder
   ```

### 场景 C: 应用崩溃

**调试步骤**:
1. 查看 Logcat 输出
2. 清除应用数据: `adb shell pm clear com.example.foodexpiryreminder`
3. 重新启动应用
4. 查看是否有数据库错误

---

## 数据库查询示例

### 查看所有食品

```kotlin
// 在 Repository 中
val allFoods = foodItemDao.getAllFoodItems()
```

### 查询即将过期的食品（7天内）

```kotlin
val upcoming = database.foodItemDao().getAllFoodItems()
    .map { foods ->
        foods.filter { food ->
            val daysLeft = (food.expiryTime - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)
            daysLeft in 0..7
        }
    }
```

---

## 完整工作流示例

```
用户 A 的典型使用流程:
├─ 1. 打开应用 (首次使用)
│  ├─ 申请通知权限
│  └─ 显示空列表 + 添加按钮
│
├─ 2. 添加食品
│  ├─ 牛奶 - 2024/01/10 10:00
│  ├─ 酸奶 - 2024/01/15 14:00
│  └─ 面包 - 2024/01/08 18:00
│
├─ 3. 查看列表
│  ├─ 面包: 2天后过期 (红色)
│  ├─ 牛奶: 7天后过期 (绿色)
│  └─ 酸奶: 12天后过期 (绿色)
│
├─ 4. 同步到日历 (可选)
│  └─ 选择 1-2 项重要食品同步
│
├─ 5. 接收提醒
│  ├─ 2024/01/08: 面包过期通知
│  ├─ 2024/01/10: 牛奶过期通知
│  └─ 2024/01/15: 酸奶过期通知
│
└─ 6. 清理列表
   ├─ 删除已过期食品
   └─ 添加新的食品
```
