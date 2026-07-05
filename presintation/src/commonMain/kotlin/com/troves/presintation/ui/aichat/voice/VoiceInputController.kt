package com.troves.presintation.ui.aichat.voice

import androidx.compose.runtime.Composable

enum class VoiceError { PERMISSION_DENIED, NO_SPEECH, UNAVAILABLE, INTERRUPTED }


interface VoiceInputController {
    val isAvailable: Boolean


    fun start(
        onResult: (String) -> Unit,
        onError: (VoiceError) -> Unit,
    )

    fun stop()
}

@Composable
expect fun rememberVoiceInputController(): VoiceInputController
