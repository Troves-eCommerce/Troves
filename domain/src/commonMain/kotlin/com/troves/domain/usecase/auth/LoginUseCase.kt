package com.troves.domain.usecase.auth
import com.troves.domain.utils.Result

import com.troves.domain.repository.AuthenticationRepository

class LoginUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> =
        authenticationRepository.login(email.trim(), password)
}