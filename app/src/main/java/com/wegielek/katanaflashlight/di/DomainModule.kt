package com.wegielek.katanaflashlight.di

import com.wegielek.katanaflashlight.domain.SlashDetector
import com.wegielek.katanaflashlight.domain.repository.SettingsRepository
import com.wegielek.katanaflashlight.domain.usecase.KeepCpuAwakeUseCase
import com.wegielek.katanaflashlight.domain.usecase.SlashDetectionUseCase
import com.wegielek.katanaflashlight.domain.usecase.ToggleFlashlightUseCase
import com.wegielek.katanaflashlight.domain.usecase.TurnOffFlashlightUseCase
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module

val domainModule =
    module {
        factory {
            SlashDetector {
                runBlocking { get<SettingsRepository>().getSensitivity() }
            }
        }

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
            KeepCpuAwakeUseCase(
                get(),
            )
        }
        factory {
            SlashDetectionUseCase(
                get(),
                get(),
                get(),
            )
        }
    }
