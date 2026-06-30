package com.troves.presintation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.usecase.onboarding.IsOnboardingDoneUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Decides the first screen the user should land on, based on whether onboarding
 * has already been completed (persisted in DataStore on both Android and iOS).
 *
 * Authentication is NOT required to browse: a first-time user sees onboarding,
 * everyone else lands straight on Home. Signing in is only prompted lazily when
 * the user triggers a gated action (e.g. opening the cart).
 *
 * It only *reads* the flag — completing onboarding is the responsibility of
 * [com.troves.presintation.ui.onboarding.OnboardingViewModel].
 */
class MainViewModel(
    private val isOnboardingDone: IsOnboardingDoneUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        resolveStartDestination()
    }

    private fun resolveStartDestination() {
        viewModelScope.launch {
            val destination = try {
                if (isOnboardingDone()) StartDestination.Home else StartDestination.Onboarding
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                // A preferences read should never crash startup; fall back to onboarding.
                StartDestination.Onboarding
            }
            _uiState.update { it.copy(isLoading = false, startDestination = destination) }
        }
    }
}

data class MainUiState(
    val isLoading: Boolean = true,
    val startDestination: StartDestination = StartDestination.Onboarding,
)

enum class StartDestination {
    Onboarding,
    Home,
}
