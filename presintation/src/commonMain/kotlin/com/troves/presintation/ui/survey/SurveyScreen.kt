package com.troves.presintation.ui.survey

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import org.jetbrains.compose.resources.stringResource
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.error_view_title
import troves.presintation.generated.resources.survey_finish
import troves.presintation.generated.resources.survey_next
import troves.presintation.generated.resources.survey_ok
import troves.presintation.generated.resources.survey_saving
import troves.presintation.generated.resources.survey_step_counter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.theme.Theme
import com.troves.presintation.core.mvi.ObserveEffect
import com.troves.presintation.ui.survey.components.SurveyQuestionHeader
import com.troves.presintation.ui.survey.steps.CategoryStep
import com.troves.presintation.ui.survey.steps.GenderStep
import com.troves.presintation.ui.survey.steps.PriceRangeStep
import com.troves.presintation.ui.survey.steps.ShoppingStyleStep
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/**
 * Survey presented as a ModalBottomSheet so it feels lightweight and contextual —
 * the user never fully leaves the home feed.
 *
 * 4 condensed steps (reduced from 8) to minimise survey fatigue:
 *   0 → Categories (multi-chip)
 *   1 → Budget (single-chip, auto-advances)
 *   2 → Style (style cards, auto-advances)
 *   3 → Gender (single-chip, auto-advances & submits)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyBottomSheet(
    onDismiss: () -> Unit,
    viewModel: SurveyViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // After auto-advance on single-select, we auto-submit when we reach past last step
    fun advance() = viewModel.onIntent(SurveyIntent.NextStep)

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            SurveyEffect.SurveyCompleted -> {
                scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
            }
            SurveyEffect.NavigateBack -> {
                scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
            }
            is SurveyEffect.ShowError -> errorMessage = effect.message
        }
    }

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text(stringResource(Res.string.error_view_title)) },
            text = { Text(errorMessage!!) },
            confirmButton = {
                TextButton(onClick = { errorMessage = null }) { Text(stringResource(Res.string.survey_ok)) }
            },
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Theme.colors.backGround,
        dragHandle = null,
    ) {
        if (state.isCompleted) {
            // Sheet is hiding via effect — show nothing more
            return@ModalBottomSheet
        }

        val progress = if (state.totalSteps > 0) {
            (state.currentStep + 1).toFloat() / state.totalSteps.toFloat()
        } else 0f

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
        ) {
            // ── Linear progress bar ──────────────────────────────────────
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = Theme.colors.primary,
                trackColor = Theme.colors.hint.copy(alpha = 0.2f),
                strokeCap = StrokeCap.Round,
            )

            AnimatedContent(
                targetState = state.currentStep,
                transitionSpec = {
                    val forward = targetState > initialState
                    val enter = fadeIn(tween(200)) + slideInHorizontally(tween(200)) {
                        if (forward) (it * 0.10f).toInt() else -(it * 0.10f).toInt()
                    }
                    val exit = fadeOut(tween(200)) + slideOutHorizontally(tween(200)) {
                        if (forward) -(it * 0.10f).toInt() else (it * 0.10f).toInt()
                    }
                    enter togetherWith exit
                },
                label = "surveyStepTransition",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 320.dp),
            ) { step ->
                val question = state.questions.getOrNull(step)
                val answer = state.answers[step]
                val isSingleSelect = question is SurveyQuestion.SingleChip || question is SurveyQuestion.StyleCards

                // Auto-advance on single-select after a short moment
                if (state.answers.containsKey(step) && isSingleSelect) {
                    LaunchedEffect(answer) {
                        kotlinx.coroutines.delay(220)
                        advance()
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .padding(top = 24.dp, bottom = 16.dp),
                ) {
                    // Step counter label  e.g. "1 / 4"
                    androidx.compose.foundation.text.BasicText(
                        text = stringResource(Res.string.survey_step_counter, step + 1, state.totalSteps),
                        style = Theme.typography.body.small.copy(
                            color = Theme.colors.hint,
                            fontWeight = FontWeight.Medium,
                        ),
                    )

                    Spacer(Modifier.height(8.dp))

                    if (question != null) {
                        SurveyQuestionHeader(
                            currentStep = step,
                            totalSteps = state.totalSteps,
                            title = question.title,
                            subtitle = question.subtitle,
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    when (question) {
                        is SurveyQuestion.MultiChip ->
                            CategoryStep(question, answer, viewModel::onIntent, Modifier.fillMaxWidth())
                        is SurveyQuestion.SingleChip -> when (step) {
                            1 -> PriceRangeStep(question, answer, viewModel::onIntent, Modifier.fillMaxWidth())
                            else -> GenderStep(question, answer, viewModel::onIntent, Modifier.fillMaxWidth())
                        }
                        is SurveyQuestion.StyleCards ->
                            ShoppingStyleStep(question, answer, viewModel::onIntent, Modifier.fillMaxWidth())
                        is SurveyQuestion.ColorPicker -> Unit // not used in 4-step flow
                        null -> Unit
                    }

                    Spacer(Modifier.height(24.dp))

                    // Only show explicit "Next" button for multi-select steps
                    if (!isSingleSelect) {
                        PrimaryButton(
                            caption = if (state.isLastStep) stringResource(Res.string.survey_finish) else stringResource(Res.string.survey_next),
                            onClick = { advance() },
                            modifier = Modifier.fillMaxWidth(),
                            isDisabled = !state.isCurrentAnswered,
                            isLoading = state.isSubmitting,
                        )
                    } else if (state.isSubmitting) {
                        PrimaryButton(
                            caption = stringResource(Res.string.survey_saving),
                            onClick = {},
                            modifier = Modifier.fillMaxWidth(),
                            isLoading = true,
                        )
                    }
                }
            }
        }
    }
}

// Keep the old SurveyScreen as a thin wrapper for backward compat with the nav graph
@Composable
fun SurveyScreen(
    onNavigateBack: () -> Unit,
    viewModel: SurveyViewModel = koinViewModel(),
) {
    SurveyBottomSheet(onDismiss = onNavigateBack, viewModel = viewModel)
}
