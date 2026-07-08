package com.troves.presintation.ui.cart
import com.troves.domain.entity.CartMoney

data class CartUiState(
    val items: List<CartLineUi> = emptyList(),
    val subtotal: CartMoney? = null,
    val total: CartMoney? = null,
    val checkoutUrl: String? = null,
    val isLoading: Boolean = true,
    val shouldShowCartHint: Boolean = false,
) {
    val isEmpty: Boolean get() = !isLoading && items.isEmpty()
}

data class CartLineUi(
    val lineId: String,
    val productId: String,
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
    data class NavigateToProductDetails(val productId: String) : CartEffect
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
    data class OnItemClick(val lineId: String) : CartIntent
    data object OnDismissCartHint : CartIntent
}