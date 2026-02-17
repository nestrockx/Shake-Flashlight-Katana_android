package com.wegielek.katanaflashlight.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wegielek.katanaflashlight.Prefs
import com.wegielek.katanaflashlight.Prefs.state
import com.wegielek.katanaflashlight.domain.FlashlightController
import com.wegielek.katanaflashlight.domain.PermissionChecker
import com.wegielek.katanaflashlight.domain.ServiceController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class UiState {
    data class Properties(
        val sensitivity: Float,
        val flashlightOn: Boolean,
        val vibrationOn: Boolean,
        val katanaServiceOn: Boolean,
        val strength: Int,
        val maxStrength: Int,
        val hasStrengthLevels: Boolean,
    ) : UiState()
}

class LandingViewModel(
    private val appContext: Context,
    private val service: ServiceController,
    private val permissions: PermissionChecker,
    private val flashlightController: FlashlightController,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            UiState.Properties(
                sensitivity = 5f,
                flashlightOn = false,
                vibrationOn = false,
                katanaServiceOn = false,
                strength = 1,
                maxStrength = 1,
                hasStrengthLevels = false,
            ),
        )
    val uiState: StateFlow<UiState.Properties> = _uiState

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

    var hasCameraPermission = MutableStateFlow(false)
        private set
    var hasNotificationPermission = MutableStateFlow(false)
        private set

    fun setHasCameraPermission(value: Boolean) {
        hasCameraPermission.value = value
    }

    fun setHasNotificationPermission(value: Boolean) {
        hasNotificationPermission.value = value
    }

    private fun hasStrengthLevels(): Boolean = flashlightController.hasStrengthLevels()

    private fun getMaxStrengthLevel(): Int = flashlightController.getMaxStrengthLevel()

    init {
        viewModelScope.launch {
            Prefs.setKatanaServiceRunning(appContext, isServiceRunning())
        }
    }

    fun hasCameraPermission(): Boolean = permissions.hasCameraPermission()

    fun hasNotificationPermission(): Boolean = permissions.hasNotificationPermission()

    fun startService() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(katanaServiceOn = true)
            if (!isServiceRunning()) {
                service.startFlashlightService()
            }
        }
    }

    fun isServiceRunning(): Boolean = service.isFlashlightServiceRunning()

    fun stopService() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(katanaServiceOn = false)
            if (isServiceRunning()) {
                service.stopFlashlightService()
            }
        }
    }

    fun initialize(context: Context) {
        viewModelScope.launch {
            flashlightController.initialize()

            hasCameraPermission.value = hasCameraPermission()
            hasNotificationPermission.value = hasNotificationPermission()

            _uiState.value = context.state.first()

            _uiState.value = _uiState.value.copy(hasStrengthLevels = hasStrengthLevels())
            _uiState.value = _uiState.value.copy(maxStrength = getMaxStrengthLevel())
            _uiState.value = _uiState.value.copy(katanaServiceOn = isServiceRunning())
        }
    }

    fun canUseFlashlight(): Boolean = flashlightController.hasFlashlight()

    fun toggleFlashlight() {
        viewModelScope.launch {
            val flashOn = appContext.state.first().flashlightOn
            flashlightController.toggleFlashlight(!flashOn)
            _uiState.value = _uiState.value.copy(flashlightOn = !flashOn)
        }
    }

    fun onStrengthChange(strength: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(strength = strength)
            if (appContext.state.first().flashlightOn) {
                flashlightController.setStrength(strength)
            }
        }
    }

    fun onSensitivityChange(sensitivity: Float) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(sensitivity = sensitivity)
        }
    }

    fun onVibrationSwitch(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(vibrationOn = enabled)
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
        viewModelScope.launch {
            Prefs.setInstructionExpired(appContext, value)
        }
    }
}
