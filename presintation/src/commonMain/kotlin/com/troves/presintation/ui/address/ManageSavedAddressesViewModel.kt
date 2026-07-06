package com.troves.presintation.ui.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.Address
import com.troves.domain.usecase.address.DeleteAddressUseCase
import com.troves.domain.usecase.address.GetSavedAddressesUseCase
import com.troves.domain.usecase.address.RefreshAddressesUseCase
import com.troves.domain.usecase.address.SetDefaultAddressUseCase
import com.troves.domain.utils.Result
import com.troves.domain.utils.ShopifyAuthRequiredException
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class ManageSavedAddressesUiState(
    val addresses: List<Address> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

sealed interface ManageSavedAddressesIntent {
    data class OnDelete(val addressId: String) : ManageSavedAddressesIntent
    data class OnEdit(val address: Address) : ManageSavedAddressesIntent
    data class OnSetDefault(val addressId: String) : ManageSavedAddressesIntent
    data object OnAddNew : ManageSavedAddressesIntent
    data object OnRefresh : ManageSavedAddressesIntent
    data object OnResume : ManageSavedAddressesIntent
    data object OnBackClick : ManageSavedAddressesIntent
}

sealed interface ManageSavedAddressesEffect {
    data object NavigateBack : ManageSavedAddressesEffect
    data object NavigateToNewAddress : ManageSavedAddressesEffect
    data class NavigateToEditAddress(val address: Address) : ManageSavedAddressesEffect
    /** Shopify customer token missing — route the user to re-authenticate. */
    data class RequireLogin(val message: String) : ManageSavedAddressesEffect
    data class ShowToast(val message: String) : ManageSavedAddressesEffect
}

class ManageSavedAddressesViewModel(
    getSavedAddressesUseCase: GetSavedAddressesUseCase,
    private val refreshAddressesUseCase: RefreshAddressesUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
    private val setDefaultAddressUseCase: SetDefaultAddressUseCase,
) : ViewModel(),
    StateHolder<ManageSavedAddressesUiState> by DefaultStateHolder(ManageSavedAddressesUiState()),
    EffectPublisher<ManageSavedAddressesEffect> by DefaultEffectPublisher() {

    // Guards against pushing the Login route more than once when several refreshes fail in a row.
    private var loginRequested = false

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
            is ManageSavedAddressesIntent.OnSetDefault -> setDefault(intent.addressId)
            ManageSavedAddressesIntent.OnAddNew -> sendEffect(ManageSavedAddressesEffect.NavigateToNewAddress)
            ManageSavedAddressesIntent.OnRefresh -> refresh()
            ManageSavedAddressesIntent.OnResume -> refresh()
            ManageSavedAddressesIntent.OnBackClick -> sendEffect(ManageSavedAddressesEffect.NavigateBack)
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            val result = refreshAddressesUseCase()
            updateState { copy(isLoading = false) }
            if (result is Result.Error) reportError(result.throwable, "Couldn't load addresses")
        }
    }

    private fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            val result = deleteAddressUseCase(addressId)
            if (result is Result.Error) reportError(result.throwable, "Couldn't delete address")
        }
    }

    private fun setDefault(addressId: String) {
        viewModelScope.launch {
            val result = setDefaultAddressUseCase(addressId)
            if (result is Result.Error) reportError(result.throwable, "Couldn't set default")
        }
    }

    private fun reportError(throwable: Throwable, fallback: String) {
        if (throwable is ShopifyAuthRequiredException) {
            if (loginRequested) return
            loginRequested = true
            sendEffect(ManageSavedAddressesEffect.RequireLogin(throwable.message ?: "Please sign in again"))
        } else {
            sendEffect(ManageSavedAddressesEffect.ShowToast(throwable.message ?: fallback))
        }
    }
}
