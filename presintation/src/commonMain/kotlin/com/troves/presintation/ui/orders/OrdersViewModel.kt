package com.troves.presintation.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.CartMoney
import com.troves.domain.entity.Order
import com.troves.domain.usecase.auth.IsLoggedInUseCase
import com.troves.domain.usecase.order.GetOrdersUseCase
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

class OrdersViewModel(
    private val getOrders: GetOrdersUseCase,
    private val isLoggedIn: IsLoggedInUseCase,
    private val observeConnectivity: ObserveConnectivityUseCase,
) : ViewModel(),
    StateHolder<OrdersUiState> by DefaultStateHolder(OrdersUiState()),
    EffectPublisher<OrdersEffect> by DefaultEffectPublisher() {

    init {
        load()
        observeConnectivity()
            .map { it == ConnectivityStatus.Available }
            .distinctUntilChanged()
            .drop(1)
            .onEach { online -> if (online) load() }
            .launchIn(viewModelScope)
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
            if (!isLoggedIn()) {
                updateState { copy(isLoading = false, isNotSignedIn = true, orders = emptyList()) }
                return@launch
            }
            try {
                val orders = getOrders()
                    .sortedByDescending { it.processedAt }
                    .map { it.toUi() }
                updateState { copy(isLoading = false, isNotSignedIn = false, orders = orders) }
            } catch (e: Exception) {
                updateState {
                    copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = e.message ?: "Failed to load orders",
                        isOffline = !observeConnectivity.isOnlineNow(),
                    )
                }
            }
        }
    }
}
