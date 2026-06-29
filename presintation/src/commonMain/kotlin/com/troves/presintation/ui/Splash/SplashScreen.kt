package com.troves.presintation.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import com.troves.designsystem.theme.Theme
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.troves_logo

@Composable
fun SplashScreen(onNavigateToOnboarding: () -> Unit) {
    val logoScale = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }
    val logoTranslationY = remember { Animatable(40f) }

    val textAlpha = remember { Animatable(0f) }
    val textTranslationY = remember { Animatable(20f) }

    val taglineAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        }
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 900,
                    easing = { input ->
                        val tension = 1.5f
                        val t = input - 1.0f
                        t * t * ((tension + 1) * t + tension) + 1.0f
                    }
                )
            )
        }
        launch {
            logoTranslationY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        }
        delay(300)
        launch {
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600, easing = LinearEasing)
            )
        }
        launch {
            textTranslationY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
            )
        }
        delay(200)
        launch {
            taglineAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500, easing = LinearEasing)
            )
        }

        delay(1500)
        onNavigateToOnboarding()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(Res.drawable.troves_logo),
                contentDescription = "Troves Logo",
                modifier = Modifier
                    .size(220.dp)
                    .graphicsLayer(
                        scaleX = logoScale.value,
                        scaleY = logoScale.value,
                        translationY = logoTranslationY.value
                    )
                    .alpha(logoAlpha.value)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Theme.colors.primary)) {
                        append("T")
                    }
                    withStyle(style = SpanStyle(color = Theme.colors.primaryFont)) {
                        append("roves")
                    }
                },
                style = Theme.typography.display.copy(
                    letterSpacing = 1.5.sp
                ),
                modifier = Modifier
                    .graphicsLayer(translationY = textTranslationY.value)
                    .alpha(textAlpha.value)
            )

            Spacer(Modifier.height(12.dp))
            Text(
                text = "Discover Your Next Treasure.",
                style = Theme.typography.body.medium,
                color = Theme.colors.secondaryFont,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(taglineAlpha.value)
            )
        }
    }
}