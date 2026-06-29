package com.troves.domain.repository
interface OnboardingRepository {
    suspend fun isOnboardingDone(): Boolean
    suspend fun setOnboardingDone()
}