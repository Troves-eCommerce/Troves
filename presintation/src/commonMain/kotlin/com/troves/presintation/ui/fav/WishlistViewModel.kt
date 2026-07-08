package com.troves.presintation.ui.fav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.Product
import com.troves.domain.usecase.auth.IsLoggedInUseCase
import com.troves.domain.usecase.shared.ObserveConnectivityUseCase
import com.troves.domain.usecase.wishlist.GetWishlistUseCase
import com.troves.domain.usecase.wishlist.SyncWishlistUseCase
import com.troves.domain.usecase.wishlist.ToggleFavoriteResult
import com.troves.domain.usecase.wishlist.ToggleFavoriteUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WishlistViewModel(
    private val getWishlist: GetWishlistUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val syncWishlist: SyncWishlistUseCase,
    private val isLoggedIn: IsLoggedInUseCase,
    private val observeConnectivity: ObserveConnectivityUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(WishlistState())
    val state: StateFlow<WishlistState> = _state.asStateFlow()

    private val _effect = Channel<WishlistEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        onIntent(WishlistIntent.Load)
    }

    fun onIntent(intent: WishlistIntent) {
        when (intent) {
            WishlistIntent.Load, WishlistIntent.Retry -> loadWishlist()
            WishlistIntent.Refresh -> refresh()
            is WishlistIntent.ProductClicked ->
                sendEffect(WishlistEffect.NavigateToProduct(intent.product.id.toString()))
            is WishlistIntent.RemoveClicked -> removeFavorite(intent.product)
            WishlistIntent.ClearAllClicked -> clearAll()
        }
    }

    private fun loadWishlist() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            if (!isLoggedIn()) {
                _state.update { it.copy(isLoading = false, isNotSignedIn = true, items = emptyList()) }
                return@launch
            }
            getWishlist().collect { items ->
                _state.update { it.copy(isLoading = false, isNotSignedIn = false, items = items) }
            }
        }
    }

    private fun refresh() {
        // Offline: keep showing the cached Room-backed list; don't nag with a sync error.
        if (!observeConnectivity.isOnlineNow()) return
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            try {
                syncWishlist(forceRefresh = true)
            } catch (_: Exception) {
                sendEffect(WishlistEffect.ShowToast("Failed to sync wishlist"))
            } finally {
                _state.update { it.copy(isRefreshing = false) }
            }
        }
    }

    /** Wishlist is view-only while offline: block mutations with a clear message. */
    private fun ensureOnline(): Boolean {
        if (observeConnectivity.isOnlineNow()) return true
        sendEffect(WishlistEffect.ShowToast(NO_CONNECTION_MESSAGE))
        return false
    }

    private fun removeFavorite(product: Product) {
        if (!ensureOnline()) return
        _state.update { current -> current.copy(items = current.items.filterNot { it.id == product.id }) }
        viewModelScope.launch {
            when (toggleFavoriteUseCase(product)) {
                ToggleFavoriteResult.Removed -> Unit
                ToggleFavoriteResult.RequiresLogin -> {
                    sendEffect(WishlistEffect.ShowLoginRequiredDialog)
                    loadWishlist()
                }
                else -> {
                    sendEffect(WishlistEffect.ShowToast("Couldn't remove item"))
                    loadWishlist()
                }
            }
        }
    }

    private fun clearAll() {
        if (!ensureOnline()) return
        val toRemove = _state.value.items
        if (toRemove.isEmpty()) return
        _state.update { it.copy(items = emptyList()) }
        viewModelScope.launch {
            toRemove.forEach { product -> toggleFavoriteUseCase(product) }
            // No toast needed, popup confirmation is enough
        }
    }

    private fun sendEffect(newEffect: WishlistEffect) {
        viewModelScope.launch { _effect.send(newEffect) }
    }

    private companion object {
        const val NO_CONNECTION_MESSAGE = "No internet connection"
    }
}