package com.troves.presintation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.usecase.onboarding.IsOnboardingDoneUseCase
import com.troves.domain.usecase.settings.ObserveProfilePreferencesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    observeProfilePreferences: ObserveProfilePreferencesUseCase,
    private val isOnboardingDone: IsOnboardingDoneUseCase
) : ViewModel() {

    val uiState: StateFlow<MainUiState> = observeProfilePreferences()
        .map { prefs ->
            val destination = try {
                if (isOnboardingDone()) StartDestination.Home else StartDestination.Onboarding
            } catch (e: Exception) {
                StartDestination.Onboarding
            }

            MainUiState(
                isDarkTheme = when (prefs.themeMode) {
                    "dark" -> true
                    "light" -> false
                    else -> false
                },
                language = prefs.language,
                currency = prefs.currency,
                startDestination = destination,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainUiState(isLoading = true) // Starts as loading
        )
}

data class MainUiState(
    val isLoading: Boolean = true,
    val isDarkTheme: Boolean = false,
    val language: String = "en",
    val currency: String = "USD",
    val startDestination: StartDestination = StartDestination.Onboarding
)

enum class StartDestination {
    Onboarding,
    Home
}