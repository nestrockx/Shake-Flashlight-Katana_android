package com.wegielek.katanaflashlight.presentation.viewmodels

import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wegielek.katanaflashlight.data.Prefs
import com.wegielek.katanaflashlight.data.Prefs.state
import com.wegielek.katanaflashlight.domain.controller.FlashlightController
import com.wegielek.katanaflashlight.domain.controller.ServiceController
import com.wegielek.katanaflashlight.domain.repository.PermissionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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

    fun updatePermissions() {
        _hasCameraPermission.value = permissionsRepository.hasCameraPermission()
        _hasNotificationPermission.value = permissionsRepository.hasNotificationPermission()
    }

    fun startService() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(katanaServiceRunning = true)
            serviceController.startFlashlightService()
        }
    }

    fun stopService() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(katanaServiceRunning = false)
            serviceController.stopFlashlightService()
        }
    }

    fun initialize(context: Context) {
        viewModelScope.launch {
            flashlightController.initialize()

            updatePermissions()

            _uiState.value = context.state.first()
            _uiState.value = _uiState.value.copy(hasStrengthLevels = flashlightController.hasStrengthLevels())
            _uiState.value = _uiState.value.copy(maxStrength = flashlightController.getMaxStrengthLevel())
            _uiState.value = _uiState.value.copy(katanaServiceRunning = serviceController.isFlashlightServiceRunning())

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && !_uiState.value.instructionExpired) {
                olderAndroidInit.value = true
            }
        }
    }

    fun hasFlashlight(): Boolean = flashlightController.hasFlashlight()

    fun toggleFlashlight() {
        viewModelScope.launch {
            flashlightController.toggleFlashlight(_uiState.value.strength)
            _uiState.value = _uiState.value.copy(flashlightEnabled = flashlightController.isFlashlightEnabled())
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

    fun monitorKatanaServiceShutdown(running: Boolean) {
        if (!running) {
            _uiState.value = _uiState.value.copy(katanaServiceRunning = false)
        }
    }
}
