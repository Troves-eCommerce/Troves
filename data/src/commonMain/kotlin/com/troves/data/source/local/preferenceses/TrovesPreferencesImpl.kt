package com.troves.data.source.local.preferenceses

import kotlinx.io.IOException

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class TrovesPreferencesImpl(
    private val dataStore: DataStore<Preferences>,
) : TrovesPreferences {

    // ── Reads ──────────────────────────────────────────────────────────
    override val shopifyCustomerAccessToken: Flow<String>
        get() {
            return dataStore
                .data
                .catchIOException()
                .map {
                    it[AppPreferencesKeys.SHOPIFY_ACCESS_TOKEN_KEY]
                        ?: error("SHOPIFY_ACCESS_TOKEN_KEY is null")
                }
        }
    override val shopifyCustomerAccessTokenExpiring: Flow<Long>
        get() {
            return dataStore
                .data
                .catchIOException()
                .map {
                    it[AppPreferencesKeys.SHOPIFY_ACCESS_TOKEN_KEY_EXPIRING]
                        ?: error("SHOPIFY_ACCESS_TOKEN_KEY_EXPIRING is null")
                }
        }

    override val shopifyCustomerAccessTokenOrNull: Flow<String?>
        get() = dataStore.data
            .catchIOException()
            .map { it[AppPreferencesKeys.SHOPIFY_ACCESS_TOKEN_KEY] }

    override val cartId: Flow<String?>
        get() = dataStore.data
            .catchIOException()
            .map { it[AppPreferencesKeys.SHOPIFY_CART_ID] }

    override val isOnboardingDone: Flow<Boolean>
        get() = dataStore.data
            .catchIOException()
            .map { it[AppPreferencesKeys.IS_ONBOARDING_DONE] ?: false }

    override val isLoggedIn: Flow<Boolean>
        get() = dataStore.data
            .catchIOException()
            .map { it[AppPreferencesKeys.IS_LOGGED_IN] ?: false }

    override val selectedLanguage: Flow<String>
        get() = dataStore.data
            .catchIOException()
            .map { it[AppPreferencesKeys.SELECTED_LANGUAGE] ?: "en" }
    override val selectedCurrency =
        dataStore.data
            .catchIOException()
            .map {
                it[AppPreferencesKeys.SELECTED_CURRENCY] ?: "USD"
            }
    override val themeMode: Flow<String>
        get() = dataStore.data
            .catchIOException()
            .map { it[AppPreferencesKeys.THEME_MODE] ?: "system" }

    override val authToken: Flow<String?>
        get() = dataStore.data
            .catchIOException()
            .map { it[AppPreferencesKeys.AUTH_TOKEN] }

    override val newsRefreshInterval: Flow<Int>
        get() = dataStore.data
            .catchIOException()
            .map { it[AppPreferencesKeys.REFRESH_INTERVAL] ?: 30 }

    // ── Writes ─────────────────────────────────────────────────────────


    override suspend fun setShopifyCustomerAccessToken(accessToken: String,accessTokenTimestamp: Long) {
        dataStore.edit {
            it[AppPreferencesKeys.SHOPIFY_ACCESS_TOKEN_KEY] = accessToken
            it[AppPreferencesKeys.SHOPIFY_ACCESS_TOKEN_KEY_EXPIRING] = accessTokenTimestamp
        }
    }

    override suspend fun clearShopifyCustomerAccessToken() {
        dataStore.edit {
            it.remove(AppPreferencesKeys.SHOPIFY_ACCESS_TOKEN_KEY)
            it.remove(AppPreferencesKeys.SHOPIFY_ACCESS_TOKEN_KEY_EXPIRING)
        }
    }

    override suspend fun setCartId(cartId: String) {
        dataStore.edit { it[AppPreferencesKeys.SHOPIFY_CART_ID] = cartId }
    }

    override suspend fun clearCartId() {
        dataStore.edit { it.remove(AppPreferencesKeys.SHOPIFY_CART_ID) }
    }

    override suspend fun setOnboardingDone(done: Boolean) {
        dataStore.edit { it[AppPreferencesKeys.IS_ONBOARDING_DONE] = done }
    }

    override suspend fun setLoggedIn(loggedIn: Boolean) {
        dataStore.edit { it[AppPreferencesKeys.IS_LOGGED_IN] = loggedIn }
    }

    override suspend fun setSelectedLanguage(language: String) {
        dataStore.edit { it[AppPreferencesKeys.SELECTED_LANGUAGE] = language }
    }

    override suspend fun setSelectedCurrency(currency: String) {
        dataStore.edit {
            it[AppPreferencesKeys.SELECTED_CURRENCY] = currency
        }
    }

    override suspend fun setThemeMode(mode: String) {
        dataStore.edit { it[AppPreferencesKeys.THEME_MODE] = mode }
    }

    override suspend fun saveAuthToken(token: String) {
        dataStore.edit { it[AppPreferencesKeys.AUTH_TOKEN] = token }
    }

    override suspend fun clearAuthToken() {
        dataStore.edit { it.remove(AppPreferencesKeys.AUTH_TOKEN) }
    }

    override suspend fun setNewsRefreshInterval(minutes: Int) {
        dataStore.edit { it[AppPreferencesKeys.REFRESH_INTERVAL] = minutes }
    }

    override suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }

    private fun Flow<Preferences>.catchIOException() =
        catch { e ->
            if (e is IOException) emit(emptyPreferences())
            else throw e
        }
}