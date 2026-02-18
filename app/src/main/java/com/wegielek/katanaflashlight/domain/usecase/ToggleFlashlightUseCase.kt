package com.wegielek.katanaflashlight.domain.usecase

import com.wegielek.katanaflashlight.domain.controller.FlashlightController
import com.wegielek.katanaflashlight.domain.controller.VibrationController
import com.wegielek.katanaflashlight.domain.repository.SettingsRepository

class ToggleFlashlightUseCase(
    private val flashlightController: FlashlightController,
    private val vibrationController: VibrationController,
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke() {
        flashlightController.toggleFlashlight()

        if (settingsRepository.isVibrationEnabled()) {
            vibrationController.vibrate()
        }
    }
}
