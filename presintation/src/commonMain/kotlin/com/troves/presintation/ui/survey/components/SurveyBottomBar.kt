package com.troves.presintation.ui.survey.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.button.SecondaryButton
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_arrow_back

@Composable
fun SurveyBottomBar(
    isLastStep: Boolean,
    isAnswered: Boolean,
    isSubmitting: Boolean,
    canGoBack: Boolean,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PrimaryButton(
            caption = if (isLastStep) "Finish" else "Next",
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            isDisabled = !isAnswered,
            isLoading = isSubmitting,
        )
        if (canGoBack) {
            SecondaryButton(
                caption = "Back",
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                iconPainter = painterResource(Res.drawable.ic_arrow_back),
            )
        }
    }
}
