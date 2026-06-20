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
