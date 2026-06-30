package com.troves.presintation.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource

@Composable
fun OnboardingPageContent(pageInfo: OnboardingPageInfo) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Theme.spacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(pageInfo.imageRes),
            contentDescription = pageInfo.title,
            modifier = Modifier.size(280.dp)
        )
        Spacer(modifier = Modifier.height(Theme.spacing.extraLarge))
        Text(
            text = pageInfo.title,
            style = Theme.typography.display.copy(fontSize = 26.sp),
            color = Theme.colors.primaryFont,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(Theme.spacing.medium))
        Text(
            text = pageInfo.description,
            style = Theme.typography.body.large,
            color = Theme.colors.secondaryFont,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(120.dp))
    }
}
