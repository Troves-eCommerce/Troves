package com.troves.presintation.ui.ProductDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import com.troves.domain.Product
import com.troves.presintation.components.productDetailsScreen.HorizontalPageIndicator
import com.troves.presintation.components.productDetailsScreen.ImagePager


@Composable
fun ProductDetailsScreen(
    product: Product,
    onAddToFavorite: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {


    val images = listOf("image1", "image2") // TODO("Fetch the network")
    val pagerState = rememberPagerState { images.size }
    Scaffold(
        topBar = {},
        bottomBar = {},
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                ImagePager(
                    images = images,
                    modifier = Modifier.fillMaxSize(),
                    pagerState = pagerState,
                )

                HorizontalPageIndicator(
                    images.size,
                    pagerState.currentPage,
                    selectedColor = Theme.colors.onPrimary,
                    unSelectedColor = Theme.colors.disable,
                    modifier = Modifier.size(width = Theme.size.large, height = Theme.size.small)
                        .align(
                            Alignment.BottomCenter
                        ).padding(bottom = Theme.size.small)
                )
            }

        }


    }
}

@Preview
@Composable
fun ProductDetailsScreenPreview() {
    ProductDetailsScreen(
        product = Product(
            id = 1,
            title = "TODO()",
            vendor = "TODO()",
            price = "TODO()",
            imageUrl = "TODO()",
            status = "TODO()"
        ),
        onAddToFavorite = { TODO() },
        onAddToCart = { TODO() },
        modifier = Modifier
    )
}