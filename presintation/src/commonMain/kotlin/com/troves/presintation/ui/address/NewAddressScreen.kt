package com.troves.presintation.ui.address

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.troves.designsystem.components.button.PrimaryButton
import com.troves.designsystem.components.textfield.CustomTextField
import com.troves.designsystem.components.toast.TrovesSnackbarHost
import com.troves.designsystem.components.topbar.BaseTopAppBar
import com.troves.designsystem.theme.Theme
import com.troves.presintation.core.mvi.ObserveEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import troves.designsystem.generated.resources.Res
import troves.designsystem.generated.resources.address_city_hint
import troves.designsystem.generated.resources.address_city_no_country_hint
import troves.designsystem.generated.resources.address_city_title
import troves.designsystem.generated.resources.address_country_title
import troves.designsystem.generated.resources.address_edit_title
import troves.designsystem.generated.resources.address_error_cities
import troves.designsystem.generated.resources.address_error_countries
import troves.designsystem.generated.resources.address_label_hint
import troves.designsystem.generated.resources.address_label_title
import troves.designsystem.generated.resources.address_loading
import troves.designsystem.generated.resources.address_name_hint
import troves.designsystem.generated.resources.address_name_title
import troves.designsystem.generated.resources.address_new_title
import troves.designsystem.generated.resources.address_note_hint
import troves.designsystem.generated.resources.address_note_title
import troves.designsystem.generated.resources.address_phone_hint
import troves.designsystem.generated.resources.address_phone_title
import troves.designsystem.generated.resources.address_province_hint
import troves.designsystem.generated.resources.address_province_title
import troves.designsystem.generated.resources.address_save
import troves.designsystem.generated.resources.address_street_hint
import troves.designsystem.generated.resources.address_street_title
import troves.designsystem.generated.resources.address_zip_hint
import troves.designsystem.generated.resources.address_zip_title
import troves.designsystem.generated.resources.button_caption_select_address_from_map
import troves.designsystem.generated.resources.ic_arrow_back
import troves.designsystem.generated.resources.ic_arrow_drop_down
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
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
    var showMap by remember { mutableStateOf(false) }

    LaunchedEffect(addressId) {
        viewModel.onIntent(NewAddressIntent.Load(addressId))
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            NewAddressEffect.NavigateBack -> onNavigateBack()
            is NewAddressEffect.SavedAndClose -> {
                // Briefly confirm the save, then return to the list (which reflects it reactively and
                // re-fetches on resume). The snackbar is short-lived; navigation shouldn't wait on it.
                scope.launch {
                    snackbarHostState.showSnackbar(
                        effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
                scope.launch {
                    delay(900.milliseconds)
                    onNavigateBack()
                }
            }

            is NewAddressEffect.RequireLogin -> {
                scope.launch { snackbarHostState.showSnackbar(effect.message) }
                onNavigateToLogin()
            }

            is NewAddressEffect.ShowToast -> scope.launch { snackbarHostState.showSnackbar(effect.message) }
            NewAddressEffect.ShowMap -> {
                showMap = true
            }

            NewAddressEffect.HideMap -> {
                showMap = false
            }
        }
    }

    if (showMap) {
        MapSelectionScreenContent(
            selectedLocationAddress = state.selectedLocationAddress,
            isGeocodingLoading = state.isGeocodingLoading,
            selectedLatitude = state.selectedMapLocation.lan.takeIf { it != 0.0 },
            selectedLongitude = state.selectedMapLocation.lon.takeIf { it != 0.0 },
            onDismissRequest = { viewModel.onIntent(NewAddressIntent.HideMap) },
            onMapClick = { lat, lng -> viewModel.onIntent(NewAddressIntent.OnMapClick(lat, lng)) },
            onAddNewAddressClick = { viewModel.onIntent(NewAddressIntent.OnNewMapAddressSelected) },
        )
    } else {
        NewAddressScreenContent(
            state = state,
            snackbarHostState = snackbarHostState,
            onIntent = viewModel::onIntent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAddressScreenContent(
    state: NewAddressUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (NewAddressIntent) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var cityExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            containerColor = Theme.colors.backGround,
            topBar = {
                BaseTopAppBar(
                    title = if (state.isEditMode) stringResource(Res.string.address_edit_title) else stringResource(
                        Res.string.address_new_title
                    ),
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
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = Theme.spacing.medium)
            ) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(Modifier.height(Theme.spacing.small))

                PrimaryButton(
                    onClick = { onIntent(NewAddressIntent.ShowMap) },
                    caption = stringResource(Res.string.button_caption_select_address_from_map),
                    modifier = Modifier.fillMaxWidth(fraction = 0.9f)
                )

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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                                text = {
                                    Text(
                                        stringResource(Res.string.address_loading),
                                        style = Theme.typography.body.medium,
                                        color = Theme.colors.secondaryFont
                                    )
                                },
                                onClick = {}
                            )
                        } else if (state.countriesError != null) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        stringResource(Res.string.address_error_countries),
                                        style = Theme.typography.body.medium,
                                        color = Theme.colors.error
                                    )
                                },
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
                            hint = if (state.country.isBlank()) stringResource(Res.string.address_city_no_country_hint) else stringResource(
                                Res.string.address_city_hint
                            ),
                            readOnly = false,
                            trailingIcon = painterResource(Res.drawable.ic_arrow_drop_down),
                            modifier = Modifier.fillMaxWidth(),
                            onClickTrailingIcon = {
                                if (state.country.isNotBlank()) cityExpanded = !cityExpanded
                            }
                        )
                    }

                    ExposedDropdownMenu(
                        expanded = cityExpanded,
                        onDismissRequest = { cityExpanded = false },
                        modifier = Modifier.background(Theme.colors.surface)
                    ) {
                        if (state.isCitiesLoading) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        stringResource(Res.string.address_loading),
                                        style = Theme.typography.body.medium,
                                        color = Theme.colors.secondaryFont
                                    )
                                },
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
            } } }
        }
        TrovesSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(16.dp),
        )
    }
}
