package com.thinkingplanet.data.repository

import com.thinkingplanet.data.source.QuestionPackLoader
import com.thinkingplanet.domain.model.Dimension
import com.thinkingplanet.domain.model.Level
import com.thinkingplanet.domain.repository.QuestionRepository
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val loader: QuestionPackLoader
) : QuestionRepository {
    private val cache = mutableMapOf<String, Map<Dimension, List<Level>>>()

    override fun getLevels(packId: String): Map<Dimension, List<Level>> =
        cache.getOrPut(packId) { loader.load(packId) }
}
