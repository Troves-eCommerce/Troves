package com.troves.domain.usecase.onboarding

import com.troves.domain.repository.AuthenticationRepository

class IsOnboardingDoneUseCase(
    private val repository: AuthenticationRepository
) {
    suspend operator fun invoke(): Boolean = repository.isOnboardingDone()
}
