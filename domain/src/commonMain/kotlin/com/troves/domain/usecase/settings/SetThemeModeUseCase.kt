package com.troves.domain.usecase.settings

import com.troves.domain.repository.TrovesRepository

class SetThemeModeUseCase(
    private val repository: TrovesRepository
) {
    suspend operator fun invoke(mode: String) =
        repository.setThemeMode(mode)
}