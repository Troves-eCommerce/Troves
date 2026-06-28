package com.troves.presintation.ui.Splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.troves.designsystem.theme.Theme
import com.troves.domain.AuthenticationRepository
import org.koin.compose.koinInject

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    authRepository: AuthenticationRepository = koinInject(),
) {
    LaunchedEffect(Unit) {
        val isLoggedIn = authRepository.isLoggedIn()
        if (isLoggedIn) {
            onNavigateToHome()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = "Troves",
            style = Theme.typography.display.copy(
                color = Theme.colors.primary,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
