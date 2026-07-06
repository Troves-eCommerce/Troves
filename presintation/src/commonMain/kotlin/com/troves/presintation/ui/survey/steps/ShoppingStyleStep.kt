package com.troves.presintation.ui.survey.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.troves.designsystem.theme.Theme
import com.troves.presintation.ui.survey.SurveyAnswer
import com.troves.presintation.ui.survey.SurveyIntent
import com.troves.presintation.ui.survey.SurveyQuestion
import com.troves.presintation.ui.survey.components.SurveyOptionCard

@Composable
fun ShoppingStyleStep(
    question: SurveyQuestion.StyleCards,
    answer: SurveyAnswer?,
    onIntent: (SurveyIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = (answer as? SurveyAnswer.SingleSelection)?.selected
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        question.options.forEach { option ->
            SurveyOptionCard(
                label = option.label,
                description = option.description,
                icon = option.icon,
                isSelected = option.label == selected,
                onClick = { onIntent(SurveyIntent.SelectSingleOption(option.label)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
