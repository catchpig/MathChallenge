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
    val packId: String,
    val grade: Int,
    val isUpper: Boolean,
    val dimensions: Map<Dimension, List<Level>>
)

data class Level(
    val levelId: String,
    val dimension: Dimension,
    val questions: List<Question>
)
