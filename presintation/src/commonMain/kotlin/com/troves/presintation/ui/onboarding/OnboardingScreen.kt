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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.button.SecondaryButton
import com.troves.designsystem.theme.Theme
import com.troves.presintation.core.mvi.ObserveEffect
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ads_placholder

private val onboardingPages = listOf(
    OnboardingPageInfo(
        title = "Discover\nCurated Styles",
        description = "Explore thousands of trendy fashion pieces handpicked just for you.",
        imageRes = Res.drawable.ads_placholder
    ),
    OnboardingPageInfo(
        title = "Find What\nFits You",
        description = "Find looks that match your style, mood, and everyday moments.",
        imageRes = Res.drawable.ads_placholder
    ),
    OnboardingPageInfo(
        title = "Shop. Love.\nRepeat.",
        description = "Shop your favorites, save what you love, and stay ahead of trends.",
        imageRes = Res.drawable.ads_placholder
    )
)

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is OnboardingEffect.NavigateToHome -> onOnboardingComplete()
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onIntent(
            OnboardingIntent.PageChanged(pagerState.currentPage, onboardingPages.size),
        )
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
            TextButton(onClick = { viewModel.onIntent(OnboardingIntent.CompleteOnboarding) }) {
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
                        targetValue = if (isSelected) {
                            Theme.colors.primary
                        } else {
                            Color(0xFFDCDCDC) // تعديل اللون الرصاصي الفاتح للـ dots غير النشطة هنا
                        },
                        animationSpec = tween(300)
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = Theme.spacing.extraSmall)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(indicatorColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            if (uiState.isLastPage) {
                PrimaryButton(
                    caption = "Let's get started",
                    onClick = { viewModel.onIntent(OnboardingIntent.CompleteOnboarding) },
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