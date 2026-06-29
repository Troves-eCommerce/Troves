package com.troves.domain

interface AuthenticationRepository {
    /**
     * Sign in with email and password.
     * @return [Result.Success] with Unit on success, [Result.Error] on failure.
     */
    suspend fun login(email: String, password: String): Result<Unit>

    /**
     * Create a new account with email and password.
     * @return [Result.Success] with Unit on success, [Result.Error] on failure.
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
}
