package com.troves.presintation.ui.search.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
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

/**
 * Copyright (c) 2026 Wahid Ali Wahid Hussien.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Author: Wahid Ali Wahid Hussien
 * Created: 30/06/2026
*/
@Composable
fun ProductGrid(
    products: List<Product>,
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
    onProductClick: (Product) -> Unit,
    onFavoriteClick: (Product) -> Unit = {},
    favoriteProductIds: Set<String> = emptySet(),
) {
    val placeholder = painterResource(Res.drawable.img_onboarding1)
    val starIcon = painterResource(Res.drawable.ic_star)
    val heartIcon = painterResource(Res.drawable.ic_heart)

    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = contentPadding,
    ) {
        if (header != null) {
            item(span = { GridItemSpan(2) }) {
                header()
            }
        }

        items(
            items = products,
            key = { it.id }
        ) { product ->
            MainCard(
                title = product.title,
                price = formatPrice(product.price),
                rating = product.rating.toDouble(),
                imagePainter = rememberAsyncImagePainter(
                    model = product.imageUrl,
                    placeholder = placeholder,
                    error = placeholder,
                ),
                ratingIconPainter = starIcon,
                favoriteIconPainter = heartIcon,
                isFavorite = product.id.toString() in favoriteProductIds,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onProductClick(product) },
                onFavoriteClick = { onFavoriteClick(product) },
            )
        }
    }
}