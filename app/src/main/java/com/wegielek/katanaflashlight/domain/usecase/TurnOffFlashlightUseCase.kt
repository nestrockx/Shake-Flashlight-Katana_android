package com.wegielek.katanaflashlight.domain.usecase

import com.wegielek.katanaflashlight.domain.controller.FlashlightController

class TurnOffFlashlightUseCase(
    private val flashlightController: FlashlightController,
) {
    operator fun invoke() {
        flashlightController.turnOffFlashlight()
    }
}
