package com.troves.presintation.ui.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troves.domain.entity.Address
import com.troves.domain.usecase.address.DeleteAddressUseCase
import com.troves.domain.usecase.address.GetSavedAddressesUseCase
import com.troves.domain.usecase.address.RefreshAddressesUseCase
import com.troves.domain.usecase.address.SetDefaultAddressUseCase
import com.troves.domain.usecase.shared.ObserveConnectivityUseCase
import com.troves.domain.utils.Result
import com.troves.domain.utils.ShopifyAuthRequiredException
import com.troves.domain.utils.connectivity.ConnectivityStatus
import com.troves.presintation.core.mvi.DefaultEffectPublisher
import com.troves.presintation.core.mvi.DefaultStateHolder
import com.troves.presintation.core.mvi.EffectPublisher
import com.troves.presintation.core.mvi.StateHolder
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import troves.presintation.generated.resources.Res
import troves.presintation.generated.resources.address_delete_failed
import troves.presintation.generated.resources.address_load_failed
import troves.presintation.generated.resources.address_set_default_failed
import troves.presintation.generated.resources.address_sign_in_again

data class ManageSavedAddressesUiState(
    val addresses: List<Address> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isOffline: Boolean = false,
) {
    val showOfflineState: Boolean get() = isOffline && addresses.isEmpty() && !isLoading
}

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
    private val observeConnectivity: ObserveConnectivityUseCase,
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

        val online = observeConnectivity()
            .map { it == ConnectivityStatus.Available }
            .distinctUntilChanged()

        online
            .onEach { isOnline -> updateState { copy(isOffline = !isOnline) } }
            .launchIn(viewModelScope)

        online
            .drop(1)
            .onEach { isOnline -> if (isOnline) refresh() }
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
        // Offline: keep showing cached addresses; the offline placeholder covers the
        // empty case. Don't hit the network or nag with an error toast.
        if (!observeConnectivity.isOnlineNow()) {
            updateState { copy(isLoading = false) }
            return
        }
        viewModelScope.launch {
            val result = refreshAddressesUseCase()
            updateState { copy(isLoading = false) }
            if (result is Result.Error) reportError(result.throwable, getString(Res.string.address_load_failed))
        }
    }

    private fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            val result = deleteAddressUseCase(addressId)
            if (result is Result.Error) reportError(result.throwable, getString(Res.string.address_delete_failed))
        }
    }

    private fun setDefault(addressId: String) {
        viewModelScope.launch {
            val result = setDefaultAddressUseCase(addressId)
            if (result is Result.Error) reportError(result.throwable, getString(Res.string.address_set_default_failed))
        }
    }

    private suspend fun reportError(throwable: Throwable, fallback: String) {
        if (throwable is ShopifyAuthRequiredException) {
            if (loginRequested) return
            loginRequested = true
            sendEffect(ManageSavedAddressesEffect.RequireLogin(throwable.message ?: getString(Res.string.address_sign_in_again)))
        } else {
            sendEffect(ManageSavedAddressesEffect.ShowToast(throwable.message ?: fallback))
        }
    }
}
