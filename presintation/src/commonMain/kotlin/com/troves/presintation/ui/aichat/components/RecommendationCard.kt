package com.troves.presintation.ui.aichat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import com.troves.presintation.ui.aichat.AiProductUi
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_chevron_right

@Composable
fun RecommendationCard(
    products: List<AiProductUi>,
    viewAllLabel: String,
    onProductClick: (AiProductUi) -> Unit,
    onFavoriteClick: (AiProductUi) -> Unit,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.shapes.large)
            .background(Theme.colors.surface)
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
        ) {
            items(products, key = { it.id }) { product ->
                AiProductCard(
                    product = product,
                    onClick = { onProductClick(product) },
                    onFavoriteClick = { onFavoriteClick(product) },
                )
            }
        }

//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable(onClick = onViewAll)
//                .padding(vertical = 4.dp),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            Text(
//                text = viewAllLabel,
//                style = Theme.typography.body.medium,
//                color = Theme.colors.primary,
//            )
//            Icon(
//                painter = painterResource(Res.drawable.ic_chevron_right),
//                contentDescription = null,
//                tint = Theme.colors.primary,
//                modifier = Modifier.size(18.dp),
//            )
//        }
    }
}
