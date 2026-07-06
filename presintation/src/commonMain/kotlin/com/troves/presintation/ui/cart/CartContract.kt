package com.troves.presintation.ui.cart

data class CartUiState(
    val items: List<CartLineUi> = emptyList(),
    val subtotalFormatted: String = "$0.00",
    val totalFormatted: String = "$0.00",
    val checkoutUrl: String? = null,
    val isLoading: Boolean = true,
) {
    val isEmpty: Boolean get() = !isLoading && items.isEmpty()
}

data class CartLineUi(
    val lineId: String,
    val title: String,
    val variantTitle: String,
    val imageUrl: String,
    val price: String,
    val quantity: Int,
    val maxQuantity: Int?,
) {
    val atMaxQuantity: Boolean get() = maxQuantity != null && quantity >= maxQuantity
}

sealed interface CartEffect {
    data object NavigateBack : CartEffect
    data object NavigateToCheckout : CartEffect
    data object ShowLoginRequiredDialog : CartEffect
    data class ShowToast(val message: String) : CartEffect
    data class ShowRemoveConfirmationDialog(val item: CartLineUi) : CartEffect
    data object ShowClearCartConfirmationDialog : CartEffect
}

sealed interface CartIntent {
    data object OnBackClick : CartIntent
    data object OnCheckout : CartIntent
    data class OnIncrement(val lineId: String) : CartIntent
    data class OnDecrement(val lineId: String) : CartIntent
    data class OnRemoveItemClick(val lineId: String) : CartIntent
    data class OnRemoveItemConfirm(val lineId: String) : CartIntent
    data object OnClearCartClick : CartIntent
    data object OnClearCartConfirm : CartIntent
}
