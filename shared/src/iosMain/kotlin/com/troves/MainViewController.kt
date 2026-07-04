package com.troves

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import com.troves.presintation.ui.auth.google.GoogleAuthHandler
import com.troves.presintation.ui.auth.google.LocalGoogleAuthHandler
import com.troves.presintation.ui.checkout.IosCheckoutBridge
import com.troves.presintation.ui.checkout.LocalIosCheckoutBridge

fun MainViewController(
    googleAuthHandler: GoogleAuthHandler,
    checkoutBridge: IosCheckoutBridge,
) = ComposeUIViewController {
    CompositionLocalProvider(
        LocalGoogleAuthHandler provides googleAuthHandler,
        LocalIosCheckoutBridge provides checkoutBridge,
    ) {
        App()
    }
}
