package com.troves.presintation.ui.orderdetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme

data class TimelineStepData(
    val title: String,
    val date: String? = null,
    val description: String? = null,
    val isCompleted: Boolean,
    val isCurrent: Boolean,
    val icon: Painter? = null,
)

@Composable
fun OrderTimeline(
    steps: List<TimelineStepData>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, step ->
            TimelineStep(
                step = step,
                isLast = index == steps.lastIndex,
            )
        }
    }
}

@Composable
fun TimelineStep(
    step: TimelineStepData,
    isLast: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
    ) {
        // Left Column: Circle & Line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            // Circle
            val circleColor = when {
                step.isCompleted -> Theme.colors.success
                step.isCurrent -> Theme.colors.primary
                else -> Color.Transparent
            }
            val borderColor = when {
                step.isCompleted -> Theme.colors.success
                step.isCurrent -> Theme.colors.primary
                else -> Theme.colors.disable
            }
            val iconTint = when {
                step.isCompleted -> Theme.colors.onSuccess
                step.isCurrent -> Theme.colors.onPrimary
                else -> Theme.colors.hint
            }

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(circleColor)
                    .border(2.dp, borderColor, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                if (step.icon != null) {
                    Icon(
                        painter = step.icon,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = iconTint,
                    )
                } else if (step.isCurrent) {
                    // Small inner circle for current step without icon
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Theme.colors.onPrimary)
                    )
                }
            }

            // Line
            if (!isLast) {
                val lineColor = if (step.isCompleted) Theme.colors.success else Theme.colors.disable
                Box(
                    modifier = Modifier
                        .padding(vertical = Theme.spacing.extraSmall)
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(lineColor)
                )
            }
        }

        // Right Column: Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else Theme.spacing.large)
                .padding(start = Theme.spacing.small)
        ) {
            val titleColor = if (step.isCompleted || step.isCurrent) Theme.colors.primaryFont else Theme.colors.hint
            BasicText(
                text = step.title,
                style = Theme.typography.body.large.copy(
                    color = titleColor,
                    fontWeight = if (step.isCurrent) FontWeight.Bold else FontWeight.Medium,
                )
            )
            if (step.date != null) {
                BasicText(
                    text = step.date,
                    style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            if (step.description != null) {
                BasicText(
                    text = step.description,
                    style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
