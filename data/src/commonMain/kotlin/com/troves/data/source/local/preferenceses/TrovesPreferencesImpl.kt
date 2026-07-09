package com.troves.data.source.local.preferenceses

import kotlinx.io.IOException

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

    override val exchangeRatesJson: Flow<String?>
        get() = dataStore.data
            .catchIOException()
            .map { it[AppPreferencesKeys.EXCHANGE_RATES_JSON] }

    override suspend fun setExchangeRatesJson(json: String) {
        dataStore.edit { it[AppPreferencesKeys.EXCHANGE_RATES_JSON] = json }
    }

    override suspend fun setShopifyCustomerAccessToken(accessToken: String) {
        dataStore.edit {
            it[AppPreferencesKeys.SHOPIFY_ACCESS_TOKEN_KEY] = accessToken
        }
    }

    override val isOnboardingDone: Flow<Boolean>
        get() = dataStore.data
            .catchIOException()
            .map { it[AppPreferencesKeys.IS_ONBOARDING_DONE] ?: false }

    override val surveyBannerDismissedUids: Flow<Set<String>>
        get() = dataStore.data
            .catchIOException()
            .map { it[AppPreferencesKeys.SURVEY_BANNER_DISMISSED_UIDS] ?: emptySet() }

    override val isCartHintShown: Flow<Boolean>
        get() = dataStore.data
            .catchIOException()
            .map { it[AppPreferencesKeys.IS_CART_HINT_SHOWN] ?: false }

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
                it[AppPreferencesKeys.SELECTED_CURRENCY] ?: "EGP"
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

    override suspend fun addSurveyBannerDismissedUid(userId: String) {
        dataStore.edit {
            val current = it[AppPreferencesKeys.SURVEY_BANNER_DISMISSED_UIDS] ?: emptySet()
            it[AppPreferencesKeys.SURVEY_BANNER_DISMISSED_UIDS] = current + userId
        }
    }

    override suspend fun setCartHintShown(shown: Boolean) {
        dataStore.edit { it[AppPreferencesKeys.IS_CART_HINT_SHOWN] = shown }
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

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getOrCreateDeviceId(): String {
        val existing = dataStore.data
            .catchIOException()
            .map { it[AppPreferencesKeys.AI_DEVICE_ID] }
            .first()
        if (existing != null) return existing
        val generated = Uuid.random().toString()
        dataStore.edit { it[AppPreferencesKeys.AI_DEVICE_ID] = generated }
        return generated
    }

    private fun Flow<Preferences>.catchIOException() =
        catch { e ->
            if (e is IOException) emit(emptyPreferences())
            else throw e
        }
}