package com.troves.presintation.ui.address

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.theme.Theme
import com.troves.domain.entity.LocationAddress
import com.troves.domain.entity.LocationCoordinates
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_arrow_back
import troves.designsystem.generated.resources.ic_location

/**
 * Copyright (c) 2026 Wahid Ali Wahid Hussien.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@Composable
expect fun MapBox(
    selectedLatitude: Double?,
    selectedLongitude: Double?,
    currentLocation: LocationCoordinates,
    flyToCurrentLocationTrigger: Int,
    onMapClick: (latitude: Double, longitude: Double) -> Unit
)

@Composable
fun MapSelectionScreenContent(
    selectedLocationAddress: LocationAddress?,
    isGeocodingLoading: Boolean,
    selectedLatitude: Double?,
    selectedLongitude: Double?,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    currentLocation: LocationCoordinates,
    onAddNewAddressClick: () -> Unit,
    onMapClick: (latitude: Double, longitude: Double) -> Unit,
    onGetCurrentLocationClick: () -> Unit
) {
    var flyToTrigger by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        MapBox(
            selectedLatitude = selectedLatitude,
            selectedLongitude = selectedLongitude,
            onMapClick = onMapClick,
            currentLocation = currentLocation,
            flyToCurrentLocationTrigger = flyToTrigger
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(Theme.spacing.medium)
        ) {
            IconButton(
                onClick = onDismissRequest,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .shadow(4.dp, CircleShape)
                    .background(Theme.colors.surface, CircleShape)
                    .size(48.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    tint = Theme.colors.primaryFont
                )
            }

            AnimatedVisibility(
                visible = selectedLocationAddress != null || isGeocodingLoading,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp) // Below the back button
            ) {
                LocationInfoCard(
                    locationAddress = selectedLocationAddress,
                    isLoading = isGeocodingLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        IconButton(
            onClick = { 
                flyToTrigger++
                onGetCurrentLocationClick() 
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = Theme.spacing.medium) // Above the confirm button
                .shadow(4.dp, CircleShape)
                .background(Theme.colors.surface, CircleShape)
                .size(56.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_location),
                contentDescription = "Fly to current location",
                tint = Theme.colors.primaryFont
            )
        }

        PrimaryButton(
            caption = "Confirm location",
            onClick = onAddNewAddressClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing.medium)
                .padding(bottom = Theme.spacing.medium)
        )
    }
}

@Composable
fun LocationInfoCard(
    locationAddress: LocationAddress?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Theme.colors.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        if (isLoading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Theme.colors.primary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(Theme.spacing.small))
                Text(
                    text = "Getting address details…",
                    style = Theme.typography.body.medium,
                    color = Theme.colors.secondaryFont
                )
            }
        } else if (locationAddress != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.spacing.medium)
            ) {
                val streetLine = buildString {
                    if (locationAddress.houseNumber.isNotBlank()) {
                        append(locationAddress.houseNumber)
                        append(" ")
                    }
                    append(locationAddress.road)
                }.trim()

                if (streetLine.isNotBlank()) {
                    Text(
                        text = streetLine,
                        style = Theme.typography.body.large,
                        fontWeight = FontWeight.SemiBold,
                        color = Theme.colors.primaryFont,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                val areaLine = listOf(locationAddress.neighbourhood, locationAddress.suburb)
                    .filter { it.isNotBlank() }
                    .joinToString(", ")
                if (areaLine.isNotBlank()) {
                    Text(
                        text = areaLine,
                        style = Theme.typography.body.medium,
                        color = Theme.colors.secondaryFont,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                val locationLine = listOf(
                    locationAddress.city,
                    locationAddress.state,
                    locationAddress.country,
                    locationAddress.postcode
                ).filter { it.isNotBlank() }.joinToString(", ")
                if (locationLine.isNotBlank()) {
                    Text(
                        text = locationLine,
                        style = Theme.typography.body.small,
                        color = Theme.colors.secondaryFont,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}