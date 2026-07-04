package com.troves.presintation.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.Address
import com.troves.domain.entity.Cart
import com.troves.domain.entity.CartMoney
import com.troves.domain.usecase.address.GetDefaultSavedAddressUseCase
import com.troves.domain.usecase.cart.GetCartStreamUseCase
import com.troves.domain.usecase.order.AttachAddressToCartUseCase
import com.troves.domain.usecase.order.PlaceCodOrderUseCase
import com.troves.domain.usecase.order.PlaceOrderResult
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CheckoutViewModel(
    getCartStream: GetCartStreamUseCase,
    private val getDefaultSavedAddress: GetDefaultSavedAddressUseCase,
    private val placeCodOrder: PlaceCodOrderUseCase,
    private val attachAddressToCart: AttachAddressToCartUseCase,
) : ViewModel(), CheckoutEvent,
    StateHolder<CheckoutUiState> by DefaultStateHolder(CheckoutUiState()),
    EffectPublisher<CheckoutEffect> by DefaultEffectPublisher() {

    private var cart: Cart? = null
    private var address: Address? = null

    init {
        getCartStream()
            .onEach { latest ->
                cart = latest
                updateState { applyCart(latest) }
            }
            .launchIn(viewModelScope)

        loadAddress()
    }

    private fun loadAddress() {
        viewModelScope.launch {
            val loaded = getDefaultSavedAddress()
            address = loaded
            updateState {
                copy(
                    hasAddress = loaded?.isDeliverable == true,
                    recipientName = loaded?.recipientName.orEmpty(),
                    addressLine = loaded?.singleLine.orEmpty(),
                    isLoading = false,
                )
            }
        }
    }

    fun onIntent(intent: CheckoutIntent) {
        when (intent) {
            CheckoutIntent.OnBack -> sendEffect(CheckoutEffect.NavigateBack)
            CheckoutIntent.OnPlaceCodOrder -> placeCod()
            CheckoutIntent.OnPayByCard -> payByCard()
            CheckoutIntent.OnManageAddress -> sendEffect(CheckoutEffect.NavigateToAddresses)
            CheckoutIntent.OnResume -> loadAddress()
        }
    }

    private fun placeCod() {
        val currentCart = cart
        if (currentCart == null || currentCart.lines.isEmpty()) {
            sendEffect(CheckoutEffect.ShowToast("Your cart is empty"))
            return
        }
        if (address?.isDeliverable != true) {
            sendEffect(CheckoutEffect.ShowToast("Sorry you don't have an address to deliver to"))
            return
        }
        updateState { copy(isPlacingOrder = true) }
        viewModelScope.launch {
            val result = placeCodOrder(currentCart, address)
            updateState { copy(isPlacingOrder = false) }
            when (result) {
                is PlaceOrderResult.Success -> sendEffect(CheckoutEffect.OrderPlaced(result.orderName))
                PlaceOrderResult.RequiresLogin -> sendEffect(CheckoutEffect.ShowLoginRequiredDialog)
                PlaceOrderResult.NoAddress ->
                    sendEffect(CheckoutEffect.ShowToast("Sorry you don't have an address to deliver to"))
                PlaceOrderResult.EmptyCart -> sendEffect(CheckoutEffect.ShowToast("Your cart is empty"))
                is PlaceOrderResult.Error -> sendEffect(CheckoutEffect.ShowToast("Couldn't place order"))
            }
        }
    }

    private fun payByCard() {
        val currentAddress = address
        if (currentAddress?.isDeliverable != true) {
            sendEffect(CheckoutEffect.ShowToast("Sorry you don't have an address to deliver to"))
            return
        }
        val currentCart = cart
        val url = currentCart?.checkoutUrl
        if (currentCart == null || url.isNullOrBlank()) {
            sendEffect(CheckoutEffect.ShowToast("Checkout is unavailable"))
            return
        }
        updateState { copy(isPlacingOrder = true) }
        viewModelScope.launch {
            // Attach the buyer + delivery address so Shopify's hosted checkout is prefilled/valid.
            attachAddressToCart(currentCart.cartId, currentAddress)
            updateState { copy(isPlacingOrder = false) }
            sendEffect(CheckoutEffect.OpenCheckoutUrl(url))
        }
    }

    private fun CheckoutUiState.applyCart(cart: Cart?): CheckoutUiState {
        if (cart == null) {
            return copy(itemCount = 0, subtotalFormatted = format(null), totalFormatted = format(null))
        }
        return copy(
            itemCount = cart.totalQuantity,
            subtotalFormatted = format(cart.subtotal),
            totalFormatted = format(cart.total),
        )
    }

    override fun onCheckoutCanceled() {
    }

    override fun onCheckoutCompleted(checkoutCompletedEvent: CheckoutCompletedEvent) {
    }

    override fun onCheckoutFailed(error: Exception) {
    }
}

private fun format(money: CartMoney?): String {
    if (money == null) return "$0.00"
    return if (money.currencyCode == "USD") "$${money.amount}" else "${money.amount} ${money.currencyCode}"
}
