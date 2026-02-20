package com.wegielek.katanaflashlight.domain.usecase

import com.wegielek.katanaflashlight.domain.wakelock.WakeLockManager

class KeepCpuAwakeUseCase(
    private val wakeLockManager: WakeLockManager,
) {
    operator fun invoke(value: Boolean) {
        if (value) {
            wakeLockManager.acquire()
        } else {
            wakeLockManager.release()
        }
    }
}
