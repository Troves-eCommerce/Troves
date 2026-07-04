package com.troves.presintation.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import com.troves.presintation.ui.profile.components.LiveRatesRow
import com.troves.presintation.ui.profile.components.ProfileHeaderCard
import com.troves.presintation.ui.profile.components.ProfileRowItem
import com.troves.presintation.ui.profile.components.ProfileSection
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.*
import troves.presintation.generated.resources.*
import troves.presintation.generated.resources.Res as PresRes

@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToAddresses: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToPaymentMethods: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToAiAssistant: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showLanguageSheet by remember { mutableStateOf(false) }

    val isDarkModeEnabled = when (uiState.themeMode) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProfileEffect.NavigateToLogin -> onNavigateToLogin()
                is ProfileEffect.NavigateToAddresses -> onNavigateToAddresses()
                is ProfileEffect.NavigateToOrders -> onNavigateToOrders()
                is ProfileEffect.NavigateToPaymentMethods -> onNavigateToPaymentMethods()
                is ProfileEffect.NavigateToEditProfile -> onNavigateToEditProfile()
                is ProfileEffect.NavigateToAiAssistant -> onNavigateToAiAssistant()
                is ProfileEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    if (uiState.showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(ProfileIntent.LogoutDismissed) },
            title = { Text(stringResource(PresRes.string.profile_sign_out), style = Theme.typography.title) },
            text = { Text(stringResource(PresRes.string.profile_sign_out_confirmation), style = Theme.typography.body.medium) },
            confirmButton = {
                TextButton(onClick = { viewModel.onIntent(ProfileIntent.LogoutConfirmed) }) {
                    Text(stringResource(PresRes.string.profile_sign_out), color = Theme.colors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onIntent(ProfileIntent.LogoutDismissed) }) {
                    Text(stringResource(PresRes.string.profile_cancel), color = Theme.colors.primary)
                }
            },
            containerColor = Theme.colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showLanguageSheet) {
        LanguageBottomSheet(
            selectedLanguage = uiState.selectedLanguage,
            onLanguageSelected = {
                viewModel.onIntent(ProfileIntent.LanguageSelected(it))
                showLanguageSheet = false
            },
            onDismiss = { showLanguageSheet = false }
        )
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
                    if (uiState.isGuest) {
                        ProfileGuestHeader(onLoginClick = { viewModel.onIntent(ProfileIntent.LoginClicked) })
                    } else {
                        ProfileHeaderCard(
                            name = uiState.userName.ifEmpty { "User" },
                            email = uiState.userEmail,
                            onEditProfileClick = { viewModel.onIntent(ProfileIntent.EditProfileClicked) }
                        )
                    }

                    if (!uiState.isGuest) {
                        ProfileSection(title = stringResource(PresRes.string.profile_account_settings)) {
                            ProfileRowItem(
                                icon = Res.drawable.ic_ai_sparkles,
                                title = stringResource(PresRes.string.ai_profile_entry),
                                onClick = { viewModel.onIntent(ProfileIntent.AiAssistantClicked) },
                                iconColor = Theme.colors.primary
                            )
                            HorizontalDivider(color = Theme.colors.backGround, thickness = 1.dp)
                            ProfileRowItem(
                                icon = Res.drawable.ic_location,
                                title = stringResource(PresRes.string.profile_manage_addresses),
                                onClick = { viewModel.onIntent(ProfileIntent.ManageAddressesClicked) },
                                iconColor = Theme.colors.primary
                            )
                            HorizontalDivider(color = Theme.colors.backGround, thickness = 1.dp)
                            ProfileRowItem(
                                icon = Res.drawable.ic_order_history,
                                title = stringResource(PresRes.string.profile_order_history),
                                onClick = { viewModel.onIntent(ProfileIntent.OrderHistoryClicked) },
                                iconColor = Theme.colors.primary
                            )
                            HorizontalDivider(color = Theme.colors.backGround, thickness = 1.dp)
                            ProfileRowItem(
                                icon = Res.drawable.ic_payment_method,
                                title = stringResource(PresRes.string.profile_payment_methods),
                                onClick = { viewModel.onIntent(ProfileIntent.PaymentMethodsClicked) },
                                iconColor = Theme.colors.primary
                            )
                        }
                    }

                    ProfileSection(title = stringResource(PresRes.string.profile_market_preferences)) {
                        LiveRatesRow()
                    }

                    ProfileSection(title = stringResource(PresRes.string.profile_application)) {
                        ProfileRowItem(
                            icon = Res.drawable.ic_language,
                            title = stringResource(PresRes.string.profile_language),
                            iconColor = Theme.colors.primary,
                            trailingContent = {
                                Text(
                                    text = if (uiState.selectedLanguage == "en") "English" else "العربية",
                                    style = Theme.typography.hint.medium.copy(color = Theme.colors.secondaryFont),
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            },
                            onClick = { showLanguageSheet = true }
                        )
                        HorizontalDivider(color = Theme.colors.backGround, thickness = 1.dp)

                        ProfileRowItem(
                            icon = Res.drawable.ic_dark_mode,
                            title = stringResource(PresRes.string.profile_dark_mode),
                            showArrow = false,
                            iconColor = Theme.colors.primary,
                            trailingContent = {
                                Switch(
                                    checked = isDarkModeEnabled,
                                    onCheckedChange = { viewModel.onIntent(ProfileIntent.DarkModeChanged(it)) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Theme.colors.surface,
                                        checkedTrackColor = Theme.colors.primary,
                                        uncheckedThumbColor = Theme.colors.secondaryFont,
                                        uncheckedTrackColor = Theme.colors.backGround
                                    )
                                )
                            },
                            onClick = { viewModel.onIntent(ProfileIntent.DarkModeChanged(!isDarkModeEnabled)) }
                        )
                        HorizontalDivider(color = Theme.colors.backGround, thickness = 1.dp)
                        if (!uiState.isGuest) {
                            ProfileRowItem(
                                icon = Res.drawable.ic_logout,
                                title = stringResource(PresRes.string.profile_sign_out),
                                textColor = Theme.colors.error,
                                iconColor = Theme.colors.error,
                                showArrow = false,
                                onClick = { viewModel.onIntent(ProfileIntent.LogoutClicked) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageBottomSheet(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Theme.colors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(PresRes.string.profile_select_language),
                style = Theme.typography.title,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            LanguageItem(
                title = "English",
                isSelected = selectedLanguage == "en",
                onClick = { onLanguageSelected("en") }
            )
            HorizontalDivider(color = Theme.colors.backGround)
            LanguageItem(
                title = "العربية",
                isSelected = selectedLanguage == "ar",
                onClick = { onLanguageSelected("ar") }
            )
        }
    }
}

@Composable
fun LanguageItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = Theme.typography.body.medium,
            color = if (isSelected) Theme.colors.primary else Theme.colors.primaryFont
        )
        if (isSelected) {
            Icon(
                painter = painterResource(Res.drawable.ic_star), // Using ic_star as a checkmark for now if no checkmark icon
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ProfileGuestHeader(
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.colors.surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            painter = painterResource(Res.drawable.troves_logo),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.Unspecified
        )
        Text(
            text = stringResource(PresRes.string.profile_welcome),
            style = Theme.typography.displayMedium,
            color = Theme.colors.primaryFont
        )
        Text(
            text = stringResource(PresRes.string.profile_guest_msg),
            style = Theme.typography.body.medium,
            color = Theme.colors.secondaryFont,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Theme.colors.primary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(stringResource(PresRes.string.profile_login_signup), color = Color.White)
        }
    }
}