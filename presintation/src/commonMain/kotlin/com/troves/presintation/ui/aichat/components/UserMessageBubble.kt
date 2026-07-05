package com.troves.presintation.ui.aichat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.troves.designsystem.theme.Theme
import com.troves.presintation.ui.aichat.PendingImageUi

@Composable
fun UserMessageBubble(
    text: String,
    modifier: Modifier = Modifier,
    image: PendingImageUi? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (image != null) {
            AsyncImage(
                model = image.bytes,
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp))
                    .background(Theme.colors.surfaceVariant),
                contentScale = ContentScale.Crop,
            )
        }

        if (text.isNotBlank()) {
            Box(contentAlignment = Alignment.CenterEnd) {
                Text(
                    text = text,
                    style = Theme.typography.body.medium,
                    color = Theme.colors.primaryFont,
                    modifier = Modifier
                        .widthIn(max = 300.dp)
                        .clip(RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp))
                        .background(Theme.colors.surfaceVariant)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                )
            }
        }
    }
}
