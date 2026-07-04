package com.troves.presintation.ui.checkout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIApplication

@Composable
actual fun rememberCheckout(checkoutEvent: CheckoutEvent): Checkout {
    val bridge = LocalIosCheckoutBridge.current
    return remember(checkoutEvent, bridge) {
        IosCheckout(bridge, checkoutEvent)
    }
}

class CheckoutFailedException(message: String) : Exception(message)

class IosCheckout(
    private val bridge: IosCheckoutBridge?,
    private val eventHandler: CheckoutEvent,
) : Checkout {

    override fun presentCheckout(checkoutUrl: String) {
        val activeBridge = bridge
        if (activeBridge != null) {
            activeBridge.present(
                checkoutUrl = checkoutUrl,
                onCompleted = eventHandler::onCheckoutCompleted,
                onFailed = { eventHandler.onCheckoutFailed(CheckoutFailedException(it)) },
                onCanceled = eventHandler::onCheckoutCanceled,
            )
        } else {
            presentInSafari(checkoutUrl)
        }
    }

    private fun presentInSafari(checkoutUrl: String) {
        val url = NSURL.URLWithString(checkoutUrl) ?: return
        val safari = SFSafariViewController(url)
        UIApplication.sharedApplication.keyWindow?.rootViewController
            ?.presentViewController(safari, animated = true, completion = null)
    }
}
