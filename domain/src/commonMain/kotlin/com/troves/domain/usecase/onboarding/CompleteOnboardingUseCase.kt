package com.troves.domain.usecase.onboarding

import com.troves.domain.repository.OnboardingRepository

class CompleteOnboardingUseCase(
    private val repository: OnboardingRepository
) {
    suspend operator fun invoke() = repository.setOnboardingDone()
}