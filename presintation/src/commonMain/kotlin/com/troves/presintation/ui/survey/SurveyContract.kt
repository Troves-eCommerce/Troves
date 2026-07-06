package com.troves.presintation.ui.survey

import androidx.compose.ui.graphics.Color

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
    data class ShowError(val message: String) : SurveyEffect
}

fun buildSurveyQuestions(): List<SurveyQuestion> = listOf(
    // Step 0 — Categories
    SurveyQuestion.MultiChip(
        title = "What do you love to shop?",
        subtitle = "Pick everything that matches your style",
        options = listOf("Shoes", "T-Shirts", "Hoodies", "Jackets", "Accessories", "Pants", "Dresses", "Bags"),
    ),
    // Step 1 — Price Range
    SurveyQuestion.SingleChip(
        title = "What's your budget?",
        subtitle = "We'll personalise picks to fit your wallet",
        options = listOf("Budget", "Mid-range", "Premium", "Luxury"),
    ),
    // Step 2 — Style
    SurveyQuestion.StyleCards(
        title = "Describe your style",
        subtitle = "Pick the vibe that fits you best",
        options = listOf(
            StyleOption(StyleIcon.Casual, "Casual", "Everyday comfort and ease"),
            StyleOption(StyleIcon.Sport, "Sport", "Performance meets style"),
            StyleOption(StyleIcon.Streetwear, "Streetwear", "Bold, urban expression"),
            StyleOption(StyleIcon.Elegant, "Elegant", "Refined and sophisticated"),
            StyleOption(StyleIcon.Minimal, "Minimal", "Clean lines, simple palette"),
            StyleOption(StyleIcon.Formal, "Formal", "Sharp and professional"),
        ),
    ),
    // Step 3 — Gender
    SurveyQuestion.SingleChip(
        title = "Shop for …",
        subtitle = null,
        options = listOf("Men", "Women", "Unisex"),
    ),
)
