package com.troves.presintation.ui.search.component

import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.stringResource
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.search_discovery_title
import troves.presintation.generated.resources.search_discovery_desc
import troves.presintation.generated.resources.search_hint_title
import troves.presintation.generated.resources.search_hint_desc

@Composable
fun IdleSearchScreen(
    recentSearches: List<String>,
    onSearchClick: (String) -> Unit,
    onRemoveClick: (String) -> Unit,
    onClearAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        if (recentSearches.isEmpty()) {
            SearchDiscoveryPrompt(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .padding(horizontal = 32.dp, vertical = 48.dp)
            )
        } else {
            SearchHintBanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            )

            RecentSearchSection(
                recentSearches = recentSearches,
                onSearchClick = onSearchClick,
                onRemoveClick = onRemoveClick,
                onClearAllClick = onClearAllClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SearchDiscoveryPrompt(modifier: Modifier = Modifier) {
    val isDark = isSystemInDarkTheme()
    val iconBg = if (isDark) Color(0xFF2C2C2C) else Theme.colors.primary.copy(alpha = 0.08f)

    // Gentle pulse animation on the icon
    val pulse = rememberInfiniteTransition(label = "pulse")
    val scale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = Ease),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_scale"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .scale(scale)
                .size(96.dp)
                .background(iconBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Lucide.Search,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Theme.colors.primary
            )
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = stringResource(Res.string.search_discovery_title),
            style = MaterialTheme.typography.headlineSmall,
            color = Theme.colors.primaryFont,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = stringResource(Res.string.search_discovery_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = Theme.colors.secondaryFont,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SearchHintBanner(modifier: Modifier = Modifier) {
    val isDark = isSystemInDarkTheme()
    val iconBg = if (isDark) Color(0xFF2C2C2C) else Theme.colors.primary.copy(alpha = 0.08f)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(iconBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Lucide.Search,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = Theme.colors.primary
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(Res.string.search_hint_title),
            style = MaterialTheme.typography.titleMedium,
            color = Theme.colors.primaryFont,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = stringResource(Res.string.search_hint_desc),
            style = MaterialTheme.typography.bodySmall,
            color = Theme.colors.secondaryFont,
            textAlign = TextAlign.Center,
        )
    }
}
