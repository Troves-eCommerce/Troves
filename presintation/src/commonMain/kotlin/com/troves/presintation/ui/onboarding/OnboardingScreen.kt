package com.troves.presintation.ui.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.button.SecondaryButton
import com.troves.designsystem.theme.Theme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.img_onboarding1
import troves.designsystem.generated.resources.img_onboarding2

private val onboardingPages = listOf(
    OnboardingPageInfo(
        title = "Uncover Hidden Gems",
        description = "Browse thousands of unique products hand-picked from top suppliers around the world.",
        imageRes = Res.drawable.img_onboarding1
    ),
    OnboardingPageInfo(
        title = "Explore What's Out There",
        description = "Find rare, trending, and high-demand products before anyone else does.",
        imageRes = Res.drawable.img_onboarding2
    ),
    OnboardingPageInfo(
        title = "Handpick What Sells",
        description = "Select only the products that match your brand, your audience, and your vision.",
        imageRes = Res.drawable.img_onboarding1
    )
)

@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is OnboardingUiEvent.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.updatePage(pagerState.currentPage, onboardingPages.size)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPageContent(pageInfo = onboardingPages[page])
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = Theme.spacing.medium, end = Theme.spacing.large),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { viewModel.completeOnboarding() }) {
                Text(
                    text = "Skip",
                    style = Theme.typography.body.medium,
                    color = Theme.colors.secondaryFont
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(
                    start = Theme.spacing.large,
                    end = Theme.spacing.large,
                    bottom = Theme.spacing.extraLarge
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(onboardingPages.size) { iteration ->
                    val isSelected = pagerState.currentPage == iteration
                    val indicatorColor by animateColorAsState(
                        targetValue = if (isSelected) Theme.colors.primary
                        else Theme.colors.surfaceVariant,
                        animationSpec = tween(300)
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = Theme.spacing.extraSmall)
                            .size(Theme.spacing.small)
                            .clip(CircleShape)
                            .background(indicatorColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Theme.spacing.extraLarge))
            if (uiState.isLastPage) {
                PrimaryButton(
                    caption = "Let's get started",
                    onClick = { viewModel.completeOnboarding() },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                SecondaryButton(
                    caption = "Next",
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}