package com.troves.data.local.preferenceses

import kotlinx.coroutines.flow.Flow

interface AppPreferencesDataSource {
    val isOnboardingDone: Flow<Boolean>
    val isLoggedIn: Flow<Boolean>
    val selectedLanguage: Flow<String>
    val themeMode: Flow<String>
    val authToken: Flow<String?>
    val newsRefreshInterval: Flow<Int>

    suspend fun setOnboardingDone(done: Boolean)
    suspend fun setLoggedIn(loggedIn: Boolean)
    suspend fun setSelectedLanguage(language: String)
    suspend fun setThemeMode(mode: String)
    suspend fun saveAuthToken(token: String)
    suspend fun clearAuthToken()
    suspend fun setNewsRefreshInterval(minutes: Int)
    suspend fun clearAll()
}