package com.wegielek.katanaflashlight.domain.repository

interface PermissionsRepository {
    fun hasCameraPermission(): Boolean

    fun hasNotificationPermission(): Boolean
}
