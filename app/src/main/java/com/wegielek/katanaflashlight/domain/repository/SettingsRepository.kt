package com.wegielek.katanaflashlight.domain.repository

interface SettingsRepository {
    suspend fun getSensitivity(): Int

    suspend fun isVibrationEnabled(): Boolean
}
