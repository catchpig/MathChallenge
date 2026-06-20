package com.thinkingplanet.data.local.entity

import androidx.room.Entity

@Entity(tableName = "medals", primaryKeys = ["packId", "dimension"])
data class MedalEntity(
    val packId: String,
    val dimension: String,
    val medalLevel: String = "NONE"
)
