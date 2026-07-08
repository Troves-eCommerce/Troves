package com.troves.data.source.local.preferenceses

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

internal object AppPreferencesKeys {
    val IS_ONBOARDING_DONE    = booleanPreferencesKey("is_onboarding_done")
    val IS_SURVEY_DONE         = booleanPreferencesKey("is_survey_done")
    val IS_CART_HINT_SHOWN    = booleanPreferencesKey("is_cart_hint_shown")
    val IS_LOGGED_IN          = booleanPreferencesKey("is_logged_in")
    val SELECTED_LANGUAGE     = stringPreferencesKey("selected_language")
    val THEME_MODE            = stringPreferencesKey("theme_mode")
    val AUTH_TOKEN            = stringPreferencesKey("auth_token")
    val LAST_SYNCED_AT        = stringPreferencesKey("last_synced_at")
    val REFRESH_INTERVAL = intPreferencesKey("news_refresh_interval_minutes")
    val SELECTED_CURRENCY = stringPreferencesKey("selected_currency")
    val EXCHANGE_RATES_JSON = stringPreferencesKey("exchange_rates_json")

    val SHOPIFY_ACCESS_TOKEN_KEY            = stringPreferencesKey("SHOPIFY_ACCESS_TOKEN_KEY")
    val SHOPIFY_ACCESS_TOKEN_KEY_EXPIRING   = longPreferencesKey("SHOPIFY_ACCESS_TOKEN_KEY_EXPIRING")
    val SHOPIFY_CART_ID                     = stringPreferencesKey("shopify_cart_id")

    val AI_DEVICE_ID                        = stringPreferencesKey("ai_device_id")
}