package com.troves.domain.usecase.auth

import com.troves.domain.AuthenticationRepository

class IsLoggedInUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(): Boolean =
        authenticationRepository.isLoggedIn()
}