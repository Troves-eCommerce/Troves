package com.troves.presintation.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.facebook
import troves.designsystem.generated.resources.ic_google

@Composable
fun AuthDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            Modifier
                .weight(1f)
                .height(1.dp)
                .background(Theme.colors.hint.copy(alpha = 0.4f))
        )
        BasicText(
            text = "or",
            style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont)
        )
        Box(
            Modifier
                .weight(1f)
                .height(1.dp)
                .background(Theme.colors.hint.copy(alpha = 0.4f))
        )
    }
}

@Composable
fun SocialLoginSection(
    onGoogleClick: () -> Unit = {},
    onFacebookClick: () -> Unit = {}
) {
    val googleIcon = Res.drawable.ic_google
    val facebookIcon = Res.drawable.facebook

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Theme.colors.surface)
                .border(1.dp, Theme.colors.hint.copy(alpha = 0.3f), CircleShape)
                .clickable { onGoogleClick() }
                .padding(12.dp)
        ) {
            Image(
                painter = painterResource(googleIcon),
                contentDescription = "Continue with Google",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.size(16.dp))

        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Theme.colors.surface)
                .border(1.dp, Theme.colors.hint.copy(alpha = 0.3f), CircleShape)
                .clickable { onFacebookClick() }
                .padding(12.dp)
        ) {
            Image(
                painter = painterResource(facebookIcon),
                contentDescription = "Continue with Facebook",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
