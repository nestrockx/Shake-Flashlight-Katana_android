package com.wegielek.katanaflashlight.di

import com.wegielek.katanaflashlight.domain.detector.SlashDetector
import com.wegielek.katanaflashlight.domain.repository.SettingsRepository
import com.wegielek.katanaflashlight.domain.usecase.KeepCpuAwakeUseCase
import com.wegielek.katanaflashlight.domain.usecase.ToggleFlashlightUseCase
import com.wegielek.katanaflashlight.domain.usecase.TurnOffFlashlightUseCase
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module

val domainModule =
    module {
        factory {
            ToggleFlashlightUseCase(
                get(),
                get(),
                get(),
            )
        }
        factory {
            TurnOffFlashlightUseCase(
                get(),
            )
        }
        factory {
            SlashDetector {
                runBlocking { get<SettingsRepository>().getSensitivity() }
            }
        }
        single {
            KeepCpuAwakeUseCase(
                get(),
            )
        }
    }
