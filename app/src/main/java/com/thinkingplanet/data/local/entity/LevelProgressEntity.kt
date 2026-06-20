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
