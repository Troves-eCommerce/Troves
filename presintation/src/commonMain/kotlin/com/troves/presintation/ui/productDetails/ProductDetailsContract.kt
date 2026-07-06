package com.troves.presintation.ui.productDetails

import com.troves.domain.entity.Product
import com.troves.domain.entity.ProductOption
import com.troves.domain.entity.ProductVariant
import com.troves.presintation.ui.productDetails.models.ReviewUi

data class ProductDetailUiState(
    val title: String = "",
    val priceFormatted: String = "",
    val rating: Int = 0,
    val reviewCount: Int = 0,
    val description: String = "",
    val images: List<String> = emptyList(),
    val reviews: List<ReviewUi> = emptyList(),
    val selectedOptions: Map<String, String> = emptyMap(),
    val currentImageIndex: Int = 0,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true,
    val isAddingToCart: Boolean = false,
    val errorMessage: String? = null,
    val product: Product? = null,
    val showCartConfirmation: Boolean = false,
    val productCartQuantity: Int = 0,
) {
    val hasError = errorMessage != null


    val displayOptions: List<ProductOption>
        get() = product?.options.orEmpty()
            .filterNot { it.name == "Title" && it.values == listOf("Default Title") }


    val selectedVariant: ProductVariant?
        get() {
            val p = product ?: return null
            if (p.variants.isEmpty()) return null
            if (displayOptions.isEmpty()) return p.variants.firstOrNull()
            val names = displayOptions.map { it.name }
            if (names.any { selectedOptions[it].isNullOrEmpty() }) return null
            return p.variants.firstOrNull { variant ->
                names.all { variant.selectedOptions[it] == selectedOptions[it] }
            }
        }

    /** Price of the chosen variant, falling back to the product's from-price. */
    val displayPrice: String
        get() = selectedVariant?.price ?: priceFormatted

    val canAddToCart: Boolean
        get() = !isAddingToCart && selectedVariant?.available == true
}

sealed interface ProductDetailsEffect {
    data object NavigateBack : ProductDetailsEffect
    data class ShowToast(val message: String) : ProductDetailsEffect
    data object ShowLoginRequiredDialog : ProductDetailsEffect
    data object NavigateToCart : ProductDetailsEffect
}

sealed interface ProductDetailsIntent {
    data class Load(val productId: String) : ProductDetailsIntent
    data class Retry(val productId: String) : ProductDetailsIntent
    data object OnSeeAllReviews : ProductDetailsIntent
    data object OnSizeGuide : ProductDetailsIntent
    data object OnBackClick : ProductDetailsIntent
    data class OnOptionSelected(val optionName: String, val value: String) : ProductDetailsIntent
    data object OnAddToCart : ProductDetailsIntent
    data object OnFavoriteClick : ProductDetailsIntent
    data object OnViewCartClick : ProductDetailsIntent
    data object OnDismissCartConfirmation : ProductDetailsIntent
}
