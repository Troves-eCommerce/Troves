package com.troves.presintation.ui.checkout

import com.troves.domain.entity.Address
import com.troves.domain.entity.CartItem

data class CheckoutUiState(
    val cartItems: List<CartItem> = emptyList(),
    val addresses: List<Address> = emptyList(),
    val selectedAddress: Address? = null,
    val isLoading: Boolean = true,
    val isPlacingOrder: Boolean = false,
    val showAddressSheet: Boolean = false
) {
    val subtotal: Double get() = cartItems.sumOf { (it.product.price.toDoubleOrNull() ?: 0.0) * it.quantity }
    val shipping: Double get() = if (cartItems.isEmpty()) 0.0 else 10.0
    val total: Double get() = subtotal + shipping
    val subtotalFormatted: String get() = "$${subtotal}"
    val shippingFormatted: String get() = "$${shipping}"
    val totalFormatted: String get() = "$${total}"
}

sealed interface CheckoutIntent {
    data object OnBackClick : CheckoutIntent
    data object OnPlaceOrder : CheckoutIntent
    data object OnChangeAddressClick : CheckoutIntent
    data object OnDismissAddressSheet : CheckoutIntent
    data class OnAddressSelected(val address: Address) : CheckoutIntent
    data object OnAddNewAddress : CheckoutIntent
}

sealed interface CheckoutEffect {
    data object NavigateBack : CheckoutEffect
    data object NavigateToOrderSuccess : CheckoutEffect
    data object NavigateToNewAddress : CheckoutEffect
}
