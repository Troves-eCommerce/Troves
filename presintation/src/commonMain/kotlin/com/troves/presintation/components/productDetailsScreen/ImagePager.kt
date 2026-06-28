package com.troves.presintation.components.productDetailsScreen

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.util.lerp
import coil3.compose.AsyncImage
import com.troves.designsystem.theme.Theme
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun ImagePager(
    images: List<String>,
    modifier: Modifier = Modifier,
    pagerState: PagerState,
) {
    val pagerIsDragged by pagerState.interactionSource.collectIsDraggedAsState()
    val pageInteractionSource = remember { MutableInteractionSource() }
    val pageIsPressed by pageInteractionSource.collectIsPressedAsState()

    // Stop auto-advancing when pager is dragged or one of the pages is pressed
    val autoAdvance = !pagerIsDragged && !pageIsPressed

    if (autoAdvance) {
        LaunchedEffect(pagerState, pageInteractionSource) {
            while (true) {
                delay(2000.milliseconds)
                val nextPage = (pagerState.currentPage + 1) % images.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier,
    ) { page ->

        Card(
            modifier = Modifier.fillMaxSize()
                .graphicsLayer {
                    // Calculate the absolute offset for the current page from the
                    // scroll position. We use the absolute value which allows us to mirror
                    // any effects for both directions
                    val pageOffset = (
                            (pagerState.currentPage - page) + pagerState
                                .currentPageOffsetFraction
                            ).absoluteValue

                    // We animate the alpha, between 50% and 100%
                    alpha = lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                },
            shape = Theme.shapes.medium
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = images[page],
                contentDescription = "Product Image",
                contentScale = ContentScale.Crop,
            )
        }
    }
}


