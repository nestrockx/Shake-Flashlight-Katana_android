package com.wegielek.katanaflashlight.domain.usecase

import com.wegielek.katanaflashlight.domain.SlashDetector
import com.wegielek.katanaflashlight.domain.repository.CallStateRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SlashDetectionUseCaseTest {
    val mSlashDetector = mockk<SlashDetector>(relaxed = true)
    val mToggleFlashlightUseCase = mockk<ToggleFlashlightUseCase>(relaxed = true)
    val mCallStateRepository = mockk<CallStateRepository>(relaxed = true)

    private lateinit var slashDetectionUseCase: SlashDetectionUseCase

    @Before
    fun setup() {
        slashDetectionUseCase = SlashDetectionUseCase(mSlashDetector, mToggleFlashlightUseCase, mCallStateRepository)
    }

    @Test
    fun testSlashDetected() =
        runTest {
            every { mCallStateRepository.isCallActive() } returns false
            every {
                mSlashDetector.onAcceleration(
                    eq(20f),
                    eq(20f),
                    eq(20f),
                )
            } returns true

            slashDetectionUseCase.invoke(20f, 20f, 20f)

            coVerify(exactly = 1) { mToggleFlashlightUseCase.invoke() }
        }

    @Test
    fun testSlashNotDetected() =
        runTest {
            every { mCallStateRepository.isCallActive() } returns false
            every {
                mSlashDetector.onAcceleration(
                    eq(1f),
                    eq(1f),
                    eq(1f),
                )
            } returns false

            slashDetectionUseCase.invoke(1f, 1f, 1f)
            coVerify(exactly = 0) { mToggleFlashlightUseCase.invoke() }
        }

    @Test
    fun testCallActive() =
        runTest {
            every { mCallStateRepository.isCallActive() } returns true

            slashDetectionUseCase.invoke(1f, 1f, 1f)

            coVerify(exactly = 0) { mToggleFlashlightUseCase.invoke() }
        }
}
