package com.wegielek.katanaflashlight.domain.usecase

import com.wegielek.katanaflashlight.domain.detector.SlashDetector
import com.wegielek.katanaflashlight.domain.repository.CallStateRepository

class SlashDetectionUseCase(
    private val slashDetector: SlashDetector,
    private val toggleFlashlightUseCase: ToggleFlashlightUseCase,
    private val callStateRepository: CallStateRepository,
) {
    suspend operator fun invoke(
        x: Float,
        y: Float,
        z: Float,
    ) {
        if (callStateRepository.isCallActive()) return

        val slashDetected = slashDetector.onAcceleration(x, y, z)

        if (slashDetected) {
            toggleFlashlightUseCase()
        }
    }
}
