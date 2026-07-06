package com.troves.presintation.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.CartMoney
import com.troves.domain.entity.Order
import com.troves.domain.usecase.order.GetOrdersUseCase
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val getOrders: GetOrdersUseCase,
) : ViewModel(),
    StateHolder<OrdersUiState> by DefaultStateHolder(OrdersUiState()),
    EffectPublisher<OrdersEffect> by DefaultEffectPublisher() {

    init {
        load()
    }

    fun onIntent(intent: OrdersIntent) {
        when (intent) {
            OrdersIntent.Load -> load()
            OrdersIntent.OnBack -> sendEffect(OrdersEffect.NavigateBack)
            is OrdersIntent.OnOrderClick -> sendEffect(OrdersEffect.NavigateToOrderDetails(intent.id))
        }
    }

    private fun load() {
        updateState { copy(isLoading = true, isError = false) }
        viewModelScope.launch {
            try {
                val orders = getOrders()
                    .sortedByDescending { it.processedAt }
                    .map { it.toUi() }
                updateState { copy(isLoading = false, orders = orders) }
            } catch (e: Exception) {
                updateState { 
                    copy(
                        isLoading = false, 
                        isError = true, 
                        errorMessage = e.message ?: "Failed to load orders"
                    ) 
                }
            }
        }
    }
}
