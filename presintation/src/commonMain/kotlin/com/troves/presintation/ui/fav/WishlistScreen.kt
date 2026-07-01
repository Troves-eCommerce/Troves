package com.troves.presintation.ui.fav

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.troves.designsystem.components.cards.MainCard
import com.troves.designsystem.theme.Theme
import com.troves.domain.entity.Product
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_heart
import troves.designsystem.generated.resources.ic_star
import troves.designsystem.generated.resources.img_onboarding1

@Composable
fun WishlistScreen(
    onNavigateToProduct: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: WishlistViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val placeholder = painterResource(Res.drawable.img_onboarding1)
    val starIcon = painterResource(Res.drawable.ic_star)
    val heartIcon = painterResource(Res.drawable.ic_heart)

    var productToRemove by remember { mutableStateOf<Product?>(null) }
    var showClearAllConfirmation by remember { mutableStateOf(false) }
    var showLoginRequiredDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is WishlistUiEffect.NavigateToProduct -> onNavigateToProduct(effect.productId)
                is WishlistUiEffect.ShowToast -> snackbarHostState.showSnackbar(effect.message)
                WishlistUiEffect.ShowLoginRequiredDialog -> showLoginRequiredDialog = true
            }
        }
    }

    if (productToRemove != null) {
        AlertDialog(
            onDismissRequest = { productToRemove = null },
            title = { Text("Remove from Wishlist") },
            text = { Text("Are you sure you want to remove ${productToRemove?.title} from your wishlist?") },
            confirmButton = {
                TextButton(onClick = {
                    productToRemove?.let { viewModel.onIntent(WishlistIntent.RemoveClicked(it)) }
                    productToRemove = null
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToRemove = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showClearAllConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearAllConfirmation = false },
            title = { Text("Clear Wishlist") },
            text = { Text("Are you sure you want to clear your entire wishlist?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onIntent(WishlistIntent.ClearAllClicked)
                    showClearAllConfirmation = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showLoginRequiredDialog) {
        AlertDialog(
            onDismissRequest = { showLoginRequiredDialog = false },
            title = { Text("Login Required") },
            text = { Text("You need to be logged in to manage your wishlist.") },
            confirmButton = {
                TextButton(onClick = {
                    showLoginRequiredDialog = false
                    onNavigateToRegister()
                }) {
                    Text("Log In")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginRequiredDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Theme.colors.backGround)) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = "Your Wishlist",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Theme.colors.primaryFont,
            )

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${state.items.size} items saved for later.",
                    color = Theme.colors.secondaryFont,
                    fontSize = 14.sp,
                )
                if (state.items.isNotEmpty()) {
                    Text(
                        text = "Clear All",
                        color = Theme.colors.secondaryFont,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            showClearAllConfirmation = true
                        },
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
                state.errorMessage != null -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(state.errorMessage.orEmpty(), color = Theme.colors.secondaryFont)
                }
                state.isEmpty -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Your wishlist is empty", color = Theme.colors.secondaryFont, fontSize = 16.sp)
                }
                else -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                ) {
                    items(state.items, key = { it.id }) { product ->
                        MainCard(
                            title = product.title,
                            price = "$${product.price}",
                            rating = 4.5,
                            imagePainter = rememberAsyncImagePainter(
                                model = product.imageUrl,
                                placeholder = placeholder,
                                error = placeholder,
                            ),
                            ratingIconPainter = starIcon,
                            favoriteIconPainter = heartIcon,
                            isFavorite = true,
                            onClick = { viewModel.onIntent(WishlistIntent.ProductClicked(product)) },
                            onFavoriteClick = { productToRemove = product },
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        )
    }
}