package com.troves.designsystem.components.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.login_required_title
import troves.designsystem.generated.resources.login_required_msg
import troves.designsystem.generated.resources.login_required_btn
import troves.designsystem.generated.resources.profile_cancel
import troves.designsystem.generated.resources.ic_profile

/**
 * Confirmation dialog used across the app for negative / irreversible actions.
 *
 * Matches the shared design: a circular tinted [icon] on top, a bold [title], a centered
 * [message], and two equal-width filled buttons (light dismiss + solid confirm). The [icon]
 * and [confirmColor] change per action (e.g. red trash for delete, heart for wishlist removal).
 */
@Composable
fun TrovesDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    dismissText: String? = "Cancel",
    icon: Painter? = null,
    confirmColor: Color = Theme.colors.primary,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Theme.colors.surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(confirmColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        colorFilter = ColorFilter.tint(confirmColor),
                    )
                }
                Spacer(Modifier.height(20.dp))
            }

            Text(
                text = title,
                style = Theme.typography.title,
                fontWeight = FontWeight.Bold,
                color = Theme.colors.primaryFont,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = message,
                style = Theme.typography.body.medium,
                color = Theme.colors.secondaryFont,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (dismissText != null) {
                    PrimaryButton(
                        caption = dismissText,
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        containerColor = Theme.colors.surfaceVariant,
                        contentColor = Theme.colors.primary,
                    )
                }
                PrimaryButton(
                    caption = confirmText,
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    containerColor = confirmColor,
                    contentColor = Theme.colors.onPrimary,
                )
            }
        }
    }
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
        icon = painterResource(Res.drawable.ic_profile),
        onConfirm = onLoginClick,
        onDismiss = onDismiss,
    )
}
