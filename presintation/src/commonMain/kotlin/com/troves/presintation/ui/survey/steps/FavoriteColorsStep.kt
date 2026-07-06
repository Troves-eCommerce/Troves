package com.troves.presintation.ui.survey.steps

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.bounceClick
import com.troves.presintation.ui.survey.ColorOption
import com.troves.presintation.ui.survey.SurveyAnswer
import com.troves.presintation.ui.survey.SurveyIntent
import com.troves.presintation.ui.survey.SurveyQuestion

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FavoriteColorsStep(
    question: SurveyQuestion.ColorPicker,
    answer: SurveyAnswer?,
    onIntent: (SurveyIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = (answer as? SurveyAnswer.ColorSelection)?.selected ?: emptySet()
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        question.colors.forEach { colorOption ->
            ColorDot(
                option = colorOption,
                isSelected = colorOption.name in selected,
                onClick = { onIntent(SurveyIntent.ToggleColor(colorOption.name)) },
            )
        }
    }
}

@Composable
private fun ColorDot(
    option: ColorOption,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Theme.colors.primary else Color.Transparent,
        animationSpec = tween(150),
        label = "colorDotBorder",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .semantics {
                selected = isSelected
                contentDescription = option.name
            }
            .bounceClick(shape = CircleShape, onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(option.color)
                .border(2.dp, borderColor, CircleShape),
        )
        BasicText(
            text = option.name,
            style = Theme.typography.body.small.copy(
                color = Theme.colors.secondaryFont,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            ),
        )
    }
}
