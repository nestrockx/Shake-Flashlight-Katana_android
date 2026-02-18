package com.wegielek.katanaflashlight.domain.repository

interface CallStateRepository {
    fun isCallActive(): Boolean
}
