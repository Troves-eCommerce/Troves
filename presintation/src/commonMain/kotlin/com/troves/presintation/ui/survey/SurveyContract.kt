package com.troves.presintation.ui.survey

import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.StringResource
import troves.presintation.generated.resources.*

data class SurveyUiState(
    val questions: List<SurveyQuestion> = buildSurveyQuestions(),
    val currentStep: Int = 0,
    val answers: Map<Int, SurveyAnswer> = emptyMap(),
    val isSubmitting: Boolean = false,
) {
    val totalSteps: Int get() = questions.size
    val currentQuestion: SurveyQuestion? get() = questions.getOrNull(currentStep)
    val isLastStep: Boolean get() = currentStep == questions.lastIndex
    val isCurrentAnswered: Boolean get() = answers.containsKey(currentStep)
    val isCompleted: Boolean get() = totalSteps > 0 && currentStep >= totalSteps
}

sealed interface SurveyQuestion {
    val title: StringResource
    val subtitle: StringResource?

    data class MultiChip(
        override val title: StringResource,
        override val subtitle: StringResource? = null,
        val options: List<SurveyOption>,
    ) : SurveyQuestion

    data class SingleChip(
        override val title: StringResource,
        override val subtitle: StringResource? = null,
        val options: List<SurveyOption>,
    ) : SurveyQuestion

    data class StyleCards(
        override val title: StringResource,
        override val subtitle: StringResource? = null,
        val options: List<StyleOption>,
    ) : SurveyQuestion

    data class ColorPicker(
        override val title: StringResource,
        override val subtitle: StringResource? = null,
        val colors: List<ColorOption>,
    ) : SurveyQuestion
}

data class SurveyOption(val id: String, val label: StringResource)

data class StyleOption(
    val id: String,
    val icon: StyleIcon,
    val label: StringResource,
    val description: StringResource,
)

enum class StyleIcon {
    Casual, Sport, Streetwear, Elegant, Minimal, Formal
}

data class ColorOption(
    val name: String,
    val color: Color,
)

sealed interface SurveyAnswer {
    data class MultiSelection(val selected: Set<String>) : SurveyAnswer
    data class SingleSelection(val selected: String) : SurveyAnswer
    data class ColorSelection(val selected: Set<String>) : SurveyAnswer
}

sealed interface SurveyIntent {
    data object NextStep : SurveyIntent
    data object PreviousStep : SurveyIntent
    data object Submit : SurveyIntent
    data class ToggleMultiOption(val option: String) : SurveyIntent
    data class SelectSingleOption(val option: String) : SurveyIntent
    data class ToggleColor(val colorName: String) : SurveyIntent
}

sealed interface SurveyEffect {
    data object SurveyCompleted : SurveyEffect
    data object NavigateBack : SurveyEffect
    data class ShowError(val message: String) : SurveyEffect
}

fun buildSurveyQuestions(): List<SurveyQuestion> = listOf(
    // Step 0 — Categories
    SurveyQuestion.MultiChip(
        title = Res.string.survey_categories_title,
        subtitle = Res.string.survey_categories_subtitle,
        options = listOf(SurveyOption("Shoes", Res.string.survey_opt_shoes), SurveyOption("T-Shirts", Res.string.survey_opt_tshirts), SurveyOption("Hoodies", Res.string.survey_opt_hoodies), SurveyOption("Jackets", Res.string.survey_opt_jackets), SurveyOption("Accessories", Res.string.survey_opt_accessories), SurveyOption("Pants", Res.string.survey_opt_pants), SurveyOption("Dresses", Res.string.survey_opt_dresses), SurveyOption("Bags", Res.string.survey_opt_bags)),
    ),
    // Step 1 — Price Range
    SurveyQuestion.SingleChip(
        title = Res.string.survey_budget_title,
        subtitle = Res.string.survey_budget_subtitle,
        options = listOf(SurveyOption("Budget", Res.string.survey_opt_budget), SurveyOption("Mid-range", Res.string.survey_opt_midrange), SurveyOption("Premium", Res.string.survey_opt_premium), SurveyOption("Luxury", Res.string.survey_opt_luxury)),
    ),
    // Step 2 — Style
    SurveyQuestion.StyleCards(
        title = Res.string.survey_style_title,
        subtitle = Res.string.survey_style_subtitle,
        options = listOf(
            StyleOption("Casual", StyleIcon.Casual, Res.string.survey_style_casual, Res.string.survey_style_casual_desc),
            StyleOption("Sport", StyleIcon.Sport, Res.string.survey_style_sport, Res.string.survey_style_sport_desc),
            StyleOption("Streetwear", StyleIcon.Streetwear, Res.string.survey_style_streetwear, Res.string.survey_style_streetwear_desc),
            StyleOption("Elegant", StyleIcon.Elegant, Res.string.survey_style_elegant, Res.string.survey_style_elegant_desc),
            StyleOption("Minimal", StyleIcon.Minimal, Res.string.survey_style_minimal, Res.string.survey_style_minimal_desc),
            StyleOption("Formal", StyleIcon.Formal, Res.string.survey_style_formal, Res.string.survey_style_formal_desc),
        ),
    ),
    // Step 3 — Gender
    SurveyQuestion.SingleChip(
        title = Res.string.survey_gender_title,
        subtitle = null,
        options = listOf(SurveyOption("Men", Res.string.survey_opt_men), SurveyOption("Women", Res.string.survey_opt_women)),
    ),

)
