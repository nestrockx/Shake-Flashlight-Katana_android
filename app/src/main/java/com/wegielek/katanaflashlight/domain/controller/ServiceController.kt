package com.wegielek.katanaflashlight.domain.controller

import kotlinx.coroutines.flow.StateFlow

interface ServiceController {
    val isRunning: StateFlow<Boolean>

    fun startFlashlightService()

    fun stopFlashlightService()

    fun isFlashlightServiceRunning(): Boolean

    fun onServiceStarted()

    fun onServiceStopped()

    suspend fun onAcceleration(
        x: Float,
        y: Float,
        z: Float,
    )
}
