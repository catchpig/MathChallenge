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
