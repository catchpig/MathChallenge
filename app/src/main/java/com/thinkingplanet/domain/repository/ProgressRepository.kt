package com.thinkingplanet.domain.repository

import com.thinkingplanet.domain.model.Dimension
import com.thinkingplanet.domain.model.MedalLevel
import com.thinkingplanet.domain.model.TrophyLevel
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun getLevelStars(packId: String): Flow<Map<String, Int>>
    fun getMedals(packId: String): Flow<Map<Dimension, MedalLevel>>
    fun getAllMedals(): Flow<List<Triple<String, Dimension, MedalLevel>>>
    fun getAllTrophies(): Flow<Map<String, TrophyLevel>>
    fun getTotalStars(): Flow<Int>
    suspend fun saveLevelResult(packId: String, levelId: String, dimension: Dimension, stars: Int)
    suspend fun saveMedal(packId: String, dimension: Dimension, level: MedalLevel)
    suspend fun saveTrophy(packId: String, level: TrophyLevel)
}
