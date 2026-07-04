package com.troves.presintation.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.Address
import com.troves.domain.entity.Cart
import com.troves.domain.usecase.address.GetSavedAddressesUseCase
import com.troves.domain.usecase.address.RefreshAddressesUseCase
import com.troves.domain.usecase.cart.ApplyDiscountResult
import com.troves.domain.usecase.cart.ApplyDiscountUseCase
import com.troves.domain.usecase.cart.GetCartStreamUseCase
import com.troves.domain.usecase.order.AttachAddressToCartUseCase
import com.troves.domain.usecase.order.ClearCartUseCase
import com.troves.domain.usecase.order.PlaceCodOrderUseCase
import com.troves.domain.usecase.order.PlaceOrderResult
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import com.troves.presintation.navigation.AppRoute
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CheckoutViewModel(
    getCartStream: GetCartStreamUseCase,
    getSavedAddresses: GetSavedAddressesUseCase,
    private val refreshAddresses: RefreshAddressesUseCase,
    private val applyDiscount: ApplyDiscountUseCase,
    private val attachAddressToCart: AttachAddressToCartUseCase,
    private val placeCodOrder: PlaceCodOrderUseCase,
    private val clearCart: ClearCartUseCase,
) : ViewModel(), CheckoutEvent,
    StateHolder<CheckoutUiState> by DefaultStateHolder(CheckoutUiState()),
    EffectPublisher<CheckoutEffect> by DefaultEffectPublisher() {

    private var cart: Cart? = null

    init {
        getCartStream()
            .onEach { latest ->
                cart = latest
                updateState { applyCart(latest) }
            }
            .launchIn(viewModelScope)

        getSavedAddresses()
            .onEach { list -> updateState { applyAddresses(list) } }
            .launchIn(viewModelScope)

        viewModelScope.launch { refreshAddresses() }
    }

    fun onIntent(intent: CheckoutIntent) {
        when (intent) {
            CheckoutIntent.OnBack -> onBack()
            CheckoutIntent.OnNext -> onNext()
            CheckoutIntent.OnEditCart -> sendEffect(CheckoutEffect.NavigateToCart)
            is CheckoutIntent.OnCouponChange -> updateState { copy(couponInput = intent.value) }
            CheckoutIntent.OnApplyCoupon -> applyCoupon()
            is CheckoutIntent.OnSelectAddress -> updateState { copy(selectedAddressId = intent.id) }
            CheckoutIntent.OnAddAddress -> sendEffect(CheckoutEffect.NavigateToNewAddress(null))
            is CheckoutIntent.OnEditAddress -> sendEffect(CheckoutEffect.NavigateToNewAddress(intent.id))
            is CheckoutIntent.OnSelectPaymentMethod -> updateState { copy(paymentMethod = intent.method) }
            CheckoutIntent.OnChangePayment -> updateState { copy(step = CheckoutStep.Payment) }
            CheckoutIntent.OnChangeAddress -> updateState { copy(step = CheckoutStep.Address) }
            CheckoutIntent.OnPlaceOrder -> placeCod()
            CheckoutIntent.OnResume -> viewModelScope.launch { refreshAddresses() }
        }
    }

    private fun onBack() {
        val current = currentState.step
        val previous = when (current) {
            CheckoutStep.Review -> null
            CheckoutStep.Address -> CheckoutStep.Review
            CheckoutStep.Payment -> CheckoutStep.Address
            CheckoutStep.PlaceOrder -> CheckoutStep.Payment
        }
        if (previous == null) {
            resetCheckoutProgress()
            sendEffect(CheckoutEffect.NavigateBack)
        } else {
            updateState { copy(step = previous) }
        }
    }


    private fun resetCheckoutProgress() {
        updateState {
            copy(
                step = CheckoutStep.Review,
                couponInput = "",
                paymentMethod = null,
                isApplyingCoupon = false,
                isBusy = false,
            )
        }
    }

    private fun onNext() {
        when (currentState.step) {
            CheckoutStep.Review -> {
                if (currentState.itemCount == 0) {
                    sendEffect(CheckoutEffect.ShowToast("Your cart is empty"))
                } else {
                    updateState { copy(step = CheckoutStep.Address) }
                }
            }
            CheckoutStep.Address -> {
                if (currentState.selectedAddress?.isDeliverable != true) {
                    sendEffect(CheckoutEffect.ShowToast("Select a delivery address"))
                } else {
                    updateState { copy(step = CheckoutStep.Payment) }
                }
            }
            CheckoutStep.Payment -> when (currentState.paymentMethod) {
                CheckoutPaymentMethod.CashOnDelivery -> updateState { copy(step = CheckoutStep.PlaceOrder) }
                CheckoutPaymentMethod.Online -> startOnlinePayment()
                null -> sendEffect(CheckoutEffect.ShowToast("Choose a payment method"))
            }
            CheckoutStep.PlaceOrder -> Unit // handled by OnPlaceOrder
        }
    }

    private fun startOnlinePayment() {
        val address = currentState.selectedAddress
        if (address?.isDeliverable != true) {
            sendEffect(CheckoutEffect.ShowToast("Select a delivery address"))
            updateState { copy(step = CheckoutStep.Address) }
            return
        }
        val currentCart = cart
        val url = currentCart?.checkoutUrl
        if (currentCart == null || url.isNullOrBlank()) {
            sendEffect(CheckoutEffect.ShowToast("Checkout is unavailable right now"))
            return
        }
        updateState { copy(isBusy = true) }
        viewModelScope.launch {
            val attached = attachAddressToCart(currentCart.cartId, address)
            updateState { copy(isBusy = false) }
            if (attached.isSuccess) {
                sendEffect(CheckoutEffect.PresentCheckoutSheet(url))
            } else {
                sendEffect(CheckoutEffect.ShowToast("Couldn't start payment. Please try again."))
            }
        }
    }

    private fun placeCod() {
        val currentCart = cart
        if (currentCart == null || currentCart.lines.isEmpty()) {
            sendEffect(CheckoutEffect.ShowToast("Your cart is empty"))
            sendEffect(CheckoutEffect.NavigateToCart)
            return
        }
        val address = currentState.selectedAddress
        if (address?.isDeliverable != true) {
            sendEffect(CheckoutEffect.ShowToast("Sorry you don't have an address to deliver to"))
            updateState { copy(step = CheckoutStep.Address) }
            return
        }
        val snapshot = currentState
        updateState { copy(isBusy = true) }
        viewModelScope.launch {
            val result = placeCodOrder(currentCart, address)
            updateState { copy(isBusy = false) }
            when (result) {
                is PlaceOrderResult.Success -> {
                    resetCheckoutProgress()
                    sendEffect(CheckoutEffect.NavigateToOrderResult(
                        buildResultFromState(
                            state = snapshot,
                            address = address,
                            success = true,
                            orderName = result.orderName,
                            errorMessage = null,
                            paymentLabel = "Cash on Delivery (COD)",
                        )
                    ))
                }
                PlaceOrderResult.RequiresLogin -> sendEffect(CheckoutEffect.ShowLoginRequiredDialog)
                PlaceOrderResult.NoAddress -> {
                    sendEffect(CheckoutEffect.ShowToast("Sorry you don't have an address to deliver to"))
                    updateState { copy(step = CheckoutStep.Address) }
                }
                PlaceOrderResult.EmptyCart -> {
                    sendEffect(CheckoutEffect.ShowToast("Your cart is empty"))
                    sendEffect(CheckoutEffect.NavigateToCart)
                }
                is PlaceOrderResult.Error -> {
                    resetCheckoutProgress()
                    sendEffect(CheckoutEffect.NavigateToOrderResult(
                        buildResultFromState(
                            state = snapshot,
                            address = address,
                            success = false,
                            orderName = null,
                            errorMessage = "We couldn't place your order. Please try again.",
                            paymentLabel = "Cash on Delivery (COD)",
                        )
                    ))
                }
            }
        }
    }

    private fun applyCoupon() {
        val code = currentState.couponInput.trim()
        if (code.isEmpty()) {
            sendEffect(CheckoutEffect.ShowToast("Enter a coupon code"))
            return
        }
        updateState { copy(isApplyingCoupon = true) }
        viewModelScope.launch {
            val result = applyDiscount(listOf(code))
            updateState { copy(isApplyingCoupon = false) }
            when (result) {
                is ApplyDiscountResult.Success -> {
                    val applied = result.cart.discountCodes.firstOrNull { it.code.equals(code, ignoreCase = true) }
                    if (applied?.applicable == true) {
                        sendEffect(CheckoutEffect.ShowToast("Discount applied"))
                    } else {
                        sendEffect(CheckoutEffect.ShowToast("\"$code\" is not a valid or eligible code"))
                    }
                    // The cart stream re-emits with updated totals + discountCodes.
                }
                ApplyDiscountResult.RequiresLogin -> sendEffect(CheckoutEffect.ShowLoginRequiredDialog)
                is ApplyDiscountResult.Error -> sendEffect(CheckoutEffect.ShowToast("Couldn't apply discount"))
            }
        }
    }


    override fun onCheckoutCompleted(checkoutCompletedEvent: CheckoutCompletedEvent) {
        viewModelScope.launch { clearCart() }
        val result = checkoutCompletedEvent.orderDetails.toSuccessResult()
        resetCheckoutProgress()
        sendEffect(CheckoutEffect.NavigateToOrderResult(result))
    }

    override fun onCheckoutFailed(error: Exception) {
        val result = buildResultFromState(
            state = currentState,
            address = currentState.selectedAddress,
            success = false,
            orderName = null,
            errorMessage = error.message ?: "Payment failed. Please try again.",
            paymentLabel = "Online Payment",
        )
        resetCheckoutProgress()
        sendEffect(CheckoutEffect.NavigateToOrderResult(result))
    }

    override fun onCheckoutCanceled() {
        updateState { copy(isBusy = false) }
        sendEffect(CheckoutEffect.ShowToast("Payment canceled"))
    }


    private fun CheckoutUiState.applyCart(cart: Cart?): CheckoutUiState {
        if (cart == null) {
            return copy(
                lines = emptyList(),
                itemCount = 0,
                subtotalFormatted = format(null),
                totalFormatted = format(null),
                appliedDiscountCode = null,
                discountValueFormatted = null,
                isLoading = false,
            )
        }
        return copy(
            lines = cart.lines.map { line ->
                CheckoutLineUi(
                    lineId = line.lineId,
                    imageUrl = line.imageUrl,
                    title = line.productTitle,
                    specs = line.variantTitle,
                    quantity = line.quantity,
                    priceFormatted = lineTotal(line.unitPrice, line.quantity, line.currencyCode),
                )
            },
            itemCount = cart.totalQuantity,
            subtotalFormatted = format(cart.subtotal),
            totalFormatted = format(cart.total),
            appliedDiscountCode = cart.discountCodes.firstOrNull { it.applicable }?.code,
            discountValueFormatted = savings(cart.subtotal, cart.total),
            isLoading = false,
        )
    }

    private fun CheckoutUiState.applyAddresses(list: List<Address>): CheckoutUiState {
        val stillValid = selectedAddressId != null && list.any { it.id == selectedAddressId }
        val nextSelected = when {
            stillValid -> selectedAddressId
            else -> (list.firstOrNull { it.isDefault && it.isDeliverable }
                ?: list.firstOrNull { it.isDeliverable }
                ?: list.firstOrNull())?.id
        }
        return copy(addresses = list, selectedAddressId = nextSelected)
    }

    private fun buildResultFromState(
        state: CheckoutUiState,
        address: Address?,
        success: Boolean,
        orderName: String?,
        errorMessage: String?,
        paymentLabel: String,
    ): AppRoute.OrderResult = AppRoute.OrderResult(
        success = success,
        orderName = orderName,
        errorMessage = errorMessage,
        paymentLabel = paymentLabel,
        recipientName = address?.recipientName.orEmpty(),
        addressLines = address?.lines.orEmpty(),
        phone = address?.phone.orEmpty(),
        itemImageUrls = state.lines.map { it.imageUrl },
        itemCount = state.itemCount,
        subtotalFormatted = state.subtotalFormatted,
        discountLabel = state.appliedDiscountCode?.let { "Discount ($it)" },
        discountValueFormatted = state.discountValueFormatted,
        totalFormatted = state.totalFormatted,
    )

    private fun OrderDetails.toSuccessResult(): AppRoute.OrderResult {
        val cartInfo = this.cart
        val subtotal = cartInfo.price.subtotal
        val total = cartInfo.price.total
        val delivery = deliveries.firstOrNull()?.details?.location ?: billingAddress
        val recipient = listOfNotNull(delivery?.firstName, delivery?.lastName)
            .filter { it.isNotBlank() }.joinToString(" ")
        val addressLines = listOfNotNull(
            delivery?.address1, delivery?.address2, delivery?.city, delivery?.zoneCode, delivery?.countryCode,
        ).filter { it.isNotBlank() }
        val savings = savings(subtotal?.amount, total?.amount, subtotal?.currencyCode)
        return AppRoute.OrderResult(
            success = true,
            orderName = null,
            errorMessage = null,
            paymentLabel = paymentMethods.firstOrNull()?.type?.ifBlank { "Online Payment" } ?: "Online Payment",
            recipientName = recipient,
            addressLines = addressLines,
            phone = phone.orEmpty(),
            itemImageUrls = cartInfo.lines.map { it.image?.md.orEmpty() },
            itemCount = cartInfo.lines.sumOf { it.quantity },
            subtotalFormatted = formatMoney(subtotal?.amount, subtotal?.currencyCode),
            discountLabel = if (savings != null) "Discount" else null,
            discountValueFormatted = savings,
            totalFormatted = formatMoney(total?.amount, total?.currencyCode),
        )
    }
}
