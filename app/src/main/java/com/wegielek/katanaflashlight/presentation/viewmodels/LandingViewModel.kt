package com.wegielek.katanaflashlight.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wegielek.katanaflashlight.domain.controller.FlashlightController
import com.wegielek.katanaflashlight.domain.controller.ServiceController
import com.wegielek.katanaflashlight.domain.repository.PermissionsRepository
import com.wegielek.katanaflashlight.preferences.Prefs
import com.wegielek.katanaflashlight.preferences.Prefs.state
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class UiState {
    data class Settings(
        val sensitivity: Float,
        val flashlightEnabled: Boolean,
        val vibrationEnabled: Boolean,
        val katanaServiceRunning: Boolean,
        val strength: Int,
        val maxStrength: Int,
        val hasStrengthLevels: Boolean,
        val instructionExpired: Boolean,
    ) : UiState()
}

class LandingViewModel(
    private val appContext: Context,
    private val service: ServiceController,
    private val permissions: PermissionsRepository,
    private val flashlightController: FlashlightController,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            UiState.Settings(
                sensitivity = 5f,
                flashlightEnabled = false,
                vibrationEnabled = true,
                katanaServiceRunning = false,
                strength = 1,
                maxStrength = 1,
                hasStrengthLevels = false,
                instructionExpired = false,
            ),
        )
    val uiState: StateFlow<UiState.Settings> = _uiState

    var olderAndroidInit = MutableStateFlow(false)
        private set
    var olderAndroidClicked = MutableStateFlow(false)
        private set

    fun clickStart() {
        olderAndroidClicked.value = true
        olderAndroidInit.value = false
    }

    fun setOldAndroidInit(value: Boolean) {
        olderAndroidInit.value = value
    }

    private val _hasCameraPermission = MutableStateFlow(false)
    val hasCameraPermission: StateFlow<Boolean> = _hasCameraPermission

    private val _hasNotificationPermission = MutableStateFlow(false)
    val hasNotificationPermission: StateFlow<Boolean> = _hasNotificationPermission

    fun updatePermissions() {
        _hasCameraPermission.value = permissions.hasCameraPermission()
        _hasNotificationPermission.value = permissions.hasNotificationPermission()
    }

    fun startService() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(katanaServiceRunning = true)
            service.startFlashlightService()
        }
    }

    fun stopService() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(katanaServiceRunning = false)
            service.stopFlashlightService()
        }
    }

    fun initialize(context: Context) {
        viewModelScope.launch {
            flashlightController.initialize()

            updatePermissions()

            _uiState.value = context.state.first()
            _uiState.value = _uiState.value.copy(hasStrengthLevels = flashlightController.hasStrengthLevels())
            _uiState.value = _uiState.value.copy(maxStrength = flashlightController.getMaxStrengthLevel())
            _uiState.value = _uiState.value.copy(katanaServiceRunning = service.isFlashlightServiceRunning())
        }
    }

    fun hasFlashlight(): Boolean = flashlightController.hasFlashlight()

    fun toggleFlashlight() {
        viewModelScope.launch {
            val flashOn = appContext.state.first().flashlightEnabled
            flashlightController.toggleFlashlight()
            _uiState.value = _uiState.value.copy(flashlightEnabled = !flashOn)
        }
    }

    fun onStrengthChange(strength: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(strength = strength)
            flashlightController.setStrength(strength)
        }
    }

    fun onSensitivityChange(sensitivity: Float) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(sensitivity = sensitivity)
        }
    }

    fun onVibrationSwitch(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(vibrationEnabled = enabled)
        }
    }

    fun onKatanaServiceSwitch(enabled: Boolean) {
        if (enabled) {
            startService()
        } else {
            stopService()
        }
    }

    fun saveState(context: Context) {
        viewModelScope.launch {
            Prefs.saveState(context, _uiState.value)
        }
    }

    fun setInstructionExpired(value: Boolean) {
        _uiState.value = _uiState.value.copy(instructionExpired = value)
    }

    override fun onCleared() {
        super.onCleared()
        // TODO
//        flashlightController.release()
    }
}
