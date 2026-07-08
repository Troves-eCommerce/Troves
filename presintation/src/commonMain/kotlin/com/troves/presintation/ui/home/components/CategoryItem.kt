package com.troves.presintation.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.troves.designsystem.theme.SpTheme
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.bounceClick
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_eye

@Composable
fun CategoryItem(
    name: String,
    iconPainter: DrawableResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconAndTextColor = Theme.colors.tint
    val shape = Theme.shapes.large

    Column(
        modifier = modifier
            .bounceClick(
                shape = RoundedCornerShape(10.dp),
                onClick = onClick
            )
            .border(
                width = 1.dp,
                color = Theme.colors.onPrimary,
                shape = shape
            )
            .background(Theme.colors.surfaceVariant, shape = shape)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(iconPainter),
                contentDescription = name,
                modifier = Modifier.size(36.dp),
                colorFilter = ColorFilter.tint(iconAndTextColor)
            )
        }

        BasicText(
            text = name,
            style = Theme.typography.body.small.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun CategoryItemPreview() {
    SpTheme {
        CategoryItem(
            name = "Men",
            iconPainter = Res.drawable.ic_eye,
            onClick = {},
            modifier = Modifier
                .size(width = 85.dp, height = 100.dp)
        )
    }
}