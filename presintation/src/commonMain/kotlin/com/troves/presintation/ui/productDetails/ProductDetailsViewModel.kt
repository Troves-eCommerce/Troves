package com.troves.presintation.ui.productDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.Result
import com.troves.domain.entity.Product
import com.troves.domain.usecase.details.GetProductByIdUseCase
import com.troves.domain.usecase.wishlist.IsProductFavoritedUseCase
import com.troves.domain.usecase.wishlist.ToggleFavoriteUseCase
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val isProductFavorite: IsProductFavoritedUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailUiState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ProductDetailsEffect>(
        Channel.BUFFERED,
        /*onBufferOverflow = BufferOverflow.DROP_OLDEST*/
    )
    val effect = _effect.receiveAsFlow()
    private var currentProduct: Product? = null
    private var favoriteJob: kotlinx.coroutines.Job? = null

    fun onIntent(intent: ProductDetailsIntent) {
        when (intent) {
            ProductDetailsIntent.OnBackClick -> {
                sendEffect(ProductDetailsEffect.NavigateBack)
            }

            ProductDetailsIntent.OnSeeAllReviews -> {
                sendEffect(ProductDetailsEffect.ShowToast("Coming soon..."))
            }

            ProductDetailsIntent.OnSizeGuide -> {
                sendEffect(ProductDetailsEffect.NavigateBack)
            }

            ProductDetailsIntent.OnAddToCart -> {
                sendEffect(ProductDetailsEffect.ShowToast("Coming soon..."))
            }

            is ProductDetailsIntent.OnColorSelectedChange -> {
                _state.update { state ->
                    state.copy(selectedColorIndex = intent.colorIndex)
                }
            }

            is ProductDetailsIntent.OnFavoriteClick -> {
                onFavoriteClick()
            }

            is ProductDetailsIntent.OnSizeSelectedChange -> {
                _state.update { state ->
                    state.copy(selectedSizeLabel = intent.newSize)
                }
            }

            is ProductDetailsIntent.Retry -> {
                fetchProduct(intent.productId)
            }

            is ProductDetailsIntent.Load -> {
                fetchProduct(intent.productId)
            }
        }
    }

    private fun fetchProduct(productId: String) {
        viewModelScope.launch {
            val product = getProductByIdUseCase(productId)
            when (product) {
                Result.Loading -> {
                    _state.update {
                        it.copy(isLoading = true)
                    }
                }

                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = product.throwable.message ?: "Unknown error"
                        )
                    }
                }

                is Result.Success<Product> -> {
                    val currentstate = product.value
                    currentProduct = currentstate

                    _state.update {
                        it.copy(
                            isLoading = false,
                            images = currentstate.images,
                            colors = currentstate.colors,
                            title = currentstate.title,
                            priceFormatted = currentstate.price,
                            errorMessage = null,
                            description = currentstate.description,
                            rating = currentstate.rating,
                        )
                    }

                    observeFavoriteStatus(productId)
                }
            }
        }
    }

    private fun observeFavoriteStatus(productId: String) {
        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            isProductFavorite(productId).collect { favorited ->
                _state.update { it.copy(isFavorite = favorited) }
            }
        }
    }

    private fun onFavoriteClick() {
        val product = currentProduct ?: run {
            sendEffect(ProductDetailsEffect.ShowToast("Couldn't update favorites"))
            return
        }
        val wasFavorite = _state.value.isFavorite

        _state.update { it.copy(isFavorite = !wasFavorite) }

        viewModelScope.launch {
            runCatching { toggleFavoriteUseCase(product) }
                .onSuccess {
                    val message = if (wasFavorite) {
                        "${product.title} removed from favorites"
                    } else {
                        "${product.title} added to favorites"
                    }
                    sendEffect(ProductDetailsEffect.ShowToast(message))
                }
                .onFailure {
                    _state.update { it.copy(isFavorite = wasFavorite) }
                    sendEffect(ProductDetailsEffect.ShowToast("Couldn't update favorites"))
                }
        }
    }

    private fun sendEffect(effect: ProductDetailsEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }


}