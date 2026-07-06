package com.troves.data.repository

import com.troves.data.source.local.preferenceses.TrovesPreferences
import com.troves.data.source.remote.RemoteDatasource
import com.troves.data.source.remote.service.StorefrontApiService

actual fun createAuthenticationRepository(
    preferences: TrovesPreferences,
    storefront: StorefrontApiService,
    remoteDatasource: RemoteDatasource,
): PlatformAuthenticationRepository =
    AuthenticationRepositoryFirebaseImpl(preferences, storefront, remoteDatasource)
