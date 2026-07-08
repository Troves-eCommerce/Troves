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

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.troves.presintation.ui.MainViewModel
import org.koin.compose.viewmodel.koinViewModel

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

            val mainViewModel: MainViewModel = koinViewModel()
            val appState by mainViewModel.uiState.collectAsState()

            val isDark = when (appState.themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            val view = LocalView.current
            if (!view.isInEditMode) {
                SideEffect {
                    val window = (view.context as Activity).window
                    val insetsController = WindowCompat.getInsetsController(window, view)
                    insetsController.isAppearanceLightStatusBars = !isDark
                    insetsController.isAppearanceLightNavigationBars = !isDark
                }
            }

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