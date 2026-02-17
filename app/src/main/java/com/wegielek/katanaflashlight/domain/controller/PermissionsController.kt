package com.wegielek.katanaflashlight.domain.controller

// PermissionChecker.kt
interface PermissionsController {
    fun hasCameraPermission(): Boolean

    fun hasNotificationPermission(): Boolean
}
