package com.troves.domain.usecase.survey

import com.troves.domain.repository.AuthenticationRepository

class CompleteSurveyUseCase(
    private val repository: AuthenticationRepository
) {
    suspend operator fun invoke() = repository.setSurveyDone()
}
