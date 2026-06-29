package com.troves

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.troves.presintation.navigation.AppNavHost

@Composable
@Preview
fun App() {
    KoinContext {
        SpTheme {
            AppNavHost()
        }
    }
}
