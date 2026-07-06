package com.troves.presintation.ui.aichat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.troves.designsystem.theme.Theme
import com.troves.presintation.ui.aichat.AiProductUi
import androidx.compose.foundation.layout.Row
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_heart
import troves.designsystem.generated.resources.ic_star

@Composable
fun AiProductCard(
    product: AiProductUi,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(150.dp)
            .clip(Theme.shapes.large)
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(Theme.shapes.large)
                .background(Theme.colors.backGround),
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(onClick = onFavoriteClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_heart),
                    contentDescription = null,
                    tint = if (product.isFavorite) Theme.colors.primary else Color.Black,
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        Text(
            text = product.title,
            style = Theme.typography.body.small,
            color = Theme.colors.primaryFont,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = product.priceFormatted,
            style = Theme.typography.body.medium.copy(fontWeight = FontWeight.Bold),
            color = Theme.colors.primary,
        )

        product.numericId.toLongOrNull()?.let { id ->
            val rating = "4.${5 + (id % 5)}"          // app-wide placeholder convention (no real rating data)
            val reviews = (id * 17) % 150 + 50
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
                Text(
                    text = "$rating ($reviews)",
                    style = Theme.typography.hint.small,
                    color = Theme.colors.secondaryFont,
                )
            }
        }
    }
}
