package com.troves.presintation.ui.search.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.History
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.X
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.stringResource
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.recent_searches
import troves.presintation.generated.resources.clear_all

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
fun RecentSearchSection(
    recentSearches: List<String>,
    modifier: Modifier = Modifier,
    onSearchClick: (String) -> Unit,
    onRemoveClick: (String) -> Unit,
    onClearAllClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = recentSearches.isNotEmpty(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.recent_searches),
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = onClearAllClick) {
                    Text(stringResource(Res.string.clear_all), color = Theme.colors.primary)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                recentSearches.forEach { query ->
                    RecentSearchItem(
                        query = query,
                        onClick = { onSearchClick(query) },
                        onRemoveClick = { onRemoveClick(query) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentSearchItem(
    query: String,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemBg = if (androidx.compose.foundation.isSystemInDarkTheme()) Color(0xFF2C2C2C) else Color(0xFFF5F5F5)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(itemBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Lucide.History,
            contentDescription = null,
            tint = Theme.colors.secondaryFont,
            modifier = Modifier.size(20.dp)
        )
        
        Text(
            text = query,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge
        )
        
        IconButton(
            onClick = onRemoveClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Lucide.X,
                contentDescription = "Remove recent search",
                tint = Theme.colors.hint,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}