package com.troves.presintation.ui.search.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.SearchCheck
import com.troves.designsystem.theme.Theme

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
    title: String = "No products found",
    message: String = "Try another keyword or adjust your filters."
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing.large, vertical = Theme.spacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = Lucide.SearchCheck,
            contentDescription = null,
            modifier = Modifier.size(Theme.size.large),
            tint = Theme.colors.primary
        )

        Spacer(Modifier.height(Theme.spacing.large))

        BasicText(
            text = title,
            style = Theme.typography.title.copy(
                color = Theme.colors.primaryFont
            )
        )

        Spacer(Modifier.height(Theme.spacing.small))

        BasicText(
            text = message,
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.secondaryFont
            )
        )
    }
}