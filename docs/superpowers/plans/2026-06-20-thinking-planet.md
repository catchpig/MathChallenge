# 思维星球 (Thinking Planet) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a girl-themed Android tablet app for primary school students (Grade 1–6) to practice five thinking dimensions through gamified adventure worlds with a 4-layer honor system.

**Architecture:** Clean Architecture + MVVM. Domain models and use cases are pure Kotlin. Room handles persistence. Jetpack Compose renders a tablet-landscape UI split into left (question) and right (options) columns.

**Tech Stack:** Kotlin 2.0, Jetpack Compose (BOM 2024.09.00), Room 2.6.1, Hilt 2.51.1, Navigation Compose 2.8.0, Kotlinx Serialization 1.7.3, Lottie 6.5.2

**Pre-requisite:** Create a new Android Studio project — "Empty Activity", package `com.thinkingplanet`, min SDK 26, Kotlin DSL. Lock orientation to landscape in `AndroidManifest.xml`.

---

## File Map

```
app/
├── build.gradle.kts
├── src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/thinkingplanet/
│   │   ├── ThinkingPlanetApp.kt
│   │   ├── MainActivity.kt
│   │   ├── domain/model/
│   │   │   ├── World.kt
│   │   │   ├── Question.kt
│   │   │   └── Honor.kt
│   │   ├── domain/repository/
│   │   │   ├── QuestionRepository.kt
│   │   │   └── ProgressRepository.kt
│   │   ├── domain/usecase/
│   │   │   ├── GetWorldsUseCase.kt
│   │   │   ├── CalculateStarsUseCase.kt
│   │   │   ├── UpdateMedalUseCase.kt
│   │   │   └── UpdateTrophyUseCase.kt
│   │   ├── data/local/
│   │   │   ├── AppDatabase.kt
│   │   │   ├── entity/ (4 entity files)
│   │   │   └── dao/   (4 DAO files)
│   │   ├── data/repository/
│   │   │   ├── QuestionRepositoryImpl.kt
│   │   │   └── ProgressRepositoryImpl.kt
│   │   ├── data/source/
│   │   │   └── QuestionPackLoader.kt
│   │   ├── di/
│   │   │   ├── AppModule.kt
│   │   │   └── DatabaseModule.kt
│   │   └── ui/
│   │       ├── theme/Color.kt
│   │       ├── theme/Theme.kt
│   │       ├── navigation/AppNavigation.kt
│   │       ├── worldmap/WorldMapScreen.kt
│   │       ├── worldmap/WorldMapViewModel.kt
│   │       ├── chapter/ChapterScreen.kt
│   │       ├── chapter/ChapterViewModel.kt
│   │       ├── question/QuestionScreen.kt
│   │       ├── question/QuestionViewModel.kt
│   │       ├── result/ResultScreen.kt
│   │       └── honor/HonorScreen.kt
│   └── assets/packs/
│       ├── grade1_upper.json
│       └── grade1_lower.json
└── src/test/java/com/thinkingplanet/
    ├── usecase/CalculateStarsUseCaseTest.kt
    ├── usecase/UpdateMedalUseCaseTest.kt
    └── source/QuestionPackLoaderTest.kt
```

---

## Task 1: Gradle Dependencies

**Files:**
- Modify: `app/build.gradle.kts`
- Modify: `build.gradle.kts` (project root)
- Modify: `gradle/libs.versions.toml`

- [ ] **Step 1: Update `gradle/libs.versions.toml`**

```toml
[versions]
kotlin = "2.0.0"
composeBom = "2024.09.00"
room = "2.6.1"
hilt = "2.51.1"
navigationCompose = "2.8.0"
hiltNavigationCompose = "1.2.0"
kotlinxSerialization = "1.7.3"
lottie = "6.5.2"
ksp = "2.0.0-1.0.24"

[libraries]
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
compose-animation = { group = "androidx.compose.animation", name = "animation" }
activity-compose = { group = "androidx.activity", name = "activity-compose", version = "1.9.2" }
lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version = "2.8.5" }
navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hiltNavigationCompose" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinxSerialization" }
lottie-compose = { group = "com.airbnb.android", name = "lottie-compose", version.ref = "lottie" }
junit = { group = "junit", name = "junit", version = "4.13.2" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version = "1.8.1" }

[plugins]
android-application = { id = "com.android.application", version = "8.5.2" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

- [ ] **Step 2: Update `app/build.gradle.kts`**

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.thinkingplanet"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.thinkingplanet"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.animation)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.navigation.compose)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.lottie.compose)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    debugImplementation(libs.compose.ui.tooling.preview)
}
```

- [ ] **Step 3: Update root `build.gradle.kts`**

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}
```

- [ ] **Step 4: Sync Gradle and verify build succeeds**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

---

## Task 2: AndroidManifest — Lock to Landscape

**Files:**
- Modify: `app/src/main/AndroidManifest.xml`

- [ ] **Step 1: Set landscape and add Application class**

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application
        android:name=".ThinkingPlanetApp"
        android:label="思维星球"
        android:theme="@style/Theme.ThinkingPlanet">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:screenOrientation="sensorLandscape"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

---

## Task 3: Domain Models

**Files:**
- Create: `domain/model/World.kt`
- Create: `domain/model/Question.kt`
- Create: `domain/model/Honor.kt`

- [ ] **Step 1: Write failing test for star calculation**

Create `src/test/java/com/thinkingplanet/usecase/CalculateStarsUseCaseTest.kt`:

```kotlin
package com.thinkingplanet.usecase

import com.thinkingplanet.domain.usecase.CalculateStarsUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateStarsUseCaseTest {
    private val useCase = CalculateStarsUseCase()

    @Test fun `returns 1 star when correct rate is between 60 and 79`() {
        assertEquals(1, useCase(correct = 6, total = 10))
    }

    @Test fun `returns 2 stars when correct rate is between 80 and 99`() {
        assertEquals(2, useCase(correct = 8, total = 10))
    }

    @Test fun `returns 3 stars when all correct`() {
        assertEquals(3, useCase(correct = 10, total = 10))
    }

    @Test fun `returns 0 stars when correct rate below 60`() {
        assertEquals(0, useCase(correct = 5, total = 10))
    }
}
```

- [ ] **Step 2: Run test — expect compilation failure** (CalculateStarsUseCase not yet created)

- [ ] **Step 3: Create `domain/model/World.kt`**

```kotlin
package com.thinkingplanet.domain.model

enum class WorldTheme(
    val worldName: String,
    val grade: Int,
    val emoji: String,
    val primaryColor: Long,
    val secondaryColor: Long
) {
    SAKURA("樱花仙境", 1, "🌸", 0xFFFFB7D5, 0xFFFF85A1),
    UNICORN("独角兽草原", 2, "🦄", 0xFFD8B4FE, 0xFFA78BFA),
    MERMAID("人鱼海湾", 3, "🧜", 0xFF6EE7B7, 0xFF34D399),
    CANDY("甜心糖果城", 4, "🍭", 0xFFFDE68A, 0xFFFBBF24),
    CLOUD("云朵城堡", 5, "☁️", 0xFFBAE6FD, 0xFF7DD3FC),
    RAINBOW("彩虹星球", 6, "🌈", 0xFFF9A8D4, 0xFFC084FC)
}

enum class Dimension(val label: String, val emoji: String) {
    MATH("数学", "🔢"),
    LOGIC("逻辑", "🧩"),
    SPATIAL("空间", "🎯"),
    PATTERN("规律", "🔍"),
    CREATIVE("创意", "🎨")
}

data class World(
    val theme: WorldTheme,
    val upperChapter: Chapter,
    val lowerChapter: Chapter
)

data class Chapter(
    val packId: String,       // e.g. "grade1_upper"
    val grade: Int,
    val isUpper: Boolean,
    val dimensions: Map<Dimension, List<Level>>
)

data class Level(
    val levelId: String,
    val dimension: Dimension,
    val questions: List<Question>
)
```

- [ ] **Step 4: Create `domain/model/Question.kt`**

```kotlin
package com.thinkingplanet.domain.model

data class Question(
    val id: String,
    val type: QuestionType,
    val text: String,
    val imageRes: String?,   // asset path, nullable
    val options: List<String>,
    val answerIndex: Int,    // 0-based index into options
    val hint: String
)

enum class QuestionType { CHOICE }
```

- [ ] **Step 5: Create `domain/model/Honor.kt`**

```kotlin
package com.thinkingplanet.domain.model

enum class MedalLevel { NONE, BRONZE, SILVER, GOLD }
enum class TrophyLevel { NONE, BRONZE, SILVER, GOLD }

data class Medal(
    val packId: String,
    val dimension: Dimension,
    val level: MedalLevel
)

