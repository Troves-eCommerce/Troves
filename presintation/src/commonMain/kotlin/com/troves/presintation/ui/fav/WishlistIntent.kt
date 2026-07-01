package com.troves.presintation.ui.fav

import com.troves.domain.entity.Product

sealed interface WishlistIntent {
    data object Load : WishlistIntent
    data object Retry : WishlistIntent
    data class ProductClicked(val product: Product) : WishlistIntent
    data class RemoveClicked(val product: Product) : WishlistIntent
    data object ClearAllClicked : WishlistIntent
}