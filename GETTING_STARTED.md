# 项目导览指南

欢迎使用食品保质期提醒应用！本文档将帮助你快速了解整个项目。

## 📚 文档导航

根据你的需求选择相应的文档：

### 👤 对于应用用户
**想要了解如何使用应用？** → 查看 [QUICKSTART.md](QUICKSTART.md)
- 5分钟快速上手
- 常见问题解答
- IDE 快捷键

### 👨‍💻 对于开发者
**想要了解项目结构和源代码？** → 查看 [ARCHITECTURE.md](ARCHITECTURE.md)
- 整体架构设计
- 各层职责说明
- 数据流程图
- 性能优化建议

### 📖 对于产品经理
**想要了解完整功能和技术细节？** → 查看 [README.md](README.md)
- 功能列表
- 技术栈
- 兼容性信息
- 权限说明

### 🎯 对于测试人员
**想要了解具体使用场景？** → 查看 [USAGE_EXAMPLES.md](USAGE_EXAMPLES.md)
- 8 个完整使用场景
- 数据库查询示例
- 完整工作流示例

## 🏗️ 项目结构速览

```
FoodExpiryReminder/
├── app/                          # 应用模块
│   ├── src/main/
│   │   ├── kotlin/              # Kotlin 源代码
│   │   │   └── com/example/foodexpiryreminder/
│   │   │       ├── MainActivity.kt            # 主界面
│   │   │       ├── data/                      # Room 数据库层
│   │   │       ├── ui/                        # UI 层 (ViewModel, Adapter)
│   │   │       ├── alarm/                     # AlarmManager 相关
│   │   │       └── notification/              # 通知相关
│   │   └── res/                  # 资源文件
│   │       ├── layout/           # XML 布局
│   │       ├── drawable/         # 图片和矢量图
│   │       ├── values/           # 颜色、字符串等
│   │       ├── xml/              # 配置文件
│   │       └── mipmap/           # 应用图标
│   ├── build.gradle.kts          # 应用构建配置
│   └── proguard-rules.pro        # 混淆规则
│
├── build.gradle.kts              # 项目级构建配置
├── settings.gradle.kts           # 项目设置
├── .gitignore                    # Git 忽略文件
│
├── README.md                     # 完整项目文档
├── QUICKSTART.md                 # 快速开始指南
├── ARCHITECTURE.md               # 架构设计文档
├── USAGE_EXAMPLES.md             # 使用场景示例
└── GETTING_STARTED.md            # 本文件
```

## 🚀 快速开始

### 1️⃣ 环境准备

```bash
# 检查 Java 版本（需要 JDK 17+）
java -version

# 检查 Gradle（会自动下载）
# 无需手动安装
```

### 2️⃣ 克隆和打开项目

```bash
# 克隆项目
git clone <repository-url>
cd FoodExpiryReminder

# 在 Android Studio 中打开
# File → Open → 选择项目目录
```

### 3️⃣ 运行项目

```bash
# 方式一：Android Studio
# 1. 连接设备或启动模拟器
# 2. 点击 ▶ Run 按钮

# 方式二：命令行
./gradlew installDebug
adb shell am start -n com.example.foodexpiryreminder/.MainActivity
```

## 📋 关键文件速查

### Kotlin 源文件

| 文件 | 职责 | 关键方法 |
|------|------|--------|
| `MainActivity.kt` | 主界面逻辑 | `showAddFoodDialog()`, `deleteFood()` |
| `FoodViewModel.kt` | 业务逻辑 | `insertFoodItem()`, `deleteFoodItem()` |
| `FoodAdapter.kt` | 列表适配器 | `bind()`, `FoodDiffCallback` |
| `FoodItem.kt` | 数据实体 | 无，数据类 |
| `AppDatabase.kt` | 数据库 | `getDatabase()` |
| `FoodItemDao.kt` | 数据访问 | `getAllFoodItems()`, `insertFoodItem()` |
| `ExpiryAlarmManager.kt` | 闹钟管理 | `scheduleExpiryAlarm()`, `cancelExpiryAlarm()` |
| `ExpiryAlarmReceiver.kt` | 广播接收 | `onReceive()` |
| `NotificationHelper.kt` | 通知管理 | `showExpiryNotification()` |

### XML 布局文件

| 文件 | 用途 |
|------|------|
| `activity_main.xml` | 主界面布局 |
| `item_food.xml` | 列表项卡片 |
| `dialog_add_food.xml` | 添加食品对话框 |

### 配置文件

