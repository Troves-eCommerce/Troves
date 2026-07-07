package com.troves.presintation.ui.orders

data class OrdersUiState(
    val isLoading: Boolean = true,
    val orders: List<OrderUi> = emptyList(),
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isNotSignedIn: Boolean = false,
) {
    val isEmpty: Boolean get() = !isLoading && orders.isEmpty()
}

data class OrderUi(
    val id: String,
    val name: String,
    val date: String,
    val status: String,
    val totalFormatted: String,
    val itemCount: Int,
    val imageUrl: String? = null,
)

sealed interface OrdersIntent {
    data object Load : OrdersIntent
    data object OnBack : OrdersIntent
    data class OnOrderClick(val id: String) : OrdersIntent
}

sealed interface OrdersEffect {
    data object NavigateBack : OrdersEffect
    data class NavigateToOrderDetails(val id: String) : OrdersEffect
}
