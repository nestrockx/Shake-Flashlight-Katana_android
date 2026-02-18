package com.wegielek.katanaflashlight.data.controller

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import com.wegielek.katanaflashlight.domain.controller.FlashlightController

class FlashlightControllerImpl(
    private val context: Context,
) : FlashlightController {
    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null
    private var isFlashOn: Boolean = false

    private val torchCallback =
        object : CameraManager.TorchCallback() {
            override fun onTorchModeChanged(
                id: String,
                enabled: Boolean,
            ) {
                if (id == cameraId) {
                    isFlashOn = enabled
                }
            }
        }

    override fun initialize() {
        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager?.cameraIdList?.firstOrNull()

        cameraManager?.registerTorchCallback(
            torchCallback,
            null, // callback on calling thread
        )
    }

    override fun hasFlashlight(): Boolean = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

    override fun hasStrengthLevels(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val level =
                cameraId
                    ?.let {
                        cameraManager
                            ?.getCameraCharacteristics(it)
                    }?.get(CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL)
            return level != null && level > 1
        }
        return false
    }

    override fun getMaxStrengthLevel(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            cameraId
                ?.let {
                    cameraManager
                        ?.getCameraCharacteristics(it)
                }?.get(CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL) ?: 1
        } else {
            1
        }

    override fun toggleFlashlight() {
        cameraId?.let { cameraManager?.setTorchMode(it, !isFlashOn) }
    }

    override fun turnOffFlashlight() {
        cameraId?.let { cameraManager?.setTorchMode(it, false) }
    }

    override fun setStrength(level: Int) {
        if (!isFlashOn) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            cameraId?.let { cameraManager?.turnOnTorchWithStrengthLevel(it, level) }
        }
    }

    override fun isFlashlightOn(): Boolean = isFlashOn

    override fun release() {
        cameraManager?.unregisterTorchCallback(torchCallback)
    }
}