data class Trophy(
    val packId: String,
    val level: TrophyLevel
)

enum class PlayerTitle(val label: String, val emoji: String) {
    MATH_PRINCESS("数学小公主", "🌸"),
    LOGIC_WIZARD("逻辑魔法师", "🦄"),
    SPATIAL_EXPLORER("空间探险家", "🧜"),
    PATTERN_GENIUS("规律小天才", "🍭"),
    CREATIVE_FAIRY("创意小精灵", "✨"),
    STAR_COLLECTOR("星星收集者", "⭐"),
    ALL_ROUNDER("全能小女王", "🌈"),
    LEGEND("传说级天才", "💎"),
    NONE("冒险者", "🎀")
}
```

---

## Task 4: Use Cases

**Files:**
- Create: `domain/usecase/CalculateStarsUseCase.kt`
- Create: `domain/usecase/UpdateMedalUseCase.kt`
- Create: `domain/usecase/UpdateTrophyUseCase.kt`

- [ ] **Step 1: Create `CalculateStarsUseCase.kt`**

```kotlin
package com.thinkingplanet.domain.usecase

import javax.inject.Inject

class CalculateStarsUseCase @Inject constructor() {
    operator fun invoke(correct: Int, total: Int): Int {
        if (total == 0) return 0
        val rate = correct.toFloat() / total
        return when {
            rate >= 1.0f -> 3
            rate >= 0.8f -> 2
            rate >= 0.6f -> 1
            else -> 0
        }
    }
}
```

- [ ] **Step 2: Run CalculateStarsUseCaseTest — all 4 tests pass**

Run: `./gradlew test --tests "com.thinkingplanet.usecase.CalculateStarsUseCaseTest"`
Expected: 4 tests PASS

- [ ] **Step 3: Write failing test for medal update**

Add to `UpdateMedalUseCaseTest.kt`:

```kotlin
package com.thinkingplanet.usecase

import com.thinkingplanet.domain.model.MedalLevel
import com.thinkingplanet.domain.usecase.UpdateMedalUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateMedalUseCaseTest {
    private val useCase = UpdateMedalUseCase()

    @Test fun `returns BRONZE when average stars between 1 and 1_9`() {
        assertEquals(MedalLevel.BRONZE, useCase(averageStars = 1.5f))
    }

    @Test fun `returns SILVER when average stars between 2 and 2_9`() {
        assertEquals(MedalLevel.SILVER, useCase(averageStars = 2.5f))
    }

    @Test fun `returns GOLD when average stars is 3`() {
        assertEquals(MedalLevel.GOLD, useCase(averageStars = 3.0f))
    }

    @Test fun `returns NONE when average stars below 1`() {
        assertEquals(MedalLevel.NONE, useCase(averageStars = 0.5f))
    }
}
```

- [ ] **Step 4: Create `UpdateMedalUseCase.kt`**

```kotlin
package com.thinkingplanet.domain.usecase

import com.thinkingplanet.domain.model.MedalLevel
import javax.inject.Inject

class UpdateMedalUseCase @Inject constructor() {
    operator fun invoke(averageStars: Float): MedalLevel = when {
        averageStars >= 3.0f -> MedalLevel.GOLD
        averageStars >= 2.0f -> MedalLevel.SILVER
        averageStars >= 1.0f -> MedalLevel.BRONZE
        else -> MedalLevel.NONE
    }
}
```

- [ ] **Step 5: Create `UpdateTrophyUseCase.kt`**

```kotlin
package com.thinkingplanet.domain.usecase

import com.thinkingplanet.domain.model.MedalLevel
import com.thinkingplanet.domain.model.TrophyLevel
import javax.inject.Inject

class UpdateTrophyUseCase @Inject constructor() {
    operator fun invoke(medals: List<MedalLevel>): TrophyLevel {
        if (medals.size < 5) return TrophyLevel.NONE
        return when {
            medals.all { it == MedalLevel.GOLD } -> TrophyLevel.GOLD
            medals.all { it >= MedalLevel.SILVER } -> TrophyLevel.SILVER
            medals.none { it == MedalLevel.NONE } -> TrophyLevel.BRONZE
            else -> TrophyLevel.NONE
        }
    }
}
```

- [ ] **Step 6: Run all tests**

Run: `./gradlew test`
Expected: All tests PASS

- [ ] **Step 7: Commit**

```bash
git add .
git commit -m "feat: domain models and core use cases with tests"
```

---

## Task 5: JSON Question Pack Loader

**Files:**
- Create: `data/source/QuestionPackLoader.kt`
- Create: `assets/packs/grade1_upper.json`
- Create: `src/test/java/com/thinkingplanet/source/QuestionPackLoaderTest.kt`

- [ ] **Step 1: Create `assets/packs/grade1_upper.json`**

```json
{
  "pack_id": "grade1_upper",
  "grade": 1,
  "is_upper": true,
  "dimensions": {
    "MATH": [
      {
        "level_id": "math_01",
        "questions": [
          {
            "id": "m01_q01",
            "type": "CHOICE",
            "text": "小美有🌸24朵樱花，送给朋友8朵，还剩几朵？",
            "image_res": null,
            "options": ["14", "16", "18", "32"],
            "answer_index": 1,
            "hint": "24 - 8 = ?"
          },
          {
            "id": "m01_q02",
            "type": "CHOICE",
            "text": "树上有5只小鸟，又飞来3只，一共有几只？",
            "image_res": null,
            "options": ["6", "7", "8", "9"],
            "answer_index": 2,
            "hint": "5 + 3 = ?"
          },
          {
            "id": "m01_q03",
            "type": "CHOICE",
            "text": "一共有10颗糖，吃了4颗，还剩几颗？",
            "image_res": null,
            "options": ["4", "5", "6", "7"],
            "answer_index": 2,
            "hint": "10 - 4 = ?"
          },
          {
            "id": "m01_q04",
            "type": "CHOICE",
            "text": "3+7等于几？",
            "image_res": null,
            "options": ["9", "10", "11", "12"],
            "answer_index": 1,
            "hint": "数一数：3往后数7个"
          },
          {
            "id": "m01_q05",
            "type": "CHOICE",
            "text": "小猫有6条鱼，小狗有2条鱼，小猫比小狗多几条？",
            "image_res": null,
            "options": ["2", "3", "4", "5"],
            "answer_index": 2,
            "hint": "6 - 2 = ?"
          }
        ]
      }
    ],
    "LOGIC": [
      {
        "level_id": "logic_01",
        "questions": [
          {
            "id": "l01_q01",
            "type": "CHOICE",
            "text": "苹果比香蕉重，香蕉比葡萄重，那么最重的是？",
            "image_res": null,
            "options": ["香蕉", "葡萄", "苹果", "一样重"],
            "answer_index": 2,
            "hint": "比较一下谁最大"
          },
          {
            "id": "l01_q02",
            "type": "CHOICE",
            "text": "今天是星期三，明天是星期几？",
            "image_res": null,
            "options": ["星期二", "星期三", "星期四", "星期五"],
            "answer_index": 2,
            "hint": "往后数一天"
          },
          {
            "id": "l01_q03",
            "type": "CHOICE",
            "text": "红球在蓝球左边，蓝球在黄球左边，哪个球在最右边？",
            "image_res": null,
            "options": ["红球", "蓝球", "黄球", "一样"],
            "answer_index": 2,
            "hint": "想象一排球的顺序"
          },
          {
            "id": "l01_q04",
            "type": "CHOICE",
            "text": "小明比小红高，小红比小亮高，谁最矮？",
            "image_res": null,
            "options": ["小明", "小红", "小亮", "一样高"],
            "answer_index": 2,
            "hint": "找最后一个"
          },
          {
            "id": "l01_q05",
            "type": "CHOICE",
            "text": "如果下雨了，地面会湿。地面是湿的，可能是因为？",
            "image_res": null,
            "options": ["天气很热", "下雨了", "刮风了", "有彩虹"],
            "answer_index": 1,
            "hint": "想想地面湿的原因"
          }
        ]
      }
    ],
    "SPATIAL": [
      {
        "level_id": "spatial_01",
        "questions": [
          {
            "id": "s01_q01",
            "type": "CHOICE",
            "text": "一个正方形有几个角？",
            "image_res": null,
            "options": ["3", "4", "5", "6"],
            "answer_index": 1,
            "hint": "数一数正方形的角"
          },
          {
            "id": "s01_q02",
            "type": "CHOICE",
            "text": "哪个形状像球一样圆？",
            "image_res": null,
            "options": ["正方形", "三角形", "圆形", "长方形"],
            "answer_index": 2,
            "hint": "找圆圆的形状"
          },
          {
            "id": "s01_q03",
            "type": "CHOICE",
            "text": "从上面看一个圆柱，看到的是什么形状？",
            "image_res": null,
            "options": ["长方形", "三角形", "圆形", "正方形"],
            "answer_index": 2,
            "hint": "想象从正上方往下看"
          },
          {
            "id": "s01_q04",
            "type": "CHOICE",
            "text": "小明站在房子的北面，房子在小明的哪个方向？",
            "image_res": null,
            "options": ["东", "西", "南", "北"],
            "answer_index": 2,
            "hint": "方向是相对的"
          },
          {
            "id": "s01_q05",
            "type": "CHOICE",
            "text": "三角形有几条边？",
            "image_res": null,
            "options": ["2", "3", "4", "5"],
            "answer_index": 1,
            "hint": "数一数三角形的边"
          }
        ]
      }
    ],
    "PATTERN": [
      {
        "level_id": "pattern_01",
        "questions": [
          {
            "id": "p01_q01",
            "type": "CHOICE",
            "text": "2, 4, 6, 8, ___，下一个数是？",
            "image_res": null,
            "options": ["9", "10", "11", "12"],
            "answer_index": 1,
            "hint": "每次加2"
          },
          {
            "id": "p01_q02",
            "type": "CHOICE",
            "text": "🌸🌟🌸🌟🌸___，下一个是？",
            "image_res": null,
            "options": ["🌸", "🌟", "🎀", "💫"],
            "answer_index": 1,
            "hint": "找规律：两个交替出现"
          },
          {
            "id": "p01_q03",
            "type": "CHOICE",
            "text": "1, 3, 5, 7, ___，下一个数是？",
            "image_res": null,
            "options": ["8", "9", "10", "11"],
            "answer_index": 1,
            "hint": "每次加2，都是奇数"
          },
          {
            "id": "p01_q04",
            "type": "CHOICE",
            "text": "🔴🔵🔴🔵🔴___",
            "image_res": null,
            "options": ["🔴", "🔵", "🟡", "🟢"],
            "answer_index": 1,
            "hint": "红蓝交替"
          },
          {
            "id": "p01_q05",
            "type": "CHOICE",
            "text": "10, 20, 30, ___，下一个是？",
            "image_res": null,
            "options": ["35", "38", "40", "50"],
            "answer_index": 2,
            "hint": "每次加10"
          }
        ]
      }
    ],
    "CREATIVE": [
      {
        "level_id": "creative_01",
        "questions": [
          {
            "id": "c01_q01",
            "type": "CHOICE",
            "text": "一块橡皮可以有哪些用途？选出最有创意的答案！",
            "image_res": null,
            "options": ["擦铅笔字", "当积木搭房子", "放进铅笔盒", "扔掉"],
            "answer_index": 1,
            "hint": "想想不一样的用法"
          },
          {
            "id": "c01_q02",
            "type": "CHOICE",
            "text": "如果你有一双翅膀，你最想做什么？",
            "image_res": null,
            "options": ["飞到彩虹上", "待在家里", "去超市", "睡觉"],
            "answer_index": 0,
            "hint": "大胆想象，没有标准答案"
          },
          {
            "id": "c01_q03",
            "type": "CHOICE",
            "text": "用什么方法能让小朋友记住自己的名字？选最特别的！",
            "image_res": null,
            "options": ["大声说名字", "画一幅和名字有关的画", "写在纸上", "什么都不做"],
            "answer_index": 1,
            "hint": "想想最有趣的方式"
          },
          {
            "id": "c01_q04",
            "type": "CHOICE",
            "text": "如果水变成果汁，你最希望变成什么味道？",
            "image_res": null,
            "options": ["草莓味", "普通水", "没味道", "苦的"],
            "answer_index": 0,
            "hint": "选你最喜欢的"
          },
          {
            "id": "c01_q05",
            "type": "CHOICE",
            "text": "圆圈可以变成什么？选最有创意的！",
            "image_res": null,
            "options": ["什么都不是", "太阳🌞", "点", "圆圈"],
            "answer_index": 1,
            "hint": "发挥想象力"
          }
        ]
      }
    ]
  }
}
```

- [ ] **Step 2: Create `data/source/QuestionPackLoader.kt`**

```kotlin
package com.thinkingplanet.data.source

