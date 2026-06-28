package com.troves.domain

class LoginUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> =
        authenticationRepository.login(email.trim(), password)
}
