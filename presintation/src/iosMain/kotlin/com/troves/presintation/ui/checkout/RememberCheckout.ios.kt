package com.troves.presintation.ui.checkout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.uikit.LocalUIViewController
import platform.Foundation.NSURL
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIApplication

@Composable
actual fun rememberCheckout(checkoutEvent: CheckoutEvent): Checkout {
    return remember(checkoutEvent) {
        iOSCheckout()
    }
}

class iOSCheckout(): Checkout{
    override fun presentCheckout(checkoutUrl: String) {
        val url = NSURL.URLWithString(checkoutUrl) ?: return

        val safari = SFSafariViewController(url)

        val rootController =
            UIApplication.sharedApplication.keyWindow?.rootViewController

        rootController?.presentViewController(
            safari,
            animated = true,
            completion = null
        )
    }

}
interface PaymobNativeBridge{
    fun startPayment(clientSecret: String,publicKey: String,listener: PaymobListener)
}

object PaymobBridgeHolder{
    lateinit var bridge: PaymobNativeBridge
}

class iOSPaymobCheckout(
    private val paymobListener: PaymobListener
): PaymobCheckout{
    override fun pay(clientSecret: String, publicKey: String){
        val bridge = PaymobBridgeHolder.bridge
        bridge.startPayment(
            clientSecret = clientSecret,
            publicKey = publicKey,
            listener = paymobListener
        )
    }

}

@Composable
actual fun rememberPaymobCheckout(paymobSdkListener: PaymobListener): PaymobCheckout {
    val viewController = LocalUIViewController.current
    return remember(viewController,paymobSdkListener) {
        iOSPaymobCheckout(paymobSdkListener)
    }
}