package com.troves.presintation.ui.checkout

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.paymob.paymob_sdk.PaymobSdk
import com.paymob.paymob_sdk.ui.PaymobSdkListener

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

class AndroidPaymobCheckout(
    private val activity: Activity,
    private val paymobSdkListener: PaymobListener
) : PaymobCheckout, PaymobSdkListener {

    override fun pay(clientSecret: String, publicKey: String) {
        val sdk = PaymobSdk.Builder(
            context = activity,
            clientSecret = clientSecret,
            publicKey = publicKey,
            paymobSdkListener = this
        ).showSaveCard(true)
            .showTransactionResult(false)
            .setAppName("Troves")
            .build()
        sdk.start()
    }

    override fun onSuccess(payResponse: HashMap<String, String?>) =
        paymobSdkListener.onSuccess(payResponse)

    override fun onFailure(msg: String?) = paymobSdkListener.onFailure(msg = msg)

    override fun onPending() = paymobSdkListener.onPending()
}


@Composable
actual fun rememberPaymobCheckout(paymobSdkListener: PaymobListener): PaymobCheckout {
    val activity = LocalActivity.current as Activity
    return remember(activity, paymobSdkListener) {
        AndroidPaymobCheckout(activity, paymobSdkListener)
    }
}