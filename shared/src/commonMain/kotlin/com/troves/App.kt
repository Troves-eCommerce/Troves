package com.troves

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.troves.designsystem.theme.SpTheme
import com.troves.presintation.navigation.AppNavHost
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    KoinContext {
        SpTheme {
            AppNavHost()
        }
    }
}