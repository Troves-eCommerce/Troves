package com.troves.presintation.ui.auth.google

import androidx.compose.runtime.staticCompositionLocalOf

interface GoogleAuthHandler {
    fun signIn(
        onSuccess: (idToken: String, accessToken: String?) -> Unit,
        onError: (Exception) -> Unit
    )
}

val LocalGoogleAuthHandler = staticCompositionLocalOf<GoogleAuthHandler?> { null }
