package com.example.data.source.local.datasorce.preferenceses

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

internal object AppPreferencesKeys {
    val IS_ONBOARDING_DONE    = booleanPreferencesKey("is_onboarding_done")
    val SELECTED_LANGUAGE     = stringPreferencesKey("selected_language")
    val THEME_MODE            = stringPreferencesKey("theme_mode")
    val AUTH_TOKEN            = stringPreferencesKey("auth_token")
    val LAST_SYNCED_AT        = stringPreferencesKey("last_synced_at")
    val REFRESH_INTERVAL = intPreferencesKey("news_refresh_interval_minutes")
}