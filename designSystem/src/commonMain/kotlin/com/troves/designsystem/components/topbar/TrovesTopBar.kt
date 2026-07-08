package com.troves.designsystem.components.topbar

import androidx.compose.foundation.BorderStroke // Added
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color       // Added
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp             // Added
import com.troves.designsystem.theme.SpTheme
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.app_name
import troves.designsystem.generated.resources.ic_ai_sparkles
import troves.designsystem.generated.resources.ic_cart
import troves.designsystem.generated.resources.ic_search
import org.jetbrains.compose.resources.stringResource

/**
 * The Home screen top bar: the "Troves" wordmark with trailing search and cart
 * actions. A thin, opinionated wrapper over [BaseTopAppBar].
 */
@Composable
fun TrovesTopBar(
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit,
    onAiClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(Res.string.app_name),
    cartBadgeCount: Int = 0,
    border: BorderStroke? = null,
) {
    BaseTopAppBar(
        modifier = modifier,
        title = title,
        border = border,
        actions = listOf(
            TopBarAction(
                icon = painterResource(Res.drawable.ic_ai_sparkles),
                contentDescription = "AI Chat",
                onClick = onAiClick,
            ),
            TopBarAction(
                icon = painterResource(Res.drawable.ic_search),
                contentDescription = "Search",
                onClick = onSearchClick,
            ),
            TopBarAction(
                icon = painterResource(Res.drawable.ic_cart),
                contentDescription = "Cart",
                badgeCount = cartBadgeCount,
                onClick = onCartClick,
            ),
        ),
    )
}

@Preview
@Composable
private fun TrovesTopBarPreview() {
    SpTheme {
        TrovesTopBar(
            onSearchClick = {},
            onCartClick = {},
            border = BorderStroke(1.dp, Color.LightGray),
            onAiClick = {}
        )
    }
}