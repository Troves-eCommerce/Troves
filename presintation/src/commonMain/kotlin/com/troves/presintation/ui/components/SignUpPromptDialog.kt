package com.troves.presintation.ui.components

import androidx.compose.runtime.Composable
import com.troves.designsystem.components.dialog.TrovesDialog
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.signup_prompt_title
import troves.presintation.generated.resources.signup_prompt_msg
import troves.presintation.generated.resources.signup_prompt_confirm
import troves.presintation.generated.resources.profile_cancel
// Drawable lives in the designSystem module — alias its Res and import the accessor.
import troves.designsystem.generated.resources.Res as DesignRes
import troves.designsystem.generated.resources.ic_profile

@Composable
fun SignUpPromptDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    TrovesDialog(
        title = stringResource(Res.string.signup_prompt_title),
        message = stringResource(Res.string.signup_prompt_msg),
        confirmText = stringResource(Res.string.signup_prompt_confirm),
        dismissText = stringResource(Res.string.profile_cancel),
        icon = painterResource(DesignRes.drawable.ic_profile),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}
