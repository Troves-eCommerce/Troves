package com.troves.domain.usecase.settings

import com.troves.domain.repository.UserPreferencesRepository

class SetThemeModeUseCase(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(isDark: Boolean) =
        repository.setThemeMode(if (isDark) "dark" else "light")
}