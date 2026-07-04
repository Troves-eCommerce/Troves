package com.troves.presintation.ui.search.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.troves.designsystem.theme.Theme
import com.troves.domain.entity.Brand

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
fun BrandRow(
    brands: List<Brand>,
    selectedBrand: String?,
    modifier: Modifier = Modifier,
    onBrandSelected: (Brand) -> Unit,
) {

    if (brands.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        BasicText(
            text = "Brands",
            style = Theme.typography.body.large.copy(
                color = Theme.colors.primaryFont
            ),
            modifier = Modifier.padding(
                Theme.spacing.medium
            ),
        )

        LazyRow(
            contentPadding = PaddingValues(Theme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
        ) {

            items(
                items = brands,
                key = Brand::id
            ) { brand ->

                BrandItem(
                    brand = brand,
                    selected = selectedBrand == brand.name,
                    onClick = {
                        onBrandSelected(brand)
                    }
                )

            }

        }

    }

}

@Composable
private fun BrandItem(
    brand: Brand,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    val scale by animateFloatAsState(
        targetValue = if (selected) 1.08f else 1f,
        label = "BrandScale"
    )

    Column(
        modifier = modifier.width(Theme.size.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ElevatedCard(
            onClick = onClick,
            modifier = Modifier
                .size(Theme.size.large)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            shape = CircleShape,
            colors = CardDefaults.elevatedCardColors(
                containerColor = if (selected)
                    Theme.colors.surfaceVariant
                else
                    Theme.colors.surface
            ),
        ) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                AsyncImage(
                    model = brand.logoUrl,
                    contentDescription = brand.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                    ,
                    contentScale = ContentScale.Crop
                )

                this@ElevatedCard.AnimatedVisibility(
                    modifier = Modifier.align(Alignment.TopEnd),
                    visible = selected
                ) {

                    Surface(
                        modifier = Modifier.padding(Theme.spacing.small),
                        shape = CircleShape,
                        color =  Color.Transparent
                    ) {

                        Icon(
                            imageVector = Lucide.Check,
                            contentDescription = null,
                            tint = Theme.colors.primary,
                            modifier = Modifier
                                .padding(Theme.spacing.small)
                                .size(Theme.size.medium)
                        )

                    }

                }

            }

        }

        Spacer(Modifier.height(8.dp))

        BasicText(
            text = brand.name,
            style = Theme.typography.body.small.copy(
                color = Theme.colors.primaryFont
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

    }

}