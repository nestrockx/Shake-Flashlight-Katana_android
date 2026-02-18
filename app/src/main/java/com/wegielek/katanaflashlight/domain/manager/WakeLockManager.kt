package com.wegielek.katanaflashlight.domain.manager

interface WakeLockManager {
    fun acquire()

    fun release()
}
