package com.troves.domain.repository
import com.troves.domain.utils.Result


interface AuthenticationRepository {
    suspend fun login(email: String, password: String): Result<Unit>

    suspend fun register(email: String, password: String): Result<Unit>

    suspend fun signInWithGoogle(idToken: String, accessToken: String? = null): Result<Unit>

    suspend fun logout()

    suspend fun isLoggedIn(): Boolean

    suspend fun isOnboardingDone(): Boolean

    suspend fun setOnboardingDone()
    fun getCurrentUserId(): String?

    fun getCurrentUserEmail(): String?
}
