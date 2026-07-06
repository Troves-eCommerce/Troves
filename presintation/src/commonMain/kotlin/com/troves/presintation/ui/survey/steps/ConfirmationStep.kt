package com.troves.presintation.ui.survey.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_ai_sparkles

@Composable
fun ConfirmationStep(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        Spacer(Modifier.height(Theme.spacing.extraLarge))

        Icon(
            painter = painterResource(Res.drawable.ic_ai_sparkles),
            contentDescription = null,
            tint = Theme.colors.primary,
            modifier = Modifier.size(80.dp),
        )

        Spacer(Modifier.height(Theme.spacing.medium))

        BasicText(
            text = "You're all set!",
            style = Theme.typography.display.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
        )

        BasicText(
            text = "We'll use your preferences to recommend products you'll love.",
            style = Theme.typography.body.large.copy(
                color = Theme.colors.secondaryFont,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(Modifier.height(Theme.spacing.extraLarge))

        PrimaryButton(
            caption = "Continue Shopping",
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
