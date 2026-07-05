package com.troves.presintation.ui.aichat.voice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioEngine
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryRecord
import platform.AVFAudio.AVAudioSessionModeMeasurement
import platform.AVFAudio.setActive
import platform.Speech.SFSpeechAudioBufferRecognitionRequest
import platform.Speech.SFSpeechRecognitionTask
import platform.Speech.SFSpeechRecognizer
import platform.Speech.SFSpeechRecognizerAuthorizationStatus
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun rememberVoiceInputController(): VoiceInputController =
    remember { IosVoiceInputController() }


@OptIn(ExperimentalForeignApi::class)
private class IosVoiceInputController : VoiceInputController {

    private val recognizer: SFSpeechRecognizer? = SFSpeechRecognizer()
    private val audioEngine = AVAudioEngine()
    private var request: SFSpeechAudioBufferRecognitionRequest? = null
    private var task: SFSpeechRecognitionTask? = null

    private val committed = StringBuilder()
    private var manualStop = false

    private var onResult: ((String) -> Unit)? = null
    private var onError: ((VoiceError) -> Unit)? = null

    override val isAvailable: Boolean
        get() = recognizer?.available == true

    override fun start(
        onResult: (String) -> Unit,
        onError: (VoiceError) -> Unit,
    ) {
        this.onResult = onResult
        this.onError = onError
        committed.clear()
        manualStop = false

        val recognizer = this.recognizer
        if (recognizer == null || !recognizer.available) {
            onError(VoiceError.UNAVAILABLE)
            return
        }

        SFSpeechRecognizer.requestAuthorization { status ->
            onMain {
                if (status != SFSpeechRecognizerAuthorizationStatus.SFSpeechRecognizerAuthorizationStatusAuthorized) {
                    onError(VoiceError.PERMISSION_DENIED)
                    return@onMain
                }
                AVAudioSession.sharedInstance().requestRecordPermission { granted ->
                    onMain {
                        if (granted) beginRecording(recognizer) else onError(VoiceError.PERMISSION_DENIED)
                    }
                }
            }
        }
    }

    override fun stop() {
        manualStop = true
        request?.endAudio()
        task?.cancel()
        task = null
        stopEngine()
    }

    private fun beginRecording(recognizer: SFSpeechRecognizer) {
        if (manualStop) return

        val audioSession = AVAudioSession.sharedInstance()
        audioSession.setCategory(AVAudioSessionCategoryRecord, error = null)
        audioSession.setMode(AVAudioSessionModeMeasurement, error = null)
        audioSession.setActive(true, error = null)

        val inputNode = audioEngine.inputNode
        val recordingFormat = inputNode.outputFormatForBus(0u)
        // Install the tap once; it always appends to the *current* request field,
        // which we swap out each time a task finalizes.
        inputNode.installTapOnBus(0u, 1024u, recordingFormat) { buffer, _ ->
            buffer?.let { request?.appendAudioPCMBuffer(it) }
        }
        audioEngine.prepare()
        audioEngine.startAndReturnError(null)

        startTask(recognizer)
    }

    private fun startTask(recognizer: SFSpeechRecognizer) {
        if (manualStop) return

        val recognitionRequest = SFSpeechAudioBufferRecognitionRequest().apply {
            shouldReportPartialResults = true
        }
        request = recognitionRequest

        task = recognizer.recognitionTaskWithRequest(recognitionRequest) { result, error ->
            onMain {
                if (result != null) {
                    val segment = result.bestTranscription.formattedString
                    onResult?.invoke(runningText(segment))
                    if (result.isFinal() && !manualStop) {
                        commitSegment(segment)
                        restartTask(recognizer)
                    }
                }
                if (error != null && !manualStop) {
                    restartTask(recognizer)
                }
            }
        }
    }

    private fun restartTask(recognizer: SFSpeechRecognizer) {
        task?.cancel()
        task = null
        startTask(recognizer)
    }

    private fun stopEngine() {
        if (audioEngine.running) {
            audioEngine.stop()
            audioEngine.inputNode.removeTapOnBus(0u)
        }
    }

    private fun commitSegment(segment: String) {
        val seg = segment.trim()
        if (seg.isEmpty()) return
        if (committed.isNotEmpty()) committed.append(' ')
        committed.append(seg)
    }

    private fun runningText(segment: String): String {
        val base = committed.toString()
        val seg = segment.trim()
        return when {
            base.isEmpty() -> seg
            seg.isEmpty() -> base
            else -> "$base $seg"
        }
    }

    private fun onMain(block: () -> Unit) {
        dispatch_async(dispatch_get_main_queue()) { block() }
    }
}
