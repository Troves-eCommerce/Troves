package com.troves.presintation.ui.home.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.autoMirror
import com.troves.designsystem.util.bounceClick
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

data class AdData(
    val titleTop: String,
    val titleBottom: String,
    val description: String,
    val imagePainter: Painter,
    val buttonText: String = "Copy code",
    val getTextColor: @Composable () -> Color = { Color.White },
    val getDescriptionColor: @Composable () -> Color = { Color.White.copy(alpha = 0.8f) },
    val getButtonBackgroundColor: @Composable () -> Color = { Theme.colors.primaryVariant },
    val getButtonTextColor: @Composable () -> Color = { Theme.colors.surface }
)

private const val VIRTUAL_PAGE_MULTIPLIER = 1000

@Composable
fun AdSlider(
    ads: List<AdData>,
    arrowIconPainter: Painter? = null,
    onShopNowClick: (AdData) -> Unit,
    modifier: Modifier = Modifier,
    autoScrollDurationMillis: Long = 4000L
) {
    if (ads.isEmpty()) return

    val realCount = ads.size
    val virtualCount = realCount * VIRTUAL_PAGE_MULTIPLIER
    val startPage = virtualCount / 2 - (virtualCount / 2) % realCount

    val pagerState = rememberPagerState(
        initialPage = startPage,
        pageCount = { virtualCount }
    )
    LaunchedEffect(pagerState, ads.size) {
        if (realCount <= 1) return@LaunchedEffect
        while (isActive) {
            delay(autoScrollDurationMillis)
            if (!pagerState.isScrollInProgress) {
                val nextPage = pagerState.currentPage + 1
                pagerState.animateScrollToPage(
                    page = nextPage,
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = androidx.compose.animation.core.FastOutSlowInEasing
                    )
                )
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val realIndex = page % realCount
            val ad = ads[realIndex]
            AdBannerItem(
                ad = ad,
                arrowIconPainter = arrowIconPainter,
                onClick = { onShopNowClick(ad) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val currentRealPage = pagerState.currentPage % realCount
            repeat(realCount) { index ->
                val isSelected = currentRealPage == index
                val width by animateDpAsState(
                    targetValue = if (isSelected) 20.dp else 6.dp,
                    animationSpec = tween(durationMillis = 300),
                    label = "dotWidth"
                )
                val height by animateDpAsState(
                    targetValue = 6.dp,
                    animationSpec = tween(durationMillis = 300),
                    label = "dotHeight"
                )
                Box(
                    modifier = Modifier
                        .size(width = width, height = height)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)
                        )
                )
            }
        }
    }
}

@Composable
private fun AdBannerItem(
    ad: AdData,
    arrowIconPainter: Painter?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .bounceClick(
                shape = RoundedCornerShape(10.dp),
                onClick = onClick
            )
    ) {
        Image(
            painter = ad.imagePainter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.2f),
                            Color.Black.copy(alpha = 0.8f)
                        ),
                        startY = 100f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(horizontal = 24.dp, vertical = 36.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.Start
        ) {
            BasicText(
                text = ad.titleTop,
                style = Theme.typography.title.copy(
                    color = ad.getTextColor(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            )

            if (ad.titleBottom.isNotBlank()) {
                BasicText(
                    text = ad.titleBottom,
                    style = Theme.typography.title.copy(
                        color = ad.getTextColor(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                )
            }

            BasicText(
                text = ad.description,
                style = Theme.typography.body.small.copy(
                    color = ad.getDescriptionColor(),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .height(48.dp)
                    .defaultMinSize(minWidth = 130.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(ad.getButtonBackgroundColor())
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                BasicText(
                    text = ad.buttonText,
                    style = Theme.typography.body.medium.copy(
                        color = ad.getButtonTextColor(),
                        fontWeight = FontWeight.SemiBold
                    ),

                    maxLines = 1
                )
                if (arrowIconPainter != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        painter = arrowIconPainter,
                        contentDescription = null,
                        tint = ad.getButtonTextColor(),
                        modifier = Modifier
                            .size(14.dp)
                            .autoMirror()
                    )
                }
            }
        }
    }
}