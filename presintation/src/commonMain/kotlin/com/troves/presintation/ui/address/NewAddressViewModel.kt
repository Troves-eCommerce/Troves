package com.troves.presintation.ui.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.Address
import com.troves.domain.entity.AddressIcon
import com.troves.domain.entity.LocationAddress
import com.troves.domain.entity.LocationCoordinates
import com.troves.domain.usecase.address.AddAddressUseCase
import com.troves.domain.usecase.address.GetSavedAddressByIdUseCase
import com.troves.domain.usecase.address.UpdateAddressUseCase
import com.troves.domain.usecase.shared.GetCitiesUseCase
import com.troves.domain.usecase.shared.GetCountriesUseCase
import com.troves.domain.usecase.shared.GetCurrentLocationCoordinatesUseCase
import com.troves.domain.usecase.shared.ReverseGeocodingUseCase
import com.troves.domain.utils.Result
import com.troves.domain.utils.ShopifyAuthRequiredException
import com.troves.domain.utils.fold
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okio.IOException
import org.jetbrains.compose.resources.getString
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.address_fill_required_fields
import troves.presintation.generated.resources.address_geocode_failed
import troves.presintation.generated.resources.address_invalid_phone
import troves.presintation.generated.resources.address_location_permission_required
import troves.presintation.generated.resources.address_saved
import troves.presintation.generated.resources.address_save_failed
import troves.presintation.generated.resources.address_select_location_first
import troves.presintation.generated.resources.address_sign_in_again
import troves.presintation.generated.resources.address_updated

data class NewAddressUiState(
    val label: String = "",
    val recipientName: String = "",
    val phone: String = "",
    val country: String = "",
    val city: String = "",
    val province: String = "",
    val street: String = "",
    val zip: String = "",
    val note: String = "",
    val isDefault: Boolean = false,
    val icon: AddressIcon = AddressIcon.HOME,
    val selectedMapLocation: LocationCoordinates = LocationCoordinates(0.0, 0.0),
    val selectedLocationAddress: LocationAddress? = null,
    val currentLocation: LocationCoordinates = LocationCoordinates(31.0, 31.0),
    val isGeocodingLoading: Boolean = false,
    val hasLocationPermission: Boolean = false,

    val isEditMode: Boolean = false,

    val countries: List<String> = emptyList(),
    val isCountriesLoading: Boolean = false,
    val countriesError: String? = null,
    val cities: List<String> = emptyList(),
    val isCitiesLoading: Boolean = false,
    val citiesError: String? = null,
    val isSaving: Boolean = false
)

sealed interface NewAddressIntent {
    data class OnLabelChange(val label: String) : NewAddressIntent
    data class OnRecipientNameChange(val name: String) : NewAddressIntent
    data class OnPhoneChange(val phone: String) : NewAddressIntent
    data class OnCountryChange(val country: String) : NewAddressIntent
    data object ShowMap : NewAddressIntent
    data object GetCurrentLocation : NewAddressIntent
    data class OnLocationPermissionResult(val granted: Boolean) : NewAddressIntent
    data object HideMap : NewAddressIntent
    data class OnMapClick(val latitude: Double, val longitude: Double) : NewAddressIntent
    data class OnCityChange(val city: String) : NewAddressIntent
    data class OnProvinceChange(val province: String) : NewAddressIntent
    data class OnStreetChange(val street: String) : NewAddressIntent
    data class OnZipChange(val zip: String) : NewAddressIntent
    data class OnNoteChange(val note: String) : NewAddressIntent
    data class OnDefaultChange(val isDefault: Boolean) : NewAddressIntent
    data class OnIconChange(val icon: AddressIcon) : NewAddressIntent
    data object OnNewMapAddressSelected : NewAddressIntent

    data class Load(val addressId: String?) : NewAddressIntent
    data object OnSaveClick : NewAddressIntent
    data object OnBackClick : NewAddressIntent
    data object LoadCountries : NewAddressIntent
}

sealed interface NewAddressEffect {
    data object NavigateBack : NewAddressEffect
    data object RequestLocationPermission : NewAddressEffect

    /** Address persisted on Shopify — show [message], then close. */
    data class SavedAndClose(val message: String) : NewAddressEffect
    /** Shopify customer token missing — route the user to re-authenticate. */
    data class RequireLogin(val message: String) : NewAddressEffect
    data class ShowToast(val message: String) : NewAddressEffect
    data object ShowMap : NewAddressEffect
    data object HideMap : NewAddressEffect
}

