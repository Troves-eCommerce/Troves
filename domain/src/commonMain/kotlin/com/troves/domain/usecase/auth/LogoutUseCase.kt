package com.troves.domain.usecase.auth

import com.troves.domain.AuthenticationRepository

class LogoutUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke() = authenticationRepository.logout()
}