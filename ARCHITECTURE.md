# 架构设计文档

## 整体架构

本项目采用 **MVVM + Repository Pattern** 架构，确保代码的可维护性、可测试性和可扩展性。

```
┌─────────────────────────────────────────────────────────┐
│                     UI Layer (View)                      │
│  ┌──────────────┐        ┌──────────────┐              │
│  │  MainActivity │        │  FoodAdapter │              │
│  └──────────────┘        └──────────────┘              │
└────────────┬────────────────────────────┬────────────────┘
             │                            │
             ▼                            ▼
┌─────────────────────────────────────────────────────────┐
│                   ViewModel Layer                        │
│  ┌──────────────────────────────────────────────────┐  │
│  │         FoodViewModel                            │  │
│  │  - allFoodItems: StateFlow<List<FoodItem>>      │  │
│  │  - insertFoodItem()                             │  │
│  │  - deleteFoodItem()                             │  │
│  │  - updateFoodItem()                             │  │
│  └──────────────────────────────────────────────────┘  │
└────────────┬───────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────┐
│               Repository Layer                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │    FoodRepository                               │  │
│  │  - allFoodItems: Flow<List<FoodItem>>           │  │
│  │  - insertFoodItem(FoodItem)                     │  │
│  │  - deleteFoodItem(FoodItem)                     │  │
│  │  - getFoodItemById(Int)                         │  │
│  └──────────────────────────────────────────────────┘  │
└────────────┬───────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────┐
│              Data Layer (Room)                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │         AppDatabase                             │  │
│  │  ├─ FoodItemDao                                 │  │
│  │  │  ├─ getAllFoodItems()                        │  │
│  │  │  ├─ insertFoodItem()                         │  │
│  │  │  ├─ deleteFoodItem()                         │  │
│  │  │  └─ ...                                      │  │
│  │  └─ food_items table                            │  │
│  └──────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────┘
```

## 各层职责

### View Layer (UI)
**负责**: 用户交互、数据展示

**主要组件**:
- `MainActivity`: 主界面，管理 UI 事件
- `FoodAdapter`: RecyclerView 适配器
- XML 布局文件

**职责**:
- 显示数据
- 捕获用户输入
- 调用 ViewModel 方法

### ViewModel Layer
**负责**: 业务逻辑、数据管理

**主要组件**:
- `FoodViewModel`: 核心 ViewModel

**职责**:
- 持有数据状态 (StateFlow)
- 提供公共方法
- 管理 Coroutine 作用域
- 处理生命周期

### Repository Layer
**负责**: 数据源抽象

**主要组件**:
- `FoodRepository`: 数据仓库

**职责**:
- 抽象数据来源
- 提供统一接口
- 处理数据一致性
- 支持多数据源

### Data Layer
**负责**: 数据持久化

**主要组件**:
- `AppDatabase`: Room 数据库
- `FoodItemDao`: 数据访问对象
- `FoodItem`: 数据实体

**职责**:
- 数据持久化
- 数据库操作
- 数据查询

## 系统组件

### 1. AlarmManager 系统

```
FoodItem Added
    ↓
ExpiryAlarmManager.scheduleExpiryAlarm()
    ↓
AlarmManager.setExactAndAllowWhileIdle()
    ↓
[等待到期时间]
    ↓
ExpiryAlarmReceiver.onReceive()
    ↓
NotificationHelper.showExpiryNotification()
    ↓
用户收到通知
```

**关键特性**:
- RTC_WAKEUP: 唤醒设备
- setExactAndAllowWhileIdle: 精确触发
- FLAG_IMMUTABLE: 安全标志
- 后台运行: 不依赖应用进程

### 2. 数据流

```
User Input
    ↓
MainActivity (showAddFoodDialog)
    ↓
FoodViewModel.insertFoodItem()
    ↓
FoodRepository.insertFoodItem()
    ↓
FoodItemDao.insertFoodItem()
    ↓
Room Database
    ↓
Flow Emission
    ↓
StateFlow Update
    ↓
FoodAdapter.submitList()
    ↓
RecyclerView Update
    ↓
UI Refresh
```

**优势**:
- 反应式更新
- 自动生命周期感知
- 类型安全

### 3. 权限申请流

```
App Launch
    ↓
requestNotificationPermission() (Android 13+)
    ↓
ActivityResultContract
    ↓
User Decision
    ├─ Allow → 继续运行
    └─ Deny → 显示提示
    ↓
requestNotificationPermissionLauncher.launch()
```

## 数据模型

### FoodItem 实体

```kotlin
@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,              // 主键，自增
    val name: String,              // 食品名称
    val expiryTime: Long,           // 过期时间 (毫秒)
    val createdAt: Long = System.currentTimeMillis()  // 创建时间
)
```

**字段说明**:
- `id`: 唯一标识符
- `name`: 用户输入的食品名称
- `expiryTime`: Unix 时间戳（毫秒）
- `createdAt`: 添加到系统的时间

