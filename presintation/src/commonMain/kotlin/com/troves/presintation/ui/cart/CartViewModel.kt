package com.troves.presintation.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.Cart
import com.troves.domain.entity.CartMoney
import com.troves.domain.usecase.cart.ApplyDiscountResult
import com.troves.domain.usecase.cart.ApplyDiscountUseCase
import com.troves.domain.usecase.cart.CartOperationResult
import com.troves.domain.usecase.cart.GetCartStreamUseCase
import com.troves.domain.usecase.cart.RefreshCartUseCase
import com.troves.domain.usecase.cart.RemoveAllFromCartUseCase
import com.troves.domain.usecase.cart.RemoveFromCartUseCase
import com.troves.domain.usecase.cart.UpdateCartQuantityUseCase
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CartViewModel(
    getCartStream: GetCartStreamUseCase,
    private val updateCartQuantity: UpdateCartQuantityUseCase,
    private val removeFromCart: RemoveFromCartUseCase,
    private val removeAllFromCart: RemoveAllFromCartUseCase,
    private val applyDiscount: ApplyDiscountUseCase,
    private val refreshCart: RefreshCartUseCase,
) : ViewModel(),
    StateHolder<CartUiState> by DefaultStateHolder(CartUiState()),
    EffectPublisher<CartEffect> by DefaultEffectPublisher() {

    // Lines with an in-flight quantity update. Guards against rapid taps racing the async cart
    // flow (which would otherwise compute the next target from a stale quantity and desync).
    private val updatingLines = mutableSetOf<String>()

    init {
        getCartStream()
            .onEach { cart -> updateState { applyCart(cart) } }
            .launchIn(viewModelScope)
        // Hydrate from Shopify using the persisted cartId (survives process death / reinstall).
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
            is CartIntent.OnDiscountInputChange -> updateState { copy(discountInput = intent.value) }
            CartIntent.OnApplyDiscount -> applyDiscountCode()
        }
    }

    private fun removeItem(lineId: String) {
        viewModelScope.launch {
            when (removeFromCart(lineId)) {
                CartOperationResult.RequiresLogin -> sendEffect(CartEffect.ShowLoginRequiredDialog)
                is CartOperationResult.Error ->
                    sendEffect(CartEffect.ShowToast("Couldn't remove item from cart"))
                CartOperationResult.Success -> Unit
            }
        }
    }

    private fun changeQuantity(lineId: String, delta: Int) {
        // Ignore taps while this line already has an update in flight — prevents rapid taps from
        // computing the next target off a stale quantity and desyncing from Shopify.
        if (lineId in updatingLines) return
        val item = currentState.items.find { it.lineId == lineId } ?: return
        val newQuantity = item.quantity + delta
        if (newQuantity <= 0) {
            sendEffect(CartEffect.ShowRemoveConfirmationDialog(item))
            return
        }
        // Pre-validate against known inventory so we never fire a doomed update or a false "max" toast.
        // (The old post-update check raced the async cart flow and misfired even on successful adds.)
        val max = item.maxQuantity
        if (delta > 0 && max != null && newQuantity > max) {
            sendEffect(CartEffect.ShowToast("You have reached the maximum quantity"))
            return
        }
        updatingLines.add(lineId)
        // Optimistic bump for instant feedback; the cart flow then confirms/corrects with Shopify truth.
        updateState {
            copy(items = items.map { if (it.lineId == lineId) it.copy(quantity = newQuantity) else it })
        }
        viewModelScope.launch {
            val result = updateCartQuantity(lineId, newQuantity)
            updatingLines.remove(lineId)
            when (result) {
                CartOperationResult.RequiresLogin -> {
                    refreshCart() // revert optimistic bump to authoritative state
                    sendEffect(CartEffect.ShowLoginRequiredDialog)
                }
                is CartOperationResult.Error -> {
                    refreshCart() // revert optimistic bump; re-sync from Shopify
                    sendEffect(CartEffect.ShowToast("Couldn't update cart"))
                }
                // On success the cart flow emits Shopify's authoritative quantity/total — no manual reconcile.
                CartOperationResult.Success -> Unit
            }
        }
    }

    private fun clearCart() {
        viewModelScope.launch {
            when (removeAllFromCart()) {
                CartOperationResult.RequiresLogin -> sendEffect(CartEffect.ShowLoginRequiredDialog)
                is CartOperationResult.Error -> sendEffect(CartEffect.ShowToast("Couldn't clear cart"))
                CartOperationResult.Success -> Unit
            }
        }
    }

    private fun applyDiscountCode() {
        val code = currentState.discountInput.trim()
        if (code.isEmpty()) return
        updateState { copy(isApplyingDiscount = true) }
        viewModelScope.launch {
            val result = applyDiscount(listOf(code))
            updateState { copy(isApplyingDiscount = false) }
            when (result) {
                is ApplyDiscountResult.Success -> {
                    val applied = result.cart.discountCodes.firstOrNull { it.code.equals(code, ignoreCase = true) }
                    if (applied?.applicable == true) {
                        sendEffect(CartEffect.ShowToast("Discount applied"))
                    } else {
                        sendEffect(CartEffect.ShowToast("\"$code\" is not a valid or eligible code"))
                    }
                }
                ApplyDiscountResult.RequiresLogin -> sendEffect(CartEffect.ShowLoginRequiredDialog)
                is ApplyDiscountResult.Error -> sendEffect(CartEffect.ShowToast("Couldn't apply discount"))
            }
        }
    }

    private fun CartUiState.applyCart(cart: Cart?): CartUiState {
        if (cart == null) {
            return copy(items = emptyList(), subtotalFormatted = format(null), totalFormatted = format(null), appliedDiscountCode = null, checkoutUrl = null, isLoading = false)
        }
        return copy(
            items = cart.lines.map { line ->
                CartLineUi(
                    lineId = line.lineId,
                    title = line.productTitle,
                    variantTitle = line.variantTitle,
                    imageUrl = line.imageUrl,
                    price = line.unitPrice,
                    quantity = line.quantity,
                    maxQuantity = line.maxQuantity,
                )
            },
            subtotalFormatted = format(cart.subtotal),
            totalFormatted = format(cart.total),
            appliedDiscountCode = cart.discountCodes.firstOrNull { it.applicable }?.code,
            checkoutUrl = cart.checkoutUrl,
            isLoading = false,
        )
    }
}

private fun format(money: CartMoney?): String {
    if (money == null) return "$0.00"
    val symbol = if (money.currencyCode == "USD") "$" else ""
    return if (symbol.isNotEmpty()) "$symbol${money.amount}" else "${money.amount} ${money.currencyCode}"
}
