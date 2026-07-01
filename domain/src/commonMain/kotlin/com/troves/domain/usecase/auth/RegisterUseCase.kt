package com.troves.domain.usecase.auth
import com.troves.domain.utils.Result

import com.troves.domain.repository.AuthenticationRepository

class RegisterUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> =
        authenticationRepository.register(email.trim(), password)
}