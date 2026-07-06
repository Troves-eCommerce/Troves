package com.troves.presintation.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.usecase.onboarding.CompleteOnboardingUseCase
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
) : ViewModel(),
    StateHolder<OnboardingState> by DefaultStateHolder(OnboardingState()),
    EffectPublisher<OnboardingEffect> by DefaultEffectPublisher() {

    fun onIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.PageChanged -> updateState {
                copy(
                    currentPage = intent.page,
                    isLastPage = intent.page == intent.totalPages - 1,
                )
            }
            OnboardingIntent.CompleteOnboarding -> completeOnboarding()
        }
    }

    private fun completeOnboarding() {

        if (currentState.isCompleting) return
        updateState { copy(isCompleting = true) }

        viewModelScope.launch {

            runCatching { completeOnboardingUseCase() }
            sendEffect(OnboardingEffect.NavigateToHome)
        }
    }
}
