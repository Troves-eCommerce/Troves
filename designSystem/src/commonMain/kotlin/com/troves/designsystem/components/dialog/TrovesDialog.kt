package com.troves.designsystem.components.dialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.login_required_title
import troves.designsystem.generated.resources.login_required_msg
import troves.designsystem.generated.resources.login_required_btn
import troves.designsystem.generated.resources.profile_cancel

@Composable
fun TrovesDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    dismissText: String? = "Cancel",
    confirmColor: Color = Theme.colors.primary,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Theme.colors.surface,
        shape = RoundedCornerShape(15.dp),
        tonalElevation = 0.dp,

        title = {
            Text(
                text = title,
                style = Theme.typography.title,
                color = Theme.colors.primaryFont
            )
        },

        text = {
            Text(
                text = message,
                style = Theme.typography.body.medium,
                color = Theme.colors.secondaryFont
            )
        },

        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmText,
                    color = confirmColor,
                    style = Theme.typography.body.medium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        },

        dismissButton = {
            dismissText?.let {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = it,
                        color = Theme.colors.secondaryFont,
                        style = Theme.typography.body.medium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    )
}

@Composable
fun LoginRequiredDialog(
    message: String = stringResource(Res.string.login_required_msg),
    onLoginClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    TrovesDialog(
        title = stringResource(Res.string.login_required_title),
        message = message,
        confirmText = stringResource(Res.string.login_required_btn),
        dismissText = stringResource(Res.string.profile_cancel),
        onConfirm = onLoginClick,
        onDismiss = onDismiss
    )
}