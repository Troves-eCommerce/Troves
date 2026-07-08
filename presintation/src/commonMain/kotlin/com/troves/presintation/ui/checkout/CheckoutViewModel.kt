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
import com.troves.domain.usecase.paymob.GetClientSecretUseCase
import com.troves.domain.utils.Result
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import com.troves.presintation.navigation.AppRoute
import com.troves.presintation.ui.checkout.CheckoutEffect.ShowToast
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.checkout_card_payment
import troves.presintation.generated.resources.checkout_cart_empty
import troves.presintation.generated.resources.checkout_choose_payment
import troves.presintation.generated.resources.checkout_coupon_apply_failed
import troves.presintation.generated.resources.checkout_coupon_applied
import troves.presintation.generated.resources.checkout_coupon_invalid
import troves.presintation.generated.resources.checkout_coupon_remove_failed
import troves.presintation.generated.resources.checkout_coupon_removed
import troves.presintation.generated.resources.checkout_discount_code
import troves.presintation.generated.resources.checkout_discount_label
import troves.presintation.generated.resources.checkout_enter_coupon
import troves.presintation.generated.resources.checkout_no_deliverable_address
import troves.presintation.generated.resources.checkout_order_place_failed
import troves.presintation.generated.resources.checkout_payment_canceled
import troves.presintation.generated.resources.checkout_payment_cod_title
import troves.presintation.generated.resources.checkout_payment_failed
import troves.presintation.generated.resources.checkout_payment_online_title
import troves.presintation.generated.resources.checkout_payment_pending
import troves.presintation.generated.resources.checkout_payment_start_failed
import troves.presintation.generated.resources.checkout_payment_start_failed_empty_cart
import troves.presintation.generated.resources.checkout_select_address
import troves.presintation.generated.resources.checkout_unavailable

