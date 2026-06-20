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
