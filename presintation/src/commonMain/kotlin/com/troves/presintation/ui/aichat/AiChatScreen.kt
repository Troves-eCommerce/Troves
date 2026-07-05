package com.troves.presintation.ui.aichat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import com.troves.presintation.core.mvi.ObserveEffect
import com.troves.presintation.ui.aichat.components.AiChatHeader
import com.troves.presintation.ui.aichat.components.AssistantMessageBubble
import com.troves.presintation.ui.aichat.components.ChatInputBar
import com.troves.presintation.ui.aichat.components.ErrorRetryBar
import com.troves.presintation.ui.aichat.components.PendingImagePreview
import com.troves.presintation.ui.aichat.components.RateLimitBanner
import com.troves.presintation.ui.aichat.components.TypingIndicator
import com.troves.presintation.ui.aichat.components.UserMessageBubble
import com.troves.presintation.ui.aichat.components.VoiceWaveAnimation
import com.troves.presintation.ui.aichat.image.rememberImagePicker
import com.troves.presintation.ui.aichat.voice.VoiceError
import com.troves.presintation.ui.aichat.voice.rememberVoiceInputController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.ai_assistant_name
import troves.presintation.generated.resources.ai_assistant_subtitle
import troves.presintation.generated.resources.ai_disclaimer
import troves.presintation.generated.resources.ai_input_hint
import troves.presintation.generated.resources.ai_rate_limited
import troves.presintation.generated.resources.ai_retry
import troves.presintation.generated.resources.ai_suggestion_header
import troves.presintation.generated.resources.ai_view_all_recommendations
import troves.presintation.generated.resources.ai_voice_error
import troves.presintation.generated.resources.ai_voice_listening
import troves.presintation.generated.resources.ai_voice_no_speech
import troves.presintation.generated.resources.ai_voice_permission_denied
import troves.presintation.generated.resources.ai_voice_unavailable

@Composable
fun AiChatScreen(
    state: AiChatUiState,
    effect: Flow<AiChatEffect>,
    onIntent: (AiChatIntent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (productId: String) -> Unit,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val snackBarState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val voiceController = rememberVoiceInputController()
    val imagePicker = rememberImagePicker { bytes -> onIntent(AiChatIntent.ImagePicked(bytes)) }

    val suggestionHeader = stringResource(Res.string.ai_suggestion_header)
    val viewAllLabel = stringResource(Res.string.ai_view_all_recommendations)
    val retryLabel = stringResource(Res.string.ai_retry)

    val voiceUnavailableMsg = stringResource(Res.string.ai_voice_unavailable)
    val voicePermissionMsg = stringResource(Res.string.ai_voice_permission_denied)
    val voiceNoSpeechMsg = stringResource(Res.string.ai_voice_no_speech)
    val voiceErrorMsg = stringResource(Res.string.ai_voice_error)
    val voiceErrorMessage: (VoiceError) -> String = { err ->
        when (err) {
            VoiceError.PERMISSION_DENIED -> voicePermissionMsg
            VoiceError.NO_SPEECH -> voiceNoSpeechMsg
            VoiceError.UNAVAILABLE -> voiceUnavailableMsg
            VoiceError.INTERRUPTED -> voiceErrorMsg
        }
    }

    ObserveEffect(effect) { eff ->
        when (eff) {
            AiChatEffect.NavigateBack -> onNavigateBack()
            is AiChatEffect.NavigateToProduct -> onNavigateToProduct(eff.productId)
            AiChatEffect.NavigateToSearch -> onNavigateToSearch()
            is AiChatEffect.ShowMessage -> coroutineScope.launch {
                snackBarState.showSnackbar(eff.message)
            }

            AiChatEffect.StartVoiceCapture -> {
                if (voiceController.isAvailable) {
                    voiceController.start(
                        onResult = { onIntent(AiChatIntent.VoiceTranscript(it)) },
                        onError = { err -> onIntent(AiChatIntent.VoiceFailed(voiceErrorMessage(err))) },
                    )
                } else {
                    onIntent(AiChatIntent.VoiceFailed(voiceUnavailableMsg))
                }
            }

            AiChatEffect.StopVoiceCapture -> voiceController.stop()

            AiChatEffect.PickImage -> imagePicker.pick()
        }
    }

    LaunchedEffect(state.messages.size, state.isSending) {
        val lastIndex = state.messages.size - 1 + if (state.isSending) 1 else 0
        if (lastIndex >= 0) {
            listState.animateScrollToItem(lastIndex)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Theme.colors.backGround,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
                AiChatHeader(
                    title = stringResource(Res.string.ai_assistant_name),
                    subtitle = stringResource(Res.string.ai_assistant_subtitle),
                    onBack = { onIntent(AiChatIntent.OnBack) },
                )

                state.rateLimitedSeconds?.let { seconds ->
                    val minutes = (seconds + 59) / 60
                    RateLimitBanner(
                        message = stringResource(Res.string.ai_rate_limited, "${minutes}m"),
                    )
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .navigationBarsPadding()
            ) {
                state.errorMessage?.let { message ->
                    ErrorRetryBar(
                        message = message,
                        retryLabel = retryLabel,
                        onRetry = { onIntent(AiChatIntent.Retry) },
                    )
                }

                if (state.isListening) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        VoiceWaveAnimation()
                        Text(
                            text = stringResource(Res.string.ai_voice_listening),
                            style = Theme.typography.hint.medium,
                            color = Theme.colors.primary,
                        )
                    }
                }

                state.pendingImage?.let { pending ->
                    PendingImagePreview(
                        image = pending,
                        onRemove = { onIntent(AiChatIntent.RemovePendingImage) },
                    )
                }

                ChatInputBar(
                    value = state.input,
                    hint = stringResource(Res.string.ai_input_hint),
                    canSend = state.canSend,
                    enabled = state.rateLimitedSeconds == null,
                    isListening = state.isListening,
                    onValueChange = { onIntent(AiChatIntent.InputChanged(it)) },
                    onSend = { onIntent(AiChatIntent.Send) },
                    onAttachImage = { onIntent(AiChatIntent.AttachImageClicked) },
                    onMic = { onIntent(AiChatIntent.MicClicked) },
                )

                Text(
                    text = stringResource(Res.string.ai_disclaimer),
                    style = Theme.typography.hint.small,
                    color = Theme.colors.secondaryFont,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackBarState) },
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(state.messages, key = { it.id }) { msg ->
                when (msg.sender) {
                    AiSender.USER -> UserMessageBubble(text = msg.text, image = msg.image)
                    AiSender.ASSISTANT -> AssistantMessageBubble(
                        text = msg.text,
                        products = msg.products,
                        isSuggestion = msg.isSuggestion,
                        suggestionHeader = suggestionHeader,
                        viewAllLabel = viewAllLabel,
                        onProductClick = { onIntent(AiChatIntent.ProductClicked(it)) },
                        onFavoriteClick = { onIntent(AiChatIntent.ToggleFavorite(it)) },
                        onViewAll = { onIntent(AiChatIntent.ViewAllRecommendations) },
                    )
                }
            }

            if (state.isSending) {
                item { TypingIndicator() }
            }
        }
    }
}
