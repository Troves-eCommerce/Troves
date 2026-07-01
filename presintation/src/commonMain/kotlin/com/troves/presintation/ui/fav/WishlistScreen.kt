package com.troves.presintation.ui.fav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.troves.designsystem.components.dialog.TrovesDialog
import com.troves.designsystem.theme.Theme
import com.troves.domain.entity.Product
import com.troves.presintation.core.mvi.ObserveEffect
import com.troves.presintation.ui.fav.components.WishlistContent
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WishlistScreen(
    onNavigateToProduct: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: WishlistViewModel = koinViewModel(),
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var productToRemove by remember { mutableStateOf<Product?>(null) }
    var showClearDialog by remember { mutableStateOf(false) }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {

            is WishlistEffect.NavigateToProduct -> {
                onNavigateToProduct(effect.productId)
            }

            is WishlistEffect.ShowToast -> {
                scope.launch {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }

            WishlistEffect.ShowLoginRequiredDialog -> {
                onNavigateToRegister()
            }
        }
    }

    productToRemove?.let { product ->

        TrovesDialog(
            title = "Remove Item",
            message = "Are you sure you want to remove \"${product.title}\" from your wishlist?",
            confirmText = "Remove",
            onConfirm = {
                viewModel.onIntent(WishlistIntent.RemoveClicked(product))
                productToRemove = null
            },
            onDismiss = {
                productToRemove = null
            }
        )
    }

    if (showClearDialog) {

        TrovesDialog(
            title = "Clear Wishlist",
            message = "This action will remove all wishlist items.",
            confirmText = "Clear",
            onConfirm = {
                viewModel.onIntent(WishlistIntent.ClearAllClicked)
                showClearDialog = false
            },
            onDismiss = {
                showClearDialog = false
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {

        WishlistContent(
            state = state,
            onIntent = viewModel::onIntent,
            onRemoveClick = {
                productToRemove = it
            },
            onClearAllClick = {
                showClearDialog = true
            }
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp)
        )
    }
}