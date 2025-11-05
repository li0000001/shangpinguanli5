# 贡献指南

感谢你有兴趣为食品保质期提醒应用做出贡献！

## 前置要求

- Java 17 或更高版本
- Android Studio 最新版本
- Git
- 基础的 Android 开发知识

## 开发环境设置

### 1. 克隆项目

```bash
git clone <repository-url>
cd FoodExpiryReminder
```

### 2. 同步 Gradle

```bash
./gradlew sync
# 或在 Android Studio 中点击 "Sync Now"
```

### 3. 构建项目

```bash
./gradlew build
```

### 4. 运行测试

```bash
./gradlew test
```

## 开发工作流

### 创建分支

基于分支名称前缀创建分支：

```bash
# 新功能
git checkout -b feature/my-new-feature

# 错误修复
git checkout -b fix/bug-description

# 文档更新
git checkout -b docs/documentation-update

# 代码重构
git checkout -b refactor/component-refactoring

# 性能优化
git checkout -b perf/optimization-description
```

### 编码约定

#### Kotlin 代码风格

1. **命名规范**
   ```kotlin
   // ✅ 好的做法
   class FoodItemAdapter
   fun insertFoodItem()
   val allFoodItems: StateFlow<List<FoodItem>>
   private val alarmManager: AlarmManager
   
   // ❌ 不好的做法
   class fooditemadapter
   fun insertFooditem()
   val ALL_FOOD_ITEMS
   ```

2. **类和接口**
   ```kotlin
   // 一个类一个文件（除非是 data class）
   // 文件名应与类名匹配
   class MainActivity : AppCompatActivity()  // ✅ 文件名: MainActivity.kt
   ```

3. **函数长度**
   - 保持函数简短，单一职责
   - 如果超过 50 行，考虑分解函数

4. **注释**
   ```kotlin
   // 复杂逻辑前添加注释
   // 解释"为什么"，而不是"是什么"
   
   // ✅ 好的注释
   // RTC_WAKEUP 会唤醒设备，确保在后台也能收到提醒
   alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, ...)
   
   // ❌ 不好的注释
   // 设置闹钟
   alarmManager.setExactAndAllowWhileIdle(...)
   ```

5. **使用 Kotlin 特性**
   ```kotlin
   // ✅ 使用 data class
   data class FoodItem(val id: Int, val name: String)
   
   // ❌ 避免冗长的 getter/setter
   class FoodItem {
       var id: Int = 0
       var name: String = ""
   }
   
   // ✅ 使用 scope function
   intent.apply {
       putExtra("FOOD_ID", foodItem.id)
       putExtra("FOOD_NAME", foodItem.name)
   }
   
   // ❌ 避免重复
   intent.putExtra("FOOD_ID", foodItem.id)
   intent.putExtra("FOOD_NAME", foodItem.name)
   ```

#### XML 资源风格

1. **布局文件**
   ```xml
   <!-- 使用有意义的 ID -->
   android:id="@+id/tvFoodName"      ✅ tv 前缀表示 TextView
   android:id="@+id/btnAdd"          ✅ btn 前缀表示 Button
   android:id="@+id/recyclerView"    ✅ recyclerView 前缀
   
   <!-- 避免 -->
   android:id="@+id/view1"           ❌ 无意义
   android:id="@+id/foodname"        ❌ 命名不规范
   ```

2. **资源命名**
   - 颜色: `colors.xml`
   - 字符串: `strings.xml`
   - 尺寸: `dimens.xml`
   - 主题: `themes.xml`

### 提交消息格式

遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

```
<type>(<scope>): <subject>

<body>

<footer>
```

#### 示例

```
feat(alarm): 添加闹钟重新调度功能

在应用启动时重新调度所有已过期的闹钟，确保
即使应用被杀也能继续提醒。

Closes #123
```

#### 类型

- `feat`: 新功能
- `fix`: 错误修复
- `docs`: 文档更新
- `style`: 代码风格调整（不改变功能）
- `refactor`: 代码重构
- `perf`: 性能优化
- `test`: 添加或更新测试
- `chore`: 依赖更新或其他维护性改动

### 代码审查

#### 提交 Pull Request 前的检查清单

- [ ] 代码遵循项目代码风格
- [ ] 添加了必要的单元测试
- [ ] 更新了相关文档
- [ ] 提交消息清晰有描述
- [ ] 没有调试代码或注释掉的代码
- [ ] 在本地测试通过
- [ ] 没有新的警告或错误

#### 创建 Pull Request

1. **标题清晰**
   ```
   ✅ Add notification permission request for Android 13+
   ❌ Fix stuff
   ```

2. **描述详细**
   - 修复的问题/添加的功能
   - 如何测试
   - 截图或视频（如适用）

