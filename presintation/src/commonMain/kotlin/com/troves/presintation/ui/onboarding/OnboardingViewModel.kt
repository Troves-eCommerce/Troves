package com.troves.presintation.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingState())
    val uiState: StateFlow<OnboardingState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<OnboardingUiEvent>()
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
        viewModelScope.launch {
            _uiEvent.send(OnboardingUiEvent.NavigateToLogin)
        }
    }
}
