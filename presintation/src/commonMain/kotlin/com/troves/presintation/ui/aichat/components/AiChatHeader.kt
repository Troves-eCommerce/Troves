package com.troves.presintation.ui.aichat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Ellipsis
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Sparkles
import com.troves.designsystem.components.topbar.IconBox
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_arrow_back

@Composable
fun AiChatHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.backGround)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconBox(
            icon = painterResource(Res.drawable.ic_arrow_back),
            contentDescription = "Back",
            onClick = onBack,
        )

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Theme.colors.primary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = rememberVectorPainter(Lucide.Sparkles),
                contentDescription = null,
                tint = Theme.colors.onPrimary,
                modifier = Modifier.size(22.dp),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = Theme.typography.title,
                color = Theme.colors.primary,
            )
            Text(
                text = subtitle,
                style = Theme.typography.hint.medium,
                color = Theme.colors.secondaryFont,
            )
        }

        IconBox(
            icon = rememberVectorPainter(Lucide.Clock),
            contentDescription = "Chat history",
            onClick = onHistoryClick,
            iconTint = Theme.colors.primaryFont,
            backgroundColor = Theme.colors.backGround,
        )
        IconBox(
            icon = rememberVectorPainter(Lucide.Ellipsis),
            iconTint = Theme.colors.primaryFont,
            backgroundColor = Theme.colors.backGround,
        )
    }
}
