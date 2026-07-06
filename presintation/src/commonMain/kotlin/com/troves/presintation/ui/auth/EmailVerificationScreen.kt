package com.troves.presintation.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.button.SecondaryButton
import com.troves.designsystem.components.toast.TrovesToast
import com.troves.designsystem.theme.Theme
import com.troves.presintation.core.mvi.ObserveEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res as DesignRes
import troves.designsystem.generated.resources.ic_outline_email
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.verify_email_check_status_button
import troves.presintation.generated.resources.verify_email_continue_as_guest
import troves.presintation.generated.resources.verify_email_desc
import troves.presintation.generated.resources.verify_email_not_received
import troves.presintation.generated.resources.verify_email_resend_button
import troves.presintation.generated.resources.verify_email_title

@Composable
fun EmailVerificationScreen(
    onVerificationSuccess: () -> Unit,
    onContinueAsGuest: () -> Unit,
    viewModel: EmailVerificationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var successMessage by remember { mutableStateOf<String?>(null) }
    var toastError by remember { mutableStateOf<String?>(null) }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is EmailVerificationEffect.ShowMessage -> successMessage = effect.message
            is EmailVerificationEffect.ShowError -> toastError = effect.message
            EmailVerificationEffect.NavigateToHome -> onVerificationSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(DesignRes.drawable.ic_outline_email),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = Theme.colors.primary
            )

            Spacer(Modifier.height(32.dp))

            BasicText(
                text = stringResource(Res.string.verify_email_title),
                style = Theme.typography.displayMedium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(Modifier.height(16.dp))

            BasicText(
                text = stringResource(Res.string.verify_email_desc, state.email),
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.secondaryFont,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            )

            Spacer(Modifier.height(48.dp))

            PrimaryButton(
                caption = stringResource(Res.string.verify_email_check_status_button),
                onClick = { viewModel.onIntent(EmailVerificationIntent.CheckVerificationStatus) },
                modifier = Modifier.fillMaxWidth(),
                isLoading = state.isChecking,
                isDisabled = state.isChecking || state.isResending
            )

            Spacer(Modifier.height(16.dp))

            BasicText(
                text = stringResource(Res.string.verify_email_not_received),
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.secondaryFont,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(Modifier.height(8.dp))

            val resendCaption = if (state.resendCooldownSeconds > 0) {
                "${stringResource(Res.string.verify_email_resend_button)} (${state.resendCooldownSeconds}s)"
            } else {
                stringResource(Res.string.verify_email_resend_button)
            }

            SecondaryButton(
                caption = resendCaption,
                onClick = { viewModel.onIntent(EmailVerificationIntent.ResendVerificationEmail) },
                modifier = Modifier.fillMaxWidth(),
                isLoading = state.isResending,
                isDisabled = !state.canResend || state.isChecking
            )

            Spacer(Modifier.height(16.dp))

            SecondaryButton(
                caption = stringResource(Res.string.verify_email_continue_as_guest),
                onClick = onContinueAsGuest,
                modifier = Modifier.fillMaxWidth(),
                isDisabled = state.isChecking || state.isResending
            )

            AnimatedVisibility(visible = state.errorMessage != null) {
                Spacer(Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Theme.colors.error.copy(alpha = 0.10f),
                            shape = Theme.shapes.medium
                        )
                        .padding(Theme.spacing.small)
                ) {
                    BasicText(
                        text = state.errorMessage ?: "",
                        style = Theme.typography.body.small.copy(color = Theme.colors.error)
                    )
                }
            }
        }

        TrovesToast(
            message = successMessage ?: toastError,
            onDismiss = {
                successMessage = null
                toastError = null
            },
        )
    }
}