class NewAddressViewModel(
    private val getCountriesUseCase: GetCountriesUseCase,
    private val getCitiesUseCase: GetCitiesUseCase,
    private val addAddressUseCase: AddAddressUseCase,
    private val updateAddressUseCase: UpdateAddressUseCase,
    private val getSavedAddressByIdUseCase: GetSavedAddressByIdUseCase,
    private val reverseGeocodingUseCase: ReverseGeocodingUseCase,
    private val getCurrentLocationCoordinatesUseCase: GetCurrentLocationCoordinatesUseCase
) : ViewModel(),
    StateHolder<NewAddressUiState> by DefaultStateHolder(NewAddressUiState()),
    EffectPublisher<NewAddressEffect> by DefaultEffectPublisher() {

    private var editingId: String? = null

    init {
        onIntent(NewAddressIntent.LoadCountries)
    }

    fun onIntent(intent: NewAddressIntent) {
        when (intent) {
            is NewAddressIntent.OnLabelChange -> updateState { copy(label = intent.label) }
            is NewAddressIntent.OnRecipientNameChange -> updateState { copy(recipientName = intent.name) }
            is NewAddressIntent.OnPhoneChange -> updateState { copy(phone = intent.phone) }
            is NewAddressIntent.OnCountryChange -> {
                updateState { copy(country = intent.country, city = "", cities = emptyList(), citiesError = null) }
                if (intent.country.isNotBlank()) loadCities(intent.country)
            }

            is NewAddressIntent.OnCityChange -> updateState { copy(city = intent.city) }
            is NewAddressIntent.OnProvinceChange -> updateState { copy(province = intent.province) }
            is NewAddressIntent.OnStreetChange -> updateState { copy(street = intent.street) }
            is NewAddressIntent.OnZipChange -> updateState { copy(zip = intent.zip) }
            is NewAddressIntent.OnNoteChange -> updateState { copy(note = intent.note) }
            is NewAddressIntent.OnDefaultChange -> updateState { copy(isDefault = intent.isDefault) }
            is NewAddressIntent.OnIconChange -> updateState { copy(icon = intent.icon) }
            is NewAddressIntent.Load -> load(intent.addressId)
            NewAddressIntent.OnBackClick -> sendEffect(NewAddressEffect.NavigateBack)
            NewAddressIntent.OnSaveClick -> saveAddress()
            NewAddressIntent.LoadCountries -> loadCountries()
            NewAddressIntent.ShowMap -> {
                onIntent(NewAddressIntent.GetCurrentLocation)
                sendEffect(NewAddressEffect.ShowMap)
            }
            NewAddressIntent.HideMap -> sendEffect(NewAddressEffect.HideMap)
            is NewAddressIntent.OnMapClick -> onMapClick(intent.latitude, intent.longitude)
            NewAddressIntent.OnNewMapAddressSelected -> saveMapAddress()
            NewAddressIntent.GetCurrentLocation -> {
                sendEffect(NewAddressEffect.RequestLocationPermission)
            }
            is NewAddressIntent.OnLocationPermissionResult -> {
                updateState { copy(hasLocationPermission = intent.granted) }
                if (!intent.granted) {
                    viewModelScope.launch { sendEffect(NewAddressEffect.ShowToast(getString(Res.string.address_location_permission_required))) }
                } else {
                    viewModelScope.launch {
                        getCurrentLocationCoordinatesUseCase().also { currentLocation ->
                            updateState {
                                copy(
                                    currentLocation = currentLocation
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onMapClick(latitude: Double, longitude: Double) {
        val coordinates = LocationCoordinates(lan = latitude, lon = longitude)
        updateState {
            copy(
                selectedMapLocation = coordinates,
                isGeocodingLoading = true,
                selectedLocationAddress = null
            )
        }
        viewModelScope.launch {
            try {
                val locationAddress = reverseGeocodingUseCase(coordinates)
                updateState {
                    copy(
                        selectedLocationAddress = locationAddress,
                        isGeocodingLoading = false,
                        country = locationAddress.country.ifBlank { country },
                        city = locationAddress.city.ifBlank { city },
                        street = buildString {
                            if (locationAddress.houseNumber.isNotBlank()) {
                                append(locationAddress.houseNumber)
                                append(" ")
                            }
                            append(locationAddress.road)
                        }.trim().ifBlank { street },
                        province = locationAddress.state.ifBlank { province },
                        zip = locationAddress.postcode.ifBlank { zip },
                    )
                }
            } catch (e: IOException) {
                updateState { copy(isGeocodingLoading = false) }
                sendEffect(NewAddressEffect.ShowToast(getString(Res.string.address_geocode_failed)))
            }
        }
    }

    private fun saveMapAddress() {
        val state = currentState
        if (state.selectedLocationAddress == null) {
            viewModelScope.launch { sendEffect(NewAddressEffect.ShowToast(getString(Res.string.address_select_location_first))) }
            return
        }
        sendEffect(NewAddressEffect.HideMap)
    }

    private fun load(addressId: String?) {
        if (addressId.isNullOrBlank() || editingId == addressId) return
        editingId = addressId
        viewModelScope.launch {
            val address = getSavedAddressByIdUseCase(addressId) ?: return@launch
            updateState {
                copy(
                    isEditMode = true,
                    label = address.label.orEmpty(),
                    recipientName = address.recipientName,
                    phone = address.phone.orEmpty(),
                    country = address.country.orEmpty(),
                    city = address.city.orEmpty(),
                    province = address.province.orEmpty(),
                    street = address.address1.orEmpty(),
                    zip = address.zip.orEmpty(),
                    note = address.note.orEmpty(),
                    isDefault = address.isDefault,
                    icon = address.icon,
                )
            }
            if (!address.country.isNullOrBlank()) loadCities(address.country!!)
        }
    }

    private fun loadCountries() {
        updateState { copy(isCountriesLoading = true, countriesError = null) }
        viewModelScope.launch {
            getCountriesUseCase().fold(
                onSuccess = { countries ->
                    updateState { copy(countries = countries, isCountriesLoading = false) }
                },
                onError = { error ->
                    updateState { copy(isCountriesLoading = false, countriesError = error.message) }
                },
                onLoading = {}
            )
        }
    }

    private fun loadCities(country: String) {
        updateState { copy(isCitiesLoading = true, citiesError = null) }
        viewModelScope.launch {
            getCitiesUseCase(country).fold(
                onSuccess = { cities ->
                    updateState { copy(cities = cities, isCitiesLoading = false) }
                },
                onError = { error ->
                    updateState { copy(isCitiesLoading = false, citiesError = error.message) }
                },
                onLoading = {}
            )
        }
    }

    private fun saveAddress() {
        val state = currentState
        if (state.recipientName.isBlank() || state.phone.isBlank() ||
            state.country.isBlank() || state.city.isBlank() || state.street.isBlank()
        ) {
            viewModelScope.launch { sendEffect(NewAddressEffect.ShowToast(getString(Res.string.address_fill_required_fields))) }
            return
        }

        val isEgypt = state.country.equals("Egypt", ignoreCase = true) || state.country.equals(
            "EG",
            ignoreCase = true
        )
        val cleanPhone = state.phone.filter { !it.isWhitespace() }

        if (isEgypt && !com.troves.domain.utils.PhoneUtils.isValidEgyptianPhone(cleanPhone)) {
            viewModelScope.launch { sendEffect(NewAddressEffect.ShowToast(getString(Res.string.address_invalid_phone))) }
            return
        }

        val normalizedPhone = if (isEgypt) {
            com.troves.domain.utils.PhoneUtils.normalizeEgyptianPhone(cleanPhone)
        } else {
            cleanPhone
        }

        updateState { copy(isSaving = true) }
        viewModelScope.launch {
            val address = Address(
                id = editingId.orEmpty(),
                address1 = state.street,
                address2 = null,
                city = state.city,
                province = state.province.ifBlank { null },
                provinceCode = null,
                country = state.country,
                countryCode = null,
                zip = state.zip.ifBlank { null },
                phone = normalizedPhone,
                firstName = state.recipientName.substringBefore(" ").ifBlank { null },
                lastName = state.recipientName.substringAfter(" ", "").ifBlank { null },
                company = null,
                label = state.label.ifBlank { null },
                icon = state.icon,
                note = state.note.ifBlank { null },
                isDefault = state.isDefault,
            )
            val isEdit = editingId != null
            val result = if (isEdit) {
                updateAddressUseCase(address)
            } else {
                addAddressUseCase(address)
            }
            updateState { copy(isSaving = false) }
            when (result) {
                is Result.Success -> sendEffect(
                    NewAddressEffect.SavedAndClose(
                        if (isEdit) getString(Res.string.address_updated) else getString(Res.string.address_saved)
                    )
                )

                is Result.Error -> if (result.throwable is ShopifyAuthRequiredException) {
                    sendEffect(
                        NewAddressEffect.RequireLogin(
                            result.throwable.message ?: getString(Res.string.address_sign_in_again)
                        )
                    )
                } else {
                    sendEffect(
                        NewAddressEffect.ShowToast(
                            result.throwable.message ?: getString(Res.string.address_save_failed)
                        )
                    )
                }

                is Result.Loading -> Unit
            }
        }
    }
}
