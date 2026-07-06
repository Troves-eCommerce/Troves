package com.troves.presintation.ui.checkout

import com.troves.domain.entity.Address
import com.troves.presintation.navigation.AppRoute

enum class CheckoutStep { Review, Address, Payment, PlaceOrder }

enum class CheckoutPaymentMethod { CashOnDelivery, Online, PayMob }

data class CheckoutLineUi(
    val lineId: String,
    val imageUrl: String,
    val title: String,
    val specs: String,
    val quantity: Int,
    val priceFormatted: String,
)

data class CheckoutUiState(
    val step: CheckoutStep = CheckoutStep.Review,
    val isLoading: Boolean = true,
    val lines: List<CheckoutLineUi> = emptyList(),
    val itemCount: Int = 0,
    val subtotalFormatted: String = "$0.00",
    val totalFormatted: String = "$0.00",
    val couponInput: String = "",
    val appliedDiscountCode: String? = null,
    val discountValueFormatted: String? = null,
    val isApplyingCoupon: Boolean = false,
    val addresses: List<Address> = emptyList(),
    val selectedAddressId: String? = null,
    val paymentMethod: CheckoutPaymentMethod? = null,
    val isBusy: Boolean = false,
) {
    val selectedAddress: Address? get() = addresses.firstOrNull { it.id == selectedAddressId }
    val stepNumber: Int get() = step.ordinal + 1
    val totalSteps: Int get() = CheckoutStep.entries.size
    val isCartEmpty: Boolean get() = !isLoading && itemCount == 0

    val canContinue: Boolean
        get() = when (step) {
            CheckoutStep.Review -> itemCount > 0
            CheckoutStep.Address -> selectedAddress?.isDeliverable == true
            CheckoutStep.Payment -> paymentMethod != null
            CheckoutStep.PlaceOrder -> !isBusy && itemCount > 0 && selectedAddress?.isDeliverable == true
        }
}

sealed interface CheckoutIntent {
    data object OnBack : CheckoutIntent
    data object OnNext : CheckoutIntent
    data object OnEditCart : CheckoutIntent
    data class OnCouponChange(val value: String) : CheckoutIntent
    data object OnApplyCoupon : CheckoutIntent
    data object OnRemoveCoupon : CheckoutIntent
    data class OnSelectAddress(val id: String) : CheckoutIntent
    data object OnAddAddress : CheckoutIntent
    data class OnEditAddress(val id: String) : CheckoutIntent
    data class OnSelectPaymentMethod(val method: CheckoutPaymentMethod) : CheckoutIntent
    data object OnChangePayment : CheckoutIntent
    data object OnChangeAddress : CheckoutIntent
    data object OnPlaceOrder : CheckoutIntent
    data object OnResume : CheckoutIntent
}

sealed interface CheckoutEffect {
    data object NavigateBack : CheckoutEffect
    data object NavigateToCart : CheckoutEffect
    data class NavigateToNewAddress(val addressId: String?) : CheckoutEffect
    data class PresentCheckoutSheet(val url: String) : CheckoutEffect
    data class OpenPayMobSheet(val clientSecret: String): CheckoutEffect
    data class NavigateToOrderResult(val args: AppRoute.OrderResult) : CheckoutEffect
    data class ShowToast(val message: String) : CheckoutEffect
    data object ShowLoginRequiredDialog : CheckoutEffect
}
