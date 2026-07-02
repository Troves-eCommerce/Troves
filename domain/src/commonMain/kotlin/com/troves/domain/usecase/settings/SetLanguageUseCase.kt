package com.troves.domain.usecase.settings

import com.troves.domain.repository.UserPreferencesRepository

class SetLanguageUseCase(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(language: String) =
        repository.setSelectedLanguage(language)
}