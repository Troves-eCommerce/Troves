package com.troves.designsystem.components.stepper

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.stepper_label

/**
 * Horizontal progress indicator: a "STEP X OF N" label above a row of circles
 * connected by lines.
 *
 * - The current step is a filled donut in [Theme.colors.primary].
 * - Completed steps (before current) and the lines before the current step use
 *   [Theme.colors.primary] as outlined rings / solid lines.
 * - Upcoming steps (after current) and the lines after the current step use
 *   [Theme.colors.secondary].
 *
 * @param currentStep 1-based index of the active step.
 * @param totalSteps total number of steps.
 */
@Composable
fun HorizontalStepper(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
    showStepLabel: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (showStepLabel) {
            BasicText(
                text = stringResource(Res.string.stepper_label, currentStep, totalSteps),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.primaryVariant,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp,
                ),
                modifier = Modifier.padding(bottom = Theme.spacing.small),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (index in 1..totalSteps) {
                StepCircle(index = index, currentStep = currentStep)
                if (index < totalSteps) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = Theme.spacing.extraSmall)
                            .height(2.dp)
                            .background(
                                if (index < currentStep) Theme.colors.primary
                                else Theme.colors.secondary,
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun StepCircle(index: Int, currentStep: Int) {
    val circleSize = 24.dp
    when {
        index == currentStep -> {
            // Filled donut: solid primary disc with a light center dot.
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .clip(CircleShape)
                    .background(Theme.colors.primary),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Theme.colors.backGround),
                )
            }
        }

        index < currentStep -> {
            // Completed: primary ring.
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .border(2.dp, Theme.colors.primary, CircleShape),
            )
        }

        else -> {
            // Upcoming: secondary ring.
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .border(2.dp, Theme.colors.secondary, CircleShape),
            )
        }
    }
}

@Preview
@Composable
private fun HorizontalStepperPreview() {
    SpTheme(isDarkTheme = false, languageCode = "en") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colors.backGround)
                .padding(Theme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.large),
        ) {
            HorizontalStepper(currentStep = 1, totalSteps = 4)
            HorizontalStepper(currentStep = 2, totalSteps = 4)
            HorizontalStepper(currentStep = 4, totalSteps = 4)
        }
    }
}
