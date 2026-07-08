package com.troves.presintation.ui.orderdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.usecase.order.GetOrderByIdUseCase
import com.troves.domain.usecase.shared.ObserveConnectivityUseCase
import com.troves.domain.utils.connectivity.ConnectivityStatus
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class OrderDetailsViewModel(
    private val getOrderById: GetOrderByIdUseCase,
    private val observeConnectivity: ObserveConnectivityUseCase,
) : ViewModel(),
    StateHolder<OrderDetailsUiState> by DefaultStateHolder(OrderDetailsUiState()),
    EffectPublisher<OrderDetailsEffect> by DefaultEffectPublisher() {

    private var currentOrderId: String = ""

    init {
        val online = observeConnectivity()
            .map { it == ConnectivityStatus.Available }
            .distinctUntilChanged()

        online
            .onEach { isOnline -> updateState { copy(isOffline = !isOnline) } }
            .launchIn(viewModelScope)

        online
            .drop(1)
            .onEach { isOnline ->
                if (isOnline && currentState.orderDetails == null && currentOrderId.isNotEmpty()) {
                    loadOrder(currentOrderId)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: OrderDetailsIntent) {
        when (intent) {
            is OrderDetailsIntent.Load -> loadOrder(intent.orderId)
            OrderDetailsIntent.OnBack -> sendEffect(OrderDetailsEffect.NavigateBack)
            OrderDetailsIntent.OnSupportClick -> sendEffect(OrderDetailsEffect.NavigateToSupport)
        }
    }

    private fun loadOrder(orderId: String) {
        currentOrderId = orderId
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
