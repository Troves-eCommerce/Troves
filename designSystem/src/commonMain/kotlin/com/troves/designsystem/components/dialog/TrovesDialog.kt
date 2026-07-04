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
    message: String = "You need to be logged in to perform this action.",
    onLoginClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    TrovesDialog(
        title = "Login Required",
        message = message,
        confirmText = "Log In",
        dismissText = "Cancel",
        onConfirm = onLoginClick,
        onDismiss = onDismiss
    )
}