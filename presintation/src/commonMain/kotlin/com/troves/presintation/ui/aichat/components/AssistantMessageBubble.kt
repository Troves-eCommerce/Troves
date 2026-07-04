package com.troves.presintation.ui.aichat.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Sparkles
import com.troves.designsystem.theme.Theme
import com.troves.presintation.ui.aichat.AiProductUi

@Composable
fun AssistantMessageBubble(
    text: String,
    products: List<AiProductUi>,
    isSuggestion: Boolean,
    suggestionHeader: String,
    viewAllLabel: String,
    onProductClick: (AiProductUi) -> Unit,
    onFavoriteClick: (AiProductUi) -> Unit,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Theme.colors.primary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = rememberVectorPainter(Lucide.Sparkles),
                contentDescription = null,
                tint = Theme.colors.onPrimary,
                modifier = Modifier.size(16.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (text.isNotBlank()) {
                AiMarkdownText(
                    text = text,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp))
                        .background(Theme.colors.surface)
                        .border(
                            BorderStroke(1.dp, Theme.colors.hint.copy(alpha = 0.25f)),
                            RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp),
                        )
                        .padding(14.dp),
                )
            }

            if (products.isNotEmpty()) {
                if (isSuggestion) {
                    Text(
                        text = suggestionHeader,
                        style = Theme.typography.hint.medium,
                        color = Theme.colors.secondaryFont,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
                RecommendationCard(
                    products = products,
                    viewAllLabel = viewAllLabel,
                    onProductClick = onProductClick,
                    onFavoriteClick = onFavoriteClick,
                    onViewAll = onViewAll,
                )
            }
        }
    }
}
