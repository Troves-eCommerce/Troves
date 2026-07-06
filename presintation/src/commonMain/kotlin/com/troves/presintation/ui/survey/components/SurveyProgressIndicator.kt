package com.troves.presintation.ui.survey.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme

@Composable
fun SurveyProgressIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(totalSteps) { index ->
            val isSelected = index == currentStep

            val dotColor by animateColorAsState(
                targetValue = if (isSelected) Theme.colors.primary else Theme.colors.hint.copy(alpha = 0.4f),
                animationSpec = tween(300),
                label = "surveyDotColor",
            )
            val dotWidth by animateDpAsState(
                targetValue = if (isSelected) 24.dp else 8.dp,
                animationSpec = tween(300),
                label = "surveyDotWidth",
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = Theme.spacing.extraSmall)
                    .height(8.dp)
                    .width(dotWidth)
                    .clip(CircleShape)
                    .background(dotColor),
            )
        }
    }
}
