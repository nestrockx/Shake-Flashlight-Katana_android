package com.wegielek.katanaflashlight.data.repository

import android.content.Context
import com.wegielek.katanaflashlight.data.Prefs
import com.wegielek.katanaflashlight.data.Prefs.state
import com.wegielek.katanaflashlight.domain.repository.SettingsRepository
import com.wegielek.katanaflashlight.presentation.viewmodels.UiState
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

    override suspend fun getStrength(): Int =
        context.state
            .first()
            .strength

    override suspend fun saveState(state: UiState) {
        Prefs.saveState(context, state)
    }

    override suspend fun getState(): UiState = context.state.first()
}
