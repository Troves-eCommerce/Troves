package com.troves.domain

class RegisterUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> =
        authenticationRepository.register(email.trim(), password)
}