class CheckoutViewModel(
    getCartStream: GetCartStreamUseCase,
    getSavedAddresses: GetSavedAddressesUseCase,
    private val refreshAddresses: RefreshAddressesUseCase,
    private val applyDiscount: ApplyDiscountUseCase,
    private val attachAddressToCart: AttachAddressToCartUseCase,
    private val placeCodOrder: PlaceCodOrderUseCase,
    private val clearCart: ClearCartUseCase,
    private val getClientSecretUseCase: GetClientSecretUseCase,
) : ViewModel(), CheckoutEvent, PaymobListener,
    StateHolder<CheckoutUiState> by DefaultStateHolder(CheckoutUiState()),
    EffectPublisher<CheckoutEffect> by DefaultEffectPublisher() {

    private var isPaymentHandled = false
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
            CheckoutIntent.OnRemoveCoupon -> removeCoupon()
            is CheckoutIntent.OnSelectAddress -> updateState { copy(selectedAddressId = intent.id) }
            CheckoutIntent.OnAddAddressClick -> sendEffect(CheckoutEffect.NavigateToNewAddress(null))
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

    private fun doPaymobPayment() {
        val address = currentState.selectedAddress
        if (address?.isDeliverable != true) {
            viewModelScope.launch { sendEffect(ShowToast(getString(Res.string.checkout_select_address))) }
            updateState { copy(step = CheckoutStep.Address) }
            return
        }

        val currentCart = cart
        val url = currentCart?.checkoutUrl
        if (currentCart == null || url.isNullOrBlank()) {
            viewModelScope.launch { sendEffect(CheckoutEffect.ShowToast(getString(Res.string.checkout_unavailable))) }
            return
        }
        isPaymentHandled = false
        updateState { copy(isBusy = true) }
        viewModelScope.launch {
            val attached = attachAddressToCart(currentCart.cartId, address)
            updateState { copy(isBusy = false) }
            if (attached.isSuccess) {
                require(cart?.cartId?.isNotBlank() == true) {
                    sendEffect(ShowToast(getString(Res.string.checkout_payment_start_failed_empty_cart)))
                    return@launch
                }
                when (val clientSecret = getClientSecretUseCase(cartId = cart!!.cartId)) {
                    is Result.Success -> {
                        val secret = clientSecret.value.clientSecret
                        if (secret.isNullOrBlank()) {
                            onFailure(getString(Res.string.checkout_payment_start_failed))
                        } else {
                            sendEffect(CheckoutEffect.OpenPayMobSheet(clientSecret = secret))
                        }
                    }
                    is Result.Error -> onFailure(
                        clientSecret.throwable.message ?: getString(Res.string.checkout_payment_start_failed)
                    )
                    Result.Loading -> Unit
                }
            } else {
                sendEffect(CheckoutEffect.ShowToast(getString(Res.string.checkout_payment_start_failed)))
            }
        }


    }

    private fun onNext() {
        when (currentState.step) {
            CheckoutStep.Review -> {
                if (currentState.itemCount == 0) {
                    viewModelScope.launch { sendEffect(ShowToast(getString(Res.string.checkout_cart_empty))) }
                } else {
                    updateState { copy(step = CheckoutStep.Address) }
                }
            }

            CheckoutStep.Address -> {
                if (currentState.selectedAddress?.isDeliverable != true) {
                    viewModelScope.launch { sendEffect(ShowToast(getString(Res.string.checkout_select_address))) }
                } else {
                    updateState { copy(step = CheckoutStep.Payment) }
                }
            }

            CheckoutStep.Payment -> when (currentState.paymentMethod) {
                CheckoutPaymentMethod.CashOnDelivery -> updateState { copy(step = CheckoutStep.PlaceOrder) }
                CheckoutPaymentMethod.Online -> startOnlinePayment()
                CheckoutPaymentMethod.PayMob -> {
                    doPaymobPayment()
                }

                null -> viewModelScope.launch { sendEffect(ShowToast(getString(Res.string.checkout_choose_payment))) }
            }

            CheckoutStep.PlaceOrder -> Unit // handled by OnPlaceOrder
        }
    }

    private fun startOnlinePayment() {
        val address = currentState.selectedAddress
        if (address?.isDeliverable != true) {
            viewModelScope.launch { sendEffect(ShowToast(getString(Res.string.checkout_select_address))) }
            updateState { copy(step = CheckoutStep.Address) }
            return
        }
        val currentCart = cart
        val url = currentCart?.checkoutUrl
        if (currentCart == null || url.isNullOrBlank()) {
            viewModelScope.launch { sendEffect(CheckoutEffect.ShowToast(getString(Res.string.checkout_unavailable))) }
            return
        }
        updateState { copy(isBusy = true) }
        viewModelScope.launch {
            val attached = attachAddressToCart(currentCart.cartId, address)
            updateState { copy(isBusy = false) }
            if (attached.isSuccess) {
                sendEffect(CheckoutEffect.PresentCheckoutSheet(url))
            } else {
                sendEffect(CheckoutEffect.ShowToast(getString(Res.string.checkout_payment_start_failed)))
            }
        }
    }

    private fun placeCod() {
        val currentCart = cart
        if (currentCart == null || currentCart.lines.isEmpty()) {
            viewModelScope.launch { sendEffect(CheckoutEffect.ShowToast(getString(Res.string.checkout_cart_empty))) }
            sendEffect(CheckoutEffect.NavigateToCart)
            return
        }
        val address = currentState.selectedAddress
        if (address?.isDeliverable != true) {
            viewModelScope.launch { sendEffect(CheckoutEffect.ShowToast(getString(Res.string.checkout_no_deliverable_address))) }
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
                    sendEffect(
                        CheckoutEffect.NavigateToOrderResult(
                            buildResultFromState(
                                state = snapshot,
                                address = address,
                                success = true,
                                orderName = result.orderName,
                                errorMessage = null,
                                paymentLabel = getString(Res.string.checkout_payment_cod_title),
                            )
                        )
                    )
                }

                PlaceOrderResult.RequiresLogin -> sendEffect(CheckoutEffect.ShowLoginRequiredDialog)
                PlaceOrderResult.NoAddress -> {
                    sendEffect(CheckoutEffect.ShowToast(getString(Res.string.checkout_no_deliverable_address)))
                    updateState { copy(step = CheckoutStep.Address) }
                }

                PlaceOrderResult.EmptyCart -> {
                    sendEffect(CheckoutEffect.ShowToast(getString(Res.string.checkout_cart_empty)))
                    sendEffect(CheckoutEffect.NavigateToCart)
                }

                is PlaceOrderResult.Error -> {
                    resetCheckoutProgress()
                    sendEffect(
                        CheckoutEffect.NavigateToOrderResult(
                            buildResultFromState(
                                state = snapshot,
                                address = address,
                                success = false,
                                orderName = null,
                                errorMessage = getString(Res.string.checkout_order_place_failed),
                                paymentLabel = getString(Res.string.checkout_payment_cod_title),
                            )
                        )
                    )
                }
            }
        }
    }

    private fun applyCoupon() {
        val code = currentState.couponInput.trim()
        if (code.isEmpty()) {
            viewModelScope.launch { sendEffect(CheckoutEffect.ShowToast(getString(Res.string.checkout_enter_coupon))) }
            return
        }
        updateState { copy(isApplyingCoupon = true) }
        viewModelScope.launch {
            val result = applyDiscount(listOf(code))
            updateState { copy(isApplyingCoupon = false) }
            when (result) {
                is ApplyDiscountResult.Success -> {
                    val applied = result.cart.discountCodes.firstOrNull {
                        it.code.equals(
                            code,
                            ignoreCase = true
                        )
                    }
                    if (applied?.applicable == true) {
                        sendEffect(CheckoutEffect.ShowToast(getString(Res.string.checkout_coupon_applied)))
                    } else {
                        sendEffect(CheckoutEffect.ShowToast(getString(Res.string.checkout_coupon_invalid, code)))
                    }
                    // The cart stream re-emits with updated totals + discountCodes.
                }

                ApplyDiscountResult.RequiresLogin -> sendEffect(CheckoutEffect.ShowLoginRequiredDialog)
                is ApplyDiscountResult.Error -> sendEffect(CheckoutEffect.ShowToast(getString(Res.string.checkout_coupon_apply_failed)))
            }
        }
    }

    private fun removeCoupon() {
        updateState { copy(isApplyingCoupon = true) }
        viewModelScope.launch {
            val result = applyDiscount(emptyList())
            updateState { copy(isApplyingCoupon = false) }
            when (result) {
                is ApplyDiscountResult.Success -> {
                    updateState { copy(couponInput = "") }
                    sendEffect(CheckoutEffect.ShowToast(getString(Res.string.checkout_coupon_removed)))
                }

                ApplyDiscountResult.RequiresLogin -> sendEffect(CheckoutEffect.ShowLoginRequiredDialog)
                is ApplyDiscountResult.Error -> sendEffect(CheckoutEffect.ShowToast(getString(Res.string.checkout_coupon_remove_failed)))
            }
        }
    }


    override fun onCheckoutCompleted(checkoutCompletedEvent: CheckoutCompletedEvent) {
        viewModelScope.launch {
            clearCart()
            val result = checkoutCompletedEvent.orderDetails.toSuccessResult()
            resetCheckoutProgress()
            sendEffect(CheckoutEffect.NavigateToOrderResult(result))
        }
    }

    override fun onCheckoutFailed(error: Exception) {
        viewModelScope.launch {
            val result = buildResultFromState(
                state = currentState,
                address = currentState.selectedAddress,
                success = false,
                orderName = null,
                errorMessage = error.message ?: getString(Res.string.checkout_payment_failed),
                paymentLabel = getString(Res.string.checkout_payment_online_title),
            )
            resetCheckoutProgress()
            sendEffect(CheckoutEffect.NavigateToOrderResult(result))
        }
    }

    override fun onCheckoutCanceled() {
        updateState { copy(isBusy = false) }
        viewModelScope.launch { sendEffect(CheckoutEffect.ShowToast(getString(Res.string.checkout_payment_canceled))) }
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

    private suspend fun buildResultFromState(
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
        discountLabel = state.appliedDiscountCode?.let { getString(Res.string.checkout_discount_code, it) },
        discountValueFormatted = state.discountValueFormatted,
        totalFormatted = state.totalFormatted,
    )

    private suspend fun OrderDetails.toSuccessResult(): AppRoute.OrderResult {
        val cartInfo = this.cart
        val subtotal = cartInfo.price.subtotal
        val total = cartInfo.price.total
        val delivery = deliveries.firstOrNull()?.details?.location ?: billingAddress
        val recipient = listOfNotNull(delivery?.firstName, delivery?.lastName)
            .filter { it.isNotBlank() }.joinToString(" ")
        val addressLines = listOfNotNull(
            delivery?.address1,
            delivery?.address2,
            delivery?.city,
            delivery?.zoneCode,
            delivery?.countryCode,
        ).filter { it.isNotBlank() }
        val savings = savings(subtotal?.amount, total?.amount, subtotal?.currencyCode)
        val onlineLabel = getString(Res.string.checkout_payment_online_title)
        return AppRoute.OrderResult(
            success = true,
            orderName = null,
            errorMessage = null,
            paymentLabel = paymentMethods.firstOrNull()?.type?.ifBlank { onlineLabel }
                ?: onlineLabel,
            recipientName = recipient,
            addressLines = addressLines,
            phone = phone.orEmpty(),
            itemImageUrls = cartInfo.lines.map { it.image?.md.orEmpty() },
            itemCount = cartInfo.lines.sumOf { it.quantity },
            subtotalFormatted = formatMoney(subtotal?.amount, subtotal?.currencyCode),
            discountLabel = if (savings != null) getString(Res.string.checkout_discount_label) else null,
            discountValueFormatted = savings,
            totalFormatted = formatMoney(total?.amount, total?.currencyCode),
        )
    }

    override fun onSuccess(payResponse: HashMap<String, String?>) {
        if (isPaymentHandled) return
        isPaymentHandled = true
        val isSuccess = payResponse["success"]?.toBooleanStrictOrNull() ?: true
        val snapshot = currentState
        val address = currentState.selectedAddress
        viewModelScope.launch {
            clearCart()
            resetCheckoutProgress()
            val errorMessage = if (isSuccess) null else getString(Res.string.checkout_payment_failed)
            sendEffect(
                CheckoutEffect.NavigateToOrderResult(
                    buildResultFromState(
                        state = snapshot,
                        address = address,
                        success = isSuccess,
                        orderName = payResponse["order"]?.toString() ?: payResponse["id"]?.toString(),
                        errorMessage = errorMessage,
                        paymentLabel = getString(Res.string.checkout_card_payment),
                    )
                )
            )
        }
    }

    override fun onFailure(msg: String?) {
        if (isPaymentHandled) return
        isPaymentHandled = true
        viewModelScope.launch { sendEffect(CheckoutEffect.ShowToast(getString(Res.string.checkout_payment_canceled))) }
    }

    override fun onPending() {
        if (isPaymentHandled) return
        viewModelScope.launch { sendEffect(CheckoutEffect.ShowToast(getString(Res.string.checkout_payment_pending))) }
    }
}
