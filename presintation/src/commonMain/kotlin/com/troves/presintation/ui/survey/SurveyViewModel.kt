package com.troves.presintation.ui.survey

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.usecase.survey.CompleteSurveyUseCase
import com.troves.domain.usecase.survey.IsSurveyDoneUseCase
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.launch

class SurveyViewModel(
    private val isSurveyDone: IsSurveyDoneUseCase,
    private val completeSurvey: CompleteSurveyUseCase,
) : ViewModel(),
    StateHolder<SurveyUiState> by DefaultStateHolder(SurveyUiState()),
    EffectPublisher<SurveyEffect> by DefaultEffectPublisher() {

    init {
        updateState { copy(questions = buildQuestions()) }
    }

    fun onIntent(intent: SurveyIntent) {
        when (intent) {
            SurveyIntent.NextStep -> advanceStep()
            SurveyIntent.PreviousStep -> retreatStep()
            SurveyIntent.Submit -> submit()
            is SurveyIntent.ToggleMultiOption -> toggleMulti(intent.option)
            is SurveyIntent.SelectSingleOption -> selectSingle(intent.option)
            is SurveyIntent.ToggleColor -> toggleColor(intent.colorName)
        }
    }

    private fun advanceStep() {
        val step = currentState.currentStep
        if (!currentState.isCurrentAnswered) return
        if (currentState.isLastStep) {
            submit()
        } else {
            updateState { copy(currentStep = step + 1) }
        }
    }

    private fun retreatStep() {
        val step = currentState.currentStep
        if (step > 0) {
            updateState { copy(currentStep = step - 1) }
        } else {
            sendEffect(SurveyEffect.NavigateBack)
        }
    }

    private fun toggleMulti(option: String) {
        val step = currentState.currentStep
        val current = (currentState.answers[step] as? SurveyAnswer.MultiSelection)?.selected ?: emptySet()
        val updated = if (option in current) current - option else current + option
        updateState {
            copy(answers = answers + (step to SurveyAnswer.MultiSelection(updated)))
        }
    }

    private fun selectSingle(option: String) {
        val step = currentState.currentStep
        updateState {
            copy(answers = answers + (step to SurveyAnswer.SingleSelection(option)))
        }
    }

    private fun toggleColor(colorName: String) {
        val step = currentState.currentStep
        val current = (currentState.answers[step] as? SurveyAnswer.ColorSelection)?.selected ?: emptySet()
        val updated = if (colorName in current) current - colorName else current + colorName
        updateState {
            copy(answers = answers + (step to SurveyAnswer.ColorSelection(updated)))
        }
    }

    private fun submit() {
        viewModelScope.launch {
            updateState { copy(isSubmitting = true) }
            completeSurvey()
            updateState { copy(isSubmitting = false, currentStep = questions.size) }
            sendEffect(SurveyEffect.SurveyCompleted)
        }
    }

    private fun buildQuestions(): List<SurveyQuestion> = listOf(
        SurveyQuestion.MultiChip(
            title = "What do you love to shop?",
            subtitle = "Pick all categories that match your style",
            options = listOf("Shoes", "T-Shirts", "Hoodies", "Jackets", "Accessories", "Pants", "Dresses", "Bags"),
        ),
        SurveyQuestion.MultiChip(
            title = "Your favorite brands?",
            subtitle = "Select the brands you love",
            options = listOf("Nike", "Adidas", "Puma", "Zara", "H&M", "New Balance", "Vans", "Converse"),
        ),
        SurveyQuestion.SingleChip(
            title = "What's your price range?",
            subtitle = "We'll personalise recommendations to fit your budget",
            options = listOf("Budget", "Mid-range", "Premium", "Luxury"),
        ),
        SurveyQuestion.StyleCards(
            title = "How would you describe your style?",
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
        SurveyQuestion.ColorPicker(
            title = "Favourite colours?",
            subtitle = "Select the tones you wear most",
            colors = listOf(
                ColorOption("Black", Color(0xFF111111)),
                ColorOption("White", Color(0xFFF5F5F5)),
                ColorOption("Navy", Color(0xFF1A237E)),
                ColorOption("Olive", Color(0xFF558B2F)),
                ColorOption("Beige", Color(0xFFD7CCC8)),
                ColorOption("Burgundy", Color(0xFF6D1B1B)),
                ColorOption("Grey", Color(0xFF757575)),
                ColorOption("Camel", Color(0xFFC8A96E)),
                ColorOption("Sky Blue", Color(0xFF4FC3F7)),
                ColorOption("Rust", Color(0xFFBF360C)),
            ),
        ),
        SurveyQuestion.SingleChip(
            title = "Shop for …",
            subtitle = null,
            options = listOf("Men", "Women", "Unisex"),
        ),
        SurveyQuestion.SingleChip(
            title = "Your age group?",
            subtitle = null,
            options = listOf("Under 18", "18–24", "25–34", "35–44", "45+"),
        ),
        SurveyQuestion.SingleChip(
            title = "How often do you shop?",
            subtitle = "This helps us time our recommendations",
            options = listOf("Weekly", "Monthly", "Occasionally"),
        ),
    )
}
