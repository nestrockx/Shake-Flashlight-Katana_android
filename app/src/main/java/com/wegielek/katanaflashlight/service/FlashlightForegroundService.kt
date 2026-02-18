package com.wegielek.katanaflashlight.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.wegielek.katanaflashlight.MainActivity
import com.wegielek.katanaflashlight.R
import com.wegielek.katanaflashlight.data.sensor.LinearAccelerationSensor
import com.wegielek.katanaflashlight.domain.controller.ServiceController
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val CHANNEL_ID = "ForegroundServiceChannel"
private const val LOG_TAG = "FlashlightForegroundService"

class FlashlightForegroundService :
    Service(),
    KoinComponent {
    private val controller: ServiceController by inject()

    private lateinit var sensor: LinearAccelerationSensor

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        if (intent?.extras?.getInt("close") == 1) {
            Toast
                .makeText(
                    this,
                    getString(R.string.katana_dismissed),
                    Toast.LENGTH_SHORT,
                ).show()
            stopSelf()
        }

        controller.onServiceStarted()

        sensor =
            LinearAccelerationSensor(this) { x, y, z ->
                controller.onAcceleration(x, y, z)
            }

        sensor.start()

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
            .Builder(this, CHANNEL_ID)
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
                    CHANNEL_ID,
                    "Flashlight Channel",
                    NotificationManager.IMPORTANCE_LOW,
                ).apply {
                    setShowBadge(false)
                }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()

        sensor.stop()
        controller.onServiceStopped()

        Log.d(LOG_TAG, "Foreground Service destroyed")
    }
}
