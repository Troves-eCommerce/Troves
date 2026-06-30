package com.troves.presintation.ui.fav


sealed interface WishlistUiEffect {
    data class NavigateToProduct(val productId: String) : WishlistUiEffect
    data class ShowToast(val message: String) : WishlistUiEffect
}