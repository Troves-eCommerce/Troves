package com.troves.domain.usecase.survey

import com.troves.domain.repository.AuthenticationRepository

import com.troves.domain.entity.SurveyAnswers
import com.troves.domain.utils.Result

class CompleteSurveyUseCase(
    private val repository: AuthenticationRepository
) {
    suspend operator fun invoke(answers: SurveyAnswers): Result<Unit> = repository.saveSurveyAnswers(answers)
}
