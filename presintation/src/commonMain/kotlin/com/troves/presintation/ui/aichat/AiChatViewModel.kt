package com.troves.presintation.ui.aichat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.AiChatReply
import com.troves.domain.entity.AiChatTurn
import com.troves.domain.entity.AiConversation
import com.troves.domain.entity.AiStoredMessage
import com.troves.domain.entity.AiStoredProduct
import com.troves.domain.entity.Product
import com.troves.domain.usecase.ai.AiHistoryListResult
import com.troves.domain.usecase.ai.DeleteAiConversationUseCase
import com.troves.domain.usecase.ai.GetAiConversationsUseCase
import com.troves.domain.usecase.ai.SaveAiConversationUseCase
import com.troves.domain.usecase.ai.SendAiMessageUseCase
import com.troves.domain.usecase.shared.ObserveConnectivityUseCase
import com.troves.domain.usecase.wishlist.GetWishlistUseCase
import com.troves.domain.usecase.wishlist.ToggleFavoriteResult
import com.troves.domain.usecase.wishlist.ToggleFavoriteUseCase
import com.troves.domain.utils.RateLimitException
import com.troves.domain.utils.Result
import com.troves.domain.utils.connectivity.ConnectivityStatus
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import com.troves.presintation.ui.aichat.image.encodeBase64
import com.troves.presintation.ui.aichat.image.processImageForUpload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.ai_favorite_login_required
import troves.presintation.generated.resources.ai_favorite_update_failed
import troves.presintation.generated.resources.ai_history_delete_failed
import troves.presintation.generated.resources.ai_history_error
import troves.presintation.generated.resources.ai_history_new_chat
import troves.presintation.generated.resources.ai_time_now
import troves.presintation.generated.resources.error_view_title
import troves.presintation.generated.resources.time_days_ago
import troves.presintation.generated.resources.time_hours_ago
import troves.presintation.generated.resources.time_just_now
import troves.presintation.generated.resources.time_minutes_ago
import troves.presintation.generated.resources.time_weeks_ago
import kotlinx.coroutines.withContext

