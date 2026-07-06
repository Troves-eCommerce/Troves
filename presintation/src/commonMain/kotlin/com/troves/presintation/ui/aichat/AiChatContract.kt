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
    val image: PendingImageUi? = null,
)


@Immutable
class PendingImageUi(val id: String, val bytes: ByteArray) {
    override fun equals(other: Any?): Boolean = other is PendingImageUi && other.id == id
    override fun hashCode(): Int = id.hashCode()
}

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
    val isListening: Boolean = false,
    val pendingImage: PendingImageUi? = null,
) {
    val canSend: Boolean
        get() = !isSending && rateLimitedSeconds == null &&
            (input.isNotBlank() || pendingImage != null)

    val canUseVoice: Boolean
        get() = !isSending && rateLimitedSeconds == null

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

    data object MicClicked : AiChatIntent
    data class VoiceTranscript(val text: String) : AiChatIntent
    data class VoiceFailed(val message: String) : AiChatIntent

    data object AttachImageClicked : AiChatIntent
    class ImagePicked(val bytes: ByteArray) : AiChatIntent
    data object RemovePendingImage : AiChatIntent
}

sealed interface AiChatEffect {
    data object NavigateBack : AiChatEffect
    data class NavigateToProduct(val productId: String) : AiChatEffect
    data object NavigateToSearch : AiChatEffect
    data class ShowMessage(val message: String) : AiChatEffect

    data object StartVoiceCapture : AiChatEffect
    data object StopVoiceCapture : AiChatEffect

    data object PickImage : AiChatEffect
}
