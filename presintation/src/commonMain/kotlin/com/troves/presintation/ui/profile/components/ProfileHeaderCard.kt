package com.troves.presintation.ui.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_arrow
import troves.designsystem.generated.resources.troves_logo
import troves.presintation.generated.resources.Res as ResP
import troves.presintation.generated.resources.*

@Composable
fun ProfileHeaderCard(
    name: String,
    email: String,
    profileImageUrl: String? = null,
    onEditProfileClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditProfileClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Theme.colors.surface),
        border = BorderStroke(1.dp, Theme.colors.onPrimary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.surfaceVariant)
            ) {
                if (!profileImageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(Res.drawable.troves_logo),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().padding(14.dp),
                        tint = Theme.colors.hint
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(ResP.string.profile_welcome_back),
                    style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont)
                )
                Text(
                    text = name,
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = email,
                    style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Theme.colors.backGround)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(ResP.string.profile_premium_member),
                        style = Theme.typography.body.small.copy(
                            color = Theme.colors.primaryFont,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}