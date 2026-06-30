package com.troves.presintation.ui.onboarding

data class OnboardingState(
    val currentPage: Int = 0,
    val isLastPage: Boolean = false,
    val isCompleting: Boolean = false,
)

sealed interface OnboardingEffect {
    data object NavigateToHome : OnboardingEffect
}

sealed interface OnboardingIntent {
    data class PageChanged(val page: Int, val totalPages: Int) : OnboardingIntent
    data object CompleteOnboarding : OnboardingIntent
}
