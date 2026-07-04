package com.troves.presintation.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
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

@Composable
fun AdSlider(
    ads: List<AdData>,
    arrowIconPainter: Painter? = null,
    onShopNowClick: (AdData) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { ads.size })


    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val ad = ads[page]
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
            repeat(ads.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) Color.White else Color.White.copy(alpha = 0.4f)
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
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
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
                    .clip(RoundedCornerShape(14.dp))
                    .background(ad.getButtonBackgroundColor())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicText(
                    text = ad.buttonText,
                    style = Theme.typography.body.medium.copy(
                        color = ad.getButtonTextColor(),
                        fontWeight = FontWeight.SemiBold
                    )
                )
                if (arrowIconPainter != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        painter = arrowIconPainter,
                        contentDescription = null,
                        tint = ad.getButtonTextColor(),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}