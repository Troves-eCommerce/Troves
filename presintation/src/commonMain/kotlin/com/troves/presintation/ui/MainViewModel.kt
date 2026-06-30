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
