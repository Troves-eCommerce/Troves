package com.troves.presintation.ui.survey

import com.troves.domain.entity.SurveyAnswers

fun Map<Int, SurveyAnswer>.toDomainEntity(): SurveyAnswers {
    // Assuming the structure matches SurveyViewModel.buildQuestions()
    // 0: Category (MultiChip)
    // 1: Brands (MultiChip)
    // 2: Price Range (SingleChip)
    // 3: Shopping Style (StyleCards)
    // 4: Colors (ColorPicker)
    // 5: Gender (SingleChip)
    // 6: Age Group (SingleChip)
    // 7: Shopping Frequency (SingleChip)

    val favoriteCategories = (this[0] as? SurveyAnswer.MultiSelection)?.selected?.toList() ?: emptyList()
    val favoriteBrands = (this[1] as? SurveyAnswer.MultiSelection)?.selected?.toList() ?: emptyList()
    val preferredPriceRange = (this[2] as? SurveyAnswer.SingleSelection)?.selected ?: ""
    val shoppingStyle = (this[3] as? SurveyAnswer.SingleSelection)?.selected ?: ""
    val favoriteColors = (this[4] as? SurveyAnswer.ColorSelection)?.selected?.toList() ?: emptyList()
    val gender = (this[5] as? SurveyAnswer.SingleSelection)?.selected ?: ""
    val ageGroup = (this[6] as? SurveyAnswer.SingleSelection)?.selected ?: ""
    val shoppingFrequency = (this[7] as? SurveyAnswer.SingleSelection)?.selected ?: ""

    return SurveyAnswers(
        favoriteCategories = favoriteCategories,
        favoriteBrands = favoriteBrands,
        preferredPriceRange = preferredPriceRange,
        shoppingStyle = shoppingStyle,
        favoriteColors = favoriteColors,
        gender = gender,
        ageGroup = ageGroup,
        shoppingFrequency = shoppingFrequency,
        completed = true,
    )
}
