package com.thinkingplanet.domain.repository

import com.thinkingplanet.domain.model.Dimension
import com.thinkingplanet.domain.model.Level

interface QuestionRepository {
    fun getLevels(packId: String): Map<Dimension, List<Level>>
}
