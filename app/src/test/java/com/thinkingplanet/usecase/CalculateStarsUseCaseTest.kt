package com.thinkingplanet.usecase

import com.thinkingplanet.domain.usecase.CalculateStarsUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateStarsUseCaseTest {
    private val useCase = CalculateStarsUseCase()

    @Test fun `returns 1 star when correct rate is between 60 and 79`() {
        assertEquals(1, useCase(correct = 6, total = 10))
    }

    @Test fun `returns 2 stars when correct rate is between 80 and 99`() {
        assertEquals(2, useCase(correct = 8, total = 10))
    }

    @Test fun `returns 3 stars when all correct`() {
        assertEquals(3, useCase(correct = 10, total = 10))
    }

    @Test fun `returns 0 stars when correct rate below 60`() {
        assertEquals(0, useCase(correct = 5, total = 10))
    }
}