**数据库映射**:
```sql
CREATE TABLE food_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    name TEXT NOT NULL,
    expiryTime INTEGER NOT NULL,
    createdAt INTEGER NOT NULL
)
```

## 线程模型

### Coroutine 作用域

```
viewModelScope
├─ 绑定到 ViewModel 生命周期
├─ 自动取消 (ViewModel 销毁时)
└─ 用于长期操作

viewLifecycleOwner.lifecycleScope
├─ 绑定到 Fragment 生命周期
├─ 自动取消 (Fragment 销毁时)
└─ 用于 UI 相关操作
```

### 主线程切换

```kotlin
// 写入数据库 (Dispatcher.IO)
viewModelScope.launch {
    repository.insertFoodItem(foodItem)
}

// UI 更新自动返回主线程
StateFlow → collect in viewLifecycleOwner.lifecycleScope
```

## 扩展点

### 添加新功能的步骤

#### 1. 添加数据库字段
```kotlin
// FoodItem.kt 添加字段
@Entity(tableName = "food_items")
data class FoodItem(
    // ... 现有字段
    val category: String = "其他"  // 新增字段
)
```

#### 2. 更新 DAO
```kotlin
// FoodItemDao.kt
@Query("SELECT * FROM food_items WHERE category = :category")
fun getFoodItemsByCategory(category: String): Flow<List<FoodItem>>
```

#### 3. 更新 Repository
```kotlin
// FoodRepository.kt
fun getFoodItemsByCategory(category: String): Flow<List<FoodItem>> {
    return foodItemDao.getFoodItemsByCategory(category)
}
```

#### 4. 更新 ViewModel
```kotlin
// FoodViewModel.kt
fun getFoodItemsByCategory(category: String): Flow<List<FoodItem>> {
    return repository.getFoodItemsByCategory(category)
}
```

#### 5. 更新 UI
```kotlin
// MainActivity.kt
lifecycleScope.launch {
    viewModel.getFoodItemsByCategory("蔬菜").collect { items ->
        adapter.submitList(items)
    }
}
```

### 添加新的广播接收器

```kotlin
// 1. 创建接收器
class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 处理 BOOT_COMPLETED
    }
}

// 2. 在 AndroidManifest.xml 中注册
<receiver android:name=".BootCompleteReceiver">
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
    </intent-filter>
</receiver>
```

## 性能优化

### 1. Room 查询优化

```kotlin
// ❌ 不好: 每次都查询全部
val allItems = dao.getAllFoodItems()

// ✅ 好: 使用 Flow，只在有变化时更新
val allItems: Flow<List<FoodItem>> = dao.getAllFoodItems()
```

### 2. 列表渲染优化

```kotlin
// ✅ DiffUtil 自动计算差异
class FoodDiffCallback : DiffUtil.ItemCallback<FoodItem>() {
    override fun areItemsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
        return oldItem == newItem
    }
}
```

### 3. 内存管理

```kotlin
// ✅ 使用 Lifecycle 感知的作用域
lifecycleScope.launch {
    // 生命周期结束时自动取消
}

// ❌ 避免全局作用域
GlobalScope.launch {
    // 不会自动取消
}
```

## 安全性考虑

### 1. 权限检查

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    if (ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED) {
        // 有权限，继续
    } else {
        // 请求权限
    }
}
```

### 2. Intent 安全

```kotlin
// ✅ 使用 FLAG_IMMUTABLE
val pendingIntent = PendingIntent.getBroadcast(
    context,
    foodId,
    intent,
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
)
```

### 3. 异常处理

```kotlin
try {
    alarmManager.setExactAndAllowWhileIdle(...)
} catch (e: SecurityException) {
    // 降级处理
    alarmManager.setAndAllowWhileIdle(...)
}
```

## 测试策略

### 单元测试

```kotlin
// ViewModel 测试
@Test
fun testInsertFoodItem() {
    val foodItem = FoodItem(name = "test", expiryTime = System.currentTimeMillis())
    viewModel.insertFoodItem(foodItem)
    
    // 验证
    assertTrue(viewModel.allFoodItems.value.contains(foodItem))
}
```

### UI 测试

```kotlin
// Espresso 测试
@Test
fun testAddFoodFlow() {
    onView(withId(R.id.btnAdd)).perform(click())
    onView(withId(R.id.editTextFoodName)).perform(typeText("Milk"))
    onView(withText("保存")).perform(click())
    
    onView(withText("Milk")).check(matches(isDisplayed()))
}
```

## 持续改进

### 已知限制
1. 无法编辑已添加的食品
2. 无法批量操作
3. 无法自定义通知样式
4. 无法设置周期性提醒

### 未来改进
1. ✅ 添加编辑功能
2. ✅ 批量删除
3. ✅ 自定义分类
4. ✅ 数据备份和恢复
5. ✅ 云同步
6. ✅ 条形码扫描

## 参考资源

- [Android Architecture Components](https://developer.android.com/topic/architecture)
- [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Material Design 3](https://m3.material.io/)
