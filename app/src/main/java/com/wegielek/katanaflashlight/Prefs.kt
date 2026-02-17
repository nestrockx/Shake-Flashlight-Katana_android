package com.wegielek.katanaflashlight

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

    val Context.state: Flow<UiState.Properties>
        get() =
            dataStore.data.map {
                UiState.Properties(
                    sensitivity = it[SENSITIVITY_KEY] ?: 5f,
                    flashlightOn = it[FLASH_KEY] ?: false,
                    vibrationOn = it[VIBRATION_KEY] ?: false,
                    katanaServiceOn = it[KATANA_KEY] ?: false,
                    strength = it[STRENGTH_KEY] ?: (it[MAX_STRENGTH_KEY] ?: 1),
                    maxStrength = it[MAX_STRENGTH_KEY] ?: 1,
                    hasStrengthLevels = it[HAS_STRENGTH_LEVELS_KEY] ?: false,
                )
            }

    suspend fun saveState(
        context: Context,
        state: UiState.Properties,
    ) {
        context.dataStore.edit {
            it[SENSITIVITY_KEY] = state.sensitivity
            it[FLASH_KEY] = state.flashlightOn
            it[VIBRATION_KEY] = state.vibrationOn
            it[KATANA_KEY] = state.katanaServiceOn
            it[STRENGTH_KEY] = state.strength
            it[MAX_STRENGTH_KEY] = state.maxStrength
            it[HAS_STRENGTH_LEVELS_KEY] = state.hasStrengthLevels
        }
    }

    val Context.instructionExpired: Flow<Boolean>
        get() = dataStore.data.map { it[INTRO_KEY] ?: false }

    suspend fun setFlashlightOn(
        context: Context,
        value: Boolean,
    ) {
        context.dataStore.edit { it[FLASH_KEY] = value }
    }

    suspend fun setKatanaServiceRunning(
        context: Context,
        value: Boolean,
    ) {
        context.dataStore.edit { it[KATANA_KEY] = value }
    }

    suspend fun setInstructionExpired(
        context: Context,
        value: Boolean,
    ) {
        context.dataStore.edit { it[INTRO_KEY] = value }
    }
}
