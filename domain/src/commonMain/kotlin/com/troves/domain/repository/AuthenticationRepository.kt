package com.troves.domain.repository
import com.troves.domain.utils.Result


interface AuthenticationRepository {
    /**
     * Sign in with email and password.
     * @return [com.troves.data.source.remote.Result.Success] with Unit on success, [com.troves.data.source.remote.Result.Error] on failure.
     */
    suspend fun login(email: String, password: String): Result<Unit>

    /**
     * Create a new account with email and password.
     * @return [com.troves.data.source.remote.Result.Success] with Unit on success, [com.troves.data.source.remote.Result.Error] on failure.
     */
    suspend fun register(email: String, password: String): Result<Unit>

    /**
     * Sign out the currently authenticated user.
     */
    suspend fun logout()

    /**
     * Returns true if a user is currently signed in (session persisted).
     */
    suspend fun isLoggedIn(): Boolean

    /**
     * Returns true if the user has already completed the onboarding flow
     * (persisted locally on both Android and iOS).
     */
    suspend fun isOnboardingDone(): Boolean

    /**
     * Marks the onboarding flow as completed so it is not shown again.
     */
    suspend fun setOnboardingDone()
}
