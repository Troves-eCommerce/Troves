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
import org.jetbrains.compose.resources.getString
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.survey_save_failed

class SurveyViewModel(
    private val isSurveyDone: IsSurveyDoneUseCase,
    private val completeSurvey: CompleteSurveyUseCase,
) : ViewModel(),
    StateHolder<SurveyUiState> by DefaultStateHolder(SurveyUiState(questions = buildSurveyQuestions())),
    EffectPublisher<SurveyEffect> by DefaultEffectPublisher() {

    fun onIntent(intent: SurveyIntent) {
        when (intent) {
            SurveyIntent.NextStep -> advanceStep()
            SurveyIntent.PreviousStep -> retreatStep()
            SurveyIntent.Submit -> submit()
            is SurveyIntent.ToggleMultiOption -> toggleMulti(intent.option)
            is SurveyIntent.SelectSingleOption -> selectSingle(intent.option)
            is SurveyIntent.ToggleColor -> toggleColor(intent.colorName)
            SurveyIntent.Reset -> updateState { copy(currentStep = 0, answers = emptyMap(), isSubmitting = false) }
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
            val answersEntity = currentState.answers.toDomainEntity()
            val result = completeSurvey(answersEntity)
            updateState { copy(isSubmitting = false) }

            when (result) {
                is com.troves.domain.utils.Result.Success -> {
                    updateState { copy(currentStep = questions.size) }
                    sendEffect(SurveyEffect.SurveyCompleted)
                }
                is com.troves.domain.utils.Result.Error -> {
                    sendEffect(SurveyEffect.ShowError(result.throwable.message ?: getString(Res.string.survey_save_failed)))
                }
                is com.troves.domain.utils.Result.Loading -> Unit
            }
        }
    }

}
