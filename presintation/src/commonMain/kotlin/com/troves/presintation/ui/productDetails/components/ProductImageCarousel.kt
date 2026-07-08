package com.troves.presintation.ui.productDetails.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.troves.designsystem.components.button.FavoriteButton
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.autoMirror
import com.troves.designsystem.util.bounceClick
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import troves.presintation.generated.resources.Res as ResP
import troves.presintation.generated.resources.common_back
import troves.presintation.generated.resources.product_image_carousel_desc
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_arrow_back
import troves.designsystem.generated.resources.img_placeholder

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductImageCarousel(
    imageUrls: List<String>,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onBackClick: () -> Unit,
    backEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { imageUrls.size })

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(420.dp)
            .background(Theme.colors.backGround),
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            AsyncImage(
                model = imageUrls.getOrNull(page),
                contentDescription = stringResource(ResP.string.product_image_carousel_desc, page + 1),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(Res.drawable.img_placeholder),
                error = painterResource(Res.drawable.img_placeholder),
            )
        }

        IconButton(
            onClick = { if (backEnabled) onBackClick() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 16.dp, top = 16.dp)
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .autoMirror(),
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_back),
                contentDescription = stringResource(ResP.string.common_back),
                tint = Color.Black,
                modifier = Modifier
                    .size(20.dp)
                    .autoMirror()
                    .bounceClick(
                        shape = RoundedCornerShape(12.dp),
                        onClick = onBackClick
                    )
            )
        }

        FavoriteButton(
            isFavorite = isFavorite,
            onClick = onFavoriteClick,
            size = 46.dp,
            iconSize = 24.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(end = 16.dp, top = 16.dp)
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(imageUrls.size) { index ->
                val isSelected = index == pagerState.currentPage
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 8.dp else 7.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) Theme.colors.primary else Color.Black.copy(alpha = 0.35f)
                        ),
                )
            }
        }
    }
}