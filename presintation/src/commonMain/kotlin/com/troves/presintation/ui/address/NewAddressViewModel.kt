package com.troves.presintation.ui.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.Address
import com.troves.domain.entity.AddressIcon
import com.troves.domain.utils.fold
import com.troves.domain.usecase.address.AddAddressUseCase
import com.troves.domain.usecase.shared.GetCountriesUseCase
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.launch
import kotlin.random.Random

data class NewAddressUiState(
    val label: String = "",
    val phone: String = "",
    val country: String = "",
    val city: String = "",
    val street: String = "",
    val note: String = "",
    val isDefault: Boolean = false,
    val icon: AddressIcon = AddressIcon.HOME,
    
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
    data class OnPhoneChange(val phone: String) : NewAddressIntent
    data class OnCountryChange(val country: String) : NewAddressIntent
    data class OnCityChange(val city: String) : NewAddressIntent
    data class OnStreetChange(val street: String) : NewAddressIntent
    data class OnNoteChange(val note: String) : NewAddressIntent
    data class OnDefaultChange(val isDefault: Boolean) : NewAddressIntent
    data class OnIconChange(val icon: AddressIcon) : NewAddressIntent
    
    data object OnSaveClick : NewAddressIntent
    data object OnBackClick : NewAddressIntent
    data object LoadCountries : NewAddressIntent
}

sealed interface NewAddressEffect {
    data object NavigateBack : NewAddressEffect
    data class ShowToast(val message: String) : NewAddressEffect
}

class NewAddressViewModel(
    private val getCountriesUseCase: GetCountriesUseCase,
    private val getCitiesUseCase: com.troves.domain.usecase.shared.GetCitiesUseCase,
    private val addAddressUseCase: AddAddressUseCase
) : ViewModel(),
    StateHolder<NewAddressUiState> by DefaultStateHolder(NewAddressUiState()),
    EffectPublisher<NewAddressEffect> by DefaultEffectPublisher() {

    init {
        onIntent(NewAddressIntent.LoadCountries)
    }

    fun onIntent(intent: NewAddressIntent) {
        when (intent) {
            is NewAddressIntent.OnLabelChange -> updateState { copy(label = intent.label) }
            is NewAddressIntent.OnPhoneChange -> updateState { copy(phone = intent.phone) }
            is NewAddressIntent.OnCountryChange -> {
                updateState { copy(country = intent.country, city = "", cities = emptyList(), citiesError = null) }
                if (intent.country.isNotBlank()) loadCities(intent.country)
            }
            is NewAddressIntent.OnCityChange -> updateState { copy(city = intent.city) }
            is NewAddressIntent.OnStreetChange -> updateState { copy(street = intent.street) }
            is NewAddressIntent.OnNoteChange -> updateState { copy(note = intent.note) }
            is NewAddressIntent.OnDefaultChange -> updateState { copy(isDefault = intent.isDefault) }
            is NewAddressIntent.OnIconChange -> updateState { copy(icon = intent.icon) }
            NewAddressIntent.OnBackClick -> sendEffect(NewAddressEffect.NavigateBack)
            NewAddressIntent.OnSaveClick -> saveAddress()
            NewAddressIntent.LoadCountries -> loadCountries()
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
        if (state.label.isBlank() || state.phone.isBlank() || state.country.isBlank() || state.city.isBlank() || state.street.isBlank()) {
            sendEffect(NewAddressEffect.ShowToast("Please fill all fields"))
            return
        }
        updateState { copy(isSaving = true) }
        viewModelScope.launch {
            val address = Address(
                id = Random.nextLong().toString(),
                label = state.label,
                icon = state.icon,
                phone = state.phone,
                lines = listOf(state.street, state.city, state.country),
                isDefault = state.isDefault,
                note = state.note.takeIf { it.isNotBlank() }
            )
            addAddressUseCase(address)
            updateState { copy(isSaving = false) }
            sendEffect(NewAddressEffect.NavigateBack)
        }
    }
}
