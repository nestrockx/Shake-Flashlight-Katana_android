package com.wegielek.katanaflashlight.domain.usecase

import com.wegielek.katanaflashlight.domain.wakelock.WakeLockManager
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class KeepCpuAwakeUseCaseTest {
    val mWakeLockManager = mockk<WakeLockManager>(relaxed = true)

    private lateinit var keepCpuAwakeUseCase: KeepCpuAwakeUseCase

    @Before
    fun setup() {
        keepCpuAwakeUseCase = KeepCpuAwakeUseCase(mWakeLockManager)
    }

    @Test
    fun testKeepCpuAwake() {
        keepCpuAwakeUseCase(true)
        verify(exactly = 1) { mWakeLockManager.acquire() }
    }

    @Test
    fun testKeepCpuAwakeRelease() {
        keepCpuAwakeUseCase(false)
        verify(exactly = 1) { mWakeLockManager.release() }
    }
}
