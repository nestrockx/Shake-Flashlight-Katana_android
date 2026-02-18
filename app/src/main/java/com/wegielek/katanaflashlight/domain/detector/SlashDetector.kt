package com.wegielek.katanaflashlight.domain.detector

import android.util.Log
import java.util.Timer
import java.util.TimerTask
import kotlin.math.pow
import kotlin.math.sqrt

class SlashDetector(
    private val sensitivityProvider: () -> Int,
) {
    private var coolDown = false
    private var motionStep1 = false
    private var motionStep2 = false
    private var motionStep3 = false

    fun onAcceleration(
        x: Float,
        y: Float,
        z: Float,
    ): Boolean {
        if (coolDown) return false

        val alpha = 0.8f
        val gravity = FloatArray(3)
        val linearAccelerationResult = FloatArray(3)

        gravity[0] = alpha * gravity[0] + (1 - alpha) * x
        gravity[1] = alpha * gravity[1] + (1 - alpha) * y
        gravity[2] = alpha * gravity[2] + (1 - alpha) * z

        linearAccelerationResult[0] = x - gravity[0]
        linearAccelerationResult[1] = y - gravity[1]
        linearAccelerationResult[2] = z - gravity[2]

        val accelerationX = linearAccelerationResult[0]
        val accelerationY = linearAccelerationResult[1]
        val accelerationZ = linearAccelerationResult[2]

        Log.d("Sensor data:", "linear acceleration:s $x $y $z")

        val averageAcceleration = sqrt(accelerationX.pow(2) + accelerationY.pow(2))
        val threshold = sensitivityProvider() * 3 + 7

        if (averageAcceleration >= threshold) {
            return handleMotionStep()
        }

        return false
    }

    private fun handleMotionStep(): Boolean =
        when {
            motionStep3 -> {
                reset()
                coolDown()
                true
            }

            motionStep2 -> {
                Timer().schedule(
                    object : TimerTask() {
                        override fun run() {
                            motionStep3 = true
                        }
                    },
                    150,
                )
                false
            }

            motionStep1 -> {
                Timer().schedule(
                    object : TimerTask() {
                        override fun run() {
                            motionStep2 = true
                        }
                    },
                    150,
                )
                false
            }

            else -> {
                Timer().schedule(
                    object : TimerTask() {
                        override fun run() {
                            motionStep1 = true
                        }
                    },
                    150,
                )
                Timer().schedule(
                    object : TimerTask() {
                        override fun run() {
                            reset()
                        }
                    },
                    650,
                )
                false
            }
        }

    private fun reset() {
        motionStep1 = false
        motionStep2 = false
        motionStep3 = false
    }

    private fun coolDown() {
        coolDown = true
        Timer().schedule(
            object : TimerTask() {
                override fun run() {
                    coolDown = false
                }
            },
            500,
        )
    }
}
