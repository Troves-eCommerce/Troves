package com.troves.presintation.ui.cart

data class CartUiState(
    val items: List<CartItemUi> = emptyList(),
    val totalFormatted: String = "$0.00",
    val isLoading: Boolean = true,
) {
    val isEmpty: Boolean get() = !isLoading && items.isEmpty()
}

data class CartItemUi(
    val productId: Long,
    val title: String,
    val description: String,
    val imageUrl: String,
    val price: String,
    val quantity: Int,
)

sealed interface CartEffect {
    data object NavigateBack : CartEffect
    data object NavigateToCheckout : CartEffect
}

sealed interface CartIntent {
    data object OnBackClick : CartIntent
    data object OnCheckout : CartIntent
    data class OnIncrement(val productId: Long) : CartIntent
    data class OnDecrement(val productId: Long) : CartIntent
}
