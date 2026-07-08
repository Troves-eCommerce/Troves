package com.troves.presintation.ui.orderdetails

import com.troves.presintation.ui.orderdetails.components.TimelineStepData

data class OrderDetailsUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val orderDetails: OrderDetailsUi? = null,
    val isOffline: Boolean = false,
) {
    val showOfflineState: Boolean get() = isOffline && orderDetails == null && !isLoading
}

data class OrderDetailsUi(
    val id: String,
    val orderNumber: String,
    val orderDate: String,
    val status: String,
    val totalAmount: String,
    val itemCount: Int,
    val timelineSteps: List<TimelineStepData>,
    val items: List<OrderItemUi>,
    val subtotal: String,
    val shipping: String,
    val tax: String,
    val total: String,
)

data class OrderItemUi(
    val id: String,
    val name: String,
    val variant: String,
    val quantity: Int,
    val price: String,
    val imageUrl: String?,
)

sealed interface OrderDetailsIntent {
    data class Load(val orderId: String) : OrderDetailsIntent
    data object OnBack : OrderDetailsIntent
    data object OnSupportClick : OrderDetailsIntent
}

sealed interface OrderDetailsEffect {
    data object NavigateBack : OrderDetailsEffect
    data object NavigateToSupport : OrderDetailsEffect
}
