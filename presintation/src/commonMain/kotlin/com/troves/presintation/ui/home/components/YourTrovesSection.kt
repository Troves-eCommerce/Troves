package com.troves.presintation.ui.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.troves.designsystem.components.cards.MainCard
import com.troves.designsystem.components.shimmer.shimmerEffect
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.bounceClick
import com.troves.designsystem.util.formatPrice
import com.troves.designsystem.util.autoMirror
import com.troves.domain.entity.Product
import com.troves.presintation.ui.home.HomeIntent
import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.home_your_troves
import troves.designsystem.generated.resources.home_your_troves_subtitle
import troves.designsystem.generated.resources.see_all
import troves.designsystem.generated.resources.ic_chevron_right
import troves.designsystem.generated.resources.img_placeholder
import troves.designsystem.generated.resources.ic_solid_heart
import troves.designsystem.generated.resources.ic_star
import org.jetbrains.compose.resources.painterResource

private const val PLACEHOLDER_RATING = 4.5

/**
 * "Your Troves" AI-personalised recommendation section.
 *
 * - Shows a shimmer row while [isLoading] is true.
 * - Shows the product row when [products] is non-empty.
 * - Hidden entirely when neither loading nor has products.
 */
@Composable
fun YourTrovesSection(
    products: List<Product>,
    isLoading: Boolean,
    favoriteIds: Set<Long>,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!isLoading && products.isEmpty()) return

    val placeholderPainter = painterResource(Res.drawable.img_placeholder)
    val starIcon = painterResource(Res.drawable.ic_star)
    val heartIcon = painterResource(Res.drawable.ic_solid_heart)
    val chevron = painterResource(Res.drawable.ic_chevron_right)

    Column(modifier = modifier) {
        YourTrovesSectionHeader(
            chevronPainter = chevron,
            onSeeAll = { onIntent(HomeIntent.ViewAllYourTrovesClicked) },
        )

        if (isLoading) {
            YourTrovesShimmer()
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(products, key = { it.id }) { product ->
                    MainCard(
                        title = product.title,
                        price = formatPrice(product.price),
                        rating = PLACEHOLDER_RATING,
                        imagePainter = rememberAsyncImagePainter(
                            model = product.imageUrl,
                            placeholder = placeholderPainter,
                            error = placeholderPainter,
                        ),
                        ratingIconPainter = starIcon,
                        favoriteIconPainter = heartIcon,
                        isFavorite = product.id in favoriteIds,
                        onClick = { onIntent(HomeIntent.ProductClicked(product)) },
                        onFavoriteClick = { onIntent(HomeIntent.FavoriteToggled(product)) },
                        modifier = Modifier.width(170.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun YourTrovesSectionHeader(
    chevronPainter: Painter,
    onSeeAll: () -> Unit,
) {
    // Subtle pulsing glow on the AI badge
    val pulse by rememberInfiniteTransition(label = "ai_badge_pulse").animateFloat(
        initialValue = 0.75f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "ai_badge_alpha",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                BasicText(
                    text = stringResource(Res.string.home_your_troves),
                    style = Theme.typography.title.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                // AI sparkle badge
                Box(
                    modifier = Modifier
                        .graphicsLayer { alpha = pulse }
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Theme.colors.primary,
                                    Theme.colors.primary.copy(alpha = 0.6f),
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(100f, 40f),
                            )
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    BasicText(
                        text = "AI",
                        style = Theme.typography.body.small.copy(
                            color = Theme.colors.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                        ),
                    )
                }
            }
            BasicText(
                text = stringResource(Res.string.home_your_troves_subtitle),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                    fontWeight = FontWeight.Normal,
                ),
            )
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.bounceClick(
                shape = RoundedCornerShape(10.dp),
                onClick = onSeeAll,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText(
                text = stringResource(Res.string.see_all),
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.secondaryFont,
                    fontWeight = FontWeight.Medium,
                ),
            )
            androidx.compose.material3.Icon(
                painter = chevronPainter,
                contentDescription = null,
                tint = Theme.colors.secondaryFont,
                modifier = Modifier
                    .size(18.dp)
                    .autoMirror(),
            )
        }
    }
}

@Composable
private fun YourTrovesShimmer() {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .width(170.dp)
                    .height(240.dp)
                    .clip(Theme.shapes.medium)
                    .shimmerEffect(),
            )
        }
    }
}
