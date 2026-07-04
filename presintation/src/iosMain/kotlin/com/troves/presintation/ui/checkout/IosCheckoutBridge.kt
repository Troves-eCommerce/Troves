package com.troves.presintation.ui.checkout

import androidx.compose.runtime.staticCompositionLocalOf

interface IosCheckoutBridge {
    fun present(
        checkoutUrl: String,
        onCompleted: (CheckoutCompletedEvent) -> Unit,
        onFailed: (message: String) -> Unit,
        onCanceled: () -> Unit,
    )
}

val LocalIosCheckoutBridge = staticCompositionLocalOf<IosCheckoutBridge?> { null }
