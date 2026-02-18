package com.wegielek.katanaflashlight.domain.controller

interface ServiceController {
    fun startFlashlightService()

    fun stopFlashlightService()

    fun isFlashlightServiceRunning(): Boolean

    fun onServiceStarted()

    fun onServiceStopped()

    fun onAcceleration(
        x: Float,
        y: Float,
        z: Float,
    )
}
