package com.troves.presintation.ui.fav.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.troves.designsystem.components.cards.MainCard
import com.troves.designsystem.util.formatPrice
import com.troves.domain.entity.Product
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_heart
import troves.designsystem.generated.resources.ic_star
import troves.designsystem.generated.resources.img_onboarding1


@Composable
fun WishlistGrid(
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    onRemoveClick: (Product) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 8.dp
    ),
) {

    val placeholder = painterResource(Res.drawable.img_onboarding1)
    val starIcon = painterResource(Res.drawable.ic_star)
    val heartIcon = painterResource(Res.drawable.ic_heart)

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {

        items(
            items = products,
            key = { it.id }
        ) { product ->

            MainCard(
                title = product.title,
                price = formatPrice(product.price),
                rating = 4.5,
                imagePainter = rememberAsyncImagePainter(
                    model = product.imageUrl,
                    placeholder = placeholder,
                    error = placeholder,
                ),
                ratingIconPainter = starIcon,
                favoriteIconPainter = heartIcon,
                isFavorite = true,
                modifier = Modifier.width(170.dp),
                onClick = {
                    onProductClick(product)
                },
                onFavoriteClick = {
                    onRemoveClick(product)
                }
            )
        }
    }
}