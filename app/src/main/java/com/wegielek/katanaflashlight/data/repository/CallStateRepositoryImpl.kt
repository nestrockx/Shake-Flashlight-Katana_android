package com.wegielek.katanaflashlight.data.repository

import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import com.wegielek.katanaflashlight.domain.repository.CallStateRepository

class CallStateRepositoryImpl(
    private val context: Context,
) : CallStateRepository {
    override fun isCallActive(): Boolean {
        val manager = context.getSystemService(AUDIO_SERVICE) as AudioManager
        return manager.mode == AudioManager.MODE_IN_CALL
    }
}
