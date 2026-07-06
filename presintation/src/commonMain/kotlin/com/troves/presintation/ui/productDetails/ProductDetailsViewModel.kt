package com.troves.presintation.ui.productDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.Product
import com.troves.domain.usecase.cart.AddToCartUseCase
import com.troves.domain.usecase.cart.CartOperationResult
import com.troves.domain.usecase.details.GetProductByIdUseCase
import com.troves.domain.usecase.wishlist.IsProductFavoritedUseCase
import com.troves.domain.usecase.wishlist.ToggleFavoriteResult
import com.troves.domain.usecase.wishlist.ToggleFavoriteUseCase
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.troves.domain.utils.Result

class ProductDetailsViewModel(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val isProductFavorite: IsProductFavoritedUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getCartStreamUseCase: com.troves.domain.usecase.cart.GetCartStreamUseCase,
) : ViewModel(),
    StateHolder<ProductDetailUiState> by DefaultStateHolder(ProductDetailUiState()),
    EffectPublisher<ProductDetailsEffect> by DefaultEffectPublisher() {

    private var favoriteJob: Job? = null
    private var cartJob: Job? = null
    private var bannerDismissJob: Job? = null

    fun onIntent(intent: ProductDetailsIntent) {
        when (intent) {
            ProductDetailsIntent.OnBackClick ->
                sendEffect(ProductDetailsEffect.NavigateBack)

            ProductDetailsIntent.OnSeeAllReviews ->
                sendEffect(ProductDetailsEffect.ShowToast("Coming soon..."))

            ProductDetailsIntent.OnSizeGuide ->
                sendEffect(ProductDetailsEffect.NavigateBack)

            ProductDetailsIntent.OnAddToCart -> addCurrentProductToCart()

            is ProductDetailsIntent.OnFavoriteClick -> onFavoriteClick()

            is ProductDetailsIntent.OnOptionSelected ->
                updateState {
                    copy(selectedOptions = selectedOptions + (intent.optionName to intent.value))
                }

            is ProductDetailsIntent.Retry -> fetchProduct(intent.productId)
            is ProductDetailsIntent.Load -> fetchProduct(intent.productId)

            ProductDetailsIntent.OnViewCartClick ->
                sendEffect(ProductDetailsEffect.NavigateToCart)

            ProductDetailsIntent.OnDismissCartConfirmation -> {
                bannerDismissJob?.cancel()
                updateState { copy(showCartConfirmation = false) }
            }
        }
    }

    private fun fetchProduct(productId: String) {
        viewModelScope.launch {
            when (val product = getProductByIdUseCase(productId)) {
                Result.Loading -> updateState { copy(isLoading = true) }

                is Result.Error -> updateState {
                    copy(
                        isLoading = false,
                        errorMessage = product.throwable.message ?: "Unknown error",
                    )
                }

                is Result.Success<Product> -> {
                    val value = product.value
                    updateState {
                        copy(
                            isLoading = false,
                            product = value,
                            images = value.images,
                            title = value.title,
                            priceFormatted = value.price,
                            errorMessage = null,
                            description = value.description,
                            rating = value.rating,
                        )
                    }
                    observeFavoriteStatus(productId)
                    observeCartStatus(productId)
                }
            }
        }
    }

    private fun observeFavoriteStatus(productId: String) {
        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            isProductFavorite(productId).collect { favorited ->
                updateState { copy(isFavorite = favorited) }
            }
        }
    }

    private fun observeCartStatus(productId: String) {
        cartJob?.cancel()
        cartJob = viewModelScope.launch {
            getCartStreamUseCase().collect { cart ->
                val quantity = cart?.lines
                    ?.filter { it.productId.toString() == productId }
                    ?.sumOf { it.quantity } ?: 0
                updateState { copy(productCartQuantity = quantity) }
            }
        }
    }

    private fun onFavoriteClick() {
        val product = currentState.product ?: run {
            sendEffect(ProductDetailsEffect.ShowToast("Couldn't update favorites"))
            return
        }
        val wasFavorite = currentState.isFavorite

        updateState { copy(isFavorite = !wasFavorite) }

        viewModelScope.launch {
            when (toggleFavoriteUseCase(product)) {
                ToggleFavoriteResult.Added ->
                    sendEffect(ProductDetailsEffect.ShowToast("${product.title} added to favorites"))

                ToggleFavoriteResult.Removed ->
                    sendEffect(ProductDetailsEffect.ShowToast("${product.title} removed from favorites"))

                ToggleFavoriteResult.RequiresLogin -> {
                    updateState { copy(isFavorite = wasFavorite) }
                    sendEffect(ProductDetailsEffect.ShowLoginRequiredDialog)
                }

                is ToggleFavoriteResult.Error -> {
                    updateState { copy(isFavorite = wasFavorite) }
                    sendEffect(ProductDetailsEffect.ShowToast("Couldn't update favorites"))
                }
            }
        }
    }

    private fun addCurrentProductToCart() {
        val state = currentState
        state.product ?: return
        val variant = state.selectedVariant
        if (variant == null || !variant.available) {
            sendEffect(ProductDetailsEffect.ShowToast(state.addToCartHint ?: "Select options first"))
            return
        }
        updateState { copy(isAddingToCart = true) }
        viewModelScope.launch {
            val result = addToCartUseCase(variantId = variant.variantId, quantity = 1)
            updateState { copy(isAddingToCart = false) }
            when (result) {
                CartOperationResult.Success -> showCartConfirmationBar()

                CartOperationResult.RequiresLogin ->
                    sendEffect(ProductDetailsEffect.ShowLoginRequiredDialog)

                is CartOperationResult.Error ->
                    sendEffect(ProductDetailsEffect.ShowToast("Couldn't add to cart"))
            }
        }
    }

    private fun showCartConfirmationBar() {
        updateState { copy(showCartConfirmation = true) }
        bannerDismissJob?.cancel()
        bannerDismissJob = viewModelScope.launch {
            kotlinx.coroutines.delay(4000)
            updateState { copy(showCartConfirmation = false) }
        }
    }
}