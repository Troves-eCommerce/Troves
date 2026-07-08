package com.troves.data.repository

import com.troves.data.source.local.preferenceses.TrovesPreferences
import com.troves.data.source.remote.RemoteDatasource
import com.troves.data.source.remote.service.StorefrontApiService
import com.troves.data.source.remote.service.admin.ShopifyAdminCustomerService

actual fun createAuthenticationRepository(
    preferences: TrovesPreferences,
    storefront: StorefrontApiService,
    remoteDatasource: RemoteDatasource,
    adminCustomer: ShopifyAdminCustomerService,
): PlatformAuthenticationRepository =
    AuthenticationRepositoryFirebaseImpl(preferences, storefront, remoteDatasource, adminCustomer)
