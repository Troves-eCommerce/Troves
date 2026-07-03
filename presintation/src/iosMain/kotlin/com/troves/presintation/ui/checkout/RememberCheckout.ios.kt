package com.troves.presintation.ui.checkout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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