package com.wegielek.katanaflashlight.di

import com.wegielek.katanaflashlight.data.controller.FlashlightControllerImpl
import com.wegielek.katanaflashlight.data.controller.ServiceControllerImpl
import com.wegielek.katanaflashlight.data.controller.VibrationControllerImpl
import com.wegielek.katanaflashlight.data.manager.WakeLockManagerImpl
import com.wegielek.katanaflashlight.data.repository.CallStateRepositoryImpl
import com.wegielek.katanaflashlight.data.repository.PermissionsRepositoryImpl
import com.wegielek.katanaflashlight.data.repository.SettingsRepositoryImpl
import com.wegielek.katanaflashlight.domain.controller.FlashlightController
import com.wegielek.katanaflashlight.domain.controller.ServiceController
import com.wegielek.katanaflashlight.domain.controller.VibrationController
import com.wegielek.katanaflashlight.domain.manager.WakeLockManager
import com.wegielek.katanaflashlight.domain.repository.CallStateRepository
import com.wegielek.katanaflashlight.domain.repository.PermissionsRepository
import com.wegielek.katanaflashlight.domain.repository.SettingsRepository
import org.koin.dsl.module

val dataModule =
    module {
        single<FlashlightController> { FlashlightControllerImpl(get()) }
        single<ServiceController> { ServiceControllerImpl(get()) }
        single<VibrationController> { VibrationControllerImpl(get()) }

        single<PermissionsRepository> { PermissionsRepositoryImpl(get()) }
        single<SettingsRepository> { SettingsRepositoryImpl(get()) }
        single<CallStateRepository> { CallStateRepositoryImpl(get()) }

        single<WakeLockManager> { WakeLockManagerImpl(get()) }
    }
