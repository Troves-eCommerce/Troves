package com.troves.presintation.ui.fav

import com.troves.domain.entity.Product

data class WishlistUiState(
    val isLoading: Boolean = true,
    val items: List<Product> = emptyList(),
    val errorMessage: String? = null,
) {
    val isEmpty: Boolean get() = !isLoading && items.isEmpty() && errorMessage == null
}