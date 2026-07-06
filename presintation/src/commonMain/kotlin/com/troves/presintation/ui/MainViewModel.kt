package com.troves.presintation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.ExchangeRate
import com.troves.domain.usecase.onboarding.IsOnboardingDoneUseCase
import com.troves.domain.usecase.settings.GetExchangeRatesUseCase
import com.troves.domain.usecase.settings.ObserveProfilePreferencesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    observeProfilePreferences: ObserveProfilePreferencesUseCase,
    private val isOnboardingDone: IsOnboardingDoneUseCase,
    getExchangeRatesUseCase: GetExchangeRatesUseCase,
) : ViewModel() {

    val uiState: StateFlow<MainUiState> = combine(
        observeProfilePreferences(),
        getExchangeRatesUseCase()
    ) { prefs, rates ->
        val destination = try {
            if (isOnboardingDone()) StartDestination.Home else StartDestination.Onboarding
        } catch (e: Exception) {
            StartDestination.Onboarding
        }

        MainUiState(
            themeMode = prefs.themeMode,
            language = prefs.language,
            currency = prefs.currency,
            exchangeRate = rates,
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
    val themeMode: String = "system",
    val language: String = "en",
    val currency: String = "USD",
    val exchangeRate: ExchangeRate? = null,
    val startDestination: StartDestination = StartDestination.Onboarding
)

enum class StartDestination {
    Onboarding,
    Home
}