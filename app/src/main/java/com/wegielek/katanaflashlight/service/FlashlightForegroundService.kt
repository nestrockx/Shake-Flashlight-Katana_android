package com.wegielek.katanaflashlight.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.wegielek.katanaflashlight.MainActivity
import com.wegielek.katanaflashlight.Prefs.state
import com.wegielek.katanaflashlight.R
import com.wegielek.katanaflashlight.domain.controller.FlashlightController
import com.wegielek.katanaflashlight.domain.controller.VibrationController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue
import kotlin.math.pow
import kotlin.math.sqrt

class FlashlightForegroundService :
    Service(),
    SensorEventListener,
    KoinComponent {
    private val channelID = "ForegroundServiceChannel"
    private val logTag: String = FlashlightForegroundService::class.java.getSimpleName()

    private val flashlightController: FlashlightController by inject()
    private val vibrationController: VibrationController by inject()

    private lateinit var wakeLock: PowerManager.WakeLock

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)

    private fun isCallActive(): Boolean {
        val manager = applicationContext.getSystemService(AUDIO_SERVICE) as AudioManager
        return manager.mode == AudioManager.MODE_IN_CALL
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun acquire(): Runnable =
        Runnable {
            wakeLock.acquire(10 * 60 * 1000L)
            handler.postDelayed(acquire(), 10 * 60 * 1000L)
        }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        val powerManager = applicationContext.getSystemService(POWER_SERVICE) as PowerManager
        wakeLock =
            powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                logTag,
            )
        handler.post(acquire())

        if (intent?.extras?.getInt("close") == 1) {
            Toast
                .makeText(
                    this,
                    getString(R.string.katana_dismissed),
                    Toast.LENGTH_SHORT,
                ).show()
            stopSelf()
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        accelerometerSensor?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL,
            )
        }

        startForegroundService()
        return START_STICKY
    }

    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(1, notification)
    }

    private fun createNotification(): Notification {
        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingNotificationIntent =
            PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE,
            )

        val closeIntent = Intent(this, FlashlightForegroundService::class.java)
        closeIntent.putExtra("close", 1)
        val closePendingIntent =
            PendingIntent.getService(
                this,
                0,
                closeIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        return NotificationCompat
            .Builder(this, channelID)
            .setContentText(getString(R.string.katana_is_running))
            .setContentIntent(pendingNotificationIntent)
            .setSmallIcon(R.drawable.ic_katana_with_handle)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(
                R.drawable.ic_katana_with_handle,
                getString(R.string.close_notification),
                closePendingIntent,
            ).setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel =
                NotificationChannel(
                    channelID,
                    "Flashlight Channel",
                    NotificationManager.IMPORTANCE_LOW,
                ).apply {
                    setShowBadge(false)
                }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private lateinit var sensorManager: SensorManager
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var accelerometerSensor: Sensor? = null

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isCallActive()) {
            if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION) {
                val alpha = 0.8f
                val gravity = FloatArray(3)
                val linearAccelerationResult = FloatArray(3)

                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

                linearAccelerationResult[0] = event.values[0] - gravity[0]
                linearAccelerationResult[1] = event.values[1] - gravity[1]
                linearAccelerationResult[2] = event.values[2] - gravity[2]

                val x = linearAccelerationResult[0]
                val y = linearAccelerationResult[1]
                val z = linearAccelerationResult[2]

                val avg = sqrt(x.pow(2) + y.pow(2))
                Log.i("Sensor data:", "linear acceleration: $x $y $z")

                if (!coolDown) {
                    serviceScope.launch {
                        if (avg >= applicationContext.state.first().sensitivity * 3 + 7) {
                            if (motionStep3) {
                                flashlightController.toggleFlashlight()
                                if (applicationContext.state.first().vibrationOn) vibrationController.vibrate()

                                motionStep1 = false
                                motionStep2 = false
                                motionStep3 = false
                                coolDown = true
                                handler.postDelayed({ coolDown = false }, 500)
                            } else if (motionStep2) {
                                handler.postDelayed({ triggerMotionStepThree() }, 150)
                            } else if (motionStep1) {
                                handler.postDelayed({ triggerMotionStepTwo() }, 150)
                            } else {
                                triggerMotionStepOne()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int,
    ) {
    }

    private var coolDown: Boolean = false
    private var motionStep1: Boolean = false
    private var motionStep2: Boolean = false
    private var motionStep3: Boolean = false

    private val motionReset1 = Runnable { motionStep1 = false }
    private val motionReset2 = Runnable { motionStep2 = false }
    private val motionReset3 = Runnable { motionStep3 = false }

    private fun triggerMotionStepOne() {
        motionStep1 = true
        handler.postDelayed(motionReset1, 200)
    }

    private fun triggerMotionStepTwo() {
        motionStep2 = true
        handler.postDelayed(motionReset2, 200)
    }

    private fun triggerMotionStepThree() {
        motionStep3 = true
        handler.postDelayed(motionReset3, 200)
    }

    override fun onDestroy() {
        super.onDestroy()

        handler.removeCallbacks(motionReset1)
        handler.removeCallbacks(motionReset2)
        handler.removeCallbacks(motionReset3)

        // Unregister sensor listener
        sensorManager.unregisterListener(this)

        // Remove all pending callbacks
        handler.removeCallbacksAndMessages(null)

        // Safely release wake lock
        if (wakeLock.isHeld) {
            wakeLock.release()
        }

        // Ensure torch is off when service stops
        flashlightController.turnOffFlashlight()

        // Cancel all coroutines to avoid leaks
        serviceScope.cancel()

        Log.d(logTag, "FlashlightForegroundService destroyed")
    }
}
