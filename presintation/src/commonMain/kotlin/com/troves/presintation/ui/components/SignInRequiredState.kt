package com.troves.presintation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.troves.designsystem.components.emptystate.EmptyState
import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.auth_required_action
import troves.designsystem.generated.resources.auth_required_title

/**
 * Guest / signed-out placeholder shown on screens that require authentication
 * (orders, wishlist, ...). Reuses [EmptyState] so it stays visually consistent
 * with the app's other empty states, and offers a button into the auth flow.
 */
@Composable
fun SignInRequiredState(
    description: String,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        EmptyState(
            title = stringResource(Res.string.auth_required_title),
            description = description,
            icon = Icons.Default.Lock,
            actionLabel = stringResource(Res.string.auth_required_action),
            onActionClick = onSignIn,
        )
    }
}
