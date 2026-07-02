package com.troves.presintation.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.CartItem
import com.troves.domain.usecase.cart.GetCartStreamUseCase
import com.troves.domain.usecase.cart.RemoveFromCartUseCase
import com.troves.domain.usecase.cart.UpdateCartQuantityUseCase
import com.troves.domain.usecase.cart.CartOperationResult
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToLong

class CartViewModel(
    private val getCartStream: GetCartStreamUseCase,
    private val updateCartQuantity: UpdateCartQuantityUseCase,
    private val removeFromCart: RemoveFromCartUseCase,
) : ViewModel(),
    StateHolder<CartUiState> by DefaultStateHolder(CartUiState()),
    EffectPublisher<CartEffect> by DefaultEffectPublisher() {

    init {
        getCartStream()
            .onEach { items -> updateState { buildUiState(items) } }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: CartIntent) {
        when (intent) {
            CartIntent.OnBackClick -> sendEffect(CartEffect.NavigateBack)
            CartIntent.OnCheckout -> sendEffect(CartEffect.NavigateToCheckout)
            is CartIntent.OnIncrement -> changeQuantity(intent.productId, delta = +1)
            is CartIntent.OnDecrement -> changeQuantity(intent.productId, delta = -1)
            is CartIntent.OnRemoveItemConfirm -> removeItem(intent.productId)
        }
    }

    private fun removeItem(productId: Long) {
        viewModelScope.launch {
            when (removeFromCart(productId)) {
                CartOperationResult.RequiresLogin -> {
                    sendEffect(CartEffect.ShowLoginRequiredDialog)
                }
                is CartOperationResult.Error -> {
                    sendEffect(CartEffect.ShowToast("Couldn't remove item from cart"))
                }
                CartOperationResult.Success -> Unit
            }
        }
    }

    private fun changeQuantity(productId: Long, delta: Int) {
        val item = currentState.items.find { it.productId == productId } ?: return
        val newQuantity = item.quantity + delta
        viewModelScope.launch {
            if (newQuantity <= 0) {
                sendEffect(CartEffect.ShowRemoveConfirmationDialog(item))
            } else {
                when (updateCartQuantity(productId, newQuantity)) {
                    CartOperationResult.RequiresLogin -> {
                        sendEffect(CartEffect.ShowLoginRequiredDialog)
                    }
                    is CartOperationResult.Error -> {
                        sendEffect(CartEffect.ShowToast("Couldn't update cart"))
                    }
                    CartOperationResult.Success -> Unit
                }
            }
        }
    }

    private fun buildUiState(items: List<CartItem>): CartUiState {
        val uiItems = items.map { it.toUi() }
        val total = items.sumOf { item ->
            (item.product.price.toDoubleOrNull() ?: 0.0) * item.quantity
        }
        return CartUiState(
            items = uiItems,
            totalFormatted = formatUsd(total),
            isLoading = false,
        )
    }
}


private fun formatUsd(value: Double): String {
    val totalCents = (value * 100).roundToLong()
    val sign = if (totalCents < 0) "-" else ""
    val absCents = abs(totalCents)
    val whole = absCents / 100
    val fraction = (absCents % 100).toString().padStart(2, '0')
    return "$sign\$$whole.$fraction"
}

private fun CartItem.toUi() = CartItemUi(
    productId = product.id,
    title = product.title,
    description = product.description,
    imageUrl = product.imageUrl,
    price = product.price,
    quantity = quantity,
)
