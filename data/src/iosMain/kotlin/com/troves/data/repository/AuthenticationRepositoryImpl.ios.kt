package com.troves.data.repository

import com.troves.data.source.local.preferenceses.TrovesPreferences
import com.troves.data.source.remote.service.StorefrontApiService
import com.troves.data.source.remote.service.admin.ShopifyAdminCustomerService

import com.troves.data.source.remote.RemoteDatasource

actual fun createAuthenticationRepository(
    preferences: TrovesPreferences,
    storefront: StorefrontApiService,
    remoteDatasource: RemoteDatasource,
    adminCustomer: ShopifyAdminCustomerService,
): PlatformAuthenticationRepository =
    AuthenticationRepositoryFirebaseImpl(preferences, storefront, remoteDatasource, adminCustomer)
