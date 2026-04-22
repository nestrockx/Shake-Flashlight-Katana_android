package com.wegielek.katanaflashlight.presentation.viewmodels

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wegielek.katanaflashlight.domain.controller.FlashlightController
import com.wegielek.katanaflashlight.domain.controller.ServiceController
import com.wegielek.katanaflashlight.domain.repository.PermissionsRepository
import com.wegielek.katanaflashlight.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UiState(
    val sensitivity: Float,
    val flashlightEnabled: Boolean,
    val vibrationEnabled: Boolean,
    val katanaServiceRunning: Boolean,
    val strength: Int,
    val maxStrength: Int,
    val hasStrengthLevels: Boolean,
    val instructionExpired: Boolean,
)

class KatanaViewModel(
    private val serviceController: ServiceController,
    private val permissionsRepository: PermissionsRepository,
    private val flashlightController: FlashlightController,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    val isServiceRunning = serviceController.isRunning

    private val _uiState =
        MutableStateFlow(
            UiState(
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
    val uiState: StateFlow<UiState> = _uiState

    var olderAndroidInit = MutableStateFlow(false)
        private set
    var olderAndroidClicked = MutableStateFlow(false)
        private set

    fun clickStart() {
        olderAndroidClicked.value = true
        olderAndroidInit.value = false
    }

    private val _hasCameraPermission = MutableStateFlow(false)
    val hasCameraPermission: StateFlow<Boolean> = _hasCameraPermission

    private val _hasNotificationPermission = MutableStateFlow(false)
    val hasNotificationPermission: StateFlow<Boolean> = _hasNotificationPermission

    init {
        initialize()
    }

    fun updatePermissions() {
        _hasCameraPermission.value = permissionsRepository.hasCameraPermission()
        _hasNotificationPermission.value = permissionsRepository.hasNotificationPermission()
    }

    fun startService() {
        _uiState.value = _uiState.value.copy(katanaServiceRunning = true)
        serviceController.startFlashlightService()
    }

    fun stopService() {
        _uiState.value = _uiState.value.copy(katanaServiceRunning = false)
        serviceController.stopFlashlightService()
    }

    fun initialize() {
        viewModelScope.launch {
            _uiState.value = settingsRepository.getState()
            _uiState.value =
                _uiState.value.copy(hasStrengthLevels = flashlightController.hasStrengthLevels())
            _uiState.value =
                _uiState.value.copy(maxStrength = flashlightController.getMaxStrengthLevel())
            _uiState.value =
                _uiState.value.copy(katanaServiceRunning = serviceController.isFlashlightServiceRunning())
        }

        flashlightController.initialize()

        updatePermissions()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && !_uiState.value.instructionExpired) {
            olderAndroidInit.value = true
        }
    }

    fun hasFlashlight(): Boolean = flashlightController.hasFlashlight()

    fun toggleFlashlight() {
        flashlightController.safeToggle(_uiState.value.strength)
        _uiState.value = _uiState.value.copy(flashlightEnabled = flashlightController.isFlashlightEnabled())
    }

    fun onStrengthChange(strength: Int) {
        _uiState.value = _uiState.value.copy(strength = strength)
        flashlightController.setStrength(strength)
    }

    fun onSensitivityChange(sensitivity: Float) {
        _uiState.value = _uiState.value.copy(sensitivity = sensitivity)
    }

    fun onVibrationSwitch(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(vibrationEnabled = enabled)
    }

    fun onKatanaServiceSwitch(enabled: Boolean) {
        if (enabled) {
            startService()
        } else {
            stopService()
        }
    }

    fun saveState() {
        viewModelScope.launch {
            settingsRepository.saveState(_uiState.value)
        }
    }

    fun setInstructionExpired(value: Boolean) {
        _uiState.value = _uiState.value.copy(instructionExpired = value)
    }

    fun monitorKatanaServiceShutdown(running: Boolean) {
        if (!running) {
            _uiState.value = _uiState.value.copy(katanaServiceRunning = false)
        }
    }
}
