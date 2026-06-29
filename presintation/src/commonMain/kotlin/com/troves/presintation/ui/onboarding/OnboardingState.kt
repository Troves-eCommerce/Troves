package com.troves.presintation.ui.onboarding

data class OnboardingState(
    val currentPage: Int = 0,
    val isLastPage: Boolean = false,
    val isCompleting: Boolean = false
)

sealed interface OnboardingUiEvent {
    data object Finished : OnboardingUiEvent
}
