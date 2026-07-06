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
        }
    }

    private fun load() {
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            val orders = getOrders().map { it.toUi() }
            updateState { copy(isLoading = false, orders = orders) }
        }
    }
}

private fun Order.toUi() = OrderUi(
    id = id,
    name = name,
    date = processedAt.take(10),
    status = listOfNotNull(financialStatus, fulfillmentStatus)
        .filter { it.isNotBlank() }
        .joinToString(" · ") { it.lowercase().replaceFirstChar(Char::uppercase) },
    totalFormatted = format(total),
    itemCount = lineItems.sumOf { it.quantity },
)

private fun format(money: CartMoney): String =
    if (money.currencyCode == "USD") "$${money.amount}" else "${money.amount} ${money.currencyCode}"
