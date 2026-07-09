package com.troves.domain.usecase.home

import com.troves.domain.entity.SurveyAnswers
import com.troves.domain.entity.SurveyRecommendedItem
import com.troves.domain.repository.TrovesRepository
import com.troves.domain.utils.Result

/**
 * Fetches AI-powered product recommendations based on the user's survey answers.
 *
 * The backing endpoint is a placeholder until the real backend URL is wired in.
 * When it returns an empty list, the "Your Troves" section is simply hidden on
 * the Home screen — no error state is shown.
 */
class GetSurveyRecommendationsUseCase(
    private val repository: TrovesRepository,
) {
    suspend operator fun invoke(surveyAnswers: SurveyAnswers): Result<List<SurveyRecommendedItem>> =
        repository.getSurveyRecommendations(surveyAnswers)
}
