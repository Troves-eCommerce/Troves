package com.troves.presintation.ui.checkout

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberCheckout(checkoutEvent: CheckoutEvent): Checkout {
    val activity = LocalActivity.current as ComponentActivity
    return remember(activity, checkoutEvent) {
        AndroidCheckout(
            activityProvider = { activity },
            eventHandler = checkoutEvent,
        )
    }
}