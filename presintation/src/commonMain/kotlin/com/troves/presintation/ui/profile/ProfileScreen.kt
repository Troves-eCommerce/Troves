package com.troves.presintation.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_arrow
import troves.designsystem.generated.resources.ic_dark_mode
import troves.designsystem.generated.resources.ic_language
import troves.designsystem.generated.resources.ic_live_rate
import troves.designsystem.generated.resources.ic_location
import troves.designsystem.generated.resources.ic_logout
import troves.designsystem.generated.resources.ic_order_history
import troves.designsystem.generated.resources.ic_payment_method
import troves.designsystem.generated.resources.ic_star
import troves.designsystem.generated.resources.troves_logo

@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var isDarkModeEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProfileEffect.NavigateToLogin -> onNavigateToLogin()
                is ProfileEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Theme.colors.backGround
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Theme.colors.primary
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProfileHeaderCard(
                        name = "Alex",
                        email = "alex.testo@gmail.com",
                        onEditProfileClick = { /* Handle edit profile */ }
                    )


                    ProfileSection(title = "Account Settings") {
                        ProfileRowItem(
                            icon = Res.drawable.ic_location,
                            title = "Manage Addresses",
                            onClick = {},
                            iconColor = Theme.colors.primary
                        )
                        HorizontalDivider(color = Theme.colors.backGround, thickness = 1.dp)
                        ProfileRowItem(
                            icon = Res.drawable.ic_order_history,
                            title = "Order History",
                            onClick = {},
                            iconColor = Theme.colors.primary
                        )
                        HorizontalDivider(color = Theme.colors.backGround, thickness = 1.dp)
                        ProfileRowItem(
                            icon = Res.drawable.ic_payment_method,
                            title = "Payment Methods",
                            onClick = {},
                            iconColor = Theme.colors.primary
                        )
                    }

                    ProfileSection(title = "Market Preferences") {
                        LiveRatesRow()
                    }

                    ProfileSection(title = "Application") {
                        ProfileRowItem(
                            icon = Res.drawable.ic_language,
                            title = "Language",
                            iconColor = Theme.colors.primary,
                            trailingContent = {
                                Text(
                                    text = "English",
                                    style = Theme.typography.hint.medium.copy(color = Theme.colors.secondaryFont),
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            },
                            onClick = { /* Open Language BottomSheet/Dialog */ }
                        )
                        HorizontalDivider(color = Theme.colors.backGround, thickness = 1.dp)

                        ProfileRowItem(
                            icon = Res.drawable.ic_dark_mode,
                            title = "Dark Mode",
                            showArrow = false,
                            iconColor = Theme.colors.primary,
                            trailingContent = {
                                Switch(
                                    checked = isDarkModeEnabled,
                                    onCheckedChange = { isDarkModeEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Theme.colors.surface,
                                        checkedTrackColor = Theme.colors.primary,
                                        uncheckedThumbColor = Theme.colors.secondaryFont,
                                        uncheckedTrackColor = Theme.colors.backGround
                                    )
                                )
                            },
                            onClick = { isDarkModeEnabled = !isDarkModeEnabled }
                        )
                        HorizontalDivider(color = Theme.colors.backGround, thickness = 1.dp)

                        // Sign Out Row
                        ProfileRowItem(
                            icon = Res.drawable.ic_logout,
                            title = "Sign Out",
                            textColor = Theme.colors.error,
                            iconColor = Theme.colors.error,
                            showArrow = false,
                            onClick = { viewModel.onIntent(ProfileIntent.Logout) }
                        )
                    }
                }
            }
        }
    }
}

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

@Composable
fun ProfileSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = Theme.typography.body.medium.copy(color = Theme.colors.primaryFont),
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Theme.colors.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            content = content
        )
    }
}

@Composable
fun ProfileRowItem(
    icon: DrawableResource,
    title: String,
    textColor: Color = Theme.colors.primaryFont,
    iconColor: Color = Theme.colors.secondaryFont,
    showArrow: Boolean = true,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = Theme.typography.body.medium.copy(color = textColor),
            modifier = Modifier.weight(1f)
        )

        if (trailingContent != null) {
            trailingContent()
        }

        if (showArrow) {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow),
                contentDescription = "Navigate",
                tint = Theme.colors.hint,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun LiveRatesRow() {
    var selectedCurrency by remember { mutableStateOf("GBP") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_live_rate),
                contentDescription = null,
                tint = Theme.colors.primary
            )
            Text(
                text = "Live Rates",
                style = Theme.typography.body.large.copy(color = Theme.colors.primaryFont)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Theme.colors.backGround)
                .padding(4.dp)
        ) {
            val currencies = listOf("GBP" to "£ GBP", "JPY" to "¥ JPY", "EUR" to "€ EUR")
            currencies.forEach { (code, label) ->
                val isSelected = selectedCurrency == code
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Theme.colors.surface else Color.Transparent)
                        .clickable { selectedCurrency = code }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = if (isSelected) {
                            Theme.typography.body.medium.copy(color = Theme.colors.primary)
                        } else {
                            Theme.typography.hint.medium.copy(color = Theme.colors.secondaryFont)
                        }
                    )
                }
            }
        }
    }
}