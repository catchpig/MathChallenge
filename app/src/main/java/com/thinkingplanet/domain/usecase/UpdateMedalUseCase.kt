package com.thinkingplanet.domain.usecase

import com.thinkingplanet.domain.model.MedalLevel
import javax.inject.Inject

class UpdateMedalUseCase @Inject constructor() {
    operator fun invoke(averageStars: Float): MedalLevel = when {
        averageStars >= 3.0f -> MedalLevel.GOLD
        averageStars >= 2.0f -> MedalLevel.SILVER
        averageStars >= 1.0f -> MedalLevel.BRONZE
        else -> MedalLevel.NONE
    }
}
