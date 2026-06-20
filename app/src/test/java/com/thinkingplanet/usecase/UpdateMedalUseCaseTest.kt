package com.thinkingplanet.usecase

import com.thinkingplanet.domain.model.MedalLevel
import com.thinkingplanet.domain.usecase.UpdateMedalUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateMedalUseCaseTest {
    private val useCase = UpdateMedalUseCase()

    @Test fun `returns BRONZE when average stars between 1 and 1_9`() {
        assertEquals(MedalLevel.BRONZE, useCase(averageStars = 1.5f))
    }

    @Test fun `returns SILVER when average stars between 2 and 2_9`() {
        assertEquals(MedalLevel.SILVER, useCase(averageStars = 2.5f))
    }

    @Test fun `returns GOLD when average stars is 3`() {
        assertEquals(MedalLevel.GOLD, useCase(averageStars = 3.0f))
    }

    @Test fun `returns NONE when average stars below 1`() {
        assertEquals(MedalLevel.NONE, useCase(averageStars = 0.5f))
    }
}
