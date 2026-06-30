package com.troves.presintation.ui.fav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.Product
import com.troves.domain.usecase.wishlist.GetWishlistUseCase
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
) : ViewModel() {

    private val _state = MutableStateFlow(WishlistUiState())
    val state: StateFlow<WishlistUiState> = _state.asStateFlow()

    private val _effect = Channel<WishlistUiEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        onIntent(WishlistIntent.Load)
    }

    fun onIntent(intent: WishlistIntent) {
        when (intent) {
            WishlistIntent.Load, WishlistIntent.Retry -> loadWishlist()
            is WishlistIntent.ProductClicked ->
                sendEffect(WishlistUiEffect.NavigateToProduct(intent.product.id.toString()))
            is WishlistIntent.RemoveClicked -> removeFavorite(intent.product)
            WishlistIntent.ClearAllClicked -> clearAll()
        }
    }

    private fun loadWishlist() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            getWishlist().collect { items ->
                _state.update { it.copy(isLoading = false, items = items) }
            }
        }
    }

    private fun removeFavorite(product: Product) {
        _state.update { current -> current.copy(items = current.items.filterNot { it.id == product.id }) }
        viewModelScope.launch {
            runCatching { toggleFavoriteUseCase(product) }
                .onSuccess {
                    sendEffect(WishlistUiEffect.ShowToast("${product.title} removed from wishlist"))
                }
                .onFailure {
                    sendEffect(WishlistUiEffect.ShowToast("Couldn't remove item"))
                    loadWishlist()
                }
        }
    }

    private fun clearAll() {
        val toRemove = _state.value.items
        if (toRemove.isEmpty()) return
        _state.update { it.copy(items = emptyList()) }
        viewModelScope.launch {
            toRemove.forEach { product -> runCatching { toggleFavoriteUseCase(product) } }
            sendEffect(WishlistUiEffect.ShowToast("Wishlist cleared"))
        }
    }

    private fun sendEffect(newEffect: WishlistUiEffect) {
        viewModelScope.launch { _effect.send(newEffect) }
    }
}