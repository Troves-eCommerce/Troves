package com.troves.presintation.ui.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.Address
import com.troves.domain.usecase.address.DeleteAddressUseCase
import com.troves.domain.usecase.address.GetSavedAddressesUseCase
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class ManageSavedAddressesUiState(
    val addresses: List<Address> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface ManageSavedAddressesIntent {
    data class OnDelete(val addressId: String) : ManageSavedAddressesIntent
    data class OnEdit(val address: Address) : ManageSavedAddressesIntent
    data object OnAddNew : ManageSavedAddressesIntent
    data object OnBackClick : ManageSavedAddressesIntent
}

sealed interface ManageSavedAddressesEffect {
    data object NavigateBack : ManageSavedAddressesEffect
    data object NavigateToNewAddress : ManageSavedAddressesEffect
    data class NavigateToEditAddress(val address: Address) : ManageSavedAddressesEffect
}

class ManageSavedAddressesViewModel(
    private val getSavedAddressesUseCase: GetSavedAddressesUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase
) : ViewModel(),
    StateHolder<ManageSavedAddressesUiState> by DefaultStateHolder(ManageSavedAddressesUiState()),
    EffectPublisher<ManageSavedAddressesEffect> by DefaultEffectPublisher() {

    init {
        getSavedAddressesUseCase()
            .onEach { items ->
                updateState { copy(addresses = items, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: ManageSavedAddressesIntent) {
        when (intent) {
            is ManageSavedAddressesIntent.OnDelete -> deleteAddress(intent.addressId)
            is ManageSavedAddressesIntent.OnEdit -> sendEffect(ManageSavedAddressesEffect.NavigateToEditAddress(intent.address))
            ManageSavedAddressesIntent.OnAddNew -> sendEffect(ManageSavedAddressesEffect.NavigateToNewAddress)
            ManageSavedAddressesIntent.OnBackClick -> sendEffect(ManageSavedAddressesEffect.NavigateBack)
        }
    }

    private fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            deleteAddressUseCase(addressId)
        }
    }
}
