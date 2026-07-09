package com.troves.presintation.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.troves.designsystem.components.dialog.TrovesDialog
import com.troves.designsystem.components.toast.TrovesSnackbarHost
import com.troves.designsystem.theme.Theme
import com.troves.designsystem.util.autoMirror
import com.troves.presintation.ui.profile.components.LiveRatesRow
import com.troves.presintation.ui.profile.components.ProfileHeaderCard
import com.troves.presintation.ui.profile.components.ProfileRowItem
import com.troves.presintation.ui.profile.components.ProfileSection
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.*
import troves.designsystem.generated.resources.Res
import troves.presintation.generated.resources.Res as ResP
import troves.presintation.generated.resources.profile_style_survey_desc
import troves.presintation.generated.resources.*

@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToAddresses: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToPaymentMethods: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToAiAssistant: () -> Unit,
    onNavigateToSurvey: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showLanguageSheet by remember { mutableStateOf(false) }
    var showThemeSheet by remember { mutableStateOf(false) }
    var showSurveySheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProfileEffect.NavigateToLogin -> onNavigateToLogin()
                is ProfileEffect.NavigateToAddresses -> onNavigateToAddresses()
                is ProfileEffect.NavigateToOrders -> onNavigateToOrders()
                is ProfileEffect.NavigateToPaymentMethods -> onNavigateToPaymentMethods()
                is ProfileEffect.NavigateToEditProfile -> onNavigateToEditProfile()
                is ProfileEffect.NavigateToAiAssistant -> onNavigateToAiAssistant()
                is ProfileEffect.NavigateToSurvey -> onNavigateToSurvey()
                is ProfileEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    if (uiState.showLogoutDialog) {
        TrovesDialog(
            title = stringResource(ResP.string.profile_sign_out),
            message = stringResource(ResP.string.profile_sign_out_confirmation),
            confirmText =stringResource(ResP.string.profile_sign_out),
            onConfirm = { viewModel.onIntent(ProfileIntent.LogoutConfirmed) },
            onDismiss = { viewModel.onIntent(ProfileIntent.LogoutDismissed) }
        )
    }

    if (uiState.showOfflineDialog) {
        TrovesDialog(
            title = stringResource(Res.string.no_connection_title),
            message = stringResource(Res.string.no_connection_action_blocked),
            confirmText = stringResource(ResP.string.profile_cancel),
            dismissText = null,
            onConfirm = { viewModel.onIntent(ProfileIntent.DismissOfflineDialog) },
            onDismiss = { viewModel.onIntent(ProfileIntent.DismissOfflineDialog) }
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

    if (showThemeSheet) {
        ThemeBottomSheet(
            selectedTheme = uiState.themeMode,
            onThemeSelected = {
                viewModel.onIntent(ProfileIntent.ThemeModeSelected(it))
                showThemeSheet = false
            },
            onDismiss = { showThemeSheet = false }
        )
    }

    if (showSurveySheet) {
        com.troves.presintation.ui.survey.SurveyBottomSheet(
            onDismiss = { showSurveySheet = false },
        )
    }

    Scaffold(
        containerColor = Theme.colors.backGround,
    )
    { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TrovesSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.TopCenter).zIndex(1f).padding(16.dp),
            )
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
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    if (uiState.isGuest) {
                        ProfileGuestHeader(onLoginClick = { viewModel.onIntent(ProfileIntent.LoginClicked) })
                    } else {
                        ProfileHeaderCard(
                            name = uiState.userName,
                            email = uiState.userEmail,
                            profileImageUrl = uiState.userProfileImage,
                            onEditProfileClick = { viewModel.onIntent(ProfileIntent.EditProfileClicked) }
                        )
                    }

                    ProfileSection(title = stringResource(ResP.string.profile_account_settings)) {
                        if (!uiState.isGuest) {
                            ProfileRowItem(
                                icon = Res.drawable.ic_order_history,
                                title = stringResource(ResP.string.profile_order_history),
                                description = stringResource(ResP.string.profile_order_history_desc),
                                onClick = { viewModel.onIntent(ProfileIntent.OrderHistoryClicked) }
                            )
                            HorizontalDivider(
                                color = Theme.colors.onPrimary,
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            
                            ProfileRowItem(
                                icon = Res.drawable.ic_location,
                                title = stringResource(ResP.string.profile_manage_addresses),
                                description = stringResource(ResP.string.profile_manage_addresses_desc),
                                onClick = { viewModel.onIntent(ProfileIntent.ManageAddressesClicked) }
                            )
                            HorizontalDivider(
                                color = Theme.colors.onPrimary,
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        LiveRatesRow(
                            exchangeRate = uiState.exchangeRate,
                            selectedCurrency = uiState.selectedCurrency,
                            onCurrencySelected = { viewModel.onIntent(ProfileIntent.CurrencySelected(it)) }
                        )
                        HorizontalDivider(
                            color = Theme.colors.onPrimary,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        ProfileRowItem(
                            icon = Res.drawable.ic_language,
                            title = stringResource(ResP.string.profile_language),
                            description = stringResource(ResP.string.profile_language_desc),
                            onClick = { showLanguageSheet = true }
                        )
                        HorizontalDivider(
                            color = Theme.colors.onPrimary,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        ProfileRowItem(
                            icon = Res.drawable.ic_dark_mode,
                            title = stringResource(ResP.string.profile_theme),
                            description = when (uiState.themeMode) {
                                "dark" -> stringResource(ResP.string.profile_theme_dark)
                                "light" -> stringResource(ResP.string.profile_theme_light)
                                else -> stringResource(ResP.string.profile_theme_system)
                            },
                            onClick = { showThemeSheet = true }
                        )

                        if (!uiState.isGuest) {
                            HorizontalDivider(
                                color = Theme.colors.onPrimary,
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            
                            ProfileRowItem(
                                icon = Res.drawable.ic_ai_sparkles,
                                title = stringResource(ResP.string.profile_style_survey),
                                description = stringResource(ResP.string.profile_style_survey_desc),
                                onClick = { viewModel.onIntent(ProfileIntent.SurveyClicked) }
                            )
                        }

                        if (!uiState.isGuest) {
                            HorizontalDivider(
                                color = Theme.colors.onPrimary,
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            ProfileRowItem(
                                icon = Res.drawable.ic_logout,
                                title = stringResource(ResP.string.profile_sign_out),
                                description = stringResource(ResP.string.profile_sign_out_desc),
                                autoMirrorIcon = true,
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
                text = stringResource(ResP.string.profile_select_language),
                style = Theme.typography.title,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            LanguageItem(
                title = stringResource(ResP.string.language_english),
                isSelected = selectedLanguage == "en",
                onClick = { onLanguageSelected("en") }
            )
            HorizontalDivider(color = Theme.colors.onPrimary)
            LanguageItem(
                title = stringResource(ResP.string.language_arabic),
                isSelected = selectedLanguage == "ar",
                onClick = { onLanguageSelected("ar") }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeBottomSheet(
    selectedTheme: String,
    onThemeSelected: (String) -> Unit,
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
                text = stringResource(ResP.string.profile_select_theme),
                style = Theme.typography.title.copy(
                    color = Theme.colors.primaryFont
                ),
                modifier = Modifier.padding(vertical = 16.dp)
            )

            LanguageItem(
                title = stringResource(ResP.string.profile_theme_system),
                isSelected = selectedTheme == "system",
                onClick = { onThemeSelected("system") }
            )
            HorizontalDivider(color = Theme.colors.secondary)
            LanguageItem(
                title = stringResource(ResP.string.profile_theme_light),
                isSelected = selectedTheme == "light",
                onClick = { onThemeSelected("light") }
            )
            HorizontalDivider(color = Theme.colors.secondary)
            LanguageItem(
                title = stringResource(ResP.string.profile_theme_dark),
                isSelected = selectedTheme == "dark",
                onClick = { onThemeSelected("dark") }
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
                painter = painterResource(Res.drawable.ic_check),
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Theme.colors.surface),
        border = BorderStroke(1.dp, Theme.colors.onPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BasicText(
                text = "Your Account",
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = Theme.spacing.medium)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.primary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.troves_logo),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(ResP.string.profile_welcome),
                style = Theme.typography.title.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(ResP.string.profile_guest_msg),
                style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Theme.colors.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(ResP.string.profile_login_signup),
                    style = Theme.typography.body.medium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(14.dp)
                        .autoMirror()
                )
            }
        }
    }
}