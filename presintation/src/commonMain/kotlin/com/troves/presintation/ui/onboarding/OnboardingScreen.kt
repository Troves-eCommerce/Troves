package com.troves.presintation.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.theme.Theme
import com.troves.presintation.core.mvi.ObserveEffect
import kotlinx.coroutines.launch
import kotlin.math.abs
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.img_onboarding
import troves.designsystem.generated.resources.img_onboarding0
import troves.designsystem.generated.resources.img_onboarding_1

private val onboardingPages = listOf(
    OnboardingPageInfo(
        title = "Discover\nCurated Styles",
        description = "Explore thousands of trendy fashion pieces handpicked just for you.",
        imageRes = Res.drawable.img_onboarding0
    ),
    OnboardingPageInfo(
        title = "Find What\nFits You",
        description = "Find looks that match your style, mood, and everyday moments.",
        imageRes = Res.drawable.img_onboarding_1
    ),
    OnboardingPageInfo(
        title = "Shop. Love.\nRepeat.",
        description = "Shop your favorites, save what you love, and stay ahead of trends.",
        imageRes = Res.drawable.img_onboarding
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

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }
    val screenAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(500),
        label = "screenAlpha"
    )

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
            .graphicsLayer { alpha = screenAlpha }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val pageOffset = (
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    ).coerceIn(-1f, 1f)

            OnboardingPageContent(
                pageInfo = onboardingPages[page],
                modifier = Modifier.graphicsLayer {
                    alpha = 1f - abs(pageOffset).coerceIn(0f, 1f)
                    val scale = lerp(0.85f, 1f, 1f - abs(pageOffset))
                    scaleX = scale
                    scaleY = scale
                    translationX = size.width * pageOffset * 0.15f
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = Theme.spacing.medium, end = Theme.spacing.large),
            horizontalArrangement = Arrangement.End
        ) {
            AnimatedVisibility(
                visible = !uiState.isLastPage,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(200))
            ) {
                TextButton(onClick = { viewModel.onIntent(OnboardingIntent.CompleteOnboarding) }) {
                    Text(
                        text = "Skip",
                        style = Theme.typography.body.medium,
                        color = Theme.colors.secondaryFont
                    )
                }
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
                        targetValue = if (isSelected) Theme.colors.primary else Color(0xFFE0E0E0),
                        animationSpec = tween(300),
                        label = "dotColor"
                    )
                    val indicatorWidth by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 8.dp,
                        animationSpec = tween(300),
                        label = "dotWidth"
                    )

                    Box(
                        modifier = Modifier
                            .padding(horizontal = Theme.spacing.extraSmall)
                            .height(8.dp)
                            .width(indicatorWidth)
                            .clip(CircleShape)
                            .background(indicatorColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                caption = if (uiState.isLastPage) "Let's get started" else "Next",
                onClick = {
                    if (uiState.isLastPage) {
                        viewModel.onIntent(OnboardingIntent.CompleteOnboarding)
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}