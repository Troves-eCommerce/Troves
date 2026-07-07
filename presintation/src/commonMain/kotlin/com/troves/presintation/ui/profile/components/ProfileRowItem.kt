package com.troves.presintation.ui.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_arrow
import troves.presintation.generated.resources.Res as ResP
import troves.presintation.generated.resources.*
import com.troves.designsystem.util.autoMirror

@Composable
fun ProfileRowItem(
    icon: DrawableResource,
    title: String,
    description: String? = null,
    textColor: Color = Theme.colors.primaryFont,
    iconColor: Color = Theme.colors.secondaryFont,
    showArrow: Boolean = true,
    autoMirrorIcon: Boolean = false,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier
                .size(24.dp)
                .then(if (autoMirrorIcon) Modifier.autoMirror() else Modifier)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = Theme.typography.body.medium.copy(
                    color = textColor,
                    fontWeight = FontWeight.SemiBold
                )
            )
            if (!description.isNullOrEmpty()) {
                Text(
                    text = description,
                    style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont)
                )
            }
        }

        if (trailingContent != null) {
            trailingContent()
        }

        if (showArrow) {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow),
                contentDescription = stringResource(ResP.string.profile_navigate),
                tint = Theme.colors.hint,
                modifier = Modifier
                    .size(16.dp)
                    .autoMirror()
            )
        }
    }
}