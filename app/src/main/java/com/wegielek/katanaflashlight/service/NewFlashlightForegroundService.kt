// package com.wegielek.katanaflashlight.service
//
// import android.app.Service
// import android.content.Intent
// import android.hardware.Sensor
// import android.hardware.SensorEvent
// import android.hardware.SensorEventListener
// import android.os.IBinder
// import kotlinx.coroutines.CoroutineScope
// import kotlinx.coroutines.Dispatchers
// import kotlinx.coroutines.SupervisorJob
// import kotlinx.coroutines.launch
// import org.koin.core.component.KoinComponent
// import org.koin.core.component.inject
//
// class NewFlashlightForegroundService :
//    Service(),
//    SensorEventListener,
//    KoinComponent {
//    private val processMotionUseCase: ProcessMotionUseCase by inject()
//    private val toggleFlashlightUseCase: ToggleFlashlightUseCase by inject()
//
//    private val serviceJob = SupervisorJob()
//    private val serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)
//
//    override fun onBind(intent: Intent?): IBinder? = null
//
//    override fun onAccuracyChanged(
//        sensor: Sensor?,
//        accuracy: Int,
//    ) {
//    }
//
//    override fun onSensorChanged(event: SensorEvent?) {
//        event ?: return
//        if (event.sensor.type != Sensor.TYPE_LINEAR_ACCELERATION) return
//
//        serviceScope.launch {
//            val result =
//                processMotionUseCase(
//                    x = event.values[0],
//                    y = event.values[1],
//                    z = event.values[2],
//                )
//
//            if (result == MotionResult.ToggleFlashlight) {
//                toggleFlashlightUseCase()
//            }
//        }
//    }
//
//    override fun onStartCommand(
//        intent: Intent?,
//        flags: Int,
//        startId: Int,
//    ): Int {
//        ewodke
//        return super.onStartCommand(intent, flags, startId)
//    }
// }
