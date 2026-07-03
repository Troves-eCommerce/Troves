package com.troves.domain.usecase.settings

import com.troves.domain.repository.TrovesRepository

class SetLanguageUseCase(
    private val repository: TrovesRepository
) {
    suspend operator fun invoke(language: String) =
        repository.setSelectedLanguage(language)
}