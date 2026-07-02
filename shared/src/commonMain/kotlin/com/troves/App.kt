package com.troves

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.troves.designsystem.theme.SpTheme
import com.troves.presintation.navigation.AppNav
import com.troves.presintation.ui.MainViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    val mainViewModel: MainViewModel = koinViewModel()
    val appState by mainViewModel.uiState.collectAsState()

    if (!appState.isLoading) {
        SpTheme(
            isDarkTheme = appState.isDarkTheme,
            languageCode = appState.language
        ) {
            AppNav()
        }
    }
}
