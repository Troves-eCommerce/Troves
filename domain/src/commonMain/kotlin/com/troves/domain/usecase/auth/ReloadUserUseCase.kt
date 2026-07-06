package com.troves.domain.usecase.auth

import com.troves.domain.repository.AuthenticationRepository
import com.troves.domain.utils.Result

class ReloadUserUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(): Result<Unit> =
        authenticationRepository.reloadUser()
}