import android.content.Context
import com.thinkingplanet.domain.model.Dimension
import com.thinkingplanet.domain.model.Level
import com.thinkingplanet.domain.model.Question
import com.thinkingplanet.domain.model.QuestionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Serializable
private data class PackJson(
    @SerialName("pack_id") val packId: String,
    @SerialName("grade") val grade: Int,
    @SerialName("is_upper") val isUpper: Boolean,
    @SerialName("dimensions") val dimensions: Map<String, List<LevelJson>>
)

@Serializable
private data class LevelJson(
    @SerialName("level_id") val levelId: String,
    @SerialName("questions") val questions: List<QuestionJson>
)

@Serializable
private data class QuestionJson(
    @SerialName("id") val id: String,
    @SerialName("type") val type: String,
    @SerialName("text") val text: String,
    @SerialName("image_res") val imageRes: String? = null,
    @SerialName("options") val options: List<String>,
    @SerialName("answer_index") val answerIndex: Int,
    @SerialName("hint") val hint: String
)

class QuestionPackLoader @Inject constructor(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    fun load(packId: String): Map<Dimension, List<Level>> {
        val raw = context.assets.open("packs/$packId.json")
            .bufferedReader().readText()
        val pack = json.decodeFromString<PackJson>(raw)
        return pack.dimensions.mapNotNull { (key, levels) ->
            val dimension = Dimension.entries.find { it.name == key } ?: return@mapNotNull null
            val domainLevels = levels.map { levelJson ->
                Level(
                    levelId = levelJson.levelId,
                    dimension = dimension,
                    questions = levelJson.questions.map { q ->
                        Question(
                            id = q.id,
                            type = QuestionType.valueOf(q.type),
                            text = q.text,
                            imageRes = q.imageRes,
                            options = q.options,
                            answerIndex = q.answerIndex,
                            hint = q.hint
                        )
                    }
                )
            }
            dimension to domainLevels
        }.toMap()
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add .
git commit -m "feat: question pack JSON and loader"
```

---

## Task 6: Room Database

**Files:**
- Create: `data/local/entity/LevelProgressEntity.kt`
- Create: `data/local/entity/MedalEntity.kt`
- Create: `data/local/entity/TrophyEntity.kt`
- Create: `data/local/entity/ProfileEntity.kt`
- Create: `data/local/dao/LevelProgressDao.kt`
- Create: `data/local/dao/MedalDao.kt`
- Create: `data/local/dao/TrophyDao.kt`
- Create: `data/local/dao/ProfileDao.kt`
- Create: `data/local/AppDatabase.kt`

- [ ] **Step 1: Create entities**

`LevelProgressEntity.kt`:
```kotlin
package com.thinkingplanet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "level_progress")
data class LevelProgressEntity(
    @PrimaryKey val levelId: String,
    val packId: String,
    val dimension: String,
    val bestStars: Int = 0,
    val attempts: Int = 0
)
```

`MedalEntity.kt`:
```kotlin
package com.thinkingplanet.data.local.entity

import androidx.room.Entity

@Entity(tableName = "medals", primaryKeys = ["packId", "dimension"])
data class MedalEntity(
    val packId: String,
    val dimension: String,
    val medalLevel: String = "NONE"
)
```

`TrophyEntity.kt`:
```kotlin
package com.thinkingplanet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trophies")
data class TrophyEntity(
    @PrimaryKey val packId: String,
    val trophyLevel: String = "NONE"
)
```

`ProfileEntity.kt`:
```kotlin
package com.thinkingplanet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = 1,
    val nickname: String = "小探险家",
    val currentTitle: String = "NONE",
    val totalStars: Int = 0
)
```

- [ ] **Step 2: Create DAOs**

`LevelProgressDao.kt`:
```kotlin
package com.thinkingplanet.data.local.dao

import androidx.room.*
import com.thinkingplanet.data.local.entity.LevelProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelProgressDao {
    @Query("SELECT * FROM level_progress WHERE packId = :packId")
    fun getByPack(packId: String): Flow<List<LevelProgressEntity>>

    @Query("SELECT * FROM level_progress WHERE levelId = :levelId")
    suspend fun getByLevel(levelId: String): LevelProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: LevelProgressEntity)
}
```

`MedalDao.kt`:
```kotlin
package com.thinkingplanet.data.local.dao

import androidx.room.*
import com.thinkingplanet.data.local.entity.MedalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedalDao {
    @Query("SELECT * FROM medals WHERE packId = :packId")
    fun getByPack(packId: String): Flow<List<MedalEntity>>

    @Query("SELECT * FROM medals")
    fun getAll(): Flow<List<MedalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: MedalEntity)
}
```

`TrophyDao.kt`:
```kotlin
package com.thinkingplanet.data.local.dao

