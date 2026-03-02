package com.wegielek.katanaflashlight.domain.usecase

import com.wegielek.katanaflashlight.domain.controller.FlashlightController
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class TurnOffFlashlightUseCaseTest {
    val mFlashlightController = mockk<FlashlightController>(relaxed = true)

    private lateinit var turnOffFlashlightUseCase: TurnOffFlashlightUseCase

    @Before
    fun setup() {
        turnOffFlashlightUseCase = TurnOffFlashlightUseCase(mFlashlightController)
    }

    @Test
    fun testTurnOffFlashlight() {
        turnOffFlashlightUseCase.invoke()

        verify(exactly = 1) { mFlashlightController.turnOffFlashlight() }
    }
}
