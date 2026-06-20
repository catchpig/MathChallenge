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
