package com.thinkingplanet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trophies")
data class TrophyEntity(
    @PrimaryKey val packId: String,
    val trophyLevel: String = "NONE"
)
