package com.troves.presintation.ui.aichat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.union
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import com.troves.presintation.core.mvi.ObserveEffect
import com.troves.presintation.ui.aichat.components.AiChatHeader
import com.troves.presintation.ui.aichat.components.AssistantMessageBubble
import com.troves.presintation.ui.aichat.components.ChatInputBar
import com.troves.presintation.ui.aichat.components.ErrorRetryBar
import com.troves.presintation.ui.aichat.components.RateLimitBanner
import com.troves.presintation.ui.aichat.components.TypingIndicator
import com.troves.presintation.ui.aichat.components.UserMessageBubble
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

    val suggestionHeader = stringResource(Res.string.ai_suggestion_header)
    val viewAllLabel = stringResource(Res.string.ai_view_all_recommendations)
    val retryLabel = stringResource(Res.string.ai_retry)

    ObserveEffect(effect) { eff ->
        when (eff) {
            AiChatEffect.NavigateBack -> onNavigateBack()
            is AiChatEffect.NavigateToProduct -> onNavigateToProduct(eff.productId)
            AiChatEffect.NavigateToSearch -> onNavigateToSearch()
            is AiChatEffect.ShowMessage -> coroutineScope.launch {
                snackBarState.showSnackbar(eff.message)
            }
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
                    .padding(
                        WindowInsets.ime
                            .union(WindowInsets.navigationBars)
                            .asPaddingValues(),
                    ),
            ) {
                state.errorMessage?.let { message ->
                    ErrorRetryBar(
                        message = message,
                        retryLabel = retryLabel,
                        onRetry = { onIntent(AiChatIntent.Retry) },
                    )
                }

                ChatInputBar(
                    value = state.input,
                    hint = stringResource(Res.string.ai_input_hint),
                    canSend = state.canSend,
                    enabled = state.rateLimitedSeconds == null,
                    onValueChange = { onIntent(AiChatIntent.InputChanged(it)) },
                    onSend = { onIntent(AiChatIntent.Send) },
                    onAttachImage = { /* Phase E */ },
                    onMic = { /* Phase D */ },
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
                    AiSender.USER -> UserMessageBubble(text = msg.text)
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
