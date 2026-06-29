package com.troves.data.repository

actual fun createAuthenticationRepository(): PlatformAuthenticationRepository =
    AuthenticationRepositoryFirebaseImpl()
