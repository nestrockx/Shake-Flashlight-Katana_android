package com.wegielek.katanaflashlight.domain.controller

interface ServiceController {
    fun startFlashlightService()

    fun stopFlashlightService()

    fun isFlashlightServiceRunning(): Boolean
}