class AiChatViewModel(
    private val sendAiMessage: SendAiMessageUseCase,
    private val getWishlist: GetWishlistUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val getConversations: GetAiConversationsUseCase,
    private val saveConversation: SaveAiConversationUseCase,
    private val deleteConversation: DeleteAiConversationUseCase,
    private val observeConnectivity: ObserveConnectivityUseCase,
) : ViewModel(),
    StateHolder<AiChatUiState> by DefaultStateHolder(AiChatUiState()),
    EffectPublisher<AiChatEffect> by DefaultEffectPublisher() {

    private var seq = 0
    private fun nextId() = "m${nowEpochMillis()}_${seq++}"
    private var lastUserText: String? = null
    private var lastImageBase64: String? = null // for Retry with an attached image

    private var activeConversationId: String? = null
    private var loadedConversations: List<AiConversation> = emptyList()

    init {
        getWishlist()
            .onEach { favorites ->
                val favIds = favorites.map { it.id.toString() }.toSet()
                updateState { copy(favoriteProductIds = favIds).applyFavorites(favIds) }
            }
            .launchIn(viewModelScope)

        observeConnectivity()
            .map { it == ConnectivityStatus.Available }
            .distinctUntilChanged()
            .onEach { isOnline -> updateState { copy(isOffline = !isOnline) } }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: AiChatIntent) {
        when (intent) {
            is AiChatIntent.InputChanged -> updateState { copy(input = intent.value) }
            AiChatIntent.Send -> onSendClicked()
            AiChatIntent.Retry -> lastUserText?.let { send(it, isRetry = true) }
            AiChatIntent.DismissError -> updateState { copy(errorMessage = null) }
            AiChatIntent.OnBack -> sendEffect(AiChatEffect.NavigateBack)
            is AiChatIntent.ProductClicked ->
                sendEffect(AiChatEffect.NavigateToProduct(intent.product.id))
            AiChatIntent.ViewAllRecommendations -> sendEffect(AiChatEffect.NavigateToSearch)
            is AiChatIntent.ToggleFavorite -> onToggleFavorite(intent.product)
            AiChatIntent.MicClicked -> onMicClicked()
            is AiChatIntent.VoiceTranscript ->
                if (currentState.isListening) updateState { copy(input = intent.text) }
            is AiChatIntent.VoiceFailed -> {
                updateState { copy(isListening = false) }
                sendEffect(AiChatEffect.ShowMessage(intent.message))
            }
            AiChatIntent.AttachImageClicked -> onAttachImageClicked()
            is AiChatIntent.ImagePicked -> onImagePicked(intent.bytes)
            AiChatIntent.RemovePendingImage -> updateState { copy(pendingImage = null) }
            AiChatIntent.OpenHistory -> onOpenHistory()
            AiChatIntent.CloseHistory -> updateState { copy(showHistory = false) }
            is AiChatIntent.LoadConversation -> onLoadConversation(intent.id)
            is AiChatIntent.DeleteConversation -> onDeleteConversation(intent.id)
            AiChatIntent.NewChat -> onNewChat()
        }
    }

    private fun onAttachImageClicked() {
        val s = currentState
        if (s.isSending || s.rateLimitedSeconds != null) return
        sendEffect(AiChatEffect.PickImage)
    }

    private fun onImagePicked(raw: ByteArray) {
        viewModelScope.launch {
            val processed = withContext(Dispatchers.Default) { processImageForUpload(raw) }
            updateState { copy(pendingImage = PendingImageUi(nextId(), processed)) }
        }
    }

    private fun onMicClicked() {
        if (currentState.isListening) {
            stopListening()
            return
        }
        if (!currentState.canUseVoice) return
        updateState { copy(isListening = true, errorMessage = null) }
        sendEffect(AiChatEffect.StartVoiceCapture)
    }

    private fun stopListening() {
        if (!currentState.isListening) return
        updateState { copy(isListening = false) }
        sendEffect(AiChatEffect.StopVoiceCapture)
    }

    private fun onSendClicked() {
        stopListening()
        send(currentState.input.trim())
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
                    sendEffect(AiChatEffect.ShowMessage(getString(Res.string.ai_favorite_login_required)))
                is ToggleFavoriteResult.Error ->
                    sendEffect(AiChatEffect.ShowMessage(result.throwable.message ?: getString(Res.string.ai_favorite_update_failed)))
                ToggleFavoriteResult.Added, ToggleFavoriteResult.Removed -> Unit // wishlist flow updates state
            }
        }
    }

    private fun send(text: String, isRetry: Boolean = false) {
        val s = currentState
        if (s.isSending || s.rateLimitedSeconds != null) return
        val pending = s.pendingImage
        if (!isRetry && text.isBlank() && pending == null) return
        lastUserText = text

        if (!isRetry) {
            val userMsg = ChatMessageUi(
                id = nextId(),
                sender = AiSender.USER,
                text = text,
                image = pending,
            )
            updateState {
                copy(
                    messages = messages + userMsg,
                    input = "",
                    isSending = true,
                    errorMessage = null,
                    pendingImage = null,
                )
            }
        } else {
            updateState { copy(isSending = true, errorMessage = null) }
        }

        val history = buildHistory()
        viewModelScope.launch {
            val imageBase64 = if (isRetry) {
                lastImageBase64
            } else {
                pending?.let { withContext(Dispatchers.Default) { encodeBase64(it.bytes) } }
                    .also { lastImageBase64 = it }
            }
            when (val r = sendAiMessage(text, imageBase64 = imageBase64, history = history)) {
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

    private suspend fun handleError(t: Throwable) {
        when (t) {
            is RateLimitException ->
                updateState { copy(isSending = false, rateLimitedSeconds = t.retryAfterSeconds) }
            else -> {
                val fallback = getString(Res.string.error_view_title)
                updateState { copy(isSending = false, errorMessage = t.message ?: fallback) }
            }
        }
    }

    private fun buildHistory(): List<AiChatTurn> =
        currentState.messages
            .filter { it.text.isNotBlank() }
            .takeLast(6)
            .map { AiChatTurn(if (it.sender == AiSender.USER) "user" else "assistant", it.text) }


    private fun onOpenHistory() {
        updateState { copy(showHistory = true) }
        loadHistory()
    }

    private fun loadHistory() {
        updateState {
            copy(historyLoading = true, historyError = null, historyRequiresLogin = false)
        }
        viewModelScope.launch {
            when (val result = getConversations()) {
                is AiHistoryListResult.Success -> {
                    loadedConversations = result.conversations
                    val summaries = buildSummaries(result.conversations)
                    updateState {
                        copy(historyLoading = false, conversations = summaries)
                    }
                }
                AiHistoryListResult.RequiresLogin -> updateState {
                    copy(historyLoading = false, historyRequiresLogin = true, conversations = emptyList())
                }
                is AiHistoryListResult.Error -> {
                    val historyFallback = getString(Res.string.ai_history_error)
                    updateState {
                        copy(
                            historyLoading = false,
                            historyError = result.throwable.message ?: historyFallback,
                        )
                    }
                }
            }
        }
    }

    private fun onLoadConversation(id: String) {
        // Selecting the already-open chat just dismisses the sheet — no reload, no request.
        if (id == CURRENT_SESSION_ID || id == activeConversationId) {
            updateState { copy(showHistory = false) }
            return
        }
        val conversation = loadedConversations.firstOrNull { it.id == id }
        if (conversation == null) {
            updateState { copy(showHistory = false) }
            return
        }
        // Persist whatever is on screen before we replace it, so nothing is lost.
        persistCurrentConversation()

        activeConversationId = conversation.id
        val favIds = currentState.favoriteProductIds
        val restored = conversation.messages.map { it.toUi(favIds) }
        lastUserText = restored.lastOrNull { it.sender == AiSender.USER }?.text
        lastImageBase64 = null
        updateState {
            copy(
                messages = restored,
                showHistory = false,
                input = "",
                pendingImage = null,
                errorMessage = null,
                isSending = false,
            )
        }
    }

    private fun onDeleteConversation(id: String) {
        if (id == CURRENT_SESSION_ID) return // the current unsaved chat isn't in Firestore yet
        loadedConversations = loadedConversations.filterNot { it.id == id }
        updateState { copy(conversations = conversations.filterNot { it.id == id }) }
        if (id == activeConversationId) activeConversationId = null
        viewModelScope.launch {
            if (!deleteConversation(id)) {
                sendEffect(AiChatEffect.ShowMessage(getString(Res.string.ai_history_delete_failed)))
                loadHistory() // resync so the row reappears
            }
        }
    }

    private fun onNewChat() {
        persistCurrentConversation()
        activeConversationId = null
        lastUserText = null
        lastImageBase64 = null
        updateState {
            copy(
                messages = emptyList(),
                input = "",
                pendingImage = null,
                errorMessage = null,
                isSending = false,
                showHistory = false,
            )
        }
    }


    private fun persistCurrentConversation() {
        CoroutineScope(Dispatchers.Default).launch {
            val snapshot = buildExitSnapshot() ?: return@launch
            saveConversation(snapshot)
        }
    }

    private suspend fun buildExitSnapshot(): AiConversation? {
        val messages = currentState.messages
        if (messages.none { it.sender == AiSender.ASSISTANT }) return null
        return AiConversation(
            id = activeConversationId ?: "",
            title = deriveTitle(messages, getString(Res.string.ai_history_new_chat)),
            updatedAt = nowEpochMillis(),
            messages = messages.map { it.toStored() },
        )
    }

    private suspend fun buildSummaries(remote: List<AiConversation>): List<ConversationSummaryUi> {
        val now = nowEpochMillis()
        val current = currentSessionSummary()
        val newChatLabel = getString(Res.string.ai_history_new_chat)
        val rows = remote
            .filter { it.id != activeConversationId }
            .map {
                ConversationSummaryUi(
                    id = it.id,
                    title = it.title.ifBlank { newChatLabel },
                    timeLabel = relativeTime(now, it.updatedAt),
                    isActive = it.id == activeConversationId,
                )
            }
        return listOfNotNull(current) + rows
    }

    private suspend fun currentSessionSummary(): ConversationSummaryUi? {
        val messages = currentState.messages
        if (messages.none { it.sender == AiSender.ASSISTANT }) return null
        return ConversationSummaryUi(
            id = activeConversationId ?: CURRENT_SESSION_ID,
            title = deriveTitle(messages, getString(Res.string.ai_history_new_chat)),
            timeLabel = getString(Res.string.ai_time_now),
            isActive = true,
        )
    }

    private fun deriveTitle(messages: List<ChatMessageUi>, newChatLabel: String): String {
        val firstUser = messages.firstOrNull { it.sender == AiSender.USER }?.text?.trim().orEmpty()
        val base = firstUser.ifBlank { newChatLabel }
        return if (base.length > TITLE_MAX) base.take(TITLE_MAX).trimEnd() + "…" else base
    }

    private suspend fun relativeTime(now: Long, then: Long): String {
        if (then <= 0L) return ""
        val diff = (now - then).coerceAtLeast(0L)
        val minutes = diff / 60_000
        val hours = diff / 3_600_000
        val days = diff / 86_400_000
        return when {
            minutes < 1 -> getString(Res.string.time_just_now)
            minutes < 60 -> getString(Res.string.time_minutes_ago, minutes.toInt())
            hours < 24 -> getString(Res.string.time_hours_ago, hours.toInt())
            days < 7 -> getString(Res.string.time_days_ago, days.toInt())
            else -> getString(Res.string.time_weeks_ago, (days / 7).toInt())
        }
    }

    override fun onCleared() {
        persistCurrentConversation()
        super.onCleared()
    }

    private fun ChatMessageUi.toStored() = AiStoredMessage(
        id = id,
        role = if (sender == AiSender.USER) "user" else "assistant",
        text = text,
        isSuggestion = isSuggestion,
        clarifying = clarifying,
        declined = declined,
        products = products.map { it.toStored() },
    )

    private fun AiProductUi.toStored() = AiStoredProduct(
        id = id,
        handle = handle,
        title = title,
        imageUrl = imageUrl,
        priceFormatted = priceFormatted,
        priceAmount = priceAmount,
        why = why,
    )

    private fun AiStoredMessage.toUi(favIds: Set<String>) = ChatMessageUi(
        id = id,
        sender = if (role == "user") AiSender.USER else AiSender.ASSISTANT,
        text = text,
        products = products.map { it.toUi(favIds) },
        isSuggestion = isSuggestion,
        clarifying = clarifying,
        declined = declined,
        image = null,
    )

    private fun AiStoredProduct.toUi(favIds: Set<String>): AiProductUi {
        val numericId = id.substringAfterLast('/')
        return AiProductUi(
            id = id,
            numericId = numericId,
            handle = handle,
            title = title,
            imageUrl = imageUrl,
            priceFormatted = priceFormatted,
            priceAmount = priceAmount,
            why = why,
            isFavorite = numericId in favIds,
        )
    }

    private companion object {
        const val CURRENT_SESSION_ID = "__current__"
        const val TITLE_MAX = 40
    }
}
