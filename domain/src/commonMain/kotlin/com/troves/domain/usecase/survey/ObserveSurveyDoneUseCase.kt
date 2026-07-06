package com.troves.domain.usecase.survey

import com.troves.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.Flow

class ObserveSurveyDoneUseCase(
    private val repository: AuthenticationRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.isSurveyDoneStream
}
