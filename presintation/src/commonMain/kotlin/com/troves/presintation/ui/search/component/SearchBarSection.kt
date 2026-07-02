package com.troves.presintation.ui.search.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.inputFieldColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ListFilter
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.X
import com.troves.designsystem.theme.Theme
import com.troves.domain.entity.Brand
import org.jetbrains.compose.resources.painterResource
import org.koin.core.component.getScopeId
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_search

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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarSection(
    query: String,
    modifier: Modifier = Modifier,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onFilterClick: () -> Unit,
    searchSuggestions: List<String>
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    DockedSearchBar(
        colors = SearchBarDefaults.colors(inputFieldColors = inputFieldColors(focusedTextColor = Theme.colors.primary)),
        modifier = modifier
            .fillMaxWidth().background(
                color = Theme.colors.backGround
            )
            .padding(horizontal = 16.dp),
        inputField = {
            SearchBarDefaults.InputField(
                modifier = Modifier.background(
                    color = Theme.colors.surface
                ),
                query = query,
                onQueryChange = onQueryChange,
                onSearch = {
                    expanded = false
                    onSearch(it)
                },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                placeholder = {
                    Text("Search products", color = Theme.colors.primary)
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_search),
                        contentDescription = null,
                        tint = Theme.colors.primary
                    )
                },
                trailingIcon = {
                    Row {
                        AnimatedVisibility(
                            visible = query.isNotEmpty()
                        ) {
                            IconButton(
                                onClick = { onQueryChange("") }
                            ) {
                                Icon(
                                    imageVector = Lucide.X,
                                    contentDescription = "Clear search"
                                )
                            }
                        }

                        IconButton(
                            onClick = onFilterClick
                        ) {
                            Icon(
                                imageVector = Lucide.ListFilter,
                                contentDescription = "Open filters",
                                tint = Theme.colors.primary
                            )
                        }
                    }
                },
            )
        },
        expanded = expanded,
        onExpandedChange = {
            expanded = it
        },
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(
                color = Theme.colors.backGround
            )
        ) {
            items(
                items = searchSuggestions,
                itemContent = {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth().padding(
                                horizontal = Theme.spacing.small,
                                vertical = Theme.spacing.medium
                            ).clickable { onQueryChange(it) },
                        text = it,
                        color = Theme.colors.primary
                    )
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                }
            )
        }
    }
}