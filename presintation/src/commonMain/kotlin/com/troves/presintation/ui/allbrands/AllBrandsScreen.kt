package com.troves.presintation.ui.allbrands

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.shimmer.shimmerEffect
import com.troves.designsystem.components.topbar.IconBox
import com.troves.designsystem.theme.Theme
import com.troves.domain.entity.Brand
import com.troves.presintation.core.mvi.ObserveEffect
import com.troves.presintation.ui.allbrands.components.BrandCard
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res as DesignRes
import troves.designsystem.generated.resources.ic_arrow_back
import troves.designsystem.generated.resources.img_onboarding1
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.products_retry

private val BrandCardHeight = 140.dp

@Composable
fun AllBrandsScreen(
    onNavigateToProducts: (sourceType: String, sourceId: String, sourceName: String) -> Unit = { _, _, _ -> },
    onNavigateBack: () -> Unit = {},
    viewModel: AllBrandsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is AllBrandsEffect.NavigateToProducts -> onNavigateToProducts(
                effect.sourceType,
                effect.sourceId,
                effect.sourceName,
            )
            is AllBrandsEffect.NavigateBack -> onNavigateBack()
            is AllBrandsEffect.ShowToast ->
                scope.launch { snackbarHostState.showSnackbar(effect.message) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            AllBrandsToolbar(
                title = "Brands",
                onBackClick = { viewModel.onIntent(AllBrandsIntent.OnBackClick) },
            )

            when {
                state.isLoading -> AllBrandsShimmer()

                state.hasError -> AllBrandsError(
                    message = state.errorMessage.orEmpty(),
                    onRetry = { viewModel.onIntent(AllBrandsIntent.Retry) },
                )

                else -> AllBrandsList(
                    onBrandClick = { viewModel.onIntent(AllBrandsIntent.BrandClicked(it)) },
                    state = state,
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(Theme.spacing.medium),
        )
    }
}

@Composable
private fun AllBrandsToolbar(
    title: String,
    onBackClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        IconBox(
            icon = painterResource(DesignRes.drawable.ic_arrow_back),
            contentDescription = "Navigate up",
            onClick = onBackClick,
        )
        BasicText(
            text = title,
            modifier = Modifier.weight(1f),
            style = Theme.typography.title.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

@Composable
private fun AllBrandsList(
    state: AllBrandsUiState,
    onBrandClick: (Brand) -> Unit,
) {
    val placeholder = painterResource(DesignRes.drawable.img_onboarding1)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = Theme.spacing.medium,
            end = Theme.spacing.medium,
            bottom = Theme.spacing.large,
        ),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        items(state.brands, key = { it.id }) { brand ->
            BrandCard(
                name = brand.name,
                imagePainter = rememberAsyncImagePainter(
                    model = brand.logoUrl,
                    placeholder = placeholder,
                    error = placeholder,
                ),
                onClick = { onBrandClick(brand) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BrandCardHeight),
            )
        }
    }
}

@Composable
private fun AllBrandsShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        repeat(6) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BrandCardHeight)
                    .clip(Theme.shapes.medium)
                    .shimmerEffect(),
            )
        }
    }
}

@Composable
private fun AllBrandsError(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Theme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium, Alignment.CenterVertically),
    ) {
        BasicText(
            text = message,
            style = Theme.typography.body.large.copy(color = Theme.colors.error),
        )
        PrimaryButton(
            caption = stringResource(Res.string.products_retry),
            onClick = onRetry,
        )
    }
}
