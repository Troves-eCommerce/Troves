package com.troves.presintation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.IsLoggedInUseCase
import com.troves.domain.usecase.onboarding.IsOnboardingDoneUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Decides the first screen the user should land on:
 *  - onboarding not completed yet            → [StartDestination.Onboarding]
 *  - onboarding done but no signed-in user   → [StartDestination.Login]
 *  - onboarding done and a user is signed in → [StartDestination.Home]
 *
 * Both flags are persisted (onboarding in DataStore, the auth session by
 * Firebase) and only *read* here — completing onboarding is the responsibility
 * of [com.troves.presintation.ui.onboarding.OnboardingViewModel] and signing in
 * of [com.troves.presintation.ui.auth.AuthViewModel].
 */
class MainViewModel(
    private val isOnboardingDone: IsOnboardingDoneUseCase,
    private val isLoggedIn: IsLoggedInUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        resolveStartDestination()
    }

    private fun resolveStartDestination() {
        viewModelScope.launch {
            val destination = try {
                when {
                    !isOnboardingDone() -> StartDestination.Onboarding
                    isLoggedIn()        -> StartDestination.Home
                    else                -> StartDestination.Login
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                // A startup read should never crash the app; fall back to onboarding.
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
    Login,
    Home,
}
