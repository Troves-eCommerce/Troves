package com.troves.domain.usecase.onboarding

import com.troves.domain.AuthenticationRepository

class IsOnboardingDoneUseCase(
    private val repository: AuthenticationRepository
) {
    suspend operator fun invoke(): Boolean = repository.isOnboardingDone()
}
