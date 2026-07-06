package com.troves.presintation.ui.survey.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme

@Composable
fun SurveyQuestionHeader(
    currentStep: Int,
    totalSteps: Int,
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall),
    ) {
        BasicText(
            text = "Question ${currentStep + 1} of $totalSteps",
            style = Theme.typography.body.small.copy(
                color = Theme.colors.secondaryFont,
            ),
        )
        Spacer(Modifier.height(4.dp))
        BasicText(
            text = title,
            style = Theme.typography.displayMedium.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
            ),
        )
        if (subtitle != null) {
            BasicText(
                text = subtitle,
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.secondaryFont,
                ),
            )
        }
    }
}
