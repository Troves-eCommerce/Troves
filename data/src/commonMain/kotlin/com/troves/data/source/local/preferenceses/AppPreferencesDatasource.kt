package com.troves.data.source.local.preferenceses

import kotlinx.coroutines.flow.Flow

interface TrovesPreferences {
    val isOnboardingDone: Flow<Boolean>
    val isLoggedIn: Flow<Boolean>
    val selectedLanguage: Flow<String>

    val selectedCurrency: Flow<String>
    val themeMode: Flow<String>
    val authToken: Flow<String?>
    val newsRefreshInterval: Flow<Int>

    val shopifyCustomerAccessToken: Flow<String>
    val shopifyCustomerAccessTokenExpiring: Flow<Long>

    val shopifyCustomerAccessTokenOrNull: Flow<String?>

    val cartId: Flow<String?>



    suspend fun setShopifyCustomerAccessToken(accessToken: String)
    suspend fun clearShopifyCustomerAccessToken()

    suspend fun setCartId(cartId: String)
    suspend fun clearCartId()


    suspend fun setOnboardingDone(done: Boolean)
    suspend fun setLoggedIn(loggedIn: Boolean)
    suspend fun setSelectedLanguage(language: String)
    suspend fun setSelectedCurrency(currency: String)
    suspend fun setThemeMode(mode: String)
    suspend fun saveAuthToken(token: String)
    suspend fun clearAuthToken()
    suspend fun setNewsRefreshInterval(minutes: Int)
    suspend fun clearAll()
}