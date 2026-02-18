package com.wegielek.katanaflashlight.data.wakelock

import android.content.Context
import android.os.PowerManager
import com.wegielek.katanaflashlight.domain.manager.WakeLockManager

class WakeLockManagerImpl(
    context: Context,
) : WakeLockManager {
    private val tag: String = WakeLockManagerImpl::class.java.getSimpleName()

    private val wakeLock: PowerManager.WakeLock by lazy {
        val powerManager =
            context.getSystemService(Context.POWER_SERVICE) as PowerManager

        powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            tag,
        )
    }

    override fun acquire() {
        wakeLock.acquire()
    }

    override fun release() {
        if (wakeLock.isHeld) wakeLock.release()
    }
}
