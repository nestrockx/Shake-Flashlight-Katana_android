package com.wegielek.katanaflashlight.domain.controller

interface FlashlightController {
    fun initialize()

    fun hasFlashlight(): Boolean

    fun toggleFlashlight()

    fun turnOffFlashlight()

    fun setStrength(level: Int)

    fun getMaxStrengthLevel(): Int

    fun hasStrengthLevels(): Boolean

    fun isFlashlightOn(): Boolean

    fun release()
}
