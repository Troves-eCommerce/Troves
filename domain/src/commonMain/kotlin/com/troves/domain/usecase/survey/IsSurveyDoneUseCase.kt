package com.troves.domain.usecase.survey

import com.troves.domain.repository.AuthenticationRepository

class IsSurveyDoneUseCase(
    private val repository: AuthenticationRepository
) {
    suspend operator fun invoke(): Boolean = repository.isSurveyDone()
}
