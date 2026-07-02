package com.troves.presintation.ui.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.troves.designsystem.theme.Theme
import com.troves.domain.entity.Product
import kotlin.math.absoluteValue
import kotlin.to

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

private val ProductAspectRatios = listOf(
    0.72f,
    0.85f,
    1.1f,
    1.3f
)

@Composable
private fun rememberAspectRatio(productId: Long): Float {
    return remember(productId) {
        ProductAspectRatios[(productId.hashCode().absoluteValue) % ProductAspectRatios.size]
    }
}

@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    onClick: (Product) -> Unit,
) {

    ElevatedCard(
        modifier = modifier,
        onClick = { onClick(product) },
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(rememberAspectRatio(product.id))
        ) {

            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = product.imageUrl,
                contentDescription = product.title,
                contentScale = ContentScale.Crop
            )

            ProductGradient()

            Text(
                text = product.title,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )

        }

    }

}

@Composable
private fun ProductGradient() {
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                alpha = .99f
            }
            .drawWithCache {
                val gradient = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to Color.Transparent,
                        .55f to Color.Transparent,
                        1f to Color.Gray.copy(alpha = 0.82f)
                    )
                )
                onDrawWithContent {
                    drawContent()
                    drawRect(gradient)
                }
            }
    )
}