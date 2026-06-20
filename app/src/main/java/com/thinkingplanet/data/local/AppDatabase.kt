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
