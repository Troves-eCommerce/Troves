package com.troves.presintation.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.troves.designsystem.theme.Theme

@Composable
fun SignUpPromptDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Theme.colors.surface,
        title = { Text("Sign up required") },
        text = { Text("You need an account to do that. Would you like to sign up?") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Sign Up") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
