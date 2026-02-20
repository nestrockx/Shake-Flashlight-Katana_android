package com.wegielek.katanaflashlight.data.controller

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.wegielek.katanaflashlight.domain.controller.ServiceController
import com.wegielek.katanaflashlight.domain.usecase.KeepCpuAwakeUseCase
import com.wegielek.katanaflashlight.domain.usecase.SlashDetectionUseCase
import com.wegielek.katanaflashlight.domain.usecase.TurnOffFlashlightUseCase
import com.wegielek.katanaflashlight.service.FlashlightForegroundService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ServiceControllerImpl(
    private val context: Context,
    private val keepCpuAwake: KeepCpuAwakeUseCase,
    private val slashDetection: SlashDetectionUseCase,
    private val turnOffFlashlight: TurnOffFlashlightUseCase,
) : ServiceController {
    private val _isRunning = MutableStateFlow(false)
    override val isRunning: StateFlow<Boolean> = _isRunning

    override fun startFlashlightService() {
        if (isFlashlightServiceRunning()) return

        val intent = Intent(context, FlashlightForegroundService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    override fun stopFlashlightService() {
        if (!isFlashlightServiceRunning()) return

        val intent = Intent(context, FlashlightForegroundService::class.java)
        context.stopService(intent)
    }

    override fun isFlashlightServiceRunning(): Boolean {
        try {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val services = manager.getRunningServices(Int.MAX_VALUE)
            for (serviceInfo in services) {
                if (serviceInfo.service.className == FlashlightForegroundService::class.java.name) {
                    return true
                }
            }
        } catch (_: Throwable) {
            // ignore
        }
        return false
    }

    override fun onServiceStarted() {
        keepCpuAwake(true)
        _isRunning.value = true
    }

    override fun onServiceStopped() {
        keepCpuAwake(false)
        turnOffFlashlight()
        _isRunning.value = false
    }

    override suspend fun onAcceleration(
        x: Float,
        y: Float,
        z: Float,
    ) {
        slashDetection(x, y, z)
    }
}
