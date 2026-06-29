package com.troves.presintation.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.usecase.onboarding.CompleteOnboardingUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val completeOnboardingUseCase: CompleteOnboardingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingState())
    val uiState: StateFlow<OnboardingState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<OnboardingUiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    fun updatePage(page: Int, totalPages: Int) {
        _uiState.update {
            it.copy(
                currentPage = page,
                isLastPage = page == totalPages - 1
            )
        }
    }

    fun completeOnboarding() {
        // Guard against double taps (e.g. spamming "Skip"/"Let's get started")
        // emitting multiple navigation events.
        if (_uiState.value.isCompleting) return
        _uiState.update { it.copy(isCompleting = true) }

        viewModelScope.launch {
            // Persisting the flag must never block navigation; ignore write failures.
            runCatching { completeOnboardingUseCase() }
            _uiEvent.send(OnboardingUiEvent.Finished)
        }
    }
}