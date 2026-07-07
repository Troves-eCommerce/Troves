package com.troves.presintation.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.stringResource
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.signup_prompt_title
import troves.presintation.generated.resources.signup_prompt_msg
import troves.presintation.generated.resources.signup_prompt_confirm
import troves.presintation.generated.resources.profile_cancel

@Composable
fun SignUpPromptDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Theme.colors.surface,
        title = { Text(stringResource(Res.string.signup_prompt_title)) },
        text = { Text(stringResource(Res.string.signup_prompt_msg)) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(Res.string.signup_prompt_confirm)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.profile_cancel)) }
        },
    )
}
