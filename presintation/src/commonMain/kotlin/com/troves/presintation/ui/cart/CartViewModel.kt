package com.troves.presintation.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.Cart
import com.troves.domain.usecase.cart.CartOperationResult
import com.troves.domain.usecase.cart.GetCartStreamUseCase
import com.troves.domain.usecase.cart.RefreshCartUseCase
import com.troves.domain.usecase.cart.RemoveAllFromCartUseCase
import com.troves.domain.usecase.cart.RemoveFromCartUseCase
import com.troves.domain.usecase.cart.SetCartHintShownUseCase
import com.troves.domain.usecase.cart.ShouldShowCartHintUseCase
import com.troves.domain.usecase.cart.UpdateCartQuantityUseCase
import com.troves.domain.usecase.shared.ObserveConnectivityUseCase
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.cart_clear_failed
import troves.presintation.generated.resources.cart_max_quantity
import troves.presintation.generated.resources.cart_remove_failed
import troves.presintation.generated.resources.cart_update_failed
import troves.presintation.generated.resources.no_internet_connection

class CartViewModel(
    getCartStream: GetCartStreamUseCase,
    shouldShowCartHint: ShouldShowCartHintUseCase,
    private val setCartHintShown: SetCartHintShownUseCase,
    private val updateCartQuantity: UpdateCartQuantityUseCase,
    private val removeFromCart: RemoveFromCartUseCase,
    private val removeAllFromCart: RemoveAllFromCartUseCase,
    private val refreshCart: RefreshCartUseCase,
    private val observeConnectivity: ObserveConnectivityUseCase,
) : ViewModel(),
    StateHolder<CartUiState> by DefaultStateHolder(CartUiState()),
    EffectPublisher<CartEffect> by DefaultEffectPublisher() {

    private val updatingLines = mutableSetOf<String>()

    private fun ensureOnline(): Boolean {
        if (observeConnectivity.isOnlineNow()) return true
        viewModelScope.launch { sendEffect(CartEffect.ShowToast(getString(Res.string.no_internet_connection))) }
        return false
    }

    init {
        combine(
            getCartStream(),
            shouldShowCartHint()
        ) { cart, hintShown ->
            updateState { applyCart(cart).copy(shouldShowCartHint = !hintShown) }
        }.launchIn(viewModelScope)

        viewModelScope.launch { refreshCart() }
    }

    fun onIntent(intent: CartIntent) {
        when (intent) {
            CartIntent.OnBackClick -> sendEffect(CartEffect.NavigateBack)
            CartIntent.OnCheckout -> sendEffect(CartEffect.NavigateToCheckout)
            is CartIntent.OnIncrement -> changeQuantity(intent.lineId, delta = +1)
            is CartIntent.OnDecrement -> changeQuantity(intent.lineId, delta = -1)
            is CartIntent.OnRemoveItemClick -> {
                val item = currentState.items.find { it.lineId == intent.lineId } ?: return
                sendEffect(CartEffect.ShowRemoveConfirmationDialog(item))
            }

            is CartIntent.OnRemoveItemConfirm -> removeItem(intent.lineId)
            CartIntent.OnClearCartClick -> {
                if (currentState.items.isNotEmpty()) sendEffect(CartEffect.ShowClearCartConfirmationDialog)
            }

            CartIntent.OnClearCartConfirm -> clearCart()
            is CartIntent.OnItemClick -> {
                val item = currentState.items.find { it.lineId == intent.lineId } ?: return
                sendEffect(CartEffect.NavigateToProductDetails(item.productId))
            }

            CartIntent.OnDismissCartHint -> dismissHint()
        }
    }

    private fun dismissHint() {
        viewModelScope.launch {
            setCartHintShown(true)
        }
    }

    private fun removeItem(lineId: String) {
        if (!ensureOnline()) return
        viewModelScope.launch {
            when (removeFromCart(lineId)) {
                CartOperationResult.RequiresLogin -> sendEffect(CartEffect.ShowLoginRequiredDialog)
                is CartOperationResult.Error ->
                    sendEffect(CartEffect.ShowToast(getString(Res.string.cart_remove_failed)))
                CartOperationResult.Success -> Unit
            }
        }
    }

    private fun changeQuantity(lineId: String, delta: Int) {
        // Ignore taps while this line already has an update in flight — prevents rapid taps from
        // computing the next target off a stale quantity and desyncing from Shopify.
        if (lineId in updatingLines) return
        if (!ensureOnline()) return
        val item = currentState.items.find { it.lineId == lineId } ?: return
        val newQuantity = item.quantity + delta
        if (newQuantity <= 0) {
            sendEffect(CartEffect.ShowRemoveConfirmationDialog(item))
            return
        }
        val max = item.maxQuantity
        if (delta > 0 && max != null && newQuantity > max) {
            viewModelScope.launch { sendEffect(CartEffect.ShowToast(getString(Res.string.cart_max_quantity))) }
            return
        }
        updatingLines.add(lineId)
        updateState {
            copy(items = items.map { if (it.lineId == lineId) it.copy(quantity = newQuantity) else it })
        }
        viewModelScope.launch {
            val result = updateCartQuantity(lineId, newQuantity)
            updatingLines.remove(lineId)
            when (result) {
                CartOperationResult.RequiresLogin -> {
                    refreshCart()
                    sendEffect(CartEffect.ShowLoginRequiredDialog)
                }
                is CartOperationResult.Error -> {
                    refreshCart() // revert optimistic bump; re-sync from Shopify
                    sendEffect(CartEffect.ShowToast(getString(Res.string.cart_update_failed)))
                }
                CartOperationResult.Success -> Unit
            }
        }
    }

    private fun clearCart() {
        if (!ensureOnline()) return
        viewModelScope.launch {
            when (removeAllFromCart()) {
                CartOperationResult.RequiresLogin -> sendEffect(CartEffect.ShowLoginRequiredDialog)
                is CartOperationResult.Error -> sendEffect(CartEffect.ShowToast(getString(Res.string.cart_clear_failed)))
                CartOperationResult.Success -> Unit
            }
        }
    }

    private fun CartUiState.applyCart(cart: Cart?): CartUiState {
        if (cart == null) {
            return copy(items = emptyList(), subtotal = null, total = null, checkoutUrl = null, isLoading = false)
        }
        return copy(
            items = cart.lines.map { line ->
                CartLineUi(
                    lineId = line.lineId,
                    productId = line.productId.toString(),
                    title = line.productTitle,
                    variantTitle = line.variantTitle,
                    imageUrl = line.imageUrl,
                    price = line.unitPrice,
                    quantity = line.quantity,
                    maxQuantity = line.maxQuantity,
                )
            },
            subtotal = cart.subtotal,
            total = cart.total,
            checkoutUrl = cart.checkoutUrl,
            isLoading = false,
        )
    }
}