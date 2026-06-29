package com.troves.presintation.ui.onboarding

data class OnboardingState(
    val currentPage: Int = 0,
    val isLastPage: Boolean = false
)

sealed interface OnboardingUiEvent {
    data object NavigateToLogin : OnboardingUiEvent
}
