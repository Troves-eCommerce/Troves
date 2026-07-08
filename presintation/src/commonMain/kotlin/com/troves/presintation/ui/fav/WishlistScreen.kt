package com.troves.presintation.ui.fav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.SnackbarHostState
import com.troves.designsystem.components.toast.TrovesSnackbarHost
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.troves.designsystem.components.topbar.BaseTopAppBar
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.troves.designsystem.components.dialog.LoginRequiredDialog
import com.troves.designsystem.components.dialog.TrovesDialog
import com.troves.designsystem.theme.Theme
import com.troves.domain.entity.Product
import com.troves.presintation.core.mvi.ObserveEffect
import com.troves.presintation.ui.components.SignInRequiredState
import com.troves.presintation.ui.fav.components.WishlistContent
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.wishlist_auth_required_desc
import troves.presintation.generated.resources.wishlist_header_title

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
    var showLoginRequiredDialog by remember { mutableStateOf(false) }

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
                showLoginRequiredDialog = true
            }
        }
    }

    if (showLoginRequiredDialog) {
        LoginRequiredDialog(
            message = "You need to be logged in to view your wishlist.",
            onLoginClick = {
                showLoginRequiredDialog = false
                onNavigateToRegister()
            },
            onDismiss = {
                showLoginRequiredDialog = false
            }
        )
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        BaseTopAppBar(title = stringResource(troves.presintation.generated.resources.Res.string.wishlist_header_title))
        
        Box(modifier = Modifier.fillMaxSize().weight(1f)) {

        if (state.isNotSignedIn) {
            SignInRequiredState(
                description = stringResource(Res.string.wishlist_auth_required_desc),
                onSignIn = onNavigateToRegister,
            )
        } else {
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
        }

        TrovesSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(16.dp)
        )
        }
    }
}