import androidx.room.*
import com.thinkingplanet.data.local.entity.TrophyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrophyDao {
    @Query("SELECT * FROM trophies")
    fun getAll(): Flow<List<TrophyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: TrophyEntity)
}
```

`ProfileDao.kt`:
```kotlin
package com.thinkingplanet.data.local.dao

import androidx.room.*
import com.thinkingplanet.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile WHERE id = 1")
    fun get(): Flow<ProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ProfileEntity)
}
```

- [ ] **Step 3: Create `AppDatabase.kt`**

```kotlin
package com.thinkingplanet.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thinkingplanet.data.local.dao.*
import com.thinkingplanet.data.local.entity.*

@Database(
    entities = [LevelProgressEntity::class, MedalEntity::class, TrophyEntity::class, ProfileEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun levelProgressDao(): LevelProgressDao
    abstract fun medalDao(): MedalDao
    abstract fun trophyDao(): TrophyDao
    abstract fun profileDao(): ProfileDao
}
```

- [ ] **Step 4: Commit**

```bash
git add .
git commit -m "feat: Room database — entities, DAOs, AppDatabase"
```

---

## Task 7: Repositories & DI

**Files:**
- Create: `domain/repository/ProgressRepository.kt`
- Create: `domain/repository/QuestionRepository.kt`
- Create: `data/repository/ProgressRepositoryImpl.kt`
- Create: `data/repository/QuestionRepositoryImpl.kt`
- Create: `di/DatabaseModule.kt`
- Create: `di/AppModule.kt`
- Create: `ThinkingPlanetApp.kt`

- [ ] **Step 1: Repository interfaces**

`domain/repository/ProgressRepository.kt`:
```kotlin
package com.thinkingplanet.domain.repository

import com.thinkingplanet.domain.model.Dimension
import com.thinkingplanet.domain.model.MedalLevel
import com.thinkingplanet.domain.model.TrophyLevel
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun getLevelStars(packId: String): Flow<Map<String, Int>>  // levelId -> bestStars
    fun getMedals(packId: String): Flow<Map<Dimension, MedalLevel>>
    fun getAllTrophies(): Flow<Map<String, TrophyLevel>>        // packId -> TrophyLevel
    fun getTotalStars(): Flow<Int>
    suspend fun saveLevelResult(packId: String, levelId: String, dimension: Dimension, stars: Int)
    suspend fun saveMedal(packId: String, dimension: Dimension, level: MedalLevel)
    suspend fun saveTrophy(packId: String, level: TrophyLevel)
}
```

`domain/repository/QuestionRepository.kt`:
```kotlin
package com.thinkingplanet.domain.repository

import com.thinkingplanet.domain.model.Dimension
import com.thinkingplanet.domain.model.Level

interface QuestionRepository {
    fun getLevels(packId: String): Map<Dimension, List<Level>>
}
```

- [ ] **Step 2: Create `ProgressRepositoryImpl.kt`**

```kotlin
package com.thinkingplanet.data.repository

import com.thinkingplanet.data.local.dao.*
import com.thinkingplanet.data.local.entity.*
import com.thinkingplanet.domain.model.*
import com.thinkingplanet.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProgressRepositoryImpl @Inject constructor(
    private val levelProgressDao: LevelProgressDao,
    private val medalDao: MedalDao,
    private val trophyDao: TrophyDao,
    private val profileDao: ProfileDao
) : ProgressRepository {

    override fun getLevelStars(packId: String): Flow<Map<String, Int>> =
        levelProgressDao.getByPack(packId).map { list ->
            list.associate { it.levelId to it.bestStars }
        }

    override fun getMedals(packId: String): Flow<Map<Dimension, MedalLevel>> =
        medalDao.getByPack(packId).map { list ->
            list.associate { entity ->
                Dimension.valueOf(entity.dimension) to MedalLevel.valueOf(entity.medalLevel)
            }
        }

    override fun getAllTrophies(): Flow<Map<String, TrophyLevel>> =
        trophyDao.getAll().map { list ->
            list.associate { it.packId to TrophyLevel.valueOf(it.trophyLevel) }
        }

    override fun getTotalStars(): Flow<Int> =
        profileDao.get().map { it?.totalStars ?: 0 }

    override suspend fun saveLevelResult(packId: String, levelId: String, dimension: Dimension, stars: Int) {
        val existing = levelProgressDao.getByLevel(levelId)
        val best = maxOf(stars, existing?.bestStars ?: 0)
        levelProgressDao.upsert(LevelProgressEntity(levelId, packId, dimension.name, best, (existing?.attempts ?: 0) + 1))
        val profile = ProfileEntity(totalStars = (profileDao.get() as? ProfileEntity)?.totalStars?.plus(stars) ?: stars)
        profileDao.upsert(profile)
    }

    override suspend fun saveMedal(packId: String, dimension: Dimension, level: MedalLevel) {
        medalDao.upsert(MedalEntity(packId, dimension.name, level.name))
    }

    override suspend fun saveTrophy(packId: String, level: TrophyLevel) {
        trophyDao.upsert(TrophyEntity(packId, level.name))
    }
}
```

- [ ] **Step 3: Create `QuestionRepositoryImpl.kt`**

```kotlin
package com.thinkingplanet.data.repository

import com.thinkingplanet.data.source.QuestionPackLoader
import com.thinkingplanet.domain.model.Dimension
import com.thinkingplanet.domain.model.Level
import com.thinkingplanet.domain.repository.QuestionRepository
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val loader: QuestionPackLoader
) : QuestionRepository {
    private val cache = mutableMapOf<String, Map<Dimension, List<Level>>>()

    override fun getLevels(packId: String): Map<Dimension, List<Level>> =
        cache.getOrPut(packId) { loader.load(packId) }
}
```

- [ ] **Step 4: Create Hilt modules**

`di/DatabaseModule.kt`:
```kotlin
package com.thinkingplanet.di

import android.content.Context
import androidx.room.Room
import com.thinkingplanet.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "thinking_planet.db").build()

    @Provides fun provideLevelProgressDao(db: AppDatabase) = db.levelProgressDao()
    @Provides fun provideMedalDao(db: AppDatabase) = db.medalDao()
    @Provides fun provideTrophyDao(db: AppDatabase) = db.trophyDao()
    @Provides fun provideProfileDao(db: AppDatabase) = db.profileDao()
}
```

`di/AppModule.kt`:
```kotlin
package com.thinkingplanet.di

import android.content.Context
import com.thinkingplanet.data.repository.ProgressRepositoryImpl
import com.thinkingplanet.data.repository.QuestionRepositoryImpl
import com.thinkingplanet.data.source.QuestionPackLoader
import com.thinkingplanet.domain.repository.ProgressRepository
import com.thinkingplanet.domain.repository.QuestionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton
    fun provideQuestionPackLoader(@ApplicationContext context: Context) =
        QuestionPackLoader(context)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindProgressRepository(impl: ProgressRepositoryImpl): ProgressRepository

    @Binds @Singleton
    abstract fun bindQuestionRepository(impl: QuestionRepositoryImpl): QuestionRepository
}
```

`ThinkingPlanetApp.kt`:
```kotlin
package com.thinkingplanet

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ThinkingPlanetApp : Application()
```

- [ ] **Step 5: Commit**

```bash
git add .
git commit -m "feat: repositories, DI modules, Hilt setup"
```

---

## Task 8: Theme & Colors

**Files:**
- Create: `ui/theme/Color.kt`
- Create: `ui/theme/Theme.kt`

- [ ] **Step 1: Create `ui/theme/Color.kt`**

```kotlin
package com.thinkingplanet.ui.theme

import androidx.compose.ui.graphics.Color

// Global
val PinkPrimary    = Color(0xFFFFB7D5)
val PinkSecondary  = Color(0xFFFF85A1)
val PurplePrimary  = Color(0xFFD8B4FE)
val PurpleSecondary= Color(0xFFA78BFA)
val MintPrimary    = Color(0xFF6EE7B7)
val MintSecondary  = Color(0xFF34D399)
val HoneyPrimary   = Color(0xFFFDE68A)
val HoneySecondary = Color(0xFFFBBF24)
val SkyPrimary     = Color(0xFFBAE6FD)
val SkySecondary   = Color(0xFF7DD3FC)
val RainbowStart   = Color(0xFFF9A8D4)
val RainbowEnd     = Color(0xFFC084FC)

