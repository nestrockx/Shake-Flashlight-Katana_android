package com.wegielek.katanaflashlight.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.wegielek.katanaflashlight.presentation.viewmodels.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object Prefs {
    private const val PREFS_NAME = "prefs"

    private val Context.dataStore by preferencesDataStore(PREFS_NAME)

    // Keys
    private val SENSITIVITY_KEY = floatPreferencesKey("sensitivity")
    private val FLASH_KEY = booleanPreferencesKey("flash")
    private val VIBRATION_KEY = booleanPreferencesKey("vibration")
    private val KATANA_KEY = booleanPreferencesKey("katana")
    private val STRENGTH_KEY = intPreferencesKey("strength")
    private val MAX_STRENGTH_KEY = intPreferencesKey("max_strength")
    private val HAS_STRENGTH_LEVELS_KEY = booleanPreferencesKey("has_strength_levels")
    private val INTRO_KEY = booleanPreferencesKey("intro")

    val Context.state: Flow<UiState.Settings>
        get() =
            dataStore.data.map {
                UiState.Settings(
                    sensitivity = it[SENSITIVITY_KEY] ?: 5f,
                    flashlightEnabled = it[FLASH_KEY] ?: false,
                    vibrationEnabled = it[VIBRATION_KEY] ?: true,
                    katanaServiceRunning = it[KATANA_KEY] ?: false,
                    strength = it[STRENGTH_KEY] ?: (it[MAX_STRENGTH_KEY] ?: 1),
                    maxStrength = it[MAX_STRENGTH_KEY] ?: 1,
                    hasStrengthLevels = it[HAS_STRENGTH_LEVELS_KEY] ?: false,
                    instructionExpired = it[INTRO_KEY] ?: false,
                )
            }

    suspend fun saveState(
        context: Context,
        state: UiState.Settings,
    ) {
        context.dataStore.edit {
            it[SENSITIVITY_KEY] = state.sensitivity
            it[FLASH_KEY] = state.flashlightEnabled
            it[VIBRATION_KEY] = state.vibrationEnabled
            it[KATANA_KEY] = state.katanaServiceRunning
            it[STRENGTH_KEY] = state.strength
            it[MAX_STRENGTH_KEY] = state.maxStrength
            it[HAS_STRENGTH_LEVELS_KEY] = state.hasStrengthLevels
            it[INTRO_KEY] = state.instructionExpired
        }
    }
}
