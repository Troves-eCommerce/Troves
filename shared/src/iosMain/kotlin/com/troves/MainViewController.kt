package com.troves

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import com.troves.presintation.ui.auth.google.GoogleAuthHandler
import com.troves.presintation.ui.auth.google.LocalGoogleAuthHandler

fun MainViewController(googleAuthHandler: GoogleAuthHandler) = ComposeUIViewController {
    CompositionLocalProvider(
        LocalGoogleAuthHandler provides googleAuthHandler
    ) {
        App()
    }
}