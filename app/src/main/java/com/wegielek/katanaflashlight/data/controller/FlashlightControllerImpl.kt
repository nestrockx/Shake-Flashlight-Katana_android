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
    private var isFlashEnabled: Boolean = false

//    private val torchCallback =
//        object : CameraManager.TorchCallback() {
//            override fun onTorchModeChanged(
//                id: String,
//                enabled: Boolean,
//            ) {
//                if (id == cameraId) {
//                    isFlashEnabled = enabled
//                }
//            }
//        }

    override fun initialize() {
        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = findBackCameraWithFlash(cameraManager)

//        cameraManager?.registerTorchCallback(
//            torchCallback,
//            null, // callback on calling thread
//        )
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

    override fun toggleFlashlight(level: Int) {
        if (hasStrengthLevels()) {
            if (!isFlashEnabled) {
                cameraId?.let { cameraManager?.turnOnTorchWithStrengthLevel(it, level) }
            } else {
                cameraId?.let { cameraManager?.setTorchMode(it, false) }
            }
        } else {
            cameraId?.let { cameraManager?.setTorchMode(it, !isFlashEnabled) }
        }
        isFlashEnabled = !isFlashEnabled
    }

    override fun turnOffFlashlight() {
        cameraId?.let { cameraManager?.setTorchMode(it, false) }
    }

    override fun setStrength(level: Int) {
        if (!isFlashEnabled) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            cameraId?.let { cameraManager?.turnOnTorchWithStrengthLevel(it, level) }
        }
    }

    override fun isFlashlightEnabled(): Boolean = isFlashEnabled

    private fun findBackCameraWithFlash(cameraManager: CameraManager?): String? {
        cameraManager?.cameraIdList?.forEach { id ->
            val characteristics = cameraManager.getCameraCharacteristics(id)

            val hasFlash =
                characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true

            val isBackCamera =
                characteristics.get(CameraCharacteristics.LENS_FACING) ==
                    CameraCharacteristics.LENS_FACING_BACK

            if (hasFlash && isBackCamera) {
                return id
            }
        }
        return null
    }
}
