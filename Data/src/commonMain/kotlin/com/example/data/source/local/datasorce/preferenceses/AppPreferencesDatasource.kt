package com.example.data.source.local.datasorce.preferenceses

import kotlinx.coroutines.flow.Flow

interface AppPreferencesDataSource {
    val isOnboardingDone: Flow<Boolean>
    val selectedLanguage: Flow<String>
    val themeMode: Flow<String>
    val authToken: Flow<String?>
    val newsRefreshInterval: Flow<Int>

    suspend fun setOnboardingDone(done: Boolean)
    suspend fun setSelectedLanguage(language: String)
    suspend fun setThemeMode(mode: String)
    suspend fun saveAuthToken(token: String)
    suspend fun clearAuthToken()
    suspend fun setNewsRefreshInterval(minutes: Int)
    suspend fun clearAll()
}