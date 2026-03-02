package com.wegielek.katanaflashlight.domain.repository

import com.wegielek.katanaflashlight.presentation.viewmodels.UiState

interface SettingsRepository {
    suspend fun getSensitivity(): Int

    suspend fun isVibrationEnabled(): Boolean

    suspend fun getStrength(): Int

    suspend fun saveState(state: UiState)

    suspend fun getState(): UiState
}
