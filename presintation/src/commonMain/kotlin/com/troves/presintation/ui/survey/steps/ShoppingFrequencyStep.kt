package com.troves.presintation.ui.survey.steps

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.troves.presintation.ui.survey.SurveyAnswer
import com.troves.presintation.ui.survey.SurveyIntent
import com.troves.presintation.ui.survey.SurveyQuestion
import com.troves.presintation.ui.survey.components.SurveyChipGroup

@Composable
fun ShoppingFrequencyStep(
    question: SurveyQuestion.SingleChip,
    answer: SurveyAnswer?,
    onIntent: (SurveyIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = (answer as? SurveyAnswer.SingleSelection)?.selected
    SurveyChipGroup(
        options = question.options,
        selectedOptions = if (selected != null) setOf(selected) else emptySet(),
        onOptionToggled = { onIntent(SurveyIntent.SelectSingleOption(it)) },
        multiSelect = false,
        modifier = modifier,
    )
}
