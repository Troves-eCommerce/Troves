package com.troves

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import com.troves.auth.AndroidGoogleAuthHandler
import com.troves.presintation.ui.auth.google.LocalGoogleAuthHandler

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val googleAuthHandler = AndroidGoogleAuthHandler(
                activity = this,
                webClientId = "339149506183-ahop6ekj4n38pookan3dkqpt0a132hpi.apps.googleusercontent.com"
            )
//            TODO("We need to remove this id")

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