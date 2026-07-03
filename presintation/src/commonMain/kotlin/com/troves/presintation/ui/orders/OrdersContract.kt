package com.troves.presintation.ui.orders

data class OrdersUiState(
    val isLoading: Boolean = true,
    val orders: List<OrderUi> = emptyList(),
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
)

sealed interface OrdersIntent {
    data object Load : OrdersIntent
    data object OnBack : OrdersIntent
}

sealed interface OrdersEffect {
    data object NavigateBack : OrdersEffect
}
