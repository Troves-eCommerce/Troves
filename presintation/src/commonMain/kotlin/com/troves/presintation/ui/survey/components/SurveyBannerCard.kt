package com.troves.presintation.ui.survey.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_ai_sparkles
import troves.presintation.generated.resources.Res as ResP
import troves.presintation.generated.resources.survey_banner_close
import troves.presintation.generated.resources.survey_banner_dismiss
import troves.presintation.generated.resources.survey_banner_eyebrow
import troves.presintation.generated.resources.survey_banner_start
import troves.presintation.generated.resources.survey_banner_subtitle
import troves.presintation.generated.resources.survey_banner_title

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box

@Composable
fun SurveyBannerCard(
    onStartSurvey: () -> Unit,
    onDismiss: () -> Unit,
    onNeverShowAgain: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = Theme.shapes.large

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = shape, clip = false)
            .clip(shape)
            .background(Theme.colors.surface)
            .padding(Theme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_ai_sparkles),
                    contentDescription = null,
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(20.dp),
                )
                BasicText(
                    text = stringResource(ResP.string.survey_banner_eyebrow),
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.primary,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
            
            IconButton(
                onClick = onDismiss, 
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(ResP.string.survey_banner_close),
                    tint = Theme.colors.secondaryFont
                )
            }
        }

        BasicText(
            text = stringResource(ResP.string.survey_banner_title),
            style = Theme.typography.title.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
            ),
        )

        BasicText(
            text = stringResource(ResP.string.survey_banner_subtitle),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.secondaryFont,
            ),
        )

        Spacer(Modifier.height(Theme.spacing.extraSmall))

        PrimaryButton(
            caption = stringResource(ResP.string.survey_banner_start),
            onClick = onStartSurvey,
            modifier = Modifier.fillMaxWidth(),
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onNeverShowAgain)
                .padding(vertical = Theme.spacing.small),
            contentAlignment = Alignment.Center
        ) {
            BasicText(
                text = stringResource(ResP.string.survey_banner_dismiss),
                style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont, fontWeight = FontWeight.SemiBold)
            )
        }
    }
}
