package com.troves.domain.usecase.home

import com.troves.domain.entity.SurveyRecommendedItem
import com.troves.domain.repository.TrovesRepository
import com.troves.domain.utils.Result

/**
 * Fetches AI-powered product recommendations from the user's saved survey.
 *
 * Returns an empty list when the user hasn't taken the survey, in which case the
 * "Your Troves" section is simply hidden on the Home screen — no error state.
 */
class GetSurveyRecommendationsUseCase(
    private val repository: TrovesRepository,
) {
    suspend operator fun invoke(): Result<List<SurveyRecommendedItem>> =
        repository.getSurveyRecommendations()
}
