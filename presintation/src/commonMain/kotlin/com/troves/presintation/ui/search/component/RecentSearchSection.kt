package com.troves.presintation.ui.search.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.History
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.X
import com.troves.presintation.ui.search.RecentSearchUi

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
    recentSearches: List<RecentSearchUi>,
    modifier: Modifier = Modifier,
    onSearchClick: (String) -> Unit,
    onRemoveClick: (String) -> Unit,
) {
    AnimatedVisibility(
        visible = recentSearches.isNotEmpty(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        Column(
            modifier = modifier.fillMaxWidth()
        ) {

            Text(
                text = "Recent Searches",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                )
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(
                    items = recentSearches,
                    key = { it }
                ) { query ->

                    RecentSearchChip(
                        query = query.query,
                        onClick = {
                            onSearchClick(query.query)
                        },
                        onRemoveClick = {
                            onRemoveClick(query.query)
                        }
                    )

                }

            }
        }
    }
}
@Composable
private fun RecentSearchChip(
    query: String,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    AssistChip(
        modifier = modifier.animateContentSize(),
        onClick = onClick,
        label = {
            Text(
                text = query,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Lucide.History,
                contentDescription = null,
                modifier = Modifier.size(AssistChipDefaults.IconSize)
            )
        },
        trailingIcon = {
            IconButton(
                onClick = onRemoveClick,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Lucide.X,
                    contentDescription = "Remove recent search"
                )
            }
        }
    )
}
/*
* RecentSearchSection(
    recentSearches = state.recentSearches,
    onSearchClick = {
        onIntent(SearchIntent.SearchQueryChange(it))
    },
    onRemoveClick = {
        // ViewModel action
    }
)*/
