package com.troves.domain.usecase.auth

import com.troves.domain.AuthenticationRepository
import com.troves.domain.Result

class SignInWithGoogleUseCase(private val repository: AuthenticationRepository) {
    suspend operator fun invoke(idToken: String, accessToken: String? = null): Result<Unit> {
        return repository.signInWithGoogle(idToken, accessToken)
    }
}
