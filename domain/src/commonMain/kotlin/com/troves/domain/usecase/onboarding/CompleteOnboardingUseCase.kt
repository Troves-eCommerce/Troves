package com.troves.domain.usecase.onboarding

import com.troves.domain.AuthenticationRepository

class CompleteOnboardingUseCase(
    private val repository: AuthenticationRepository
) {
    suspend operator fun invoke() = repository.setOnboardingDone()
}
