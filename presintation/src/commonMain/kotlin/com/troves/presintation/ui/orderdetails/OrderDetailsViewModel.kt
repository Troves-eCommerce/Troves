package com.troves.presintation.ui.orderdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.usecase.order.GetOrderByIdUseCase
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.launch

class OrderDetailsViewModel(
    private val getOrderById: GetOrderByIdUseCase,
) : ViewModel(),
    StateHolder<OrderDetailsUiState> by DefaultStateHolder(OrderDetailsUiState()),
    EffectPublisher<OrderDetailsEffect> by DefaultEffectPublisher() {

    fun onIntent(intent: OrderDetailsIntent) {
        when (intent) {
            is OrderDetailsIntent.Load -> loadOrder(intent.orderId)
            OrderDetailsIntent.OnBack -> sendEffect(OrderDetailsEffect.NavigateBack)
            OrderDetailsIntent.OnSupportClick -> sendEffect(OrderDetailsEffect.NavigateToSupport)
        }
    }

    private fun loadOrder(orderId: String) {
        updateState { copy(isLoading = true, isError = false) }
        viewModelScope.launch {
            try {
                val order = getOrderById(orderId)
                if (order != null) {
                    updateState { 
                        copy(
                            isLoading = false, 
                            orderDetails = order.toDetailsUi(),
                        ) 
                    }
                } else {
                    updateState { copy(isLoading = false, isError = true, errorMessage = "Order not found") }
                }
            } catch (e: Exception) {
                updateState { copy(isLoading = false, isError = true, errorMessage = e.message ?: "Failed to load order") }
            }
        }
    }
}
