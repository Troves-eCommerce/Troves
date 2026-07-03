package com.troves

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import com.troves.data.source.remote.service.TrovesApiService
import com.troves.designsystem.theme.SpTheme
import com.troves.presintation.navigation.AppNav
import com.troves.presintation.ui.MainViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject

@Composable
fun App() {
    val mainViewModel: MainViewModel = koinViewModel()
    val appState by mainViewModel.uiState.collectAsState()

    if (!appState.isLoading) {
        SpTheme(
            isDarkTheme = appState.isDarkTheme,
            languageCode = appState.language
        ) {
            SpTheme {
                val apiService = koinInject<TrovesApiService>()
                LaunchedEffect(key1 = Unit) {
                    apiService.getAllProducts()
                }
                AppNav()
            }
        }
    }
}
