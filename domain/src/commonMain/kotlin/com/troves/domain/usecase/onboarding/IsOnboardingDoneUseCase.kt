package com.troves.domain.usecase.onboarding
import com.troves.domain.repository.OnboardingRepository

class IsOnboardingDoneUseCase(
    private val repository: OnboardingRepository
) {
    suspend operator fun invoke(): Boolean = repository.isOnboardingDone()
}