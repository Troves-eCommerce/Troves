package com.troves.presintation.ui.productDetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_heart
import troves.designsystem.generated.resources.img_onboarding1

@Composable
fun ProductImageCarousel(
    imageUrls: List<String>,
    currentIndex: Int,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val currentIndexState = rememberCarouselState(initialItem = currentIndex) { imageUrls.size }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(Theme.colors.backGround),
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center,
        ) {

            HorizontalMultiBrowseCarousel(
                state = currentIndexState,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Theme.colors.backGround)
                    .height(400.dp)
                    .align(Alignment.Center),
                preferredItemWidth = 380.dp,
                itemSpacing = 8.dp,
                minSmallItemWidth = 180.dp,
                maxSmallItemWidth = 300.dp,
                contentPadding = PaddingValues(horizontal = 8.dp),
            ) { i ->
                AsyncImage(
                    model = imageUrls.getOrNull(i),
                    contentDescription = "Product image $i",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .maskClip(MaterialTheme.shapes.extraLarge),
                    placeholder = painterResource(Res.drawable.img_onboarding1),
                    error = painterResource(Res.drawable.img_onboarding1),
                )
            }
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(Theme.size.small)
                    .background(Theme.colors.backGround, CircleShape),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_heart),
                    contentDescription = if (isFavorite) "Remove from favourites" else "Add to favourites",
                    tint = if (isFavorite) Theme.colors.amber else Theme.colors.onDisable,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(imageUrls.size) { index ->
                PageDot(isSelected = index == currentIndexState.currentItem)
            }
        }
    }
}

@Composable
private fun PageDot(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val PageDotSelected = 8.dp
    val PageDotIdle = 6.dp
    Box(
        modifier = modifier
            .size(if (isSelected) PageDotSelected else PageDotIdle)
            .clip(CircleShape)
            .background(Theme.colors.secondary),
    )
}
