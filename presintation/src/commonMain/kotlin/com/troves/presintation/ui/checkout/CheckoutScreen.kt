package com.troves.presintation.ui.checkout

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.dialog.LoginRequiredDialog
import com.troves.designsystem.components.topbar.BaseTopAppBar
import com.troves.designsystem.theme.Theme
import com.troves.domain.entity.Address
import com.troves.domain.entity.AddressIcon
import com.troves.presintation.core.mvi.ObserveEffect
import com.troves.presintation.navigation.AppRoute
import com.troves.presintation.ui.checkout.steps.AddressStepContent
import com.troves.presintation.ui.checkout.steps.AddressUi
import com.troves.presintation.ui.checkout.steps.OrderSummaryItemUi
import com.troves.presintation.ui.checkout.steps.OrderSummaryStepContent
import com.troves.presintation.ui.checkout.steps.PaymentOption
import com.troves.presintation.ui.checkout.steps.PaymentStepContent
import com.troves.presintation.ui.checkout.steps.PlaceOrderStepContent
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_arrow_back
import troves.designsystem.generated.resources.ic_home
import troves.designsystem.generated.resources.ic_location
import troves.designsystem.generated.resources.ic_payment_method

@Composable
fun CheckoutScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToNewAddress: (String?) -> Unit,
    onNavigateToOrderResult: (AppRoute.OrderResult) -> Unit,
    viewModel: CheckoutViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val checkout = rememberCheckout(viewModel)
    var showLoginDialog by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.onIntent(CheckoutIntent.OnResume)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            CheckoutEffect.NavigateBack -> onNavigateBack()
            CheckoutEffect.NavigateToCart -> onNavigateToCart()
            is CheckoutEffect.NavigateToNewAddress -> onNavigateToNewAddress(effect.addressId)
            is CheckoutEffect.PresentCheckoutSheet -> checkout.presentCheckout(effect.url)
            is CheckoutEffect.NavigateToOrderResult -> onNavigateToOrderResult(effect.args)
            is CheckoutEffect.ShowToast -> scope.launch { snackBarHostState.showSnackbar(effect.message) }
            CheckoutEffect.ShowLoginRequiredDialog -> showLoginDialog = true
        }
    }

    if (showLoginDialog) {
        LoginRequiredDialog(
            message = "You need to be logged in to place an order.",
            onLoginClick = {
                showLoginDialog = false
                onNavigateToLogin()
            },
            onDismiss = { showLoginDialog = false },
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            containerColor = Theme.colors.backGround,
            topBar = {
                BaseTopAppBar(
                    title = titleFor(state.step),
                    leadingIcon = painterResource(Res.drawable.ic_arrow_back),
                    onLeadingClick = { viewModel.onIntent(CheckoutIntent.OnBack) },
                    modifier = Modifier.background(Theme.colors.backGround),
                )
            },
            bottomBar = {
                CheckoutBottomBar(
                    isPlaceOrderStep = state.step == CheckoutStep.PlaceOrder,
                    enabled = state.canContinue,
                    isBusy = state.isBusy,
                    onClick = {
                        viewModel.onIntent(
                            if (state.step == CheckoutStep.PlaceOrder) CheckoutIntent.OnPlaceOrder
                            else CheckoutIntent.OnNext
                        )
                    },
                )
            },
        ) { innerPadding ->
            AnimatedContent(
                targetState = state.step,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                label = "checkoutStep",
            ) { step ->
                when (step) {
                    CheckoutStep.Review -> OrderSummaryStepContent(
                        couponInput = state.couponInput,
                        onCouponChange = { viewModel.onIntent(CheckoutIntent.OnCouponChange(it)) },
                        onApplyCoupon = { viewModel.onIntent(CheckoutIntent.OnApplyCoupon) },
                        items = state.lines.map {
                            OrderSummaryItemUi(
                                imagePainter = rememberAsyncImagePainter(it.imageUrl),
                                name = it.title,
                                specs = it.specs,
                                quantity = it.quantity,
                                priceFormatted = it.priceFormatted,
                            )
                        },
                        itemCount = state.itemCount,
                        subtotalFormatted = state.subtotalFormatted,
                        totalFormatted = state.totalFormatted,
                        currentStep = state.stepNumber,
                        totalSteps = state.totalSteps,
                        discountCode = state.appliedDiscountCode,
                        discountValueFormatted = state.discountValueFormatted,
                        isApplyingCoupon = state.isApplyingCoupon,
                    )

                    CheckoutStep.Address -> AddressStepContent(
                        addresses = state.addresses.map { it.toAddressUi() },
                        selectedAddressId = state.selectedAddressId,
                        onSelectAddress = { viewModel.onIntent(CheckoutIntent.OnSelectAddress(it)) },
                        onEditAddress = { viewModel.onIntent(CheckoutIntent.OnEditAddress(it)) },
                        onAddAddress = { viewModel.onIntent(CheckoutIntent.OnAddAddress) },
                        currentStep = state.stepNumber,
                        totalSteps = state.totalSteps,
                    )

                    CheckoutStep.Payment -> PaymentStepContent(
                        selected = state.paymentMethod?.toOption(),
                        onSelect = { viewModel.onIntent(CheckoutIntent.OnSelectPaymentMethod(it.toMethod())) },
                        currentStep = state.stepNumber,
                        totalSteps = state.totalSteps,
                    )

                    CheckoutStep.PlaceOrder -> {
                        val address = state.selectedAddress
                        val cod = state.paymentMethod != CheckoutPaymentMethod.Online
                        PlaceOrderStepContent(
                            currentStep = state.stepNumber,
                            totalSteps = state.totalSteps,
                            paymentIcon = painterResource(Res.drawable.ic_payment_method),
                            paymentTitle = if (cod) "Cash on Delivery (COD)" else "Online Payment",
                            paymentDescription = if (cod) "Pay with cash when your order is delivered."
                            else "Pay securely using your card.",
                            paymentSubDescription = if (cod) "Cash limit: up to \$500.00" else null,
                            addressTitle = address?.label ?: "Delivery address",
                            recipientName = address?.recipientName.orEmpty(),
                            addressLines = address?.lines.orEmpty(),
                            phone = address?.phone.orEmpty(),
                            itemImages = state.lines.map { rememberAsyncImagePainter(it.imageUrl) },
                            itemCount = state.itemCount,
                            subtotalFormatted = state.subtotalFormatted,
                            totalFormatted = state.totalFormatted,
                            discountCode = state.appliedDiscountCode,
                            discountValueFormatted = state.discountValueFormatted,
                            onChangePayment = { viewModel.onIntent(CheckoutIntent.OnChangePayment) },
                            onChangeAddress = { viewModel.onIntent(CheckoutIntent.OnChangeAddress) },
                            onEditCart = { viewModel.onIntent(CheckoutIntent.OnEditCart) },
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(16.dp),
        )
    }
}

@Composable
private fun CheckoutBottomBar(
    isPlaceOrderStep: Boolean,
    enabled: Boolean,
    isBusy: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Theme.colors.backGround)
            .navigationBarsPadding()
            .padding(Theme.spacing.medium),
    ) {
        PrimaryButton(
            caption = if (isPlaceOrderStep) "Place Order" else "Continue",
            onClick = onClick,
            isDisabled = !enabled,
            isLoading = isBusy,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private fun titleFor(step: CheckoutStep): String = when (step) {
    CheckoutStep.Review -> "Order Summary"
    CheckoutStep.Address -> "Delivery Address"
    CheckoutStep.Payment -> "Payment"
    CheckoutStep.PlaceOrder -> "Confirm Order"
}

private fun CheckoutPaymentMethod.toOption(): PaymentOption = when (this) {
    CheckoutPaymentMethod.CashOnDelivery -> PaymentOption.CashOnDelivery
    CheckoutPaymentMethod.Online -> PaymentOption.Online
}

private fun PaymentOption.toMethod(): CheckoutPaymentMethod = when (this) {
    PaymentOption.CashOnDelivery -> CheckoutPaymentMethod.CashOnDelivery
    PaymentOption.Online -> CheckoutPaymentMethod.Online
}

@Composable
private fun Address.toAddressUi(): AddressUi {
    val iconRes = when (icon) {
        AddressIcon.HOME -> Res.drawable.ic_home
        AddressIcon.WORK -> Res.drawable.ic_location
        AddressIcon.OTHER -> Res.drawable.ic_location
    }
    val title = label ?: when (icon) {
        AddressIcon.HOME -> "Home"
        AddressIcon.WORK -> "Work"
        AddressIcon.OTHER -> "Address"
    }
    return AddressUi(
        id = id,
        title = title,
        recipientName = recipientName,
        addressLines = lines,
        phone = phone.orEmpty(),
        iconPainter = painterResource(iconRes),
        isDefault = isDefault,
    )
}
