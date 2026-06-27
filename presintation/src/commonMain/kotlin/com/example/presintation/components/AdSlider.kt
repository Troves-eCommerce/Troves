package com.example.presintation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.painter.ColorPainter
import com.example.designsystem.theme.SpTheme
import com.example.designsystem.theme.Theme
import androidx.compose.ui.tooling.preview.Preview

data class AdData(
    val titleTop: String,
    val titleBottom: String,
    val description: String,
    val imagePainter: Painter,
    val buttonText: String = "Shop now"
)

@Composable
fun AdSlider(
    ads: List<AdData>,
    arrowIconPainter: Painter? = null,
    onShopNowClick: (AdData) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { ads.size })
    Box(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val ad = ads[page]
            AdBannerItem(
                ad = ad,
                arrowIconPainter = arrowIconPainter,
                onClick = { onShopNowClick(ad) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        
        // Pager indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(ads.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Theme.colors.primary else Color.White.copy(alpha = 0.6f))
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
            .height(180.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        // Background Image
        Image(
            painter = ad.imagePainter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        // Overlay Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            BasicText(
                text = ad.titleTop,
                style = Theme.typography.title.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
            BasicText(
                text = ad.titleBottom,
                style = Theme.typography.title.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            BasicText(
                text = ad.description,
                style = Theme.typography.body.small.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Normal
                ),
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // "Shop now" pill button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicText(
                    text = ad.buttonText,
                    style = Theme.typography.body.medium.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                if (arrowIconPainter != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = arrowIconPainter,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AdSliderPreview() {
    SpTheme {
        Box(modifier = Modifier.background(Theme.colors.backGround).padding(16.dp)) {
            AdSlider(
                ads = listOf(
                    AdData(
                        titleTop = "30% DISCOUNT",
                        titleBottom = "Today special",
                        description = "Get discount for every order, only valid for today.",
                        imagePainter = ColorPainter(Color.DarkGray)
                    )
                ),
                onShopNowClick = {}
            )
        }
    }
}
