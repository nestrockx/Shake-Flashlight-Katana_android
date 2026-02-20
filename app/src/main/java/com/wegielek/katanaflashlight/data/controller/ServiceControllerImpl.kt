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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ServiceControllerImpl(
    private val context: Context,
    private val keepCpuAwake: KeepCpuAwakeUseCase,
    private val slashDetection: SlashDetectionUseCase,
    private val turnOffFlashlight: TurnOffFlashlightUseCase,
) : ServiceController {
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
    }

    override fun onServiceStopped() {
        keepCpuAwake(false)
        turnOffFlashlight()
    }

    override suspend fun onAcceleration(
        x: Float,
        y: Float,
        z: Float,
    ) {
        slashDetection(x, y, z)
    }
}
