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
import com.troves.presintation.ui.productDetails.models.ReviewUi

import org.jetbrains.compose.resources.getString
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.product_details_coming_soon
import troves.designsystem.generated.resources.product_details_out_of_stock
import troves.designsystem.generated.resources.product_details_select_option
import troves.designsystem.generated.resources.product_details_unavailable_combination

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
                viewModelScope.launch {
                    sendEffect(ProductDetailsEffect.ShowToast(getString(Res.string.product_details_coming_soon)))
                }

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
                    val randomReviews = MOCK_REVIEWS.shuffled().take((2..MOCK_REVIEWS.size).random())
                    val averageRating = randomReviews.map { it.rating }.average().toInt()
                    updateState {
                        copy(
                            isLoading = false,
                            product = value,
                            images = value.images,
                            title = value.title,
                            priceFormatted = value.price,
                            errorMessage = null,
                            description = value.description,
                            rating = averageRating,
                            reviews = randomReviews,
                            reviewCount = randomReviews.size
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
            viewModelScope.launch {
                val message = when {
                    variant == null -> {
                        val missing = state.displayOptions.firstOrNull { state.selectedOptions[it.name].isNullOrEmpty() }
                        if (missing != null) getString(Res.string.product_details_select_option, missing.name)
                        else getString(Res.string.product_details_unavailable_combination)
                    }
                    !variant.available -> getString(Res.string.product_details_out_of_stock)
                    else -> ""
                }
                sendEffect(ProductDetailsEffect.ShowToast(message))
            }
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

private val MOCK_REVIEWS = listOf(
    ReviewUi("Ahmed", 5, "Oct 1, 2023", "Excellent product, very high quality!"),
    ReviewUi("Sara", 4, "Oct 5, 2023", "Good value for money, but shipping was a bit slow."),
    ReviewUi("Mohamed", 5, "Oct 10, 2023", "I love it! Exactly what I was looking for."),
    ReviewUi("Layla", 3, "Oct 12, 2023", "It's okay, but the color is slightly different from the photos."),
    ReviewUi("Omar", 5, "Oct 15, 2023", "Perfect fit and very comfortable."),
    ReviewUi("Nour", 4, "Oct 18, 2023", "Great quality, will definitely buy again."),
    ReviewUi("Khaled", 2, "Oct 20, 2023", "Disappointed, it broke after two days of use."),
    ReviewUi("Mona", 5, "Oct 22, 2023", "Super fast delivery and amazing customer service."),
    ReviewUi("Zaid", 4, "Oct 25, 2023", "Nice design, but a bit smaller than expected."),
    ReviewUi("Huda", 5, "Oct 28, 2023", "Absolutely beautiful! Highly recommend.")
)
