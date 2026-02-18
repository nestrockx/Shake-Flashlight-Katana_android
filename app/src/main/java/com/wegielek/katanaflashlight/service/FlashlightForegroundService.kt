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
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.wegielek.katanaflashlight.MainActivity
import com.wegielek.katanaflashlight.R
import com.wegielek.katanaflashlight.domain.detector.SlashDetector
import com.wegielek.katanaflashlight.domain.usecase.KeepCpuAwakeUseCase
import com.wegielek.katanaflashlight.domain.usecase.ToggleFlashlightUseCase
import com.wegielek.katanaflashlight.domain.usecase.TurnOffFlashlightUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FlashlightForegroundService :
    Service(),
    SensorEventListener,
    KoinComponent {
    private val channelID = "ForegroundServiceChannel"
    private val logTag: String = FlashlightForegroundService::class.java.getSimpleName()

    private val turnOffFlashlightUseCase: TurnOffFlashlightUseCase by inject()
    private val toggleFlashlightUseCase: ToggleFlashlightUseCase by inject()
    private val keepCpuAwakeUseCase: KeepCpuAwakeUseCase by inject()

    private val slashDetector: SlashDetector by inject()

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)

    private fun isCallActive(): Boolean {
        val manager = applicationContext.getSystemService(AUDIO_SERVICE) as AudioManager
        return manager.mode == AudioManager.MODE_IN_CALL
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        keepCpuAwakeUseCase(true)

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
    private var accelerometerSensor: Sensor? = null

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_LINEAR_ACCELERATION) return

        if (!isCallActive()) {
            val slashDetected =
                slashDetector.onAcceleration(
                    event.values[0],
                    event.values[1],
                    event.values[2],
                )

            if (slashDetected) {
                serviceScope.launch {
                    toggleFlashlightUseCase()
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int,
    ) {
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister sensor listener
        sensorManager.unregisterListener(this)

        // Safely release wake lock
        keepCpuAwakeUseCase(false)

        // Ensure torch is off when service stops
        turnOffFlashlightUseCase()

        // Cancel all coroutines to avoid leaks
        serviceScope.cancel()

        Log.d(logTag, "FlashlightForegroundService destroyed")
    }
}