val Background     = Color(0xFFFDF2F8)
val Surface        = Color(0xFFFFFFFF)
val OnSurface      = Color(0xFF1E1B4B)
val Correct        = Color(0xFF34D399)
val Incorrect      = Color(0xFFFB7185)
val StarGold       = Color(0xFFFBBF24)
val LockedGray     = Color(0xFF9CA3AF)
```

- [ ] **Step 2: Create `ui/theme/Theme.kt`**

```kotlin
package com.thinkingplanet.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val ColorScheme = lightColorScheme(
    primary = PinkPrimary,
    secondary = PurplePrimary,
    background = Background,
    surface = Surface,
    onSurface = OnSurface
)

val Typography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    bodyLarge = TextStyle(fontSize = 16.sp),
    bodyMedium = TextStyle(fontSize = 14.sp),
    labelSmall = TextStyle(fontSize = 11.sp)
)

@Composable
fun ThinkingPlanetTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = ColorScheme, typography = Typography, content = content)
}
```

---

## Task 9: Navigation & MainActivity

**Files:**
- Create: `ui/navigation/AppNavigation.kt`
- Modify: `MainActivity.kt`

- [ ] **Step 1: Create `AppNavigation.kt`**

```kotlin
package com.thinkingplanet.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.thinkingplanet.ui.chapter.ChapterScreen
import com.thinkingplanet.ui.honor.HonorScreen
import com.thinkingplanet.ui.question.QuestionScreen
import com.thinkingplanet.ui.result.ResultScreen
import com.thinkingplanet.ui.worldmap.WorldMapScreen

sealed class Screen(val route: String) {
    object WorldMap : Screen("worldmap")
    object Chapter  : Screen("chapter/{packId}") {
        fun go(packId: String) = "chapter/$packId"
    }
    object Question : Screen("question/{packId}/{dimension}/{levelId}") {
        fun go(packId: String, dimension: String, levelId: String) =
            "question/$packId/$dimension/$levelId"
    }
    object Result : Screen("result/{stars}") {
        fun go(stars: Int) = "result/$stars"
    }
    object Honor : Screen("honor")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.WorldMap.route) {
        composable(Screen.WorldMap.route) {
            WorldMapScreen(
                onChapterClick = { packId -> navController.navigate(Screen.Chapter.go(packId)) },
                onHonorClick   = { navController.navigate(Screen.Honor.route) }
            )
        }
        composable(Screen.Chapter.route, listOf(navArgument("packId") { type = NavType.StringType })) { back ->
            val packId = back.arguments!!.getString("packId")!!
            ChapterScreen(
                packId = packId,
                onLevelClick = { dim, levelId -> navController.navigate(Screen.Question.go(packId, dim, levelId)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Question.route, listOf(
            navArgument("packId")    { type = NavType.StringType },
            navArgument("dimension") { type = NavType.StringType },
            navArgument("levelId")   { type = NavType.StringType }
        )) { back ->
            val args = back.arguments!!
            QuestionScreen(
                packId    = args.getString("packId")!!,
                dimension = args.getString("dimension")!!,
                levelId   = args.getString("levelId")!!,
                onFinish  = { stars -> navController.navigate(Screen.Result.go(stars)) { popUpTo(Screen.WorldMap.route) } }
            )
        }
        composable(Screen.Result.route, listOf(navArgument("stars") { type = NavType.IntType })) { back ->
            ResultScreen(
                stars  = back.arguments!!.getInt("stars"),
                onDone = { navController.popBackStack(Screen.WorldMap.route, false) }
            )
        }
        composable(Screen.Honor.route) {
            HonorScreen(onBack = { navController.popBackStack() })
        }
    }
}
```

- [ ] **Step 2: Update `MainActivity.kt`**

```kotlin
package com.thinkingplanet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.thinkingplanet.ui.navigation.AppNavigation
import com.thinkingplanet.ui.theme.ThinkingPlanetTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThinkingPlanetTheme {
                AppNavigation()
            }
        }
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add .
git commit -m "feat: navigation graph, theme, MainActivity"
```

---

## Task 10: World Map Screen

**Files:**
- Create: `ui/worldmap/WorldMapViewModel.kt`
- Create: `ui/worldmap/WorldMapScreen.kt`

- [ ] **Step 1: Create `WorldMapViewModel.kt`**

```kotlin
package com.thinkingplanet.ui.worldmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thinkingplanet.domain.model.*
import com.thinkingplanet.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class WorldUiState(
    val theme: WorldTheme,
    val upperPackId: String,
    val lowerPackId: String,
    val upperTrophy: TrophyLevel,
    val lowerTrophy: TrophyLevel,
    val isUnlocked: Boolean
)

@HiltViewModel
class WorldMapViewModel @Inject constructor(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val allPackIds = WorldTheme.entries.flatMap { theme ->
        listOf("grade${theme.grade}_upper", "grade${theme.grade}_lower")
    }

    val worldStates: StateFlow<List<WorldUiState>> = progressRepository.getAllTrophies()
        .map { trophies -> buildWorldStates(trophies) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, buildWorldStates(emptyMap()))

    val totalStars: StateFlow<Int> = progressRepository.getTotalStars()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    private fun buildWorldStates(trophies: Map<String, TrophyLevel>): List<WorldUiState> {
        return WorldTheme.entries.mapIndexed { index, theme ->
            val upperPack = "grade${theme.grade}_upper"
            val lowerPack = "grade${theme.grade}_lower"
            WorldUiState(
                theme = theme,
                upperPackId = upperPack,
                lowerPackId = lowerPack,
                upperTrophy = trophies[upperPack] ?: TrophyLevel.NONE,
                lowerTrophy = trophies[lowerPack] ?: TrophyLevel.NONE,
                isUnlocked = index == 0 || trophies.getOrDefault("grade${index}_upper", TrophyLevel.NONE) != TrophyLevel.NONE
            )
        }
    }
}
```

- [ ] **Step 2: Create `WorldMapScreen.kt`**

```kotlin
package com.thinkingplanet.ui.worldmap

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thinkingplanet.domain.model.TrophyLevel
import com.thinkingplanet.domain.model.WorldTheme
import com.thinkingplanet.ui.theme.*

@Composable
fun WorldMapScreen(
    onChapterClick: (packId: String) -> Unit,
    onHonorClick: () -> Unit,
    viewModel: WorldMapViewModel = hiltViewModel()
) {
    val worlds by viewModel.worldStates.collectAsState()
    val totalStars by viewModel.totalStars.collectAsState()

    Column(Modifier.fillMaxSize().background(Background)) {
        // Top bar
        Row(
            Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(PinkPrimary, PurplePrimary)))
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("🧚", fontSize = 28.sp)
                Column {
                    Text("思维星球", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    Text("✨ 小探险家", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("⭐ $totalStars", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                TextButton(onClick = onHonorClick) {
                    Text("🏆 荣誉殿堂", color = Color.White, fontSize = 13.sp)
                }
            }
        }

        // World grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(worlds) { world ->
                WorldCard(world = world, onClick = {
                    if (world.isUnlocked) onChapterClick(world.upperPackId)
                })
            }
        }
    }
}

@Composable
private fun WorldCard(world: WorldUiState, onClick: () -> Unit) {
    val alpha = if (world.isUnlocked) 1f else 0.4f
    Card(
        modifier = Modifier.fillMaxWidth().alpha(alpha).clickable(enabled = world.isUnlocked, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(if (world.isUnlocked) 6.dp else 2.dp)
    ) {
        Column {
            Box(
                Modifier.fillMaxWidth().height(100.dp)
                    .background(Brush.verticalGradient(listOf(
                        Color(world.theme.primaryColor), Color(world.theme.secondaryColor)
                    ))),
                contentAlignment = Alignment.Center
            ) {
                Text(world.theme.emoji, fontSize = 40.sp)
                if (!world.isUnlocked) {
                    Text("🔒", fontSize = 20.sp, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp))
                }
            }
            Column(Modifier.padding(12.dp)) {
                Text(world.theme.worldName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = OnSurface)
                Text("${world.theme.grade}年级", fontSize = 12.sp, color = OnSurface.copy(alpha = 0.6f))
                Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TrophyBadge(world.upperTrophy, "上")
                    TrophyBadge(world.lowerTrophy, "下")
                }
            }
        }
    }
}

@Composable
private fun TrophyBadge(level: TrophyLevel, label: String) {
    val emoji = when (level) {
        TrophyLevel.GOLD -> "🥇"; TrophyLevel.SILVER -> "🥈"; TrophyLevel.BRONZE -> "🥉"; TrophyLevel.NONE -> "○"
    }
    Text("$emoji$label", fontSize = 11.sp, color = OnSurface.copy(alpha = 0.7f))
}
```

- [ ] **Step 3: Commit**

```bash
git add .
git commit -m "feat: world map screen with 3x2 grid and unlock states"
```

---

## Task 11: Chapter Screen

**Files:**
- Create: `ui/chapter/ChapterViewModel.kt`
- Create: `ui/chapter/ChapterScreen.kt`

- [ ] **Step 1: Create `ChapterViewModel.kt`**

```kotlin
package com.thinkingplanet.ui.chapter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thinkingplanet.domain.model.*
import com.thinkingplanet.domain.repository.ProgressRepository
import com.thinkingplanet.domain.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class DimensionUiState(
    val dimension: Dimension,
    val levels: List<LevelUiState>
)

