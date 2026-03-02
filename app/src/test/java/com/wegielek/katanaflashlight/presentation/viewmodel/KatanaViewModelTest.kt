package com.wegielek.katanaflashlight.presentation.viewmodel

import com.wegielek.katanaflashlight.domain.controller.FlashlightController
import com.wegielek.katanaflashlight.domain.controller.ServiceController
import com.wegielek.katanaflashlight.domain.repository.PermissionsRepository
import com.wegielek.katanaflashlight.domain.repository.SettingsRepository
import com.wegielek.katanaflashlight.presentation.viewmodels.KatanaViewModel
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class KatanaViewModelTest {
    val mServiceController = mockk<ServiceController>(relaxed = true)
    val mPermissionsRepository = mockk<PermissionsRepository>(relaxed = true)
    val mFlashlightController = mockk<FlashlightController>(relaxed = true)
    val mSettingsRepository = mockk<SettingsRepository>(relaxed = true)

    private lateinit var katanaViewModel: KatanaViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())

        katanaViewModel =
            KatanaViewModel(
                mServiceController,
                mPermissionsRepository,
                mFlashlightController,
                mSettingsRepository,
            )
    }

    @Test
    fun testInitialize() {
        verify(exactly = 1) { mFlashlightController.initialize() }
        verify(exactly = 1) { mPermissionsRepository.hasCameraPermission() }
        verify(exactly = 1) { mPermissionsRepository.hasNotificationPermission() }
    }

    @Test
    fun testToggleFlashlight() {
        katanaViewModel.toggleFlashlight()

        verify(exactly = 1) { mFlashlightController.toggleFlashlight(1) }
        verify(exactly = 1) { mFlashlightController.isFlashlightEnabled() }
        assertFalse(katanaViewModel.uiState.value.flashlightEnabled)
    }

    @Test
    fun testOnStrengthChange() {
        katanaViewModel.onStrengthChange(2)

        verify(exactly = 1) { mFlashlightController.setStrength(2) }
        assertEquals(2, katanaViewModel.uiState.value.strength)
    }

    @Test
    fun testOnSensitivityChange() {
        katanaViewModel.onSensitivityChange(2f)

        assertEquals(2f, katanaViewModel.uiState.value.sensitivity)
    }

    @Test
    fun testOnVibrationSwitch() {
        katanaViewModel.onVibrationSwitch(true)
        assertTrue(katanaViewModel.uiState.value.vibrationEnabled)

        katanaViewModel.onVibrationSwitch(false)
        assertFalse(katanaViewModel.uiState.value.vibrationEnabled)
    }

    @Test
    fun testOnKatanaServiceSwitch() {
        katanaViewModel.onKatanaServiceSwitch(true)

        verify(exactly = 1) { mServiceController.startFlashlightService() }
        assertTrue(katanaViewModel.uiState.value.katanaServiceRunning)

        katanaViewModel.onKatanaServiceSwitch(false)

        verify(exactly = 1) { mServiceController.stopFlashlightService() }
        assertFalse(katanaViewModel.uiState.value.katanaServiceRunning)
    }

    @Test
    fun testMySaveState() =
        runTest {
            katanaViewModel.saveState()
            advanceUntilIdle()

            coVerify(exactly = 1) { mSettingsRepository.saveState(any()) }
        }

    @Test
    fun setInstructionExpired() {
        katanaViewModel.setInstructionExpired(true)
        assertTrue(katanaViewModel.uiState.value.instructionExpired)

        katanaViewModel.setInstructionExpired(false)
        assertFalse(katanaViewModel.uiState.value.instructionExpired)
    }

    @Test
    fun testMonitorKatanaServiceShutdown() {
        katanaViewModel.onKatanaServiceSwitch(true)

        katanaViewModel.monitorKatanaServiceShutdown(true)
        assertTrue(katanaViewModel.uiState.value.katanaServiceRunning)

        katanaViewModel.monitorKatanaServiceShutdown(false)
        assertFalse(katanaViewModel.uiState.value.katanaServiceRunning)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
