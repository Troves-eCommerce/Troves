package com.troves

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.troves.designsystem.theme.SpTheme
import com.troves.presintation.navigation.AppNavHost
import com.troves.presintation.ui.Auth.LoginScreen
import com.troves.presintation.ui.Auth.RegisterScreen

@Composable
@Preview
fun App() {
    SpTheme {
        // AppNavHost()
        AuthTestFlow()
    }
}

@Composable
fun AuthTestFlow() {
    var isLogin by remember { mutableStateOf(true) }

    if (isLogin) {
        LoginScreen(
            onNavigateToRegister = { isLogin = false },
            onLoginSuccess = { /* Handle success in test flow */ }
        )
    } else {
        RegisterScreen(
            onNavigateToLogin = { isLogin = true },
            onRegisterSuccess = { /* Handle success in test flow */ }
        )
    }
}
