package com.troves.domain.usecase.auth

import com.troves.domain.AuthenticationRepository
import com.troves.domain.Result

class LoginUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> =
        authenticationRepository.login(email.trim(), password)
}