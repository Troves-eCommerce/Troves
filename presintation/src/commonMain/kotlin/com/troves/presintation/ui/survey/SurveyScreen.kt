package com.troves.presintation.ui.survey

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.troves.designsystem.theme.Theme
import com.troves.presintation.core.mvi.ObserveEffect
import com.troves.presintation.ui.survey.components.SurveyBottomBar
import com.troves.presintation.ui.survey.components.SurveyProgressIndicator
import com.troves.presintation.ui.survey.components.SurveyQuestionHeader
import com.troves.presintation.ui.survey.steps.AgeGroupStep
import com.troves.presintation.ui.survey.steps.BrandStep
import com.troves.presintation.ui.survey.steps.CategoryStep
import com.troves.presintation.ui.survey.steps.ConfirmationStep
import com.troves.presintation.ui.survey.steps.FavoriteColorsStep
import com.troves.presintation.ui.survey.steps.GenderStep
import com.troves.presintation.ui.survey.steps.PriceRangeStep
import com.troves.presintation.ui.survey.steps.ShoppingFrequencyStep
import com.troves.presintation.ui.survey.steps.ShoppingStyleStep
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SurveyScreen(
    onNavigateBack: () -> Unit,
    viewModel: SurveyViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var errorMessage by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            SurveyEffect.SurveyCompleted -> Unit
            SurveyEffect.NavigateBack -> onNavigateBack()
            is SurveyEffect.ShowError -> errorMessage = effect.message
        }
    }

    if (errorMessage != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { androidx.compose.material3.Text("Error") },
            text = { androidx.compose.material3.Text(errorMessage!!) },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = { errorMessage = null }) {
                    androidx.compose.material3.Text("OK")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        if (state.isCompleted) {
            ConfirmationStep(
                onContinue = onNavigateBack,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Theme.spacing.large),
            )
        } else {
            AnimatedContent(
                targetState = state.currentStep,
                transitionSpec = {
                    val forward = targetState > initialState
                    val enter = fadeIn(tween(150)) + slideInHorizontally(tween(150)) {
                        if (forward) (it * 0.08f).toInt() else -(it * 0.08f).toInt()
                    }
                    val exit = fadeOut(tween(150)) + slideOutHorizontally(tween(150)) {
                        if (forward) -(it * 0.08f).toInt() else (it * 0.08f).toInt()
                    }
                    enter togetherWith exit
                },
                label = "surveyStepTransition",
                modifier = Modifier.weight(1f),
            ) { step ->
                val question = state.questions.getOrNull(step)
                val answer = state.answers[step]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = Theme.spacing.large),
                ) {
                    Spacer(Modifier.height(Theme.spacing.large))

                    SurveyProgressIndicator(
                        currentStep = step,
                        totalSteps = state.totalSteps,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )

                    Spacer(Modifier.height(Theme.spacing.large))

                    if (question != null) {
                        SurveyQuestionHeader(
                            currentStep = step,
                            totalSteps = state.totalSteps,
                            title = question.title,
                            subtitle = question.subtitle,
                        )
                    }

                    Spacer(Modifier.height(Theme.spacing.extraLarge))

                    when (question) {
                        is SurveyQuestion.MultiChip -> when (step) {
                            0 -> CategoryStep(question, answer, viewModel::onIntent, Modifier.fillMaxWidth())
                            else -> BrandStep(question, answer, viewModel::onIntent, Modifier.fillMaxWidth())
                        }
                        is SurveyQuestion.SingleChip -> when (step) {
                            2 -> PriceRangeStep(question, answer, viewModel::onIntent, Modifier.fillMaxWidth())
                            5 -> GenderStep(question, answer, viewModel::onIntent, Modifier.fillMaxWidth())
                            6 -> AgeGroupStep(question, answer, viewModel::onIntent, Modifier.fillMaxWidth())
                            else -> ShoppingFrequencyStep(question, answer, viewModel::onIntent, Modifier.fillMaxWidth())
                        }
                        is SurveyQuestion.StyleCards -> ShoppingStyleStep(question, answer, viewModel::onIntent, Modifier.fillMaxWidth())
                        is SurveyQuestion.ColorPicker -> FavoriteColorsStep(question, answer, viewModel::onIntent, Modifier.fillMaxWidth())
                        null -> Unit
                    }

                    Spacer(Modifier.height(Theme.spacing.extraLarge))
                }
            }

            SurveyBottomBar(
                isLastStep = state.isLastStep,
                isAnswered = state.isCurrentAnswered,
                isSubmitting = state.isSubmitting,
                canGoBack = true,
                onNext = { viewModel.onIntent(SurveyIntent.NextStep) },
                onBack = { viewModel.onIntent(SurveyIntent.PreviousStep) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Theme.spacing.large, vertical = Theme.spacing.medium),
            )
        }
    }
}
