package com.troves.presintation.ui.search.component

import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.SearchCheck
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.stringResource
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.empty_search_title
import troves.presintation.generated.resources.empty_search_msg
import com.troves.designsystem.components.emptystate.EmptyState
import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.search_empty_title
import troves.designsystem.generated.resources.search_empty_desc

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
fun EmptySearchResult(
    modifier: Modifier = Modifier,
    title: String = stringResource(Res.string.empty_search_title),
    message: String = stringResource(Res.string.empty_search_msg)
) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier,
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        EmptyState(
            title = stringResource(Res.string.search_empty_title),
            description = stringResource(Res.string.search_empty_desc),
            // Using custom icon since SearchCheck is from Lucide not Icons.Default
            customIcon = {
                androidx.compose.material3.Icon(
                    imageVector = Lucide.SearchCheck,
                    contentDescription = null,
                    tint = com.troves.designsystem.theme.Theme.colors.primary,
                    modifier = Modifier.size(34.dp)
                )
            }
        )
    }
}