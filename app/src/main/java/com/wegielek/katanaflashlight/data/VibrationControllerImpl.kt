package com.wegielek.katanaflashlight.data

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.ContextCompat
import com.wegielek.katanaflashlight.domain.controller.VibrationController

class VibrationControllerImpl(
    private val context: Context,
) : VibrationController {
    override fun vibrate() {
        val v = ContextCompat.getSystemService(context, Vibrator::class.java) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(300)
        }
    }
}
