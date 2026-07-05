package com.troves.presintation.ui.aichat.voice

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberVoiceInputController(): VoiceInputController {
    val context = LocalContext.current
    val controller = remember { AndroidVoiceInputController(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted -> controller.onPermissionResult(granted) }

    DisposableEffect(controller) {
        controller.permissionLauncher = permissionLauncher
        onDispose { controller.dispose() }
    }

    return controller
}


private class AndroidVoiceInputController(
    private val context: Context,
) : VoiceInputController {

    var permissionLauncher: ActivityResultLauncher<String>? = null

    private val mainHandler = Handler(Looper.getMainLooper())
    private var recognizer: SpeechRecognizer? = null
    private var onResult: ((String) -> Unit)? = null
    private var onError: ((VoiceError) -> Unit)? = null

    private val committed = StringBuilder()

    private var manualStop = false

    override val isAvailable: Boolean
        get() = SpeechRecognizer.isRecognitionAvailable(context)

    override fun start(
        onResult: (String) -> Unit,
        onError: (VoiceError) -> Unit,
    ) {
        this.onResult = onResult
        this.onError = onError
        committed.clear()
        manualStop = false

        if (!isAvailable) {
            onError(VoiceError.UNAVAILABLE)
            return
        }
        if (hasRecordPermission()) {
            beginListening()
        } else {
            val launcher = permissionLauncher
            if (launcher != null) {
                launcher.launch(Manifest.permission.RECORD_AUDIO)
            } else {
                onError(VoiceError.PERMISSION_DENIED)
            }
        }
    }

    fun onPermissionResult(granted: Boolean) {
        if (granted) beginListening() else onError?.invoke(VoiceError.PERMISSION_DENIED)
    }

    override fun stop() {
        manualStop = true
        mainHandler.removeCallbacksAndMessages(null)
        recognizer?.stopListening()
    }

    fun dispose() {
        manualStop = true
        mainHandler.removeCallbacksAndMessages(null)
        recognizer?.destroy()
        recognizer = null
        onResult = null
        onError = null
    }

    private fun hasRecordPermission(): Boolean =
        context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED

    private fun beginListening() {
        if (manualStop) return
        val speechRecognizer = recognizer ?: SpeechRecognizer
            .createSpeechRecognizer(context)
            .also {
                it.setRecognitionListener(listener)
                recognizer = it
            }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
            )
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
        }
        speechRecognizer.startListening(intent)
    }

    private fun scheduleRestart() {
        if (manualStop) return
        mainHandler.postDelayed({ beginListening() }, RESTART_DELAY_MS)
    }

    private fun commitSegment(segment: String?) {
        val seg = segment?.trim().orEmpty()
        if (seg.isEmpty()) return
        if (committed.isNotEmpty()) committed.append(' ')
        committed.append(seg)
    }

    private fun runningText(partial: String?): String {
        val base = committed.toString()
        val seg = partial?.trim().orEmpty()
        return when {
            base.isEmpty() -> seg
            seg.isEmpty() -> base
            else -> "$base $seg"
        }
    }

    private fun firstResult(results: Bundle?): String? =
        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) = Unit
        override fun onBeginningOfSpeech() = Unit
        override fun onRmsChanged(rmsdB: Float) = Unit
        override fun onBufferReceived(buffer: ByteArray?) = Unit
        override fun onEndOfSpeech() = Unit
        override fun onEvent(eventType: Int, params: Bundle?) = Unit

        override fun onPartialResults(partialResults: Bundle?) {
            onResult?.invoke(runningText(firstResult(partialResults)))
        }

        override fun onResults(results: Bundle?) {
            commitSegment(firstResult(results))
            onResult?.invoke(committed.toString())
            // A finalized segment just means the user paused — keep going.
            scheduleRestart()
        }

        override fun onError(error: Int) {
            when (error) {
                SpeechRecognizer.ERROR_NO_MATCH,
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT,
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY,
                SpeechRecognizer.ERROR_CLIENT -> scheduleRestart()

                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                    manualStop = true
                    onError?.invoke(VoiceError.PERMISSION_DENIED)
                }

                else -> {
                    manualStop = true
                    onError?.invoke(VoiceError.UNAVAILABLE)
                }
            }
        }
    }

    private companion object {
        const val RESTART_DELAY_MS = 120L
    }
}
