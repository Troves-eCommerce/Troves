package com.troves.domain

class IsLoggedInUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(): Boolean =
        authenticationRepository.isLoggedIn()
}
