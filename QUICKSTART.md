# 快速开始指南

## 环境要求

- **Java 版本**: JDK 17 或更高版本
- **Android Studio**: 最新版本 (建议 Giraffe 或更新)
- **Android SDK**: API 34 (Android 14)
- **Gradle**: 8.0 或更高版本

## 5 分钟快速上手

### 1. 导入项目

```bash
# 方式一：使用 Git 克隆
git clone <repository-url>
cd FoodExpiryReminder

# 方式二：在 Android Studio 中
File → Open → 选择项目目录
```

### 2. 同步 Gradle

- 在 Android Studio 中，点击菜单 `File → Sync Now`
- 或在终端运行：
  ```bash
  ./gradlew sync
  ```

### 3. 运行应用

**方式一：使用 Android Studio**
1. 连接 Android 设备或启动模拟器
2. 点击工具栏的绿色 ▶ Run 按钮
3. 选择目标设备

**方式二：使用命令行**
```bash
# 构建并运行
./gradlew installDebug

# 启动应用
adb shell am start -n com.example.foodexpiryreminder/.MainActivity
```

### 4. 首次使用

1. **授予权限**
   - App 首次启动会申请通知权限
   - 点击"允许"授予权限

2. **添加食品**
   - 点击"添加食品"按钮
   - 输入食品名称，如 "牛奶"
   - 点击"选择过期时间"
   - 选择日期和时间（建议选择未来的时间以便测试）
   - 点击"保存"

3. **查看列表**
   - 添加的食品会显示在列表中
   - 显示过期时间和剩余天数

4. **测试提醒**
   - 选择一个很快过期的时间（例如 5 分钟后）
   - 等待到达时间时，会看到系统通知
   - 点击通知可打开应用

## 项目构建

### 构建变体

```bash
# 构建 Debug 版本
./gradlew assembleDebug

# 构建 Release 版本
./gradlew assembleRelease

# 构建所有版本
./gradlew assemble
```

### 构建输出

- Debug APK: `app/build/outputs/apk/debug/`
- Release APK: `app/build/outputs/apk/release/`

## 常见问题

### Q1: 编译时出现 "Gradle sync failed"

**解决方案**：
```bash
# 清理 Gradle 缓存
./gradlew clean

# 重新同步
./gradlew sync
```

### Q2: "android.permission.SCHEDULE_EXACT_ALARM" 错误

**解决方案**：
- 确保 targetSdk ≥ 30
- 在 AndroidManifest.xml 中已声明此权限
- 在 build.gradle.kts 中 targetSdk 设置为 34

### Q3: 应用启动时崩溃

**解决方案**：
```bash
# 查看崩溃日志
adb logcat | grep -i crash

# 清除应用数据后重新启动
adb shell pm clear com.example.foodexpiryreminder
```

### Q4: 提醒不工作

**检查清单**：
1. ✅ 已授予通知权限
2. ✅ 已授予设置闹钟权限
3. ✅ 设备时间正确
4. ✅ App 未被杀死或被限制

### Q5: 如何卸载应用

```bash
adb uninstall com.example.foodexpiryreminder
```

## 开发技巧

### 启用 Debug 模式

在 `MainActivity.kt` 中添加日志：
```kotlin
private val TAG = "FoodExpiryReminder"

Log.d(TAG, "Food added: ${foodItem.name}")
```

查看日志：
```bash
adb logcat | grep "FoodExpiryReminder"
```

### 测试提醒功能

为了快速测试，可以修改过期时间为 1 分钟后：
```kotlin
val futureTime = System.currentTimeMillis() + (1 * 60 * 1000) // 1 分钟后
```

### 访问应用数据

应用的 Room 数据库存储在：
```
/data/data/com.example.foodexpiryreminder/databases/food_expiry_db
```

使用 Android Device File Explorer 查看。

## IDE 快捷键

| 快捷键 | 功能 |
|--------|------|
| `Ctrl+Shift+F10` | 运行当前项目 |
| `Ctrl+K` | 提交代码 |
| `Ctrl+Shift+K` | 推送代码 |
| `Ctrl+Shift+L` | 格式化代码 |
| `Ctrl+O` | 重写方法 |
| `Ctrl+I` | 实现接口 |

## 下一步

- 阅读 [README.md](README.md) 了解完整项目信息
- 查看源代码学习 MVVM 架构
- 探索如何扩展功能：
  - 添加编辑功能
  - 支持周期性提醒
  - 添加数据导入/导出功能
  - 集成云端备份

## 获取帮助

- 检查 [README.md](README.md) 中的故障排查部分
- 查看应用日志：`adb logcat`
- 查看 build 输出：`./gradlew build -i`

祝你使用愉快！
