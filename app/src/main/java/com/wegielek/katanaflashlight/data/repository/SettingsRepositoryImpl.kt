package com.wegielek.katanaflashlight.data.repository

import android.content.Context
import com.wegielek.katanaflashlight.data.Prefs.state
import com.wegielek.katanaflashlight.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first

class SettingsRepositoryImpl(
    private val context: Context,
) : SettingsRepository {
    override suspend fun getSensitivity(): Int =
        context.state
            .first()
            .sensitivity
            .toInt()

    override suspend fun isVibrationEnabled(): Boolean =
        context.state
            .first()
            .vibrationEnabled
}
