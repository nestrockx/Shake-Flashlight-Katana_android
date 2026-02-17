package com.wegielek.katanaflashlight.di

import com.wegielek.katanaflashlight.data.FlashlightControllerImpl
import com.wegielek.katanaflashlight.data.PermissionsControllerImpl
import com.wegielek.katanaflashlight.data.ServiceControllerImpl
import com.wegielek.katanaflashlight.data.VibrationControllerImpl
import com.wegielek.katanaflashlight.domain.controller.FlashlightController
import com.wegielek.katanaflashlight.domain.controller.PermissionsController
import com.wegielek.katanaflashlight.domain.controller.ServiceController
import com.wegielek.katanaflashlight.domain.controller.VibrationController
import org.koin.dsl.module

val dataModule =
    module {
        single<PermissionsController> { PermissionsControllerImpl(get()) }
        single<FlashlightController> { FlashlightControllerImpl(get()) }
        single<ServiceController> { ServiceControllerImpl(get()) }
        single<VibrationController> { VibrationControllerImpl(get()) }
    }
