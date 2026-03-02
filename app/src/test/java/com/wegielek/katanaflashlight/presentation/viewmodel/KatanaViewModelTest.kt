package com.wegielek.katanaflashlight.presentation.viewmodel

import android.content.Context
import com.wegielek.katanaflashlight.domain.controller.FlashlightController
import com.wegielek.katanaflashlight.domain.controller.ServiceController
import com.wegielek.katanaflashlight.domain.repository.PermissionsRepository
import com.wegielek.katanaflashlight.presentation.viewmodels.KatanaViewModel
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class KatanaViewModelTest {
    val mContext = mockk<Context>(relaxed = true)
    val mServiceController = mockk<ServiceController>(relaxed = true)
    val mPermissionsRepository = mockk<PermissionsRepository>(relaxed = true)
    val mFlashlightController = mockk<FlashlightController>(relaxed = true)

    private lateinit var katanaViewModel: KatanaViewModel

    @Before
    fun setup() {
        katanaViewModel = KatanaViewModel(mServiceController, mPermissionsRepository, mFlashlightController)
    }

    @Test
    fun testInitialize() {
        katanaViewModel.initialize(mContext)

        verify(exactly = 1) { mFlashlightController.initialize() }
        verify(exactly = 1) { mPermissionsRepository.hasCameraPermission() }
        verify(exactly = 1) { mPermissionsRepository.hasNotificationPermission() }
    }

    @Test
    fun testToggleFlashlight() {
        katanaViewModel.toggleFlashlight()

        verify(exactly = 1) { mFlashlightController.toggleFlashlight(1) }
    }

    @Test
    fun testOnStrengthChange() {
        katanaViewModel.onStrengthChange(2)

        verify(exactly = 1) { mFlashlightController.setStrength(2) }
    }

    @Test
    fun testOnSensitivityChange() {
        katanaViewModel.onSensitivityChange(2f)

        // TODO
    }

    @Test
    fun testOnVibrationSwitch() {
        katanaViewModel.onVibrationSwitch(true)

        // TODO
    }

    @Test
    fun testOnKatanaServiceSwitch() {
        katanaViewModel.onKatanaServiceSwitch(true)

        verify(exactly = 1) { mServiceController.startFlashlightService() }

        katanaViewModel.onKatanaServiceSwitch(false)

        verify(exactly = 1) { mServiceController.stopFlashlightService() }
    }

    @Test
    fun testSaveState() {
        katanaViewModel.saveState(mContext)

        // TODO
    }

    @Test
    fun testMonitorKatanaServiceShutdown() {
        katanaViewModel.monitorKatanaServiceShutdown(false)

        // TODO

        katanaViewModel.monitorKatanaServiceShutdown(true)

        // TODO
    }
}
