package com.wegielek.katanaflashlight.domain.wakelock

interface WakeLockManager {
    fun acquire()

    fun release()
}
