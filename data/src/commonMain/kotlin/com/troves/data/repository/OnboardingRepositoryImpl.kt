package com.troves.data.repository

import com.troves.data.local.preferenceses.AppPreferencesDataSource
import com.troves.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.first

class OnboardingRepositoryImpl(
    private val preferences: AppPreferencesDataSource
) : OnboardingRepository {

    override suspend fun isOnboardingDone(): Boolean =
        preferences.isOnboardingDone.first()  // reads once from Flow and returns

    override suspend fun setOnboardingDone() {
        preferences.setOnboardingDone(true)
    }
}