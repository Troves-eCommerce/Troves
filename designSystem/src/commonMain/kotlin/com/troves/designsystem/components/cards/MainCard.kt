package com.troves.designsystem.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.troves.designsystem.components.button.FavoriteButton
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.bounceClick

@Composable
fun MainCard(
    title: String,
    price: String,
    rating: Double,
    imagePainter: Painter,
    ratingIconPainter: Painter,
    favoriteIconPainter: Painter,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    containerColor: Color = Color.Transparent,
) {
    val cardShape = Theme.shapes.large
    val resolvedContainer = if (containerColor == Color.Transparent) Theme.colors.surface else containerColor

    Column(
        modifier = modifier
            .shadow(elevation = 0.2.dp, shape = cardShape, clip = false)
            .clip(cardShape)
            .background(resolvedContainer)
            .border(1.dp, Theme.colors.onPrimary, cardShape)
            .bounceClick(
                shape = cardShape,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.95f)
                .background(Theme.colors.surfaceVariant)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = imagePainter,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                )
            }
            FavoriteButton(
                isFavorite = isFavorite,
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    painter = ratingIconPainter,
                    contentDescription = "Rating",
                    tint = Theme.colors.amber,
                    modifier = Modifier.size(14.dp)
                )
                BasicText(
                    text = rating.toString(),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.secondaryFont,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            BasicText(
                text = title,
                maxLines = 1,
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 0.sp
                ),
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
            )

            BasicText(
                text = price,
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 0.sp
                ),
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
    }
}