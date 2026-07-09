package com.troves.presintation.ui.survey

import com.troves.domain.entity.SurveyAnswers

/**
 * Answers are stored keyed by step index, so they must be resolved through
 * [questions] rather than by hardcoded positions — otherwise reordering or
 * adding a question silently maps answers onto the wrong fields.
 */
fun Map<Int, SurveyAnswer>.toDomainEntity(
    questions: List<SurveyQuestion>,
    completedAt: String,
): SurveyAnswers {
    fun answerFor(key: SurveyKey): SurveyAnswer? =
        questions.indexOfFirst { it.key == key }
            .takeIf { it >= 0 }
            ?.let { this[it] }

    fun multi(key: SurveyKey): List<String> = when (val answer = answerFor(key)) {
        is SurveyAnswer.MultiSelection -> answer.selected.toList()
        is SurveyAnswer.ColorSelection -> answer.selected.toList()
        else -> emptyList()
    }

    fun single(key: SurveyKey): String =
        (answerFor(key) as? SurveyAnswer.SingleSelection)?.selected.orEmpty()

    return SurveyAnswers(
        favoriteCategories = multi(SurveyKey.Categories),
        favoriteBrands = multi(SurveyKey.Brands),
        preferredPriceRange = single(SurveyKey.PriceRange),
        shoppingStyle = single(SurveyKey.Style),
        favoriteColors = multi(SurveyKey.Colors),
        gender = single(SurveyKey.Gender),
        ageGroup = single(SurveyKey.AgeGroup),
        shoppingFrequency = single(SurveyKey.ShoppingFrequency),
        completed = true,
        completedAt = completedAt,
    )
}
