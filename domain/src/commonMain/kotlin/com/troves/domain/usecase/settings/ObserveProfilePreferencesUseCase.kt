package com.troves.domain.usecase.settings

import com.troves.domain.entity.ProfilePreferences
import com.troves.domain.entity.UserProfile
import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.repository.TrovesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveProfilePreferencesUseCase(
    private val authRepository: AuthenticationRepository,
    private val trovesRepository: TrovesRepository
) {
    operator fun invoke(): Flow<ProfilePreferences> =
        combine(
            authRepository.isLoggedInStream,
            authRepository.currentUserStream,
            trovesRepository.selectedLanguage,
            trovesRepository.themeMode,
            trovesRepository.selectedCurrency
        ) { values ->
            val isLoggedIn = values[0] as Boolean
            val user = values[1] as UserProfile?
            ProfilePreferences(
                isLoggedIn = isLoggedIn,
                language = values[2] as String,
                themeMode = values[3] as String,
                currency = values[4] as String,
                email = user?.email
            )
        }
}