package com.wegielek.katanaflashlight.domain.detector

import kotlin.math.pow
import kotlin.math.sqrt

class SlashDetector(
    private val sensitivityProvider: () -> Int,
) {
    private var firstShakeTime = 0L
    private var shakeCount = 0
    private var lastShakeTime = 0L
    private var cooldownUntil = 0L

    companion object {
        private const val TRIPLE_SHAKE_WINDOW_MS = 800L // more time for triple shake
        private const val COOLDOWN_MS = 1000L
        private const val MIN_SHAKE_GAP_MS = 200L
    }

    fun onAcceleration(
        x: Float,
        y: Float,
        z: Float,
    ): Boolean {
        val now = System.currentTimeMillis()

        if (now < cooldownUntil) return false

        val magnitude = sqrt(x.pow(2) + y.pow(2) + z.pow(2))

        val threshold = sensitivityProvider() * 3 + 7

        if (magnitude > threshold) {
            // Ignore very fast spikes
            if (now - lastShakeTime < MIN_SHAKE_GAP_MS) return false

            lastShakeTime = now

            if (shakeCount == 0) {
                firstShakeTime = now
                shakeCount = 1
                return false
            }

            if (now - firstShakeTime > TRIPLE_SHAKE_WINDOW_MS) {
                firstShakeTime = now
                shakeCount = 1
                return false
            }

            shakeCount++

            if (shakeCount == 3) {
                shakeCount = 0
                cooldownUntil = now + COOLDOWN_MS
                return true
            }
        }

        return false
    }
}
