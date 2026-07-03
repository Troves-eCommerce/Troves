package com.troves.data.repository

import com.troves.data.source.local.preferenceses.TrovesPreferences

actual fun createAuthenticationRepository(
    preferences: TrovesPreferences
): PlatformAuthenticationRepository =
    AuthenticationRepositoryFirebaseImpl(preferences)