data class LevelUiState(
    val levelId: String,
    val bestStars: Int,
    val totalQuestions: Int
)

@HiltViewModel
class ChapterViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _packId = MutableStateFlow("")
    val packId: StateFlow<String> = _packId

    val dimensionStates: StateFlow<List<DimensionUiState>> = _packId
        .filter { it.isNotEmpty() }
        .flatMapLatest { packId ->
            progressRepository.getLevelStars(packId).map { starsMap ->
                val levels = questionRepository.getLevels(packId)
                Dimension.entries.mapNotNull { dim ->
                    val dimLevels = levels[dim] ?: return@mapNotNull null
                    DimensionUiState(dim, dimLevels.map { level ->
                        LevelUiState(level.levelId, starsMap[level.levelId] ?: 0, level.questions.size)
                    })
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun load(packId: String) { _packId.value = packId }
}
```

- [ ] **Step 2: Create `ChapterScreen.kt`**

```kotlin
package com.thinkingplanet.ui.chapter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thinkingplanet.ui.theme.*

@Composable
fun ChapterScreen(
    packId: String,
    onLevelClick: (dimension: String, levelId: String) -> Unit,
    onBack: () -> Unit,
    viewModel: ChapterViewModel = hiltViewModel()
) {
    LaunchedEffect(packId) { viewModel.load(packId) }
    val dimensions by viewModel.dimensionStates.collectAsState()

    Column(Modifier.fillMaxSize().background(Background)) {
        // Header
        Row(
            Modifier.fillMaxWidth().background(PinkPrimary).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) { Text("←", fontSize = 20.sp, color = Color.White) }
            Text("选择思维维度", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
        }

        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(dimensions) { dimState ->
                DimensionSection(dimState = dimState, onLevelClick = { levelId ->
                    onLevelClick(dimState.dimension.name, levelId)
                })
            }
        }
    }
}

@Composable
private fun DimensionSection(dimState: DimensionUiState, onLevelClick: (levelId: String) -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(dimState.dimension.emoji, fontSize = 24.sp)
                Text(dimState.dimension.label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(12.dp))
            dimState.levels.forEach { level ->
                LevelRow(level = level, onClick = { onLevelClick(level.levelId) })
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun LevelRow(level: LevelUiState, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick)
            .background(Surface, RoundedCornerShape(12.dp)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("关卡 ${level.levelId.takeLast(2)}", fontSize = 14.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            repeat(3) { i ->
                Text(if (i < level.bestStars) "⭐" else "☆", fontSize = 16.sp)
            }
        }
        Text("${level.totalQuestions}题", fontSize = 12.sp, color = OnSurface.copy(alpha = 0.5f))
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add .
git commit -m "feat: chapter screen with dimension zones and level list"
```

---

## Task 12: Question Screen (Landscape Split)

**Files:**
- Create: `ui/question/QuestionViewModel.kt`
- Create: `ui/question/QuestionScreen.kt`

- [ ] **Step 1: Create `QuestionViewModel.kt`**

```kotlin
package com.thinkingplanet.ui.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thinkingplanet.domain.model.Dimension
import com.thinkingplanet.domain.model.Question
import com.thinkingplanet.domain.repository.ProgressRepository
import com.thinkingplanet.domain.repository.QuestionRepository
import com.thinkingplanet.domain.usecase.CalculateStarsUseCase
import com.thinkingplanet.domain.usecase.UpdateMedalUseCase
import com.thinkingplanet.domain.usecase.UpdateTrophyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuestionUiState(
    val question: Question?,
    val currentIndex: Int,
    val total: Int,
    val selectedIndex: Int?,
    val isAnswered: Boolean,
    val correctCount: Int,
    val isFinished: Boolean,
    val finalStars: Int,
    val showHint: Boolean
)

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val progressRepository: ProgressRepository,
    private val calculateStars: CalculateStarsUseCase,
    private val updateMedal: UpdateMedalUseCase,
    private val updateTrophy: UpdateTrophyUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(QuestionUiState(null, 0, 0, null, false, 0, false, 0, false))
    val state: StateFlow<QuestionUiState> = _state

    private lateinit var questions: List<Question>
    private lateinit var packId: String
    private lateinit var dimension: Dimension
    private lateinit var levelId: String

    fun load(packId: String, dimensionName: String, levelId: String) {
        this.packId = packId
        this.dimension = Dimension.valueOf(dimensionName)
        this.levelId = levelId
        val levels = questionRepository.getLevels(packId)
        questions = levels[dimension]?.find { it.levelId == levelId }?.questions ?: emptyList()
        _state.value = _state.value.copy(
            question = questions.firstOrNull(), currentIndex = 0, total = questions.size
        )
    }

    fun selectAnswer(index: Int) {
        if (_state.value.isAnswered) return
        _state.value = _state.value.copy(selectedIndex = index, isAnswered = true)
    }

    fun showHint() { _state.value = _state.value.copy(showHint = true) }

    fun next() {
        val s = _state.value
        val wasCorrect = s.selectedIndex == questions[s.currentIndex].answerIndex
        val correct = s.correctCount + if (wasCorrect) 1 else 0
        val nextIndex = s.currentIndex + 1
        if (nextIndex >= questions.size) {
            val stars = calculateStars(correct, questions.size)
            viewModelScope.launch {
                progressRepository.saveLevelResult(packId, levelId, dimension, stars)
                // Recalculate medal and trophy
                val starsByLevel = progressRepository.getLevelStars(packId).first()
                val levels = questionRepository.getLevels(packId)[dimension] ?: emptyList()
                val avg = levels.map { (starsByLevel[it.levelId] ?: 0).toFloat() }.average().toFloat()
                val medal = updateMedal(avg)
                progressRepository.saveMedal(packId, dimension, medal)
                val allMedals = progressRepository.getMedals(packId).first().values.toList()
                val trophy = updateTrophy(allMedals)
                progressRepository.saveTrophy(packId, trophy)
            }
            _state.value = s.copy(correctCount = correct, isFinished = true, finalStars = calculateStars(correct, questions.size))
        } else {
            _state.value = s.copy(
                currentIndex = nextIndex,
                question = questions[nextIndex],
                selectedIndex = null,
                isAnswered = false,
                correctCount = correct,
                showHint = false
            )
        }
    }
}
```

- [ ] **Step 2: Create `QuestionScreen.kt`**

```kotlin
package com.thinkingplanet.ui.question

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thinkingplanet.ui.theme.*

@Composable
fun QuestionScreen(
    packId: String,
    dimension: String,
    levelId: String,
    onFinish: (stars: Int) -> Unit,
    viewModel: QuestionViewModel = hiltViewModel()
) {
    LaunchedEffect(packId, dimension, levelId) { viewModel.load(packId, dimension, levelId) }
    val s by viewModel.state.collectAsState()

    LaunchedEffect(s.isFinished) {
        if (s.isFinished) onFinish(s.finalStars)
    }

    val q = s.question ?: return

    Row(Modifier.fillMaxSize().background(Background)) {
        // LEFT: Question panel (55%)
        Column(
            Modifier.weight(1.1f).fillMaxHeight()
                .background(Brush.verticalGradient(listOf(Color(0xFFFFF0F6), Color(0xFFFCE7F3))))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Progress row
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("🌸 第${s.currentIndex + 1}题 / ${s.total}题", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF9D174D))
            }
            LinearProgressIndicator(
                progress = { (s.currentIndex + 1f) / s.total },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = PinkSecondary,
                trackColor = PinkPrimary.copy(alpha = 0.3f)
            )
            // Mascot + hint bubble
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                Box(Modifier.size(52.dp).background(Brush.radialGradient(listOf(PinkPrimary, PurplePrimary)), CircleShape), contentAlignment = Alignment.Center) {
                    Text("🧚", fontSize = 28.sp)
                }
                Surface(shape = RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp), color = Color.White, shadowElevation = 4.dp) {
                    Text(
                        text = if (s.showHint) "💡 提示：${q.hint}" else "加油！仔细想想哦 ✨",
                        modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp,
                        color = Color(0xFF6B21A8)
                    )
                }
            }
            // Question card
            Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(6.dp), modifier = Modifier.fillMaxWidth()) {
                Text(q.text, modifier = Modifier.padding(20.dp), fontSize = 18.sp, fontWeight = FontWeight.Medium, color = OnSurface, lineHeight = 28.sp)
            }
        }

        // RIGHT: Options panel (45%)
        Column(
            Modifier.weight(0.9f).fillMaxHeight().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
        ) {
            Text("选择正确答案：", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF9D174D))
            val labels = listOf("A", "B", "C", "D")
            q.options.forEachIndexed { i, option ->
                val isSelected = s.selectedIndex == i
                val isCorrect  = s.isAnswered && i == q.answerIndex
                val isWrong    = s.isAnswered && isSelected && !isCorrect
                val bgColor = when {
                    isCorrect -> Correct
                    isWrong   -> Incorrect
                    isSelected -> PinkPrimary
                    else -> Surface
                }
                val borderColor = if (isSelected || isCorrect || isWrong) bgColor else PinkPrimary.copy(alpha = 0.4f)
                Row(
                    Modifier.fillMaxWidth()
                        .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                        .background(bgColor, RoundedCornerShape(16.dp))
                        .clickable(enabled = !s.isAnswered) { viewModel.selectAnswer(i) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(Modifier.size(32.dp).background(Color.White.copy(alpha = 0.3f), CircleShape), contentAlignment = Alignment.Center) {
                        Text(labels[i], fontWeight = FontWeight.Bold, color = if (isSelected || isCorrect || isWrong) Color.White else PinkSecondary, fontSize = 14.sp)
                    }
                    Text(option, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (isSelected || isCorrect || isWrong) Color.White else OnSurface)
                }
            }
            if (s.isAnswered) {
                Button(
                    onClick = { viewModel.next() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    Text(if (s.currentIndex + 1 >= s.total) "查看结果 ✨" else "下一题 →", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                OutlinedButton(
                    onClick = { viewModel.showHint() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("💡 需要提示？", fontSize = 14.sp, color = PurplePrimary)
                }
            }
        }
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add .
git commit -m "feat: question screen — tablet landscape split layout"
```

---

## Task 13: Result & Honor Screens

**Files:**
- Create: `ui/result/ResultScreen.kt`
- Create: `ui/honor/HonorScreen.kt`

- [ ] **Step 1: Create `ResultScreen.kt`**

```kotlin
package com.thinkingplanet.ui.result

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thinkingplanet.ui.theme.*

@Composable
fun ResultScreen(stars: Int, onDone: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "star_scale"
    )
    val (title, emoji, message) = when (stars) {
        3 -> Triple("太完美了！", "🎉", "你全部答对啦！向导精灵为你骄傲！")
        2 -> Triple("做得很好！", "🌸", "再努力一点就能全对了！")
        1 -> Triple("继续加油！", "🌟", "勇敢的尝试！下次一定更好！")
        else -> Triple("再试一次吧", "💪", "没关系，再看看题目提示哦～")
    }

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFFFF0F6), Color(0xFFEDE9FE)))), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text(emoji, fontSize = 72.sp, modifier = Modifier.scale(scale))
            Text(title, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B21A8))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { i ->
                    Text(
                        if (i < stars) "⭐" else "☆",
                        fontSize = 48.sp,
                        modifier = Modifier.scale(if (i < stars) scale else 1f)
                    )
                }
            }
            Surface(shape = RoundedCornerShape(16.dp), color = Color.White.copy(alpha = 0.8f), shadowElevation = 4.dp) {
                Text(message, Modifier.padding(16.dp), fontSize = 16.sp, color = Color(0xFF6B21A8))
            }
            Button(
                onClick = onDone,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkSecondary),
                modifier = Modifier.height(56.dp).width(200.dp)
            ) {
                Text("返回地图", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
```

- [ ] **Step 2: Create `HonorScreen.kt`**

```kotlin
package com.thinkingplanet.ui.honor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thinkingplanet.domain.model.MedalLevel
import com.thinkingplanet.domain.model.TrophyLevel
import com.thinkingplanet.ui.theme.*

@Composable
fun HonorScreen(onBack: () -> Unit, viewModel: HonorViewModel = hiltViewModel()) {
    val medals by viewModel.medals.collectAsState()
    val trophies by viewModel.trophies.collectAsState()
    val title by viewModel.currentTitle.collectAsState()

    Column(Modifier.fillMaxSize().background(Background)) {
        Row(
            Modifier.fillMaxWidth().background(PurplePrimary).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) { Text("←", fontSize = 20.sp, color = Color.White) }
            Text("🏆 荣誉殿堂", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            Spacer(Modifier.weight(1f))
            Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.2f)) {
                Text("${title.emoji} ${title.label}", Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text("维度勋章", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OnSurface) }
            items(medals) { medal ->
                Row(
                    Modifier.fillMaxWidth().background(Surface, RoundedCornerShape(14.dp)).padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${medal.dimension.emoji} ${medal.packId} · ${medal.dimension.label}", fontSize = 14.sp)
                    Text(
                        when (medal.level) { MedalLevel.GOLD -> "🥇 金牌"; MedalLevel.SILVER -> "🥈 银牌"; MedalLevel.BRONZE -> "🥉 铜牌"; MedalLevel.NONE -> "○ 未获得" },
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold
                    )
                }
            }
            item { Spacer(Modifier.height(8.dp)); Text("学期奖杯", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OnSurface) }
            items(trophies) { (packId, level) ->
                Row(
                    Modifier.fillMaxWidth().background(Surface, RoundedCornerShape(14.dp)).padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(packId.replace("grade", "").replace("_upper", "年级上").replace("_lower", "年级下"), fontSize = 14.sp)
                    Text(
                        when (level) { TrophyLevel.GOLD -> "🥇 金奖杯"; TrophyLevel.SILVER -> "🥈 银奖杯"; TrophyLevel.BRONZE -> "🥉 铜奖杯"; TrophyLevel.NONE -> "○ 未获得" },
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
```

- [ ] **Step 3: Create `HonorViewModel.kt`**

```kotlin
package com.thinkingplanet.ui.honor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thinkingplanet.domain.model.*
import com.thinkingplanet.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HonorViewModel @Inject constructor(private val progressRepository: ProgressRepository) : ViewModel() {
    val medals: StateFlow<List<Medal>> = progressRepository.getMedals("").map { emptyList<Medal>() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val trophies: StateFlow<List<Pair<String, TrophyLevel>>> = progressRepository.getAllTrophies()
        .map { it.entries.map { e -> e.key to e.value } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val currentTitle: StateFlow<PlayerTitle> = progressRepository.getTotalStars()
        .map { stars -> if (stars >= 200) PlayerTitle.STAR_COLLECTOR else PlayerTitle.NONE }
        .stateIn(viewModelScope, SharingStarted.Eagerly, PlayerTitle.NONE)
}
```

- [ ] **Step 4: Commit**

```bash
git add .
git commit -m "feat: result screen with star animation and honor hall"
```

---

## Task 14: Grade 1 Lower Semester JSON + Placeholder JSONs

**Files:**
- Create: `assets/packs/grade1_lower.json`
- Create: placeholder JSONs for grades 2–6

- [ ] **Step 1: Create `grade1_lower.json`**

```json
{
  "pack_id": "grade1_lower",
  "grade": 1,
  "is_upper": false,
  "dimensions": {
    "MATH": [{"level_id": "math_01", "questions": [
      {"id": "m01_q01","type":"CHOICE","text":"36 + 24 = ?","image_res":null,"options":["50","58","60","62"],"answer_index":2,"hint":"先算个位：6+4=10，进1"},
      {"id": "m01_q02","type":"CHOICE","text":"75 - 38 = ?","image_res":null,"options":["33","37","43","47"],"answer_index":1,"hint":"可以拆开算：75-30-8"},
      {"id": "m01_q03","type":"CHOICE","text":"一打铅笔有12支，买了3打，共几支？","image_res":null,"options":["24","30","36","42"],"answer_index":2,"hint":"12×3=?"},
      {"id": "m01_q04","type":"CHOICE","text":"48 ÷ 6 = ?","image_res":null,"options":["6","7","8","9"],"answer_index":2,"hint":"想想6的乘法表"},
      {"id": "m01_q05","type":"CHOICE","text":"100以内最大的奇数是？","image_res":null,"options":["97","98","99","100"],"answer_index":2,"hint":"奇数是不能被2整除的数"}
    ]}],
    "LOGIC": [{"level_id": "logic_01", "questions": [
      {"id":"l01_q01","type":"CHOICE","text":"所有的鸟都会飞，企鹅是鸟，所以企鹅？","image_res":null,"options":["会飞","不会飞","这个推理有问题","不确定"],"answer_index":2,"hint":"检查前提是否正确"},
      {"id":"l01_q02","type":"CHOICE","text":"如果A>B，B>C，那么A和C的关系是？","image_res":null,"options":["A<C","A=C","A>C","无法判断"],"answer_index":2,"hint":"传递性规律"},
      {"id":"l01_q03","type":"CHOICE","text":"甲乙丙三人，只有一人说了真话：甲说乙说谎，乙说丙说谎，丙说甲说谎。谁说了真话？","image_res":null,"options":["甲","乙","丙","无法判断"],"answer_index":1,"hint":"逐一假设验证"},
      {"id":"l01_q04","type":"CHOICE","text":"小红比小明重，小明比小亮重，小红比小亮？","image_res":null,"options":["轻","一样重","重","无法判断"],"answer_index":2,"hint":"传递比较"},
      {"id":"l01_q05","type":"CHOICE","text":"数字1到10中，偶数的个数是？","image_res":null,"options":["4","5","6","7"],"answer_index":1,"hint":"2,4,6,8,10"}
    ]}],
    "SPATIAL": [{"level_id": "spatial_01", "questions": [
      {"id":"s01_q01","type":"CHOICE","text":"一个长方体有几个面？","image_res":null,"options":["4","5","6","8"],"answer_index":2,"hint":"上下前后左右"},
      {"id":"s01_q02","type":"CHOICE","text":"把一张正方形纸对折两次，打开后有几个格子？","image_res":null,"options":["2","3","4","6"],"answer_index":2,"hint":"每折一次翻倍"},
      {"id":"s01_q03","type":"CHOICE","text":"时钟上3点整，时针和分针成什么角？","image_res":null,"options":["直角","钝角","锐角","平角"],"answer_index":0,"hint":"3到12是90°"},
      {"id":"s01_q04","type":"CHOICE","text":"从左数第3个，从右数是第几个？共7个。","image_res":null,"options":["3","4","5","6"],"answer_index":2,"hint":"7-3+1=?"},
      {"id":"s01_q05","type":"CHOICE","text":"一个正方形被对角线分成几个三角形？","image_res":null,"options":["2","3","4","6"],"answer_index":0,"hint":"画一条对角线试试"}
    ]}],
    "PATTERN": [{"level_id": "pattern_01", "questions": [
      {"id":"p01_q01","type":"CHOICE","text":"1, 4, 9, 16, 25, ___","image_res":null,"options":["30","36","49","64"],"answer_index":1,"hint":"1²,2²,3²..."},
      {"id":"p01_q02","type":"CHOICE","text":"3, 6, 12, 24, ___","image_res":null,"options":["36","42","48","56"],"answer_index":2,"hint":"每次乘以2"},
      {"id":"p01_q03","type":"CHOICE","text":"△○△○○△○○○___","image_res":null,"options":["△","○","△△","○○"],"answer_index":0,"hint":"三角形每次多一个重复"},
      {"id":"p01_q04","type":"CHOICE","text":"1+2=3, 2+3=5, 3+4=7, 4+5=?","image_res":null,"options":["8","9","10","11"],"answer_index":1,"hint":"相邻两数相加"},
      {"id":"p01_q05","type":"CHOICE","text":"AABBAABB中，第10个字母是？","image_res":null,"options":["A","B","A或B","无法判断"],"answer_index":1,"hint":"4个一循环，10÷4余2"}
    ]}],
    "CREATIVE": [{"level_id": "creative_01", "questions": [
      {"id":"c01_q01","type":"CHOICE","text":"如果你能发明一种新颜色，你会叫它什么？","image_res":null,"options":["紫蓝绿","天空粉","彩虹灰","星星金"],"answer_index":3,"hint":"没有标准答案，选最有创意的"},
      {"id":"c01_q02","type":"CHOICE","text":"怎么让一首歌变得更有趣？","image_res":null,"options":["唱快一倍","加入动物叫声","只哼旋律","大声唱"],"answer_index":1,"hint":"想想最特别的方式"},
      {"id":"c01_q03","type":"CHOICE","text":"用10根火柴可以搭几个正方形？","image_res":null,"options":["1个","2个","3个","取决于搭法"],"answer_index":3,"hint":"不同搭法共用边数不同"},
      {"id":"c01_q04","type":"CHOICE","text":"学校没有铃声了，用什么代替最有创意？","image_res":null,"options":["老师喊","鸟叫声广播","手机闹钟","跺脚"],"answer_index":1,"hint":"想想有趣又自然的声音"},
      {"id":"c01_q05","type":"CHOICE","text":"把两个不同的故事结合，最有趣的是？","image_res":null,"options":["白雪公主+西游记","灰姑娘+灰姑娘","小红帽+小红帽","三只小猪+三只小猪"],"answer_index":0,"hint":"不同元素碰撞才有创意"}
    ]}]
  }
}
```

- [ ] **Step 2: Create placeholder JSONs for grades 2–6**

Create `assets/packs/grade2_upper.json` (and repeat pattern for grade2_lower through grade6_lower):

```json
{
  "pack_id": "grade2_upper",
  "grade": 2,
  "is_upper": true,
  "dimensions": {
    "MATH":    [{"level_id":"math_01","questions":[{"id":"placeholder","type":"CHOICE","text":"（即将推出）","image_res":null,"options":["A","B","C","D"],"answer_index":0,"hint":""}]}],
    "LOGIC":   [{"level_id":"logic_01","questions":[{"id":"placeholder","type":"CHOICE","text":"（即将推出）","image_res":null,"options":["A","B","C","D"],"answer_index":0,"hint":""}]}],
    "SPATIAL": [{"level_id":"spatial_01","questions":[{"id":"placeholder","type":"CHOICE","text":"（即将推出）","image_res":null,"options":["A","B","C","D"],"answer_index":0,"hint":""}]}],
    "PATTERN": [{"level_id":"pattern_01","questions":[{"id":"placeholder","type":"CHOICE","text":"（即将推出）","image_res":null,"options":["A","B","C","D"],"answer_index":0,"hint":""}]}],
    "CREATIVE":[{"level_id":"creative_01","questions":[{"id":"placeholder","type":"CHOICE","text":"（即将推出）","image_res":null,"options":["A","B","C","D"],"answer_index":0,"hint":""}]}]
  }
}
```

Create the same structure for: `grade2_lower`, `grade3_upper`, `grade3_lower`, `grade4_upper`, `grade4_lower`, `grade5_upper`, `grade5_lower`, `grade6_upper`, `grade6_lower` — changing only `pack_id`, `grade`, and `is_upper`.

- [ ] **Step 3: Commit**

```bash
git add .
git commit -m "feat: grade1 full question bank + placeholder packs for grades 2-6"
```

---

## Task 15: Final Build Verification

- [ ] **Step 1: Run full test suite**

```bash
./gradlew test
```
Expected: All unit tests PASS

- [ ] **Step 2: Build release APK**

```bash
./gradlew assembleDebug
```
Expected: BUILD SUCCESSFUL, APK at `app/build/outputs/apk/debug/app-debug.apk`

- [ ] **Step 3: Install on tablet and smoke test**
  - Launch app → World Map shows 6 worlds, 樱花仙境 unlocked
  - Tap 樱花仙境 → Chapter screen shows 5 dimensions
  - Tap 数学 → Level list shows math_01
  - Tap math_01 → Landscape split question screen loads
  - Answer all 5 questions → Result screen shows correct star count
  - Back to World Map → progress persisted

- [ ] **Step 4: Final commit**

```bash
git add .
git commit -m "chore: verified full flow on tablet — MVP complete"
```

---

## Self-Review Notes

**Spec coverage check:**
- ✅ 6 worlds × 2 chapters = 12 packs — covered by WorldTheme enum + JSON files
- ✅ 5 thinking dimensions — Dimension enum in World.kt
- ✅ 4-layer honor (stars → medals → trophies → titles) — all 4 covered across Tasks 4 + 12 + 13
- ✅ Tablet landscape split layout — QuestionScreen Row with weight(1.1f)/weight(0.9f)
- ✅ Girl-themed colors — Color.kt with full pastel palette
- ✅ Mascot guide character — 🧚 in QuestionScreen and WorldMapScreen
- ✅ Expandable JSON packs — QuestionPackLoader reads from assets/packs/
- ✅ No login / single user — ProfileEntity with hardcoded id=1
- ✅ Completely free — no IAP code anywhere

**Known gaps (intentional MVP scope-outs):**
- HonorViewModel.medals currently emits empty list — needs `getAllMedals()` method added to ProgressRepository to query across all packs. Add in post-MVP.
- Lottie celebration animation not wired — ResultScreen uses Compose `animateFloatAsState` as fallback. Wire Lottie in post-MVP.
- Placeholder JSON packs for grades 2–6 contain dummy questions — fill in real content as content work, not code work.
