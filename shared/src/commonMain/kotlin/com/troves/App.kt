package com.troves

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import com.troves.data.source.remote.service.TrovesApiService
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.util.CurrencyState
import com.troves.designsystem.util.LocalCurrency

import com.troves.presintation.navigation.AppNav
import com.troves.presintation.ui.MainViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject

@Composable
fun App() {
    val mainViewModel: MainViewModel = koinViewModel()
    val appState by mainViewModel.uiState.collectAsState()

    if (!appState.isLoading) {

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
                LaunchedEffect(key1 = Unit) {
                    apiService.getAllProducts()
                }
                AppNav()
            }
        }
    }
}