package com.troves.presintation.ui.productDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.Product
import com.troves.domain.usecase.details.GetProductByIdUseCase
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.launch
import com.troves.domain.utils.Result

class ProductDetailsViewModel(
    private val getProductByIdUseCase: GetProductByIdUseCase,
) : ViewModel(),
    StateHolder<ProductDetailUiState> by DefaultStateHolder(ProductDetailUiState()),
    EffectPublisher<ProductDetailsEffect> by DefaultEffectPublisher() {

    fun onIntent(intent: ProductDetailsIntent) {
        when (intent) {
            ProductDetailsIntent.OnBackClick ->
                sendEffect(ProductDetailsEffect.NavigateBack)

            ProductDetailsIntent.OnSeeAllReviews ->
                sendEffect(ProductDetailsEffect.ShowToast("Coming soon..."))

            ProductDetailsIntent.OnSizeGuide ->
                sendEffect(ProductDetailsEffect.NavigateBack)

            ProductDetailsIntent.OnAddToCart ->
                sendEffect(ProductDetailsEffect.ShowToast("Coming soon..."))

            is ProductDetailsIntent.OnFavoriteClick ->
                sendEffect(ProductDetailsEffect.ShowToast("Coming soon..."))

            is ProductDetailsIntent.OnColorSelectedChange ->
                updateState { copy(selectedColorIndex = intent.colorIndex) }

            is ProductDetailsIntent.OnSizeSelectedChange ->
                updateState { copy(selectedSizeLabel = intent.newSize) }

            is ProductDetailsIntent.Retry -> fetchProduct(intent.productId)
            is ProductDetailsIntent.Load -> fetchProduct(intent.productId)
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
                            images = value.images,
                            colors = value.colors,
                            title = value.title,
                            priceFormatted = value.price,
                            errorMessage = null,
                            description = value.description,
                            rating = value.rating,
                        )
                    }
                }
            }
        }
    }
}
