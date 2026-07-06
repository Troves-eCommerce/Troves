package com.troves.presintation.ui.survey

import androidx.compose.ui.graphics.Color

data class SurveyUiState(
    val questions: List<SurveyQuestion> = emptyList(),
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
    val title: String
    val subtitle: String?

    data class MultiChip(
        override val title: String,
        override val subtitle: String? = null,
        val options: List<String>,
    ) : SurveyQuestion

    data class SingleChip(
        override val title: String,
        override val subtitle: String? = null,
        val options: List<String>,
    ) : SurveyQuestion

    data class StyleCards(
        override val title: String,
        override val subtitle: String? = null,
        val options: List<StyleOption>,
    ) : SurveyQuestion

    data class ColorPicker(
        override val title: String,
        override val subtitle: String? = null,
        val colors: List<ColorOption>,
    ) : SurveyQuestion
}

data class StyleOption(
    val icon: StyleIcon,
    val label: String,
    val description: String,
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
}
