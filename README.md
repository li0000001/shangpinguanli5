# 食品保质期提醒应用 (Food Expiry Reminder)

一个完整的 Android 应用，用于管理食品的保质期并在食品过期时发送提醒通知。

## 功能特性

### 主要功能
- ✅ **添加食品**: 输入食品名称和过期时间
- ✅ **食品列表**: 显示所有添加的食品及其过期状态
- ✅ **过期提醒**: 使用 AlarmManager 在食品过期时发送通知
- ✅ **日历同步**: 可选择将食品过期事件同步到系统日历
- ✅ **删除食品**: 从列表中删除食品并取消相关闹钟

### 技术亮点
- **AlarmManager.setExactAndAllowWhileIdle()**: 在国产 ROM（vivo、小米、华为等）上稳定提醒
- **Room 数据库**: 本地持久化存储食品信息
- **Material Design**: 现代化的 UI 设计
- **Kotlin Coroutines**: 异步操作和数据流处理
- **动态权限**: 自动申请通知和日历权限
- **后台保活**: 支持 App 被杀后台后仍能准时提醒

## 项目结构

```
app/
├── src/main/
│   ├── kotlin/com/example/foodexpiryreminder/
│   │   ├── MainActivity.kt              # 主界面 Activity
│   │   ├── data/
│   │   │   ├── FoodItem.kt             # 数据实体
│   │   │   ├── FoodItemDao.kt          # Room DAO
│   │   │   ├── AppDatabase.kt          # Room 数据库
│   │   │   └── FoodRepository.kt       # 数据仓库层
│   │   ├── ui/
│   │   │   ├── FoodViewModel.kt        # ViewModel
│   │   │   └── FoodAdapter.kt          # RecyclerView 适配器
│   │   ├── alarm/
│   │   │   ├── ExpiryAlarmManager.kt   # 闹钟管理器
│   │   │   └── ExpiryAlarmReceiver.kt  # 闹钟广播接收器
│   │   └── notification/
│   │       └── NotificationHelper.kt   # 通知助手
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_main.xml       # 主界面布局
│   │   │   ├── item_food.xml           # 列表项布局
│   │   │   └── dialog_add_food.xml     # 添加食品对话框
│   │   ├── values/
│   │   │   ├── colors.xml
│   │   │   ├── strings.xml
│   │   │   └── themes.xml
│   │   ├── drawable/
│   │   │   ├── ic_launcher_foreground.xml
│   │   │   └── ic_notification.xml
│   │   └── xml/
│   │       ├── backup_rules.xml
│   │       └── data_extraction_rules.xml
│   └── AndroidManifest.xml
├── build.gradle.kts                    # 应用级 build 配置
└── proguard-rules.pro                  # ProGuard 混淆规则

build.gradle.kts                        # 项目级 build 配置
settings.gradle.kts                     # 项目设置
```

## 使用说明

### 编译和运行

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd FoodExpiryReminder
   ```

2. **在 Android Studio 中打开项目**
   - 使用 Android Studio 3.6+ 或更高版本
   - 选择 File > Open 并选择项目目录

3. **构建和运行**
   ```bash
   # 构建
   ./gradlew build
   
   # 在连接的设备或模拟器上运行
   ./gradlew installDebug
   ```

### 应用使用

1. **添加食品**
   - 点击主界面的"添加食品"按钮
   - 输入食品名称
   - 点击"选择过期时间"选择日期和时间
   - 点击"保存"按钮

2. **查看食品列表**
   - 所有添加的食品以卡片形式显示
   - 每个卡片显示：食品名称、过期时间、剩余天数/已过期状态

3. **同步到系统日历**
   - 点击食品卡片上的"同步到日历"按钮
   - 系统日历会打开，允许用户添加更多细节
   - 用户可设置额外提醒

4. **删除食品**
   - 点击食品卡片上的"删除"按钮
   - 确认删除后，食品及其闹钟将被移除

## 权限说明

应用需要以下权限：

| 权限 | 目的 | 强制性 |
|------|------|--------|
| `POST_NOTIFICATIONS` | 发送过期提醒通知 | 是 (Android 13+) |
| `SCHEDULE_EXACT_ALARM` | 设置精确闹钟 | 是 |
| `READ_CALENDAR` | 读取日历 | 否 |
| `WRITE_CALENDAR` | 写入日历 | 否 |

## 兼容性

- **最低 API 级别**: 26 (Android 8.0)
- **目标 API 级别**: 34 (Android 14)
- **国产 ROM 支持**: vivo/iQOO、小米、OPPO、华为等
- **后台运行**: 支持在 App 被杀后台后仍能准时提醒

## 技术栈

- **语言**: Kotlin
- **UI 框架**: Material Design 3
- **数据库**: Room
- **异步**: Kotlin Coroutines + Flow
- **架构**: MVVM + Repository Pattern
- **通知**: NotificationCompat
- **系统集成**: AlarmManager + Calendar Provider

## 关键实现细节

### 1. AlarmManager 设置

```kotlin
// Android 12+ 优先使用 setExactAndAllowWhileIdle
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    if (alarmManager.canScheduleExactAlarms()) {
        alarmManager.setExactAndAllowWhileIdle(...)
    } else {
        alarmManager.setAndAllowWhileIdle(...)
    }
} else {
    alarmManager.setExactAndAllowWhileIdle(...)
}
```

### 2. 通知频道配置

通知频道设置为 `IMPORTANCE_HIGH`，确保通知能被用户看到。

### 3. Room 数据库

使用单例模式确保应用中只有一个数据库实例。

### 4. 权限处理

- 动态申请通知权限 (Android 13+)
- 在 AndroidManifest.xml 中声明所有必要权限

## 故障排查

### 问题 1: 通知无法显示
- **原因**: 未授予通知权限
- **解决**: 在应用设置中启用"通知"权限

### 问题 2: 提醒时间不准确
- **原因**: 系统时间异常或低电量模式干扰
- **解决**: 检查系统时间设置，禁用过激进的电池优化

### 问题 3: 后台提醒不工作
- **原因**: App 被系统完全杀死且没有恢复闹钟
- **解决**: 
  - 确保设备没有过度限制 App 的后台活动
  - 在设备设置中将应用设为"不被优化"

### 问题 4: 国产 ROM 提醒失效
- **原因**: 国产 ROM 的电源管理策略
- **解决**: 
  - 在手机设置中将应用加入白名单
  - 检查通知权限是否启用
  - 确保应用有"显示在锁屏上"权限

## 依赖版本

- compileSdk: 34
- minSdk: 26
- targetSdk: 34
- Kotlin: 1.9.10
- AndroidX AppCompat: 1.6.1
- Material Components: 1.11.0
- Room: 2.6.1
- Lifecycle: 2.6.2

## 许可证

此项目基于 MIT 许可证开源。

## 贡献

欢迎提交 Issue 和 Pull Request！

## 常见问题 (FAQ)

**Q: App 被杀后台后能否继续提醒?**
A: 能够。我们使用 AlarmManager 的 RTC_WAKEUP 模式，它会在需要时唤醒系统。

**Q: 支持多个食品吗?**
A: 支持。每个食品都有独立的数据库记录和闹钟。

**Q: 可以修改食品的过期时间吗?**
A: 当前版本需要删除后重新添加。未来版本可考虑添加编辑功能。

**Q: 为什么通知在某些手机上不显示?**
A: 可能是因为：
1. 未授予通知权限
2. 应用被系统限制
3. 自定义 ROM 的通知设置
