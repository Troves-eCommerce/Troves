package com.troves.presintation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.ExchangeRate
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.usecase.onboarding.IsOnboardingDoneUseCase
import com.troves.domain.usecase.settings.GetExchangeRatesUseCase
import com.troves.domain.usecase.settings.ObserveProfilePreferencesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    observeProfilePreferences: ObserveProfilePreferencesUseCase,
    private val isOnboardingDone: IsOnboardingDoneUseCase,
    getExchangeRatesUseCase: GetExchangeRatesUseCase,
    private val authenticationRepository: AuthenticationRepository,
) : ViewModel() {

    val uiState: StateFlow<MainUiState> = combine(
        observeProfilePreferences(),
        getExchangeRatesUseCase()
    ) { prefs, rates ->
        val destination = try {
            when {
                !isOnboardingDone() -> StartDestination.Onboarding
                hasUnverifiedSession() -> StartDestination.EmailVerification
                else -> StartDestination.Home
            }
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
            initialValue = MainUiState(isLoading = true)
        )

    /**
     * True only when there's an actual Firebase session (the user registered
     * or logged in before) whose email is still not verified. A guest with no
     * session at all returns false here and falls through to Home normally.
     */
    private suspend fun hasUnverifiedSession(): Boolean {
        val profile = authenticationRepository.getCurrentUserProfile() ?: return false
        return !profile.isEmailVerified
    }
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
    EmailVerification,
    Home
}