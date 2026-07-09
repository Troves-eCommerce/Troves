package com.troves

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import com.troves.data.source.remote.service.TrovesApiService
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.troves.designsystem.components.connectivity.ConnectivityBanner
import com.troves.designsystem.components.connectivity.OfflineBanner
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.util.CurrencyState
import com.troves.designsystem.util.LocalCurrency
import com.troves.presintation.navigation.AppNav
import com.troves.presintation.ui.MainViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject

@Composable
fun App() {
    InitializeCoil()
    val mainViewModel: MainViewModel = koinViewModel()
    val appState by mainViewModel.uiState.collectAsState()
    val banner by mainViewModel.banner.collectAsState()
    val isOnline by mainViewModel.isOnline.collectAsState()

    val isDark = when (appState.themeMode) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }

    val layoutDirection = if (appState.language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

    val currencyState = CurrencyState(
        selectedCurrency = appState.currency,
        exchangeRate = appState.exchangeRate
    )

    SpTheme(
        isDarkTheme = isDark,
        languageCode = appState.language
    ) {
        CompositionLocalProvider(
            LocalLayoutDirection provides layoutDirection,
            LocalCurrency provides currencyState
        ) {
            val apiService = koinInject<TrovesApiService>()
            // Prefetch on first launch and re-run whenever connectivity returns,
            // so cold-start-offline and reconnect both end up with fresh data.
            LaunchedEffect(key1 = isOnline) {
                if (isOnline) apiService.getAllProducts()
            }

            val bannerVisible = banner != ConnectivityBanner.Hidden
            Column(Modifier.fillMaxSize()) {
                OfflineBanner(banner)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        // While the banner occupies the status-bar area, tell the
                        // content below that the inset is already consumed so
                        // screens' own statusBarsPadding() don't double-pad.
                        .then(
                            if (bannerVisible) Modifier.consumeWindowInsets(WindowInsets.statusBars)
                            else Modifier
                        )
                ) {
                    AppNav()
                }
            }
        }
    }
}