package com.troves.data.mapper

import com.troves.data.source.remote.dto.SurveyAnswersDto
import com.troves.domain.entity.SurveyAnswers

fun SurveyAnswers.toDto(): SurveyAnswersDto {
    return SurveyAnswersDto(
        favoriteCategories = this.favoriteCategories,
        favoriteBrands = this.favoriteBrands,
        preferredPriceRange = this.preferredPriceRange,
        shoppingStyle = this.shoppingStyle,
        favoriteColors = this.favoriteColors,
        gender = this.gender,
        ageGroup = this.ageGroup,
        shoppingFrequency = this.shoppingFrequency,
        completed = this.completed,
        completedAt = this.completedAt?.takeIf { it.isNotBlank() },
    )
}

fun SurveyAnswersDto.toDomain(): SurveyAnswers {
    return SurveyAnswers(
        favoriteCategories = this.favoriteCategories,
        favoriteBrands = this.favoriteBrands,
        preferredPriceRange = this.preferredPriceRange,
        shoppingStyle = this.shoppingStyle,
        favoriteColors = this.favoriteColors,
        gender = this.gender,
        ageGroup = this.ageGroup,
        shoppingFrequency = this.shoppingFrequency,
        completed = this.completed,
        // Documents written before the timestamp fix carry "" here, which the
        // AI endpoint rejects as an invalid datetime.
        completedAt = this.completedAt?.takeIf { it.isNotBlank() },
    )
}
