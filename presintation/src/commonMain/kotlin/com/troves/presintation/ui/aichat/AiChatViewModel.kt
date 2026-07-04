package com.troves.presintation.ui.aichat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.AiChatReply
import com.troves.domain.entity.AiChatTurn
import com.troves.domain.entity.Product
import com.troves.domain.usecase.ai.SendAiMessageUseCase
import com.troves.domain.usecase.wishlist.GetWishlistUseCase
import com.troves.domain.usecase.wishlist.ToggleFavoriteResult
import com.troves.domain.usecase.wishlist.ToggleFavoriteUseCase
import com.troves.domain.utils.RateLimitException
import com.troves.domain.utils.Result
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AiChatViewModel(
    private val sendAiMessage: SendAiMessageUseCase,
    private val getWishlist: GetWishlistUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : ViewModel(),
    StateHolder<AiChatUiState> by DefaultStateHolder(AiChatUiState()),
    EffectPublisher<AiChatEffect> by DefaultEffectPublisher() {

    private var seq = 0
    private fun nextId() = "m${seq++}"
    private var lastUserText: String? = null // for Retry

    init {
        getWishlist()
            .onEach { favorites ->
                val favIds = favorites.map { it.id.toString() }.toSet()
                updateState { copy(favoriteProductIds = favIds).applyFavorites(favIds) }
            }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: AiChatIntent) {
        when (intent) {
            is AiChatIntent.InputChanged -> updateState { copy(input = intent.value) }
            AiChatIntent.Send -> send(currentState.input.trim())
            AiChatIntent.Retry -> lastUserText?.let { send(it, isRetry = true) }
            AiChatIntent.DismissError -> updateState { copy(errorMessage = null) }
            AiChatIntent.OnBack -> sendEffect(AiChatEffect.NavigateBack)
            is AiChatIntent.ProductClicked ->
                sendEffect(AiChatEffect.NavigateToProduct(intent.product.id))
            AiChatIntent.ViewAllRecommendations -> sendEffect(AiChatEffect.NavigateToSearch)
            is AiChatIntent.ToggleFavorite -> onToggleFavorite(intent.product)
        }
    }

    private fun onToggleFavorite(product: AiProductUi) {
        val numericId = product.numericId.toLongOrNull() ?: return
        val domainProduct = Product(
            id = numericId,
            title = product.title,
            vendor = "",
            price = product.priceAmount,
            imageUrl = product.imageUrl.orEmpty(),
            status = "ACTIVE",
        )
        viewModelScope.launch {
            when (val result = toggleFavorite(domainProduct)) {
                ToggleFavoriteResult.RequiresLogin ->
                    sendEffect(AiChatEffect.ShowMessage("Please sign in to save favorites."))
                is ToggleFavoriteResult.Error ->
                    sendEffect(AiChatEffect.ShowMessage(result.throwable.message ?: "Couldn't update favorites."))
                ToggleFavoriteResult.Added, ToggleFavoriteResult.Removed -> Unit // wishlist flow updates state
            }
        }
    }

    private fun send(text: String, isRetry: Boolean = false) {
        val s = currentState
        if (s.isSending || s.rateLimitedSeconds != null) return
        if (text.isBlank()) return
        lastUserText = text

        if (!isRetry) {
            val userMsg = ChatMessageUi(id = nextId(), sender = AiSender.USER, text = text)
            updateState {
                copy(messages = messages + userMsg, input = "", isSending = true, errorMessage = null)
            }
        } else {
            updateState { copy(isSending = true, errorMessage = null) }
        }

        val history = buildHistory()
        viewModelScope.launch {
            when (val r = sendAiMessage(text, imageBase64 = null, history = history)) {
                is Result.Success -> appendAssistant(r.value)
                is Result.Error -> handleError(r.throwable)
                Result.Loading -> Unit
            }
        }
    }

    private fun appendAssistant(reply: AiChatReply) {
        val bubbleText = buildString {
            append(reply.reasoning)
            reply.clarifyingQuestion?.let {
                if (isNotEmpty()) append("\n\n")
                append(it)
            }
        }
        val favIds = currentState.favoriteProductIds
        val msg = ChatMessageUi(
            id = nextId(),
            sender = AiSender.ASSISTANT,
            text = bubbleText,
            products = reply.products.map {
                val numericId = it.id.substringAfterLast('/')
                AiProductUi(
                    id = it.id,
                    numericId = numericId,
                    handle = it.handle,
                    title = it.title,
                    imageUrl = it.imageUrl,
                    priceFormatted = it.priceFormatted,
                    priceAmount = it.priceAmount,
                    why = it.why,
                    isFavorite = numericId in favIds,
                )
            },
            isSuggestion = reply.suggested,
            clarifying = reply.clarifyingQuestion != null,
            declined = reply.declined,
        )
        updateState { copy(messages = messages + msg, isSending = false) }
    }

    /** Recompute the `isFavorite` flag on every rendered product card. */
    private fun AiChatUiState.applyFavorites(favIds: Set<String>): AiChatUiState =
        copy(
            messages = messages.map { m ->
                if (m.products.isEmpty()) m
                else m.copy(products = m.products.map { it.copy(isFavorite = it.numericId in favIds) })
            },
        )

    private fun handleError(t: Throwable) {
        when (t) {
            is RateLimitException ->
                updateState { copy(isSending = false, rateLimitedSeconds = t.retryAfterSeconds) }
            else ->
                updateState { copy(isSending = false, errorMessage = t.message ?: "Something went wrong") }
        }
    }

    private fun buildHistory(): List<AiChatTurn> =
        currentState.messages
            .filter { it.text.isNotBlank() }
            .takeLast(6)
            .map { AiChatTurn(if (it.sender == AiSender.USER) "user" else "assistant", it.text) }
}
