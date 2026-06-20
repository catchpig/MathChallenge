# UI 改进：隐藏状态栏 + 答题页返回按钮

**日期**：2026-06-20
**范围**：MainActivity、QuestionScreen、AppNavigation

---

## 1. 需求

1. **隐藏状态栏**：App 运行时隐藏系统状态栏，实现全屏沉浸式体验。
2. **答题页返回按钮**：`QuestionScreen` 是唯一缺少返回按钮的二级页面，需补充。

其余二级页面（ChapterScreen、HonorScreen、ResultScreen）已有返回入口，无需修改。

---

## 2. 状态栏隐藏

### 实现位置

`MainActivity.onCreate`，在 `setContent` 之前调用。

### 方案

```kotlin
WindowCompat.setDecorFitsSystemWindows(window, false)
val controller = WindowInsetsControllerCompat(window, window.decorView)
controller.hide(WindowInsetsCompat.Type.statusBars())
controller.systemBarsBehavior =
    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
```

- `setDecorFitsSystemWindows(false)` — 内容延伸至全屏，不留状态栏占位。
- `hide(statusBars())` — 隐藏状态栏。
- `BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE` — 从顶部下滑可临时唤出状态栏，符合 Android 系统 UX 规范。
- **不隐藏导航栏**，保留底部系统导航按钮。

### 依赖

需要 `androidx.core:core-ktx`（项目已有）中的 `WindowCompat`、`WindowInsetsControllerCompat`、`WindowInsetsCompat`。

---

## 3. QuestionScreen 返回按钮

### 签名变更

```kotlin
// 原
fun QuestionScreen(packId, dimension, levelId, onFinish)

// 新增 onBack 参数
fun QuestionScreen(packId, dimension, levelId, onFinish, onBack)
```

### 按钮位置

放置在左侧题目面板顶部的渐变色彩条（已有维度图标 + 进度点的那一行）左侧：

```
[ ← ]  [ 🧩 逻辑思维 ]          [ • • ● ○ ○  3/5 ]
```

使用与 ChapterScreen 一致的样式：`CircleShape` 半透明白色背景 + `ArrowBack` 图标。

### 行为

点击直接触发 `onBack()`，无确认弹窗。如果用户误触，可重新进入关卡重做（不破坏已有最高星级记录）。

### AppNavigation 变更

`AppNavigation.kt` 中 `QuestionScreen` 的 composable 调用增加：

```kotlin
onBack = { navController.popBackStack() }
```

---

## 4. 改动文件

| 文件 | 改动内容 |
|------|---------|
| `MainActivity.kt` | 添加 WindowInsetsController 隐藏状态栏（3 行） |
| `ui/question/QuestionScreen.kt` | 新增 `onBack` 参数，顶部色彩条左侧添加返回按钮 |
| `ui/navigation/AppNavigation.kt` | 传入 `onBack = { navController.popBackStack() }` |

---

## 5. 不在范围内

- 隐藏导航栏（用户未要求）
- 修改其他二级页面的返回按钮样式
- 答题中途退出的确认弹窗（用户选择直接返回）
