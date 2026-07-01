package com.troves.domain.usecase.auth

import com.troves.domain.repository.AuthenticationRepository

class LogoutUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke() = authenticationRepository.logout()
}