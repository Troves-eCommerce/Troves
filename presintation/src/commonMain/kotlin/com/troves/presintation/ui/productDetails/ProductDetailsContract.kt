package com.troves.presintation.ui.productDetails

import com.troves.domain.entity.Product
import com.troves.presintation.ui.productDetails.models.ReviewUi

data class ProductDetailUiState(
    val title: String = "",
    val priceFormatted: String = "",
    val rating: Int = 0,
    val reviewCount: Int = 0,
    val description: String = "",
    val images: List<String> = emptyList(),
    val sizes: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    val reviews: List<ReviewUi> = emptyList(),
    val selectedSizeLabel: String = "",
    val selectedColorIndex: Int = 0,
    val currentImageIndex: Int = 0,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val product: Product? = null,
) {
    val hasError = errorMessage != null
}

sealed interface ProductDetailsEffect {
    data object NavigateBack : ProductDetailsEffect
    data class ShowToast(val message: String) : ProductDetailsEffect
}

sealed interface ProductDetailsIntent {
    data class Load(val productId: String) : ProductDetailsIntent
    data class Retry(val productId: String) : ProductDetailsIntent
    data object OnSeeAllReviews : ProductDetailsIntent
    data object OnSizeGuide : ProductDetailsIntent
    data object OnBackClick : ProductDetailsIntent
    data class OnSizeSelectedChange(val newSize: String) : ProductDetailsIntent
    data class OnColorSelectedChange(val colorIndex: Int) : ProductDetailsIntent
    data object OnAddToCart : ProductDetailsIntent
    data object OnFavoriteClick : ProductDetailsIntent
}
