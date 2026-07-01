package com.troves.presintation.ui.fav

import com.troves.domain.entity.Product

sealed interface WishlistEffect {
    data class NavigateToProduct(val productId: String) : WishlistEffect
    data class ShowToast(val message: String) : WishlistEffect
    data object ShowLoginRequiredDialog : WishlistEffect
}

data class WishlistState(
    val isLoading: Boolean = true,
    val items: List<Product> = emptyList(),
    val errorMessage: String? = null,
) {
    val isEmpty: Boolean get() = !isLoading && items.isEmpty() && errorMessage == null
}
sealed interface WishlistIntent {
    data object Load : WishlistIntent
    data object Retry : WishlistIntent
    data class ProductClicked(val product: Product) : WishlistIntent
    data class RemoveClicked(val product: Product) : WishlistIntent
    data object ClearAllClicked : WishlistIntent
}