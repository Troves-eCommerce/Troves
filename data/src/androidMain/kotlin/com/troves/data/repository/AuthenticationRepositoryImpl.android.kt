package com.troves.data.repository

import com.troves.data.local.preferenceses.AppPreferencesDataSource

actual fun createAuthenticationRepository(
    preferences: AppPreferencesDataSource
): PlatformAuthenticationRepository =
    AuthenticationRepositoryFirebaseImpl(preferences)
