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

import org.koin.compose.viewmodel.koinViewModel
import com.troves.presintation.core.mvi.ObserveEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun NewAddressScreen(
    onNavigateBack: () -> Unit,
    viewModel: NewAddressViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            NewAddressEffect.NavigateBack -> onNavigateBack()
            is NewAddressEffect.ShowToast -> scope.launch { snackbarHostState.showSnackbar(effect.message) }
        }
    }
    
    NewAddressScreenContent(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAddressScreenContent(
    state: NewAddressUiState,
    onIntent: (NewAddressIntent) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var cityExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Theme.colors.backGround,
        topBar = {
            BaseTopAppBar(
                title = "New Address",
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
                    isDisabled = state.city.isBlank() || state.street.isBlank() || state.label.isBlank() || state.phone.isBlank(),
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
