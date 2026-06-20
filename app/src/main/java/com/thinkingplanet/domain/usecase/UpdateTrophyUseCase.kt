package com.thinkingplanet.domain.usecase

import com.thinkingplanet.domain.model.MedalLevel
import com.thinkingplanet.domain.model.TrophyLevel
import javax.inject.Inject

class UpdateTrophyUseCase @Inject constructor() {
    operator fun invoke(medals: List<MedalLevel>): TrophyLevel {
        if (medals.size < 5) return TrophyLevel.NONE
        return when {
            medals.all { it == MedalLevel.GOLD } -> TrophyLevel.GOLD
            medals.all { it >= MedalLevel.SILVER } -> TrophyLevel.SILVER
            medals.none { it == MedalLevel.NONE } -> TrophyLevel.BRONZE
            else -> TrophyLevel.NONE
        }
    }
}
