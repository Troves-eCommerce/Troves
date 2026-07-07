package com.troves.presintation.ui.address

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.topbar.BaseTopAppBar
import com.troves.designsystem.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import troves.designsystem.generated.resources.*
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.ic_arrow_back
import org.koin.compose.viewmodel.koinViewModel
import com.troves.presintation.core.mvi.ObserveEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import com.troves.designsystem.components.toast.TrovesSnackbarHost
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.troves.designsystem.components.textfield.CustomTextField

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

    Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        containerColor = Theme.colors.backGround,
        topBar = {
            BaseTopAppBar(
                title = if (state.isEditMode) stringResource(Res.string.address_edit_title) else stringResource(Res.string.address_new_title),
                leadingIcon = painterResource(Res.drawable.ic_arrow_back),
                onLeadingClick = { onIntent(NewAddressIntent.OnBackClick) },
                modifier = Modifier.background(Theme.colors.backGround),
                autoMirrorLeadingIcon = true
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
                    caption = stringResource(Res.string.address_save),
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Theme.spacing.medium)
        ) {
            Spacer(Modifier.height(Theme.spacing.small))

            CustomTextField(
                text = state.label,
                onTextChange = { onIntent(NewAddressIntent.OnLabelChange(it)) },
                title = stringResource(Res.string.address_label_title),
                hint = stringResource(Res.string.address_label_hint),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Theme.spacing.medium))

            CustomTextField(
                text = state.recipientName,
                onTextChange = { onIntent(NewAddressIntent.OnRecipientNameChange(it)) },
                title = stringResource(Res.string.address_name_title),
                hint = stringResource(Res.string.address_name_hint),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Theme.spacing.medium))

            CustomTextField(
                text = state.phone,
                onTextChange = { onIntent(NewAddressIntent.OnPhoneChange(it)) },
                title = stringResource(Res.string.address_phone_title),
                hint = stringResource(Res.string.address_phone_hint),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Theme.spacing.medium))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                Box(modifier = Modifier.menuAnchor()) {
                    CustomTextField(
                        text = state.country,
                        onTextChange = {},
                        title = stringResource(Res.string.address_country_title),
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
                            text = { Text(stringResource(Res.string.address_loading), style = Theme.typography.body.medium, color = Theme.colors.secondaryFont) },
                            onClick = {}
                        )
                    } else if (state.countriesError != null) {
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.address_error_countries), style = Theme.typography.body.medium, color = Theme.colors.error) },
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
                    CustomTextField(
                        text = state.city,
                        onTextChange = { onIntent(NewAddressIntent.OnCityChange(it)) },
                        title = stringResource(Res.string.address_city_title),
                        hint = if (state.country.isBlank()) stringResource(Res.string.address_city_no_country_hint) else stringResource(Res.string.address_city_hint),
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
                            text = { Text(stringResource(Res.string.address_loading), style = Theme.typography.body.medium, color = Theme.colors.secondaryFont) },
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
                    text = stringResource(Res.string.address_error_cities),
                    style = Theme.typography.body.small,
                    color = Theme.colors.error,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                )
            }

            Spacer(Modifier.height(Theme.spacing.medium))

            CustomTextField(
                text = state.street,
                onTextChange = { onIntent(NewAddressIntent.OnStreetChange(it)) },
                title = stringResource(Res.string.address_street_title),
                hint = stringResource(Res.string.address_street_hint),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Theme.spacing.medium))

            Row(modifier = Modifier.fillMaxWidth()) {
                CustomTextField(
                    text = state.province,
                    onTextChange = { onIntent(NewAddressIntent.OnProvinceChange(it)) },
                    title = stringResource(Res.string.address_province_title),
                    hint = stringResource(Res.string.address_province_hint),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(Theme.spacing.medium))
                CustomTextField(
                    text = state.zip,
                    onTextChange = { onIntent(NewAddressIntent.OnZipChange(it)) },
                    title = stringResource(Res.string.address_zip_title),
                    hint = stringResource(Res.string.address_zip_hint),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(Theme.spacing.medium))

            CustomTextField(
                text = state.note,
                onTextChange = { onIntent(NewAddressIntent.OnNoteChange(it)) },
                title = stringResource(Res.string.address_note_title),
                hint = stringResource(Res.string.address_note_hint),
                singleLine = false,
                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp)
            )
        }
    }
        TrovesSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(16.dp),
        )
    }
}