| 文件 | 说明 |
|------|------|
| `AndroidManifest.xml` | 应用清单 |
| `build.gradle.kts` | 构建配置 |
| `colors.xml` | 颜色定义 |
| `strings.xml` | 字符串资源 |
| `themes.xml` | 主题定义 |

## 🔧 常见开发任务

### 添加新功能

1. **在数据模型中添加字段**
   ```kotlin
   // app/src/main/kotlin/.../data/FoodItem.kt
   data class FoodItem(
       // ... 现有字段
       val category: String = ""  // 新增字段
   )
   ```

2. **更新数据库访问**
   ```kotlin
   // app/src/main/kotlin/.../data/FoodItemDao.kt
   @Query("SELECT * FROM food_items WHERE category = :category")
   fun getFoodItemsByCategory(category: String): Flow<List<FoodItem>>
   ```

3. **更新 ViewModel**
   ```kotlin
   // 在 FoodViewModel 中添加新方法
   ```

4. **更新 UI**
   ```kotlin
   // 在 MainActivity 中添加 UI 逻辑
   ```

### 修改布局

1. **编辑 XML 文件**
   ```xml
   // app/src/main/res/layout/activity_main.xml
   ```

2. **在代码中使用**
   ```kotlin
   // ViewBinding 会自动生成绑定类
   binding.yourView.text = "..."
   ```

### 调整样式

1. **修改颜色**
   ```xml
   <!-- app/src/main/res/values/colors.xml -->
   <color name="custom_color">#FF6200EE</color>
   ```

2. **修改字符串**
   ```xml
   <!-- app/src/main/res/values/strings.xml -->
   <string name="custom_string">自定义文本</string>
   ```

## 🐛 调试技巧

### 查看应用日志

```bash
# 过滤应用日志
adb logcat | grep FoodExpiryReminder

# 查看崩溃堆栈
adb logcat | grep -i "crash\|exception"

# 保存日志到文件
adb logcat > logs.txt
```

### 访问应用数据

```bash
# 进入应用数据目录
adb shell
cd /data/data/com.example.foodexpiryreminder/

# 列出数据库
ls -la databases/

# 提取数据库
adb pull /data/data/com.example.foodexpiryreminder/databases/food_expiry_db .
```

### 测试闹钟功能

```kotlin
// 设置闹钟为 1 分钟后
val futureTime = System.currentTimeMillis() + (1 * 60 * 1000)
val foodItem = FoodItem(name = "test", expiryTime = futureTime)
alarmManager.scheduleExpiryAlarm(foodItem)
```

## 📊 应用统计

| 指标 | 数值 |
|------|------|
| Kotlin 代码行数 | ~800 |
| XML 资源行数 | ~600 |
| 数据库表数 | 1 |
| Activity 数 | 1 |
| BroadcastReceiver 数 | 1 |
| 通知频道数 | 1 |
| 最小 API 级别 | 26 |
| 目标 API 级别 | 34 |

## 🎓 学习资源

### Android 官方文档
- [Architecture Components](https://developer.android.com/topic/architecture)
- [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- [AlarmManager](https://developer.android.com/reference/android/app/AlarmManager)
- [NotificationCompat](https://developer.android.com/reference/androidx/core/app/NotificationCompat)

### Kotlin 资源
- [Kotlin 官方文档](https://kotlinlang.org/docs/home.html)
- [Kotlin Coroutines 指南](https://kotlinlang.org/docs/coroutines-overview.html)

### 本项目相关
- [Gradle 构建系统](https://gradle.org/)
- [Material Design 3](https://m3.material.io/)

## ❓ 常见问题

**Q: 如何修改应用名称？**
A: 编辑 `app/src/main/res/values/strings.xml` 中的 `app_name`

**Q: 如何修改应用图标？**
A: 替换 `app/src/main/res/mipmap-*/ic_launcher*.xml` 或相应的 PNG 文件

**Q: 如何添加新权限？**
A: 在 `app/src/main/AndroidManifest.xml` 中添加 `<uses-permission>` 标签

**Q: 项目编译失败怎么办？**
A: 查看 [QUICKSTART.md](QUICKSTART.md) 中的故障排查部分

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📞 获取帮助

- 查看相应的文档
- 检查 [USAGE_EXAMPLES.md](USAGE_EXAMPLES.md) 中的类似场景
- 查看应用日志 (`adb logcat`)

---

**祝你开发愉快！** 🎉

有任何问题？请参考各文档或提交 Issue！
