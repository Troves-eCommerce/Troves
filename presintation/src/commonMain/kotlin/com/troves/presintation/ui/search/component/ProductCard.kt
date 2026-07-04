package com.troves.presintation.ui.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.troves.designsystem.theme.Theme
import com.troves.domain.entity.Product
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_heart
import troves.designsystem.generated.resources.ic_star

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
fun ProductCard(
    product: Product,
    onClick: (Product) -> Unit,
    onFavoriteClick: (Product) -> Unit,
    isFavorite: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val parsedPrice = product.price.toDoubleOrNull() ?: 0.0
    val discountPercent = if (product.id % 3 == 0L) 28 else 30
    val compareAtPriceStr: String? = if (parsedPrice > 0.0) {
        val factor = if (discountPercent == 28) 0.712 else 0.7065
        val raw = parsedPrice / factor
        val rounded = ((raw + 0.005) * 100).toLong() / 100.0
        val parts = rounded.toString().split(".")
        val decimals = if (parts.size > 1) parts[1].padEnd(2, '0').take(2) else "00"
        "${parts[0]}.$decimals"
    } else null

    val showDiscountBadge = product.id % 3 == 0L
    val displayRating = (4.5 + ((product.id % 5) * 0.1))
    val reviewCount = (product.id * 17) % 150 + 50

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.shapes.large)
            .background(Theme.colors.surface)
            .clickable { onClick(product) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.85f)
                .clip(Theme.shapes.large)
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            if (showDiscountBadge) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFC88A58))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    BasicText(
                        text = "-$discountPercent%",
                        style = Theme.typography.body.small.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                        ),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { onFavoriteClick(product) },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_heart),
                    contentDescription = null,
                    tint = if (isFavorite) Theme.colors.primary else Color.Black,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_star),
                    contentDescription = null,
                    tint = Theme.colors.amber,
                    modifier = Modifier.size(14.dp),
                )
                BasicText(
                    text = "$displayRating ($reviewCount)",
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.secondaryFont,
                        fontSize = 12.sp,
                    ),
                )
            }

            BasicText(
                text = product.title,
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Medium,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                BasicText(
                    text = "$${product.price}",
                    style = Theme.typography.body.large.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Bold,
                    ),
                )

                compareAtPriceStr?.let { compareAt ->
                    BasicText(
                        text = "$$compareAt",
                        style = Theme.typography.body.small.copy(
                            color = Theme.colors.hint,
                            textDecoration = TextDecoration.LineThrough,
                        ),
                    )
                }
            }
        }
    }
}