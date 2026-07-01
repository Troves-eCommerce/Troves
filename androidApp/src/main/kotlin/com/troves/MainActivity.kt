package com.troves

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.troves.auth.AndroidGoogleAuthHandler
import com.troves.presintation.ui.auth.google.LocalGoogleAuthHandler
import com.troves.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val googleAuthHandler = AndroidGoogleAuthHandler(
                activity = this,
                webClientId = "339149506183-ahop6ekj4n38pookan3dkqpt0a132hpi.apps.googleusercontent.com"
            )

            CompositionLocalProvider(
                LocalGoogleAuthHandler provides googleAuthHandler
            ) {
                App()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}