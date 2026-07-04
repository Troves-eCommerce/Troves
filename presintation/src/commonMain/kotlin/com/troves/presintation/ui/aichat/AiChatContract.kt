package com.troves.presintation.ui.aichat

import androidx.compose.runtime.Immutable

enum class AiSender { USER, ASSISTANT }

@Immutable
data class ChatMessageUi(
    val id: String,
    val sender: AiSender,
    val text: String,
    val products: List<AiProductUi> = emptyList(),
    val isSuggestion: Boolean = false,
    val clarifying: Boolean = false,
    val declined: Boolean = false,
)

@Immutable
data class AiProductUi(
    val id: String,
    val numericId: String,
    val handle: String,
    val title: String,
    val imageUrl: String?,
    val priceFormatted: String,
    val priceAmount: String,
    val why: String,
    val isFavorite: Boolean = false,
)

@Immutable
data class AiChatUiState(
    val messages: List<ChatMessageUi> = emptyList(),
    val input: String = "",
    val isSending: Boolean = false,
    val errorMessage: String? = null,
    val rateLimitedSeconds: Int? = null,
    val favoriteProductIds: Set<String> = emptySet(),
) {
    val canSend: Boolean
        get() = !isSending && rateLimitedSeconds == null && input.isNotBlank()

    val isEmpty: Boolean get() = messages.isEmpty()
}

sealed interface AiChatIntent {
    data class InputChanged(val value: String) : AiChatIntent
    data object Send : AiChatIntent
    data object Retry : AiChatIntent
    data object DismissError : AiChatIntent
    data object OnBack : AiChatIntent
    data class ProductClicked(val product: AiProductUi) : AiChatIntent
    data object ViewAllRecommendations : AiChatIntent
    data class ToggleFavorite(val product: AiProductUi) : AiChatIntent
}

sealed interface AiChatEffect {
    data object NavigateBack : AiChatEffect
    data class NavigateToProduct(val productId: String) : AiChatEffect
    data object NavigateToSearch : AiChatEffect
    data class ShowMessage(val message: String) : AiChatEffect
}
