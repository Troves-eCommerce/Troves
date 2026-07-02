package com.troves.domain.usecase.settings

import com.troves.domain.repository.TrovesRepository

class SetThemeModeUseCase(
    private val repository: TrovesRepository
) {
    suspend operator fun invoke(isDark: Boolean) =
        repository.setThemeMode(if (isDark) "dark" else "light")
}