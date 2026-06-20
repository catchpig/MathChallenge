package com.thinkingplanet.domain.usecase

import javax.inject.Inject

class CalculateStarsUseCase @Inject constructor() {
    operator fun invoke(correct: Int, total: Int): Int {
        if (total == 0) return 0
        val rate = correct.toFloat() / total
        return when {
            rate >= 1.0f -> 3
            rate >= 0.8f -> 2
            rate >= 0.6f -> 1
            else -> 0
        }
    }
}
