package com.troves.presintation.ui.survey.steps

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import troves.presintation.generated.resources.*
import androidx.compose.ui.Modifier
import com.troves.presintation.ui.survey.SurveyAnswer
import com.troves.presintation.ui.survey.SurveyIntent
import com.troves.presintation.ui.survey.SurveyQuestion
import com.troves.presintation.ui.survey.SurveyOption
import com.troves.presintation.ui.survey.components.SurveyChipGroup

@Composable
fun BrandStep(
    question: SurveyQuestion.MultiChip,
    answer: SurveyAnswer?,
    onIntent: (SurveyIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = (answer as? SurveyAnswer.MultiSelection)?.selected ?: emptySet()
    SurveyChipGroup(
        options = question.options,
        selectedOptions = selected,
        onOptionToggled = { onIntent(SurveyIntent.ToggleMultiOption(it)) },
        multiSelect = true,
        modifier = modifier,
    )
}




