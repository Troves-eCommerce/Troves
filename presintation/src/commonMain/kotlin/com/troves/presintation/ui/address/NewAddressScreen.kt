package com.troves.presintation.ui.address

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.textfield.TextField
import com.troves.designsystem.components.topbar.BaseTopAppBar
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_arrow_back
import troves.designsystem.generated.resources.ic_arrow_drop_down

import org.koin.compose.viewmodel.koinViewModel
import com.troves.presintation.core.mvi.ObserveEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun NewAddressScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    addressId: String? = null,
    viewModel: NewAddressViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(addressId) {
        viewModel.onIntent(NewAddressIntent.Load(addressId))
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            NewAddressEffect.NavigateBack -> onNavigateBack()
            is NewAddressEffect.SavedAndClose -> {
                // Briefly confirm the save, then return to the list (which reflects it reactively and
                // re-fetches on resume). The snackbar is short-lived; navigation shouldn't wait on it.
                scope.launch { snackbarHostState.showSnackbar(effect.message, duration = SnackbarDuration.Short) }
                scope.launch {
                    delay(900)
                    onNavigateBack()
                }
            }
            is NewAddressEffect.RequireLogin -> {
                scope.launch { snackbarHostState.showSnackbar(effect.message) }
                onNavigateToLogin()
            }
            is NewAddressEffect.ShowToast -> scope.launch { snackbarHostState.showSnackbar(effect.message) }
        }
    }

    NewAddressScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAddressScreenContent(
    state: NewAddressUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (NewAddressIntent) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var cityExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        containerColor = Theme.colors.backGround,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            BaseTopAppBar(
                title = if (state.isEditMode) "Edit Address" else "New Address",
                leadingIcon = painterResource(Res.drawable.ic_arrow_back),
                onLeadingClick = { onIntent(NewAddressIntent.OnBackClick) },
                modifier = Modifier.background(Theme.colors.backGround)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Theme.colors.backGround)
                    .navigationBarsPadding()
                    .padding(Theme.spacing.medium),
                contentAlignment = Alignment.Center
            ) {
                PrimaryButton(
                    caption = "Save Address",
                    onClick = { onIntent(NewAddressIntent.OnSaveClick) },
                    isDisabled = state.city.isBlank() || state.street.isBlank() ||
                        state.recipientName.isBlank() || state.phone.isBlank() || state.country.isBlank(),
                    isLoading = state.isSaving,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Theme.spacing.medium)
        ) {
            Spacer(Modifier.height(Theme.spacing.small))

            TextField(
                text = state.label,
                onTextChange = { onIntent(NewAddressIntent.OnLabelChange(it)) },
                title = "Address Label",
                hint = "e.g. Home, Office",
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Theme.spacing.medium))

            TextField(
                text = state.recipientName,
                onTextChange = { onIntent(NewAddressIntent.OnRecipientNameChange(it)) },
                title = "Full Name",
                hint = "e.g. Jane Doe",
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Theme.spacing.medium))

            TextField(
                text = state.phone,
                onTextChange = { onIntent(NewAddressIntent.OnPhoneChange(it)) },
                title = "Phone Number",
                hint = "e.g. +1 234 567 890",
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Theme.spacing.medium))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                Box(modifier = Modifier.menuAnchor()) {
                    TextField(
                        text = state.country,
                        onTextChange = {},
                        title = "Country",
                        readOnly = true,
                        trailingIcon = painterResource(Res.drawable.ic_arrow_drop_down),
                        modifier = Modifier.fillMaxWidth(),
                        onClickTrailingIcon = { expanded = !expanded }
                    )
                }
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Theme.colors.surface)
                ) {
                    if (state.isCountriesLoading) {
                        DropdownMenuItem(
                            text = { Text("Loading...", style = Theme.typography.body.medium, color = Theme.colors.secondaryFont) },
                            onClick = {}
                        )
                    } else if (state.countriesError != null) {
                        DropdownMenuItem(
                            text = { Text("Error loading countries", style = Theme.typography.body.medium, color = Theme.colors.error) },
                            onClick = { onIntent(NewAddressIntent.LoadCountries) }
                        )
                    } else {
                        state.countries.forEach { selection ->
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        text = selection, 
                                        style = Theme.typography.body.medium,
                                        color = Theme.colors.primaryFont
                                    ) 
                                },
                                onClick = {
                                    onIntent(NewAddressIntent.OnCountryChange(selection))
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(Theme.spacing.medium))
            
            ExposedDropdownMenuBox(
                expanded = cityExpanded,
                onExpandedChange = { if (state.country.isNotBlank()) cityExpanded = it }
            ) {
                Box(modifier = Modifier.menuAnchor()) {
                    TextField(
                        text = state.city,
                        onTextChange = { onIntent(NewAddressIntent.OnCityChange(it)) },
                        title = "City",
                        hint = if (state.country.isBlank()) "Select a country first" else "Select or type City",
                        readOnly = false,
                        trailingIcon = painterResource(Res.drawable.ic_arrow_drop_down),
                        modifier = Modifier.fillMaxWidth(),
                        onClickTrailingIcon = { if (state.country.isNotBlank()) cityExpanded = !cityExpanded }
                    )
                }

                ExposedDropdownMenu(
                    expanded = cityExpanded,
                    onDismissRequest = { cityExpanded = false },
                    modifier = Modifier.background(Theme.colors.surface)
                ) {
                    if (state.isCitiesLoading) {
                        DropdownMenuItem(
                            text = { Text("Loading...", style = Theme.typography.body.medium, color = Theme.colors.secondaryFont) },
                            onClick = {}
                        )
                    } else if (state.cities.isNotEmpty()) {
                        state.cities.forEach { selection ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = selection,
                                        style = Theme.typography.body.medium,
                                        color = Theme.colors.primaryFont
                                    )
                                },
                                onClick = {
                                    onIntent(NewAddressIntent.OnCityChange(selection))
                                    cityExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Show city error / empty message inline below the field
            if (state.citiesError != null && state.cities.isEmpty() && !state.isCitiesLoading) {
                Text(
                    text = "Could not load cities. You can type your city manually.",
                    style = Theme.typography.body.small,
                    color = Theme.colors.error,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                )
            }

            Spacer(Modifier.height(Theme.spacing.medium))
            
            TextField(
                text = state.street,
                onTextChange = { onIntent(NewAddressIntent.OnStreetChange(it)) },
                title = "Street Address",
                hint = "e.g. 123 Emerald Ave, Apt 4B",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Theme.spacing.medium))

            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    text = state.province,
                    onTextChange = { onIntent(NewAddressIntent.OnProvinceChange(it)) },
                    title = "State / Province",
                    hint = "e.g. California",
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(Theme.spacing.medium))
                TextField(
                    text = state.zip,
                    onTextChange = { onIntent(NewAddressIntent.OnZipChange(it)) },
                    title = "Zip / Postal",
                    hint = "e.g. 90001",
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(Theme.spacing.medium))

            TextField(
                text = state.note,
                onTextChange = { onIntent(NewAddressIntent.OnNoteChange(it)) },
                title = "Note (Optional)",
                hint = "e.g. Leave package at the door",
                singleLine = false,
                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp)
            )
        }
    }
}
