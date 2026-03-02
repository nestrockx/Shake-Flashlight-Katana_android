package com.wegielek.katanaflashlight.domain.usecase

import com.wegielek.katanaflashlight.domain.controller.FlashlightController
import com.wegielek.katanaflashlight.domain.controller.VibrationController
import com.wegielek.katanaflashlight.domain.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ToggleFlashlightUseCaseTest {
    val mFlashlightController = mockk<FlashlightController>(relaxed = true)
    val mVibrationController = mockk<VibrationController>(relaxed = true)
    val mSettingsRepository = mockk<SettingsRepository>(relaxed = true)

    private lateinit var toggleFlashlightUseCase: ToggleFlashlightUseCase

    @Before
    fun setup() {
        toggleFlashlightUseCase =
            ToggleFlashlightUseCase(
                mFlashlightController,
                mVibrationController,
                mSettingsRepository,
            )
    }

    @Test
    fun testToggleFlashlightWithVibration() =
        runTest {
            coEvery { mSettingsRepository.getStrength() } returns 5
            coEvery { mSettingsRepository.isVibrationEnabled() } returns true

            toggleFlashlightUseCase.invoke()

            verify(exactly = 1) { mFlashlightController.toggleFlashlight(eq(5)) }
            verify(exactly = 1) { mVibrationController.vibrate() }
        }

    @Test
    fun testToggleFlashlightWithoutVibration() =
        runTest {
            coEvery { mSettingsRepository.getStrength() } returns 1
            coEvery { mSettingsRepository.isVibrationEnabled() } returns false

            toggleFlashlightUseCase.invoke()

            verify(exactly = 1) { mFlashlightController.toggleFlashlight(eq(1)) }
            verify(exactly = 0) { mVibrationController.vibrate() }
        }
}
