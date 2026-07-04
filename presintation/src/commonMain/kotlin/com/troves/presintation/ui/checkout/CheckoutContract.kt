package com.troves.presintation.ui.checkout

data class CheckoutUiState(
    val isLoading: Boolean = true,
    val itemCount: Int = 0,
    val subtotalFormatted: String = "$0.00",
    val totalFormatted: String = "$0.00",
    val recipientName: String = "",
    val addressLine: String = "",
    val hasAddress: Boolean = false,
    val isPlacingOrder: Boolean = false,
) {
    val isCartEmpty: Boolean get() = !isLoading && itemCount == 0
    val canPlaceOrder: Boolean get() = !isLoading && !isPlacingOrder && itemCount > 0 && hasAddress
}

sealed interface CheckoutIntent {
    data object OnBack : CheckoutIntent
    data object OnPlaceCodOrder : CheckoutIntent
    data object OnPayByCard : CheckoutIntent
    data object OnManageAddress : CheckoutIntent
    data object OnResume : CheckoutIntent
}

sealed interface CheckoutEffect {
    data object NavigateBack : CheckoutEffect
    data object NavigateToAddresses : CheckoutEffect
    data class OrderPlaced(val orderName: String) : CheckoutEffect
    data class OpenCheckoutUrl(val url: String) : CheckoutEffect
    data class ShowToast(val message: String) : CheckoutEffect
    data object ShowLoginRequiredDialog : CheckoutEffect
}
