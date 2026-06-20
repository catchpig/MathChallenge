package com.thinkingplanet.data.repository

import com.thinkingplanet.data.local.dao.*
import com.thinkingplanet.data.local.entity.*
import com.thinkingplanet.domain.model.*
import com.thinkingplanet.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProgressRepositoryImpl @Inject constructor(
    private val levelProgressDao: LevelProgressDao,
    private val medalDao: MedalDao,
    private val trophyDao: TrophyDao,
    private val profileDao: ProfileDao
) : ProgressRepository {

    override fun getLevelStars(packId: String): Flow<Map<String, Int>> =
        levelProgressDao.getByPack(packId).map { list ->
            list.associate { it.levelId to it.bestStars }
        }

    override fun getMedals(packId: String): Flow<Map<Dimension, MedalLevel>> =
        medalDao.getByPack(packId).map { list ->
            list.associate { entity ->
                Dimension.valueOf(entity.dimension) to MedalLevel.valueOf(entity.medalLevel)
            }
        }

    override fun getAllMedals(): Flow<List<Triple<String, Dimension, MedalLevel>>> =
        medalDao.getAll().map { list ->
            list.map { Triple(it.packId, Dimension.valueOf(it.dimension), MedalLevel.valueOf(it.medalLevel)) }
        }

    override fun getAllTrophies(): Flow<Map<String, TrophyLevel>> =
        trophyDao.getAll().map { list ->
            list.associate { it.packId to TrophyLevel.valueOf(it.trophyLevel) }
        }

    override fun getTotalStars(): Flow<Int> =
        profileDao.get().map { it?.totalStars ?: 0 }

    override suspend fun saveLevelResult(packId: String, levelId: String, dimension: Dimension, stars: Int) {
        val existing = levelProgressDao.getByLevel(levelId)
        val best = maxOf(stars, existing?.bestStars ?: 0)
        levelProgressDao.upsert(LevelProgressEntity(levelId, packId, dimension.name, best, (existing?.attempts ?: 0) + 1))
        val currentProfile = profileDao.get().first()
        val currentTotal = currentProfile?.totalStars ?: 0
        profileDao.upsert(ProfileEntity(totalStars = currentTotal + stars))
    }

    override suspend fun saveMedal(packId: String, dimension: Dimension, level: MedalLevel) {
        medalDao.upsert(MedalEntity(packId, dimension.name, level.name))
    }

    override suspend fun saveTrophy(packId: String, level: TrophyLevel) {
        trophyDao.upsert(TrophyEntity(packId, level.name))
    }
}
