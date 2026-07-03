package com.troves.presintation.ui.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.troves_logo


@Composable
fun ProfileHeaderCard(
    name: String,
    email: String,
    onEditProfileClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Theme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.surfaceVariant)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.troves_logo),
                    contentDescription = "Avatar Placeholder",
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    tint = Theme.colors.hint
                )
            }

            Text(
                text = "Welcome, $name!",
                style = Theme.typography.body.large.copy(color = Theme.colors.primaryFont)
            )

            Text(
                text = email,
                style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onEditProfileClick,
                colors = ButtonDefaults.buttonColors(containerColor = Theme.colors.primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.width(160.dp)
            ) {
                Text(
                    text = "Edit Profile",
                    style = Theme.typography.body.medium.copy(color = Theme.colors.onPrimary)
                )
            }
        }
    }
}
