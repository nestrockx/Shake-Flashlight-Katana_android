package com.wegielek.katanaflashlight.domain.controller

interface FlashlightController {
    fun initialize()

    fun hasFlashlight(): Boolean

    fun toggleFlashlight(level: Int = 1)

    fun turnOffFlashlight()

    fun setStrength(level: Int)

    fun getMaxStrengthLevel(): Int

    fun hasStrengthLevels(): Boolean

    fun isFlashlightEnabled(): Boolean
}
