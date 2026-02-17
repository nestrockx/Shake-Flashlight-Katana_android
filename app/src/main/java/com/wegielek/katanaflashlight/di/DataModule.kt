package com.wegielek.katanaflashlight.di

import com.wegielek.katanaflashlight.data.FlashlightControllerImpl
import com.wegielek.katanaflashlight.data.PermissionCheckerImpl
import com.wegielek.katanaflashlight.data.ServiceControllerImpl
import com.wegielek.katanaflashlight.domain.FlashlightController
import com.wegielek.katanaflashlight.domain.PermissionChecker
import com.wegielek.katanaflashlight.domain.ServiceController
import org.koin.dsl.module

val dataModule =
    module {
        single<PermissionChecker> { PermissionCheckerImpl(get()) }
        single<FlashlightController> { FlashlightControllerImpl(get()) }
        single<ServiceController> { ServiceControllerImpl(get()) }
    }
