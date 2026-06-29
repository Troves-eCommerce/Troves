package com.troves.data.repository

import com.troves.domain.Result

private class IosAuthenticationRepository : PlatformAuthenticationRepository {

    private val unsupported = UnsupportedOperationException(
        "Firebase authentication is not configured for iOS in this project yet."
    )

    override suspend fun login(email: String, password: String): Result<Unit> =
        Result.Error(unsupported)

    override suspend fun register(email: String, password: String): Result<Unit> =
        Result.Error(unsupported)

    override suspend fun logout() = Unit

    override suspend fun isLoggedIn(): Boolean = false
}

actual fun createAuthenticationRepository(): PlatformAuthenticationRepository =
    IosAuthenticationRepository()
