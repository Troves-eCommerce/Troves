package com.troves.designsystem.components.topbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.theme.Theme


data class TopBarAction(
    val icon: Painter,
    val contentDescription: String? = null,
    val onClick: () -> Unit,
)


@Composable
fun BaseTopAppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    leadingIcon: Painter? = null,
    onLeadingClick: (() -> Unit)? = null,
    leadingSpacing: Dp = 12.dp,
    actions: List<TopBarAction> = emptyList(),
    actionSpacing: Dp = 8.dp,
    titleStyle: TextStyle = Theme.typography.title.copy(
        color = Theme.colors.primaryFont,
        fontWeight = FontWeight.Bold,
    ),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            IconBox(
                icon = leadingIcon,
                contentDescription = "Navigate up",
                onClick = onLeadingClick,
            )
            Spacer(Modifier.width(leadingSpacing))
        }

        if (title != null) {
            BasicText(text = title, style = titleStyle)
        }

        Spacer(Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(actionSpacing)) {
            actions.forEach { action ->
                IconBox(
                    icon = action.icon,
                    contentDescription = action.contentDescription,
                    onClick = action.onClick,
                )
            }
        }
    }
}

@Preview
@Composable
private fun BaseTopAppBarPreview() {
    SpTheme {
        BaseTopAppBar(
            title = "Troves",
            leadingIcon = ColorPainter(Color.Black),
            onLeadingClick = {},
            actions = listOf(
                TopBarAction(ColorPainter(Color.Black), "Search") {},
                TopBarAction(ColorPainter(Color.Black), "Cart") {},
            ),
        )
    }
}
