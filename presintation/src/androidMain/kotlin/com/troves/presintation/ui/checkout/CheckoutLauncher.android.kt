package com.troves.presintation.ui.checkout

import androidx.activity.ComponentActivity
import com.shopify.checkoutsheetkit.CheckoutException
import com.shopify.checkoutsheetkit.DefaultCheckoutEventProcessor
import com.shopify.checkoutsheetkit.ShopifyCheckoutSheetKit.present
import com.shopify.checkoutsheetkit.lifecycleevents.CheckoutCompletedEvent

class AndroidCheckout(
    private val activityProvider: () -> ComponentActivity,
    val eventHandler:CheckoutEvent
) : Checkout {

    override fun presentCheckout(checkoutUrl: String) {
        present(
            checkoutUrl = checkoutUrl,
            context = activityProvider(),
            checkoutEventProcessor = object : DefaultCheckoutEventProcessor(context = activityProvider()) {
                override fun onCheckoutCanceled() {
                    eventHandler.onCheckoutCanceled()
                }
                override fun onCheckoutCompleted(checkoutCompletedEvent: CheckoutCompletedEvent) {
                    eventHandler.onCheckoutCompleted(checkoutCompletedEvent = checkoutCompletedEvent.toDomain())
                }
                override fun onCheckoutFailed(error: CheckoutException) {
                    eventHandler.onCheckoutFailed(error = error)
                }
            }
        )
    }
}