3. **关联 Issue**
   ```
   Closes #123
   Related to #456
   ```

## 测试指南

### 单元测试

在 `app/src/test` 中创建测试：

```kotlin
class FoodViewModelTest {
    private lateinit var viewModel: FoodViewModel
    private lateinit var repository: FoodRepository
    
    @Before
    fun setup() {
        repository = mockk()
        viewModel = FoodViewModel(repository)
    }
    
    @Test
    fun testInsertFoodItem() {
        val foodItem = FoodItem(name = "test", expiryTime = 0L)
        viewModel.insertFoodItem(foodItem)
        
        coVerify { repository.insertFoodItem(foodItem) }
    }
}
```

### UI 测试

在 `app/src/androidTest` 中创建测试：

```kotlin
class MainActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    
    @Test
    fun testAddFoodFlow() {
        onView(withId(R.id.btnAdd)).perform(click())
        onView(withId(R.id.editTextFoodName)).perform(typeText("Milk"))
        onView(withText("保存")).perform(click())
        
        onView(withText("Milk")).check(matches(isDisplayed()))
    }
}
```

### 手动测试

关键场景清单：

- [ ] 应用首次启动
- [ ] 添加食品
- [ ] 删除食品
- [ ] 同步到日历
- [ ] 接收通知
- [ ] 权限请求
- [ ] 不同 Android 版本兼容性

## 文档更新

当提交代码更改时，也请更新相关文档：

- **新功能**: 更新 `README.md` 和 `USAGE_EXAMPLES.md`
- **架构变更**: 更新 `ARCHITECTURE.md`
- **使用步骤变更**: 更新 `QUICKSTART.md`
- **API 变更**: 添加内联代码注释

## 常见贡献场景

### 场景 1: 添加新功能

1. 创建 feature 分支
2. 实现功能
   - 编写数据模型
   - 更新 DAO
   - 更新 Repository
   - 更新 ViewModel
   - 更新 UI
3. 编写测试
4. 更新文档
5. 提交 PR

### 场景 2: 修复 Bug

1. 创建 fix 分支
2. 编写测试（应该失败）
3. 修复 bug（测试通过）
4. 运行完整测试套件
5. 更新文档（如需要）
6. 提交 PR

### 场景 3: 性能优化

1. 测量性能问题
2. 创建 perf 分支
3. 实现优化
4. 测量改进
5. 编写文档说明优化
6. 提交 PR

### 场景 4: 文档改进

1. 创建 docs 分支
2. 编辑文档文件
3. 验证 Markdown 格式
4. 提交 PR

## 构建和测试命令

```bash
# 清理
./gradlew clean

# 构建
./gradlew build

# 运行单元测试
./gradlew test

# 运行 UI 测试
./gradlew connectedAndroidTest

# 检查代码覆盖率
./gradlew jacocoTestReport

# 构建 Release APK
./gradlew assembleRelease

# 运行代码分析
./gradlew lint
```

## 性能指南

### 内存优化
- 使用 Lifecycle 感知的作用域
- 避免内存泄漏（取消 Coroutine）
- 使用 `by lazy` 延迟初始化

### 数据库优化
- 使用 Flow 而不是多次查询
- 使用索引加快查询
- 批量操作时使用 Transaction

### UI 优化
- 使用 DiffUtil 优化列表更新
- 避免在主线程进行重操作
- 使用 ViewBinding 而不是 findViewById

## 安全最佳实践

### 权限处理
```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VERSION) {
    // 版本特定代码
}
```

### 数据安全
- 使用加密存储敏感数据
- 验证用户输入
- 使用 HTTPS 进行网络通信

### 代码安全
- 使用 FLAG_IMMUTABLE for PendingIntent
- 验证 Intent 数据
- 不在日志中输出敏感信息

## 发布流程

### 发布前检查

- [ ] 所有测试通过
- [ ] 代码审查完成
- [ ] 文档更新
- [ ] 版本号更新
- [ ] CHANGELOG 更新
- [ ] 没有已知 bug

### 版本号规范

使用 [Semantic Versioning](https://semver.org/)：

```
MAJOR.MINOR.PATCH
1.2.3

1: 主版本（破坏性变更）
2: 次版本（新功能）
3: 修订版本（bug 修复）
```

## 获取帮助

- 查看现有 Issue 和 PR
- 查看项目文档
- 提交 Issue 询问问题
- 在讨论区交流想法

## 行为准则

我们遵循 [Contributor Covenant Code of Conduct](https://www.contributor-covenant.org/) 行为准则。

简而言之：
- 尊重他人
- 接受建设性批评
- 关注最有利于社区的事情
- 对他人表示同情

## 许可证

通过贡献此项目，你同意你的贡献在 MIT 许可证下发布。

---

感谢你的贡献！🎉
