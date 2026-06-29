package com.troves.presintation.ui.productDetails

sealed interface ProductDetailsIntent {
    data class Load(val productId: String) : ProductDetailsIntent
    data class Retry(val productId: String) : ProductDetailsIntent
    data object OnSeeAllReviews : ProductDetailsIntent
    data object OnSizeGuide : ProductDetailsIntent
    data object OnBackClick : ProductDetailsIntent
    data class OnSizeSelectedChange(val newSize: String) : ProductDetailsIntent
    data class OnColorSelectedChange(val newColor: String) : ProductDetailsIntent
    data class OnFavoriteClick(val productId: String) : ProductDetailsIntent
}