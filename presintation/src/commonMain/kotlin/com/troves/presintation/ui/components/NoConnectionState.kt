package com.troves.presintation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.troves.designsystem.components.emptystate.EmptyState
import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.no_connection_desc
import troves.designsystem.generated.resources.no_connection_retry
import troves.designsystem.generated.resources.no_connection_title


@Composable
fun NoConnectionState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        EmptyState(
            title = stringResource(Res.string.no_connection_title),
            description = stringResource(Res.string.no_connection_desc),
            icon = Icons.Default.WifiOff,
            actionLabel = stringResource(Res.string.no_connection_retry),
            onActionClick = onRetry,
            actionIcon = Icons.Default.Refresh,
        )
    }
